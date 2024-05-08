package com.asaleksandrov.textuscognoscereandroid;

import android.content.Context;
import android.database.Cursor;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ProgressBar;

import androidx.camera.view.PreviewView;

import java.io.File;

public class OcrProcessor {
    private final Context context;
    private final PreviewView previewView;
    private final ProgressBar progressBar;

    public OcrProcessor(Context context, PreviewView previewView, ProgressBar progressBar) {
        this.context = context;
        this.previewView = previewView;
        this.progressBar = progressBar;
    }

    public void processImage(String imagePath, RectF frame, String ocrEngine) {
        TesseractHandler mTesseractHandler = new TesseractHandler(Config.LANGUAGE, context, progressBar);
        HTTPHandler mHTTPHandler = new HTTPHandler(Config.SERVER_IP);

        String cropped = String.valueOf(new File(context.getExternalFilesDir(null), "CROPPED.png"));
        String image_to_process = String.valueOf(new File(context.getExternalFilesDir(null), "image_to_process.png"));

        if (ocrEngine.equals("Tesseract")) {
            if (frame != null) {
                // Рамка включена, обрезаем изображение
                ImageCropper imageCropper = new ImageCropper(imagePath, context, previewView);
                imageCropper.cropAndSave(frame);
                String preprocessedImagePath = ImagePreprocessor.preprocessImage(cropped, context, progressBar);
                mTesseractHandler.processProcessedImage(preprocessedImagePath);
            } else {
                // Рамка выключена, используем полное изображение
                String preprocessedImagePath = ImagePreprocessor.preprocessImage(image_to_process, context, progressBar);
                mTesseractHandler.processProcessedImage(preprocessedImagePath);
            }
        } else {
            if (frame != null) {
                // Рамка включена, обрезаем изображение
                ImageCropper imageCropper = new ImageCropper(imagePath, context, previewView);
                imageCropper.cropAndSave(frame);
                String preprocessedImagePath = ImagePreprocessor.preprocessImage(cropped, context, progressBar);
                mHTTPHandler.httpProcess(preprocessedImagePath, context, progressBar);
            } else {
                // Рамка выключена, используем полное изображение
                String preprocessedImagePath = ImagePreprocessor.preprocessImage(image_to_process, context, progressBar);
                mHTTPHandler.httpProcess(preprocessedImagePath, context, progressBar);
            }
        }
    }

    void processGalleryImage(String selectedImageUri) {
        String imagePath = getPathFromUri(context, Uri.parse(selectedImageUri));
        TesseractHandler mTesseractHandler = new TesseractHandler(Config.LANGUAGE, context, progressBar);
        HTTPHandler mHTTPHandler = new HTTPHandler(Config.SERVER_IP);
        String preprocessedImagePath = ImagePreprocessor.preprocessImage(imagePath, context, progressBar);
        if (Config.OCR_ENGINE.equals("Tesseract")) {
            mTesseractHandler.processProcessedImage(preprocessedImagePath);
        } else {
            mHTTPHandler.httpProcess(preprocessedImagePath, context, progressBar);
        }
    }

    public String getPathFromUri(Context context, Uri uri) {
        String result;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
