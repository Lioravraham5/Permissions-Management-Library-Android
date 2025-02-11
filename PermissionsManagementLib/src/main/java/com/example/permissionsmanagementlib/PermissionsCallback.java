package com.example.permissionsmanagementlib;

import java.util.List;

public interface PermissionsCallback {
    void onPermissionsGranted();
    void onPermissionsDenied(List<String> deniedPermissions);
}
