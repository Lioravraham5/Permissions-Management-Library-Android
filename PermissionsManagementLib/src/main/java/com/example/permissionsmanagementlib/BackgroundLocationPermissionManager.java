package com.example.permissionsmanagementlib;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Collections;

public class BackgroundLocationPermissionManager {

    private final String TOAST_MESSAGE = "Background Location Permission Granted";

    // default PermissionRationale messages:
    private String rationaleTitle = "Background location Permission Required";
    private String rationaleMessage = "This permission is required for the app to function properly.";

    // default SettingsDialog messages:
    private String settingsTitle = "Background Location Permission Required";
    private String settingsMessage = "To allow background location access, follow these steps:\n\n" +
            "1. Open App \"Settings\".\n" +
            "2. Navigate to \"Permissions\".\n" +
            "3. Select \"Location\".\n" +
            "4. Choose \"Allow all the time\".\n\n" +
            "This ensures the app can access your location even when it's running in the background.";

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
        return PermissionUtils.getDefaultCallback(activity, TOAST_MESSAGE);
    }



    public void requestBackgroundLocationPermission() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void checkPermissionStatus() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permissionsCallback.onPermissionsGranted();
        } else {
            permissionsCallback.onPermissionsDenied(Collections.singletonList(Manifest.permission.ACCESS_BACKGROUND_LOCATION));
        }
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(settingsTitle)
                .setMessage(settingsMessage)
                .setIcon(R.drawable.setting)
                .setPositiveButton("Go to Settings", (dialog, which) -> PermissionUtils.openAppSettings(activity,settingsLauncher))
                .setNegativeButton("Cancel", (dialog, which) -> permissionsCallback.onPermissionsDenied(Collections.singletonList(Manifest.permission.ACCESS_BACKGROUND_LOCATION)))
                .setCancelable(false)
                .show();
    }

    private void showPermissionRationale() {
        new AlertDialog.Builder(activity)
                .setTitle(rationaleTitle)
                .setMessage(rationaleMessage)
                .setIcon(R.drawable.warning)
                .setPositiveButton("Allow", (dialog, which) ->
                        requestPermissionsLauncher.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION}))
                .setNegativeButton("Deny", (dialog, which) -> permissionsCallback.onPermissionsDenied(Collections.singletonList(Manifest.permission.ACCESS_BACKGROUND_LOCATION)))
                .show();
    }


    public void setPermissionsCallback(PermissionsCallback permissionsCallback) {
        this.permissionsCallback = permissionsCallback;
    }

    public BackgroundLocationPermissionManager setRationaleTitle(String rationaleTitle) {
        this.rationaleTitle = rationaleTitle;
        return this;
    }

    public BackgroundLocationPermissionManager setRationaleMessage(String rationaleMessage) {
        this.rationaleMessage = rationaleMessage;
        return this;
    }

    public BackgroundLocationPermissionManager setSettingsTitle(String settingsTitle) {
        this.settingsTitle = settingsTitle;
        return this;
    }

    public BackgroundLocationPermissionManager setSettingsMessage(String settingsMessage) {
        this.settingsMessage = settingsMessage;
        return this;
    }
}
