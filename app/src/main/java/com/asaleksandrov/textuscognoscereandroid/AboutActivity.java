package com.asaleksandrov.textuscognoscereandroid;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ImageButton;

public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());
    }
}
