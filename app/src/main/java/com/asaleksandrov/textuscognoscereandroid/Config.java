package com.asaleksandrov.textuscognoscereandroid;

import static com.asaleksandrov.textuscognoscereandroid.MainActivity.selectedLanguage;

public class Config {
    public static final String LANGUAGE = "eng+" + selectedLanguage;
    public static boolean frameEnabled = true;
    public static String OCR_ENGINE = "Tesseract"; //Tesseract EasyOCR

    public static final String SERVER_IP = "http://Koz0chka.pythonanywhere.com";
}
