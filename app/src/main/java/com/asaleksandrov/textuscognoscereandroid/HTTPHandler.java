package com.asaleksandrov.textuscognoscereandroid;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import okhttp3.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class HTTPHandler {

    private final String serverUrl;
    private final OkHttpClient client;

    public HTTPHandler(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = new OkHttpClient();
    }

    public void httpProcess(String imagePath, final Context context, final ProgressBar progressBar) {
        // Create request
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "image.png",
                        RequestBody.create(new File(imagePath), MediaType.parse("image/png")))
                .build();
        Request request = new Request.Builder()
                .url(serverUrl)
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
                    String result = Objects.requireNonNull(response.body()).string();

                    // Stop the progressBar
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }
}
