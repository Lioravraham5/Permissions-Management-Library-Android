package com.example.permissionsmanagement;

import android.Manifest;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.permissionsmanagementlib.BackgroundLocationPermissionManager;
import com.example.permissionsmanagementlib.PermissionManager;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private MaterialButton requestPermission_BTN;
    private MaterialButton requestMultiplePermissions_BTN;
    private MaterialButton requestLocationPermissions_BTN;

    private PermissionManager singlePermissionManager;
    private PermissionManager multiplePermissionsManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestPermission_BTN = findViewById(R.id.requestPermission_BTN);
        requestMultiplePermissions_BTN = findViewById(R.id.requestMultiplePermissions_BTN);
        requestLocationPermissions_BTN = findViewById(R.id.requestLocationPermissions_BTN);

        singlePermissionManager = new PermissionManager(this, new String[]{Manifest.permission.READ_SMS});

        multiplePermissionsManager = new PermissionManager(this,
                new String[]{Manifest.permission.READ_CONTACTS,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE});

        requestPermission_BTN.setOnClickListener(v -> singlePermissionManager.requestPermissions());
        requestMultiplePermissions_BTN.setOnClickListener(v -> multiplePermissionsManager.requestPermissions());


        BackgroundLocationPermissionManager backgroundLocationPermissionManager = new BackgroundLocationPermissionManager(this);
        requestLocationPermissions_BTN.setOnClickListener(v -> backgroundLocationPermissionManager.requestBackgroundLocationPermission());
    }

}