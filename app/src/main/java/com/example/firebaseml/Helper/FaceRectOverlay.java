package com.example.firebaseml.Helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class FaceRectOverlay extends GraphicOverlay.Graphic {

    private int RECT_COLOR = Color.RED;
    private float STROKE_WIDTH = 4.0f;
    private Paint rectPaint;

    private GraphicOverlay graphicOverlay;
    private Rect rect;

    public FaceRectOverlay(GraphicOverlay graphicOverlay, Rect rect) {
        super(graphicOverlay);

        rectPaint = new Paint();
        rectPaint.setColor(RECT_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STROKE_WIDTH);

        this.graphicOverlay = graphicOverlay;
        this.rect = rect;

        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {

        RectF rectF = new RectF(rect);
        rectF.left = translateX(rectF.left);
        rectF.right = translateX(rectF.right);
        rectF.top = translateX(rectF.top);
        rectF.bottom = translateX(rectF.bottom);

        canvas.drawRect(rectF, rectPaint);

    }
}
