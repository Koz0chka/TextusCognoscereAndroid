package com.asaleksandrov.textuscognoscereandroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Button buttonChoose = findViewById(R.id.button_choose);
        buttonChoose.setText(Config.OCR_ENGINE);
        buttonChoose.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setTitle("Выберите OCR")
                    .setItems(new String[]{"Tesseract", "EasyOCR"}, (dialog, which) -> {
                        // Обрабатываем выбор пользователя
                        switch (which) {
                            case 0:
                                Config.OCR_ENGINE = "Tesseract";
                                break;
                            case 1:
                                Config.OCR_ENGINE = "EasyOCR";
                                break;
                        }
                        buttonChoose.setText(Config.OCR_ENGINE);
                    });
            builder.create().show();
        });

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());
    }
}
