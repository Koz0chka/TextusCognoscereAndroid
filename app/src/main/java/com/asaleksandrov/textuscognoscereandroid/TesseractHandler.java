package com.asaleksandrov.textuscognoscereandroid;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TesseractHandler {
    private final Context context;
    private final TessBaseAPI mTess;

    public TesseractHandler(String language, Context context) {
        this.context = context;
        mTess = new TessBaseAPI();
        copyAssets();
        String datapath = this.context.getFilesDir() + "/tesseract/";
        mTess.init(datapath, language);
    }

    public String processImage(String filePath) {
        // Преобразование файла в Bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        mTess.setImage(bitmap);
        String result = mTess.getUTF8Text();
        return result;
    }

    private void copyAssets() {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("tessdata");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in;
            OutputStream out;
            try {
                in = assetManager.open("tessdata/" + filename);
                File tessdataDir = new File(context.getFilesDir() + "/tesseract/tessdata/");
                if (!tessdataDir.exists()) {
                    tessdataDir.mkdirs();
                }
                File outFile = new File(tessdataDir, filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                out.flush();
                out.close();
            } catch(Exception e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}