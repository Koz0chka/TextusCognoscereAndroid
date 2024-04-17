package com.asaleksandrov.textuscognoscereandroid;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnimationUtils {

    public interface Callback {
        String onProcessImage();
        void onPostExecute(String result);
    }

    public static void showProgressBar(final ProgressBar progressBar) {
        if (progressBar != null) {
            Log.d("AnimationUtils", "Showing ProgressBar");
            new Handler(Looper.getMainLooper()).post(() -> progressBar.setVisibility(View.VISIBLE));
        }
    }

    public static void hideProgressBar(final ProgressBar progressBar) {
        if (progressBar != null) {
            Log.d("AnimationUtils", "Hiding ProgressBar");
            new Handler(Looper.getMainLooper()).post(() -> progressBar.setVisibility(View.GONE));
        }
    }

    public static void processImageWithLoading(final ProgressBar progressBar, final Callback callback) {
        // Показать ProgressBar
        showProgressBar(progressBar);

        // Создаем ExecutorService
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Запускаем задачу в фоне
        executorService.execute(() -> {
            final String result = callback.onProcessImage();

            // Обновляем UI в основном потоке
            new Handler(Looper.getMainLooper()).post(() -> {
                // Скрыть ProgressBar
                hideProgressBar(progressBar);

                // Вызов метода, который будет выполняться после завершения обработки
                callback.onPostExecute(result);
            });
        });
    }
}
