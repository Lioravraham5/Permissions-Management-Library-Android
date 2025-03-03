![GitHub](https://img.shields.io/github/license/Lioravraham5/Permissions-Management-Library-Android)
[![](https://jitpack.io/v/Lioravraham5/Permissions-Management-Library-Android.svg)](https://jitpack.io/#Lioravraham5/Permissions-Management-Library-Android)
[![API](https://img.shields.io/badge/API-26%2B-green.svg?style=flat)]()

# PermissionsManagementLib Library
PermissionsManagementLib is an Android library designed to simplify permission management in Android applications. It helps developers request, handle, and manage app permissions effortlessly, including general and background location permissions.

## Features
- **Easy Permission Handling:** Request multiple permissions with a simple API
- **Background Location Permission Management:** Specifically handles background location requests with user guidance. Supports <ins>Android 10 (API 29) and above</ins>, ensuring compatibility with modern permission requirements.
- **Customizable Rationale & Settings Dialogs:** Display messages for denied permissions.
- **Automatic Settings Navigation:** Guides users to enable permissions in settings when required.
- **Permission Callbacks:** Handle granted or denied permissions seamlessly. The library provides a default callback that automatically displays a <ins>Toast message</ins> indicating whether permissions were granted or denied, making it easy to integrate without additional setup.

## How It Works
1) **Checks Permission Status:** Verifies if permissions are already granted.
2) **Requests Permissions:** Uses ActivityResultLauncher for modern permission handling.
3) **Handles Denied Permissions:**
    - If denied, shows rationale dialog or directs users to settings.
    -  Automatically calls onPermissionsGranted() or onPermissionsDenied() using either the default callback (which shows a toast message) or a custom callback provided by the developer.

### General Permissions 
 - **Rationale Dialog:** This dialog informs users why certain permissions are required before requesting them.
 - **Settings Dialog:** If the user has permanently denied permissions, this dialog prompts them to enable them manually via the app settings.
<div style="display: flex; justify-content: space-between; gap: 10px;">
  <img src="https://github.com/user-attachments/assets/41d31426-f44a-4d28-a47f-15b71dd58f94" alt="Image 1" style="width: 22%; height: 22%;">
  <img src="https://github.com/user-attachments/assets/9f734895-b933-4948-ae8e-8974fe38ace0" alt="Image 2" style="width: 22%; height: 22%;">
</div>

### Background Location
- **Settings Dialog (Android 11+):** For devices running **Android 11 (API 30) and above**, background location access must be granted manually in the app settings. This dialog guides users on how to enable it.
- **Android 10 (API 29) & Below:** First displays a rationale dialog explaining the need for background location. If denied, users are directed to app settings.
<div style="display: flex; justify-content: space-between; gap: 10px;">
  <img src="https://github.com/user-attachments/assets/c0c7d580-04f7-4846-a818-d68311d1fe39" alt="Image 2" style="width: 22%; height: 22%;">
  <img src="https://github.com/user-attachments/assets/9d49b687-98f2-4493-85d4-8b1cb3de5dc8" alt="Image 2" style="width: 22%; height: 22%;">
</div>

### Example Application Video:
<video width="640" height="360" controls>
  <source src="https://firebasestorage.googleapis.com/v0/b/videosads-11664.firebasestorage.app/o/examplesVideosApps%2FpermissionsLibraryVideo.mp4?alt=media&token=ceb090c2-b326-422c-999e-62694b2561bc" type="video/mp4">
  Your browser does not support the video tag.
</video>

## Setup
Step 1. Add it in your root `build.gradle` at the end of repositories:
```
allprojects {
    repositories {
        // other repositories
        maven { url = uri("https://jitpack.io" )}
    }
}
```

Step 2. Add the dependency:
```
dependencies {
  implementation ("com.github.Lioravraham5:Permissions-Management-Library-Android:1.0.0")
}
```

## Usage:
1) **Add Permissions to AndroidManifest.xml:** Before using the permission manager, you must declare the required permissions in your `AndroidManifest.xml` file.

For General Permissions:
Add the necessary permissions based on your app’s needs, for example:
```
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

For Background Location Permission:
If your app requires location access, include the following:
```
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/>
```

2) **Initialize permission manager:** Create an instance of `GeneralPermissionManager` and pass the required permissions, or use `BackgroundLocationPermissionManager` to manage background location requests.

GeneralPermissionManager:   
```
String[] permissions = {
    Manifest.permission.CAMERA,
    Manifest.permission.READ_EXTERNAL_STORAGE
};

GeneralPermissionManager generalPermissionManager = new GeneralPermissionManager(this, permissions);
```

BackgroundLocationPermissionManager:
```
BackgroundLocationPermissionManager locationPermissionManager = new BackgroundLocationPermissionManager(this);
```

3) **Define Permission Callbacks (Optional):** Set up callbacks to handle granted and denied permissions.

GeneralPermissionManager:  
```
generalPermissionManager.setPermissionsCallback(new PermissionsCallback() {
    @Override
    public void onPermissionsGranted() {
        // Handle permission granted
        // Youre code
    }

    @Override
    public void onPermissionsDenied(List<String> deniedPermissions) {
        // Handle permission denied
        // Youre code
    }
});
```

BackgroundLocationPermissionManager:
```
locationPermissionManager.setPermissionsCallback(new PermissionsCallback() {
    @Override
    public void onPermissionsGranted() {
        // Handle permission granted
        // Your code
    }

    @Override
    public void onPermissionsDenied(List<String> deniedPermissions) {
        // Handle permission denied
        // your code
    }
});

```

4) **Customize Rationale & Settings Dialog Messages (Optional):** Define custom messages for the rationale and settings dialogs.

GeneralPermissionManager:  
```
generalPermissionManager
    .setRationaleTitle("App Permissions Needed")
    .setRationaleMessage("These permissions are required for the app to function correctly.")
    .setSettingsTitle("Enable Required Permissions")
    .setSettingsMessage("Please enable the necessary permissions in app settings.");
```

BackgroundLocationPermissionManager:
```
locationPermissionManager
    .setRationaleTitle("Location Access Required")
    .setRationaleMessage("We need this permission to track your location.")
    .setSettingsTitle("Enable Location Permission")
    .setSettingsMessage("To use this feature, allow background location access in settings.");
```

5) **Request Permissions:**

GeneralPermissionManager:
```
generalPermissionManager.requestPermissions();
``` 

BackgroundLocationPermissionManager:
```
locationPermissionManager.requestBackgroundLocationPermission();
```
## License
```
Copyright 2025 Lior Avraham

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```




