package com.asaleksandrov.textuscognoscereandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCropper {
    private final Bitmap bitmap;
    private final Context context;

    public ImageCropper(String imagePath, Context context) {
        this.context = context;
        this.bitmap = BitmapFactory.decodeFile(imagePath);

        if (this.bitmap == null) {
            Log.d("ImageCropper", "Failed to load image");
        }
    }

    public void cropAndSave(RectF rect) {

        // Ensure the rect does not go outside the bitmap
        if (rect.left < 0) {
            rect.left = 0;
        }
        if (rect.right > bitmap.getWidth()) {
            rect.right = bitmap.getWidth();
        }
        if (rect.top < 0) {
            rect.top = 0;
        }
        if (rect.bottom > bitmap.getHeight()) {
            rect.bottom = bitmap.getHeight();
        }

        // Calculate the width and height of the rectangle
        int width = (int) (rect.right - rect.left);
        int height = (int) (rect.bottom - rect.top);

        // Calculate the x and y position of the rectangle on the bitmap
        int x = (int) rect.left;
        int y = (int) rect.top;

        // Log values
        Log.d("Rectangle Info", "Width: " + width + ", Height: " + height + ", X: " + x + ", Y: " + y);

        // Create the cropped bitmap
        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, x, y, width, height);

        // Save the cropped bitmap
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
