package com.asaleksandrov.textuscognoscereandroid;

import static com.asaleksandrov.textuscognoscereandroid.MainActivity.selectedLanguage;

public class Config {
    public static final String LANGUAGE = "eng+" + selectedLanguage;
    public static boolean frameEnabled = true;
    public static final String OCR_ENGINE = "Tesseract"; //Tesseract EasyOCR

    public static final String SERVER_IP = "http://192.168.0.189:5000";
}
