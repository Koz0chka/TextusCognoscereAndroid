package com.asaleksandrov.textuscognoscereandroid;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import org.opencv.core.Core;
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

        File picture = new File(imagePath);
        Mat img = Imgcodecs.imread(picture.getPath());

        // Increasing the image size
        int width = img.width() * 2; // Decreased scaling factor
        int height = img.height() * 2; // Decreased scaling factor
        Size size = new Size(width, height);
        Imgproc.resize(img, img, size, 0, 0, Imgproc.INTER_LINEAR); // Changed interpolation method

        // Converting the image to gray scale
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);

        // Create a copy of the image for the unsharp mask
        Mat imgCopy = img.clone();

        // Slight blur and convert to binary image
        Imgproc.medianBlur(img, img, 3);

        // Simple thresholding
        double thresholdValue = 127; // Adjust this value as needed
        Imgproc.threshold(img, img, thresholdValue, 255, Imgproc.THRESH_BINARY);

        // Create the unsharp mask by subtracting the blurred image from the original
        Core.subtract(imgCopy, img, img);

        // Add the unsharp mask back to the original image
        Core.addWeighted(imgCopy, 1.5, img, -0.5, 0, img);

        // Invert the colors
        Core.bitwise_not(img, img);

        // Save the preprocessed image
        File file = new File(context.getExternalFilesDir(null), "preprocessed.png");
        boolean result = Imgcodecs.imwrite(file.getAbsolutePath(), img);

        // Return the path to the preprocessed image
        return file.getAbsolutePath();
    }
}