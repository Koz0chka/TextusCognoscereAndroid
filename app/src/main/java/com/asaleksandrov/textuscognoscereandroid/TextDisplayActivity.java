package com.asaleksandrov.textuscognoscereandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TextDisplayActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_display);

        Intent intent = getIntent();
        String text = intent.getStringExtra("text");

        TextView textView = findViewById(R.id.text_view);
        textView.setText(text);
    }
}
