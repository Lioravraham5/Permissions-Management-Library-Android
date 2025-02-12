package com.example.permissionsmanagementlib;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Collections;
import java.util.List;

public class BackgroundLocationPermissionManager {

    private final AppCompatActivity activity;
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher;
    private final ActivityResultLauncher<Intent> settingsLauncher;
    private PermissionsCallback permissionsCallback;

    public BackgroundLocationPermissionManager(AppCompatActivity activity) {
        this.activity = activity;
        this.permissionsCallback = getDefaultCallback();

        requestPermissionsLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean granted = result.get(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                    if (granted != null && granted) {
                        permissionsCallback.onPermissionsGranted();
                    }
                    else if (activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        // Show permission rationale if user denied the permission
                        showPermissionRationale();
                    }
                    else {
                        // Show settings dialog if user denied the permission and checked "Don't ask again"
                        showSettingsDialog();
                    }
                }
        );

        settingsLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> checkPermissionStatus()
        );
    }

    private PermissionsCallback getDefaultCallback() {
        return new PermissionsCallback() {
            @Override
            public void onPermissionsGranted() {
                PermissionUtils.showModifyToast(activity, "Background location permission granted", R.drawable.done);
            }

            @Override
            public void onPermissionsDenied(List<String> deniedPermissions) {
                String message = "Permissions denied:\n" + PermissionUtils.permissionsStringGenerator(deniedPermissions);
                PermissionUtils.showModifyToast(activity, message, R.drawable.undone);
            }
        };
    }

    public void requestBackgroundLocationPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permissionsCallback.onPermissionsGranted();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11+ (API 30), direct the user to settings
            showSettingsDialog();
        }

        // For Android 10 and below, request permissions
        else {
            // Request ACCESS_BACKGROUND_LOCATION permission only if foreground location is granted
            if (isForegroundLocationGranted()) {
                requestPermissionsLauncher.launch(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION});
            }

            // Request ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions
            else {
                requestPermissionsLauncher.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                });
            }
        }
    }

    private boolean isForegroundLocationGranted() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void checkPermissionStatus() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permissionsCallback.onPermissionsGranted();
        } else {
            permissionsCallback.onPermissionsDenied(Collections.singletonList(Manifest.permission.ACCESS_BACKGROUND_LOCATION));
        }
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(activity)
                .setTitle("Background Location Required")
                .setMessage("To allow background location access, enable it in app settings.\n\n" +
                        "On the page that opens - click on PERMISSIONS, then on LOCATION and then select \"Allow all the time\"")
                .setPositiveButton("Go to Settings", (dialog, which) -> openAppSettings())
                .setNegativeButton("Cancel", (dialog, which) -> permissionsCallback.onPermissionsDenied(Collections.singletonList(Manifest.permission.ACCESS_BACKGROUND_LOCATION)))
                .setCancelable(false)
                .show();
    }

    private void showPermissionRationale() {
        new AlertDialog.Builder(activity)
                .setTitle("Background location Permission Required")
                .setMessage("This permission is required for the app to function properly.")
                .setPositiveButton("Allow", (dialog, which) ->
                        requestPermissionsLauncher.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION}))
                .setNegativeButton("Deny", (dialog, which) -> permissionsCallback.onPermissionsDenied(Collections.singletonList(Manifest.permission.ACCESS_BACKGROUND_LOCATION)))
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        settingsLauncher.launch(intent);
    }

    public void setPermissionsCallback(PermissionsCallback permissionsCallback) {
        this.permissionsCallback = permissionsCallback;
    }
}
