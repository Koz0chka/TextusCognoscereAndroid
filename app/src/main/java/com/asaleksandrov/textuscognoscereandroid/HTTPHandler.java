package com.asaleksandrov.textuscognoscereandroid;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HTTPHandler {

    private final String serverUrl;
    private final OkHttpClient client;

    public HTTPHandler(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = new OkHttpClient();
    }

    public void httpProcess(String fileName, final Context context, final ProgressBar progressBar) {
        // Start the progressBar
        ((Activity)context).runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

        // Create request
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", fileName,
                        RequestBody.create(new File(context.getExternalFilesDir(null), fileName), MediaType.parse("image/png")))
                .addFormDataPart("language", Config.LANGUAGE)
                .build();
        Request request = new Request.Builder()
                .url(serverUrl + "/process-image") // append the endpoint to your serverUrl
                .post(requestBody)
                .build();

        // Send request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    // Here you get the response from the server
                    String jsonResponse = Objects.requireNonNull(response.body()).string();

                    // Parse the JSON response and extract 'text'
                    String result = getString(jsonResponse);

                    // Stop the progressBar
                    ((Activity)context).runOnUiThread(() -> progressBar.setVisibility(View.GONE));

                    // Вызов функции startTextDisplayActivity
                    IntentHandler.startTextDisplayActivity(context, result);
                }
            }
        });
    }

    @NonNull
    private static String getString(String jsonResponse) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonResponse);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String result;
        try {
            result = jsonObject.getString("text");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
