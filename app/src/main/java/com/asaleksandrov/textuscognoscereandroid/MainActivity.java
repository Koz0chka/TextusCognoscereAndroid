package com.asaleksandrov.textuscognoscereandroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;

    private final int cameraFacing = CameraSelector.LENS_FACING_BACK;
    private CameraHandler cameraHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализируем элементы интерфейса
        PreviewView previewView = findViewById(R.id.cameraPreview);
        ImageButton capture = findViewById(R.id.capture);
        ImageButton toggleFlash = findViewById(R.id.toggleFlash);
        FrameLayout frameLayout = findViewById(R.id.frameLayout);
        DragResizeView dragResizeView = new DragResizeView(this);
        frameLayout.addView(dragResizeView);

        // Создаем экземпляр CameraHandler
        cameraHandler = new CameraHandler(this, previewView, capture, toggleFlash);

        // Проверяем разрешение на использование камеры
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Если разрешение есть, запускаем камеру
            cameraHandler.startCamera(cameraFacing);
        } else {
            // Иначе запрашиваем разрешение
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE))
                    .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraHandler.startCamera(cameraFacing);
            } else {
                Toast.makeText(this, "Camera Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}