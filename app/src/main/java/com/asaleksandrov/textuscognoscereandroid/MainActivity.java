package com.asaleksandrov.textuscognoscereandroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        ImageButton btnFullscreen = findViewById(R.id.btn_fullscreen);
        DragResizeView dragResizeView = new DragResizeView(this);
        frameLayout.addView(dragResizeView);
        ImageButton menuButton = findViewById(R.id.menu_button);
        RelativeLayout sideMenu = findViewById(R.id.side_menu);

        // Новый способ обработки нажатия кнопки "Назад"
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (sideMenu.getVisibility() == View.VISIBLE) {
                    // Если меню видимо, скрываем его с анимацией
                    sideMenu.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.hide_menu));
                    sideMenu.setVisibility(View.GONE);
                } else {
                    setEnabled(false);
                    onBackPressed();
                }
            }
        });

        // Создаем экземпляр CameraHandler
        cameraHandler = new CameraHandler(this, previewView, capture, toggleFlash, progressBar);

        // Проверяем разрешение на использование камеры
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Если разрешение есть, запускаем камеру
            cameraHandler.startCamera(cameraFacing);
        } else {
            // Иначе запрашиваем разрешение
            requestCameraPermission();
        }

        btnFullscreen.setOnClickListener(v -> {
            dragResizeView.toggleFrame();

            // Проверка состояния dragResizeView
            if (Config.frameEnabled) {
                // Если в полноэкранном режиме, установите иконку на icon_one
                btnFullscreen.setImageResource(R.drawable.baseline_border_all_24);
            } else {
                // Если не в полноэкранном режиме, установите иконку на icon_two
                btnFullscreen.setImageResource(R.drawable.baseline_border_clear_24);
            }
        });

        menuButton.setOnClickListener(v -> {
            if (sideMenu.getVisibility() == View.GONE) {
                // Если меню невидимо, показываем его с анимацией
                sideMenu.setVisibility(View.VISIBLE);
                sideMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.show_menu));
            } else {
                // Если меню видимо, скрываем его с анимацией
                sideMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.hide_menu));
                sideMenu.setVisibility(View.GONE);
            }
        });
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