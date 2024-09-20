package com.axel.quizup.ProgressUi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.axel.quizup.R;

public class CircularProgressBar extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint mainTextPaint;  // Paint for the main progress text
    private Paint fractionTextPaint;  // Paint for the fraction of the total
    private RectF oval;
    private float progress = 0; // Progress value (0 to 100)

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // Background circle paint
        backgroundPaint = new Paint();
        backgroundPaint.setColor(ContextCompat.getColor(context, R.color.lightGray)); // Set from colors.xml
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(25f);
        backgroundPaint.setAntiAlias(true);

        // Progress paint
        progressPaint = new Paint();
        progressPaint.setColor(ContextCompat.getColor(context, R.color.orange)); // Set from colors.xml
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(25f);
        progressPaint.setAntiAlias(true);

        // Main text paint (e.g., "50")
        mainTextPaint = new Paint();
        mainTextPaint.setColor(ContextCompat.getColor(context, R.color.orange)); // Set from colors.xml
        mainTextPaint.setTextSize(50f);
        mainTextPaint.setTextAlign(Paint.Align.CENTER);
        mainTextPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD); // Set text to bold
        mainTextPaint.setAntiAlias(true);

        // Fraction text paint (e.g., "/100")
        fractionTextPaint = new Paint();
        fractionTextPaint.setColor(Color.BLACK); // Set from colors.xml if needed
        fractionTextPaint.setTextSize(30f); // Smaller text size for the fraction
        fractionTextPaint.setTextAlign(Paint.Align.CENTER);
        fractionTextPaint.setAntiAlias(true);

        // For drawing the full circle
        oval = new RectF();
    }

    @Override

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float radius = Math.min(width, height) / 2 - 20;

        // Draw the background circle
        canvas.drawCircle(width / 2, height / 2, radius, backgroundPaint);

        // Set the bounds for the progress arc
        oval.set(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius);

        // Calculate the sweep angle for progress.
        float sweepAngle = 360 * progress / 100;  // Full circle progress

        // Draw the progress arc starting from 270 degrees (top of the circle)
        canvas.drawArc(oval, 270, sweepAngle, false, progressPaint); // Starting at the top (270 degrees)

        // Draw the main progress text in the center of the circle
        String mainText = String.valueOf((int) progress);

        // Calculate the coordinates to center the text
        float textX = width / 2;
        float textY = height / 2 - ((mainTextPaint.descent() + mainTextPaint.ascent()) / 2); // Center vertically

        // Draw the main progress text
        canvas.drawText(mainText, textX, textY, mainTextPaint);
    }


    // Method to update progress
    public void setProgress(float progress) {
        this.progress = progress;
        invalidate(); // Redraw the view
    }

    // Method to dynamically change the progress color
    public void setProgressColor(int colorResId) {
        progressPaint.setColor(ContextCompat.getColor(getContext(), colorResId));
        invalidate(); // Redraw the view
    }
}
