package com.example.permissionsmanagementlib;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionManager {

    // default PermissionRationale messages:
    private String rationaleTitle = "Permissions Required";
    private String rationaleMessage = "The following permissions are required to ensure the app operates properly:";

    // default SettingsDialog messages:
    private String settingsTitle = "Permissions Denied";
    private String settingsMessage = "Some permissions are permanently denied. Please enable them in app settings.";

    private final AppCompatActivity activity;
    private final String[] permissions;

    private final ActivityResultLauncher<String[]> requestPermissionsLauncher;
    private final ActivityResultLauncher<Intent> settingsLauncher;

    private PermissionsCallback permissionsCallback;

    public PermissionManager(AppCompatActivity activity, String[] permissions) {
        this.activity = activity;
        this.permissions = permissions;
        this.permissionsCallback = getDefaultCallback();

        // Register permission request launcher for multiple permissions.
        requestPermissionsLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    List<String> deniedPermissions = new ArrayList<>();
                    boolean allGranted = true;

                    for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                        if (!entry.getValue()) {
                            deniedPermissions.add(entry.getKey());
                            allGranted = false;
                        }
                    }

                    if (allGranted) {
                        permissionsCallback.onPermissionsGranted();
                    } else {
                        handleDeniedPermissions(deniedPermissions);
                    }
                }
        );

        // Register settings result launcher
        settingsLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> checkPermissions()
        );
    }

    private PermissionsCallback getDefaultCallback() {
        return new PermissionsCallback() {
            @Override
            public void onPermissionsGranted() {
                PermissionUtils.showModifyToast(activity, "Permissions granted", R.drawable.done);
            }

            @Override
            public void onPermissionsDenied(List<String> deniedPermissions) {
                String message = "Permissions denied:\n" + PermissionUtils.permissionsStringGenerator(deniedPermissions);
                PermissionUtils.showModifyToast(activity, message, R.drawable.undone);
            }
        };
    }

    public void requestPermissions() {
        if (areAllPermissionsGranted()) {
            permissionsCallback.onPermissionsGranted();
        } else {
            requestPermissionsLauncher.launch(permissions);
        }
    }

    private boolean areAllPermissionsGranted() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void checkPermissions() {
        if (areAllPermissionsGranted()) {
            permissionsCallback.onPermissionsGranted();
        } else {
            permissionsCallback.onPermissionsDenied(getDeniedPermissions());
        }
    }

    private List<String> getDeniedPermissions() {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    private void handleDeniedPermissions(List<String> deniedPermissions) {
        Log.d("PermissionManager", "Denied permissions: " + deniedPermissions.toString());
        boolean showRationale = false;
        for (String permission : deniedPermissions) {
            boolean isShouldShowRationale = activity.shouldShowRequestPermissionRationale(permission);
            Log.d("PermissionManager", "Should show rationale for " + permission + ": " + isShouldShowRationale);
            if (isShouldShowRationale) {
                showRationale = true;
                break;
            }
        }

        if (showRationale) {
            showPermissionRationale(deniedPermissions);
        } else {
            showSettingsDialog();
        }
    }

    private void showPermissionRationale(List<String> deniedPermissions) {
        new AlertDialog.Builder(activity)
                .setTitle(rationaleTitle)
                .setMessage(rationaleMessage + "\n" + PermissionUtils.permissionsStringGenerator(deniedPermissions))
                .setPositiveButton("Allow", (dialog, which) -> requestPermissionsLauncher.launch(deniedPermissions.toArray(new String[0])))
                .setNegativeButton("Deny", (dialog, which) -> permissionsCallback.onPermissionsDenied(deniedPermissions))
                .setCancelable(false)
                .show();
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(settingsTitle)
                .setMessage(settingsMessage)
                .setPositiveButton("Go to Settings", (dialog, which) -> openAppSettings())
                .setNegativeButton("Cancel", (dialog, which) -> permissionsCallback.onPermissionsDenied(getDeniedPermissions()))
                .setCancelable(false)
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        settingsLauncher.launch(intent);
    }

    public PermissionManager setRationaleTitle(String rationaleTitle) {
        this.rationaleTitle = rationaleTitle;
        return this;
    }

    public PermissionManager setRationaleMessage(String rationaleMessage) {
        this.rationaleMessage = rationaleMessage;
        return this;
    }

    public PermissionManager setSettingsMessage(String settingsMessage) {
        this.settingsMessage = settingsMessage;
        return this;
    }

    public PermissionManager setSettingsTitle(String settingsTitle) {
        this.settingsTitle = settingsTitle;
        return this;
    }

    public void setPermissionsCallback(PermissionsCallback permissionsCallback) {
        this.permissionsCallback = permissionsCallback;
    }

}
