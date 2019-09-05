package com.example.firebaseml.Helper;

import android.graphics.Canvas;
import android.graphics.Color;

public class TextGraphic extends GraphicOverlay.Graphic {

    private static final int TEXT_COLOR = Color.BLUE;
    private static final float TEXT_SIZE = 54.0f;
    private static final float STROCK_WIDTH = 4.0f;

    public TextGraphic(GraphicOverlay overlay) {
        super(overlay);
    }

    @Override
    public void draw(Canvas canvas) {

    }
}
