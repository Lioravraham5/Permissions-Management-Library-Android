package com.example.permissionsmanagementlib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class PermissionUtils {

    public static void showModifyToast(Activity activity, String message, int iconResId) {
        // inflate the layout
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast, null);

        // Set the text of the Toast
        TextView toastText = layout.findViewById(R.id.toast_text);
        toastText.setText(message);

        // Set the icon of the Toast
        ImageView toastIcon = layout.findViewById(R.id.toast_icon);
        toastIcon.setImageResource(iconResId);

        // Create and show the Toast
        Toast toast = new Toast(activity.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static PermissionsCallback getDefaultCallback(AppCompatActivity activity, String toastMessage) {
        return new PermissionsCallback() {
            @Override
            public void onPermissionsGranted() {
                PermissionUtils.showModifyToast(activity, toastMessage, R.drawable.done);
            }

            @Override
            public void onPermissionsDenied(List<String> deniedPermissions) {
                String message = "Permissions denied:\n" + PermissionUtils.permissionsStringGenerator(deniedPermissions);
                PermissionUtils.showModifyToast(activity, message, R.drawable.undone);
            }
        };
    }

    private static String getPermissionName(String permission) {
        return permission.toLowerCase().substring(("android.permission.").length()).replace('_', ' ');
    }

    public static String permissionsStringGenerator(List<String> permissions) {
        StringBuilder permissionsString = new StringBuilder();
        for (int i = 0; i < permissions.size(); i++) {
            String permission = permissions.get(i);
            permissionsString.append((i+1) + ") " )
                    .append(getPermissionName(permission))
                    .append("\n");
        }

        return permissionsString.toString();
    }

    public static void openAppSettings(AppCompatActivity activity, ActivityResultLauncher<Intent> settingsLauncher) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        settingsLauncher.launch(intent);
    }



}
