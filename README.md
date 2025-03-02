[![](https://jitpack.io/v/Lioravraham5/Permissions-Management-Library-Android.svg)](https://jitpack.io/#Lioravraham5/Permissions-Management-Library-Android)

# PermissionsManagementLib Library
PermissionsManagementLib is an Android library designed to simplify permission management in Android applications. It helps developers request, handle, and manage app permissions effortlessly, including general and background location permissions.

## Features
- **Easy Permission Handling:** Request multiple permissions with a simple API
- **Background Location Permission Management:** Specifically handles background location requests with user guidance. Supports <ins>Android 10 (API 29) and above</ins>, ensuring compatibility with modern permission requirements.
- **Customizable Rationale & Settings Dialogs:** Display messages for denied permissions.
- **Automatic Settings Navigation:** Guides users to enable permissions in settings when required.
- **Permission Callbacks:** Handle granted or denied permissions seamlessly.

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
