package com.asaleksandrov.textuscognoscereandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.util.Log;

import androidx.camera.view.PreviewView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCropper {
    private final Bitmap bitmap;
    private final Context context;
    private final PreviewView previewView;

    public ImageCropper(String imagePath, Context context, PreviewView previewView) { // и это
        this.context = context;
        this.previewView = previewView;

        Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath);
        // Вычисление коэффициентов масштабирования
        float scaleX = (float) previewView.getMeasuredWidth() / originalBitmap.getWidth();
        float scaleY = (float) previewView.getMeasuredHeight() / originalBitmap.getHeight();
        // Создание нового Bitmap с требуемым размером
        this.bitmap = Bitmap.createScaledBitmap(originalBitmap, (int)(originalBitmap.getWidth() * scaleX), (int)(originalBitmap.getHeight() * scaleY), true);

        if (this.bitmap == null) {
            Log.d("ImageCropper", "Failed to load image");
        }
    }

    public void cropAndSave(RectF rect) {

        // Определение размеров и положения прямоугольника в Bitmap
        int width = (int) rect.width();
        int height = (int) rect.height();
        int x = (int) rect.left;
        int y = (int) rect.top;

        // Убедитесь, что прямоугольник не выходит за пределы Bitmap
        if (x < 0 || y < 0 || x + width > bitmap.getWidth() || y + height > bitmap.getHeight()) {
            Log.d("ImageCropper", "Rectangle goes outside the bitmap bounds");
            return;
        }

        // Создайте обрезанный Bitmap
        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, x, y, width, height);

        // Save the cropped Bitmap
        try {
            File file = new File(context.getExternalFilesDir(null), "CROPPED.jpg");
            FileOutputStream outStream = new FileOutputStream(file);
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}