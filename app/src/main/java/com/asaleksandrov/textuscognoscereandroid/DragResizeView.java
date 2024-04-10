package com.asaleksandrov.textuscognoscereandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class DragResizeView extends View {
    private RectF rect;
    private Paint paint;
    private boolean dragging = false;
    private boolean scaling = false;
    private float lastX;
    private float lastY;

    public DragResizeView(Context context) {
        super(context);
        init(context);
    }

    public DragResizeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // Get screen dimensions
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidth = displayMetrics.widthPixels;
        float screenHeight = displayMetrics.heightPixels;

        // Initialize rectangle in the center of the screen
        float left = (screenWidth - 200) / 2;
        float top = (screenHeight - 100) / 2 - 200;
        rect = new RectF(left - 100, top - 100, left + 300, top + 100);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10); // Set the border width to 10 pixels
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Paint the entire canvas with 50% black
        Paint dimPaint = new Paint();
        dimPaint.setColor(Color.argb(128, 0, 0, 0)); // 50% black
        canvas.drawRect(0, 0, getWidth(), getHeight(), dimPaint);

        // Draw dimming rectangles around the frame
        // Top rectangle
        canvas.drawRect(0, 0, getWidth(), rect.top, dimPaint);
        // Bottom rectangle
        canvas.drawRect(0, rect.bottom, getWidth(), getHeight(), dimPaint);
        // Left rectangle
        canvas.drawRect(0, rect.top, rect.left, rect.bottom, dimPaint);
        // Right rectangle
        canvas.drawRect(rect.right, rect.top, getWidth(), rect.bottom, dimPaint);

        // Draw corners of the frame
        float cornerLength = 50; // length of the corners

        // top left corner
        canvas.drawLine(rect.left, rect.top, rect.left + cornerLength, rect.top, paint);
        canvas.drawLine(rect.left, rect.top, rect.left, rect.top + cornerLength, paint);

        // top right corner
        canvas.drawLine(rect.right, rect.top, rect.right - cornerLength, rect.top, paint);
        canvas.drawLine(rect.right, rect.top, rect.right, rect.top + cornerLength, paint);

        // bottom left corner
        canvas.drawLine(rect.left, rect.bottom, rect.left + cornerLength, rect.bottom, paint);
        canvas.drawLine(rect.left, rect.bottom, rect.left, rect.bottom - cornerLength, paint);

        // bottom right corner
        canvas.drawLine(rect.right, rect.bottom, rect.right - cornerLength, rect.bottom, paint);
        canvas.drawLine(rect.right, rect.bottom, rect.right, rect.bottom - cornerLength, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();

                if (rect.contains(lastX, lastY)) {
                    dragging = true;

                    // Check each corner for resize action
                    if ((Math.abs(rect.right - lastX) < 40 && Math.abs(rect.bottom - lastY) < 40) ||
                            (Math.abs(rect.left - lastX) < 40 && Math.abs(rect.bottom - lastY) < 40) ||
                            (Math.abs(rect.right - lastX) < 40 && Math.abs(rect.top - lastY) < 40) ||
                            (Math.abs(rect.left - lastX) < 40 && Math.abs(rect.top - lastY) < 40)) {
                        scaling = true;
                    }

                    return true;
                }

                break;

            case MotionEvent.ACTION_MOVE:
                if (dragging) {
                    float dx = event.getX() - lastX;
                    float dy = event.getY() - lastY;

                    if (scaling) {
                        // Modify rect for resize action
                        if (event.getX() > lastX) {
                            rect.right += 5;
                            rect.left -= 5;
                        } else if (event.getX() < lastX) {
                            rect.right -= 5;
                            rect.left += 5;
                        }

                        if (event.getY() > lastY) {
                            rect.bottom += 5;
                            rect.top -= 5;
                        } else if (event.getY() < lastY) {
                            rect.bottom -= 5;
                            rect.top += 5;
                        }
                    } else {
                        rect.offset(dx, dy);
                    }

                    // Center the rectangle
                    float newX = (getWidth() - rect.width()) / 2;
                    float newY = (getHeight() - rect.height()) / 2 - 200;
                    rect.offsetTo(newX, newY);

                    lastX = event.getX();
                    lastY = event.getY();

                    invalidate();

                    return true;
                }

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                dragging = false;
                scaling = false;

                break;
        }

        return super.onTouchEvent(event);
    }
}