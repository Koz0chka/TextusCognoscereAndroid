package com.asaleksandrov.textuscognoscereandroid;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class ImagePreprocessor {

    static { System.loadLibrary("opencv_java4"); }

    public static String preprocessImage(String imagePath, Context context, ProgressBar progressBar) {
        // Start the progressBar
        ((Activity)context).runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

        File picture = new File(context.getExternalFilesDir(null), imagePath);
        Mat img = Imgcodecs.imread(picture.getPath());

        // Increasing the image size
        int width = img.width() * 3;
        int height = img.height() * 3;
        Size size = new Size(width, height);
        Imgproc.resize(img, img, size, 0, 0, Imgproc.INTER_CUBIC);

        // Converting the image to gray
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);

        // Slight blur and convert to binary image
        Imgproc.medianBlur(img, img, 3);
        Imgproc.threshold(img, img, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

        // Save the preprocessed image
        File file = new File(context.getExternalFilesDir(null), "preprocessed.png");
        boolean result = Imgcodecs.imwrite(file.getAbsolutePath(), img);

        // Return the path to the preprocessed image
        return file.getAbsolutePath();
    }
}