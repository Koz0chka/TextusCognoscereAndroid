package com.asaleksandrov.textuscognoscereandroid;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.RectF;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class CameraHandler {

    private final Context context;
    private final PreviewView previewView;
    private final ImageButton capture, toggleFlash;
    private final int cameraFacing = CameraSelector.LENS_FACING_BACK;

    public CameraHandler(Context context, PreviewView previewView, ImageButton capture, ImageButton toggleFlash) {
        this.context = context;
        this.previewView = previewView;
        this.capture = capture;
        this.toggleFlash = toggleFlash;
    }

    public void startCamera(int cameraFacing) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permission to use camera is not granted", Toast.LENGTH_LONG).show();
            return;
        }

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                ImageCapture imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing)
                        .build();

                cameraProvider.unbindAll();
                Camera camera = cameraProvider.bindToLifecycle((AppCompatActivity) context, cameraSelector, preview, imageCapture);

                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                capture.setOnClickListener(v -> takePicture(imageCapture));

                toggleFlash.setOnClickListener(v -> setFlashIcon(camera));

            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(context, "Error starting up camera: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, ContextCompat.getMainExecutor(context));
    }

    public void takePicture(ImageCapture imageCapture) {
        final File file = new File(context.getExternalFilesDir(null), "image_to_process.png");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();

//        RectF rect = DragResizeView.getRect(); // get the rect from DragResizeView
//        ImageCropper imageCropper = new ImageCropper(file.toString(), context);
//        imageCropper.cropAndSave(rect);

        imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, "Image saved at: " + file.getPath(), Toast.LENGTH_SHORT).show();
//                    }
//                });

                RectF rect = DragResizeView.getRect(); // get the rect from DragResizeView
                ImageCropper imageCropper = new ImageCropper(file.toString(), context, previewView);
                imageCropper.cropAndSave(rect);

                startCamera(cameraFacing);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Toast.makeText(context, "Failed to save image: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }

//            @Override
//            public void onError(@NonNull ImageCaptureException exception) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, "Failed to save: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//                startCamera(cameraFacing);
//            }
        });
    }

    private void setFlashIcon(Camera camera) {
        if (camera.getCameraInfo().hasFlashUnit()) {
            if (camera.getCameraInfo().getTorchState().getValue() == 0) {
                camera.getCameraControl().enableTorch(true);
                toggleFlash.setImageResource(R.drawable.baseline_flash_off_24);
            } else {
                camera.getCameraControl().enableTorch(false);
                toggleFlash.setImageResource(R.drawable.baseline_flash_on_24);
            }
        } else {
            Toast.makeText(context, "Flash is not available currently", Toast.LENGTH_SHORT).show();
        }
    }
}