package com.artem.topdown;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BackgroundActor extends ShapeActor {
    private static final int NUM_POINTS = 200;

    private final float[] mX;
    private final float[] mY;
    private final float[] mA;
    private final float[] mR;

    public BackgroundActor(float size) {

        mX = new float[NUM_POINTS];
        mY = new float[NUM_POINTS];
        mA = new float[NUM_POINTS];
        mR = new float[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            mX[i] = (float) Math.random() * size;
            mY[i] = (float) Math.random() * size;
            mA[i] = (float) Math.random() * 0.5f + 0.2f;
            mR[i] = (float) Math.random() * 1f + 0.5f;
        }
    }

    @Override
    protected void onDraw(ShapeRenderer renderer, float parentAlpha) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw
        for (int i = 0; i < NUM_POINTS; i++) {
            renderer.setColor(1, 1, 1, mA[i]);
            renderer.circle(mX[i], mY[i], mR[i]);
        }
    }
}
