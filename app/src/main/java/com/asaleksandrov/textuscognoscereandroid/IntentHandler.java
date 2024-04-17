package com.asaleksandrov.textuscognoscereandroid;

import android.content.Context;
import android.content.Intent;

public class IntentHandler {
    public static void startTextDisplayActivity(Context context, String result) {
        Intent intent = new Intent(context, TextDisplayActivity.class);
        intent.putExtra("text", result);
        context.startActivity(intent);
    }
}
