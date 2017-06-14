package com.artem.topdown;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class BackgroundActor extends Actor {
    private static final int NUM_POINTS = 200;

    private static final float ALPHA_MIN = 0.5f;
    private static final float ALPHA_RANGE = 0.5f;

    private static final float RADIUS_MIN_PX = 0.5f;
    private static final float RADIUS_RANGE_PX = 1f;

    private static final Texture TEXTURE = new Texture("square.png");

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
            mA[i] = (float) Math.random() * ALPHA_RANGE + ALPHA_MIN;
            mR[i] = (float) Math.random() * RADIUS_RANGE_PX + RADIUS_MIN_PX;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (int i = 0; i < NUM_POINTS; i++) {
            batch.setColor(1, 1, 1, mA[i]);
            batch.draw(TEXTURE, mX[i], mY[i], mR[i], mR[i]);
        }
    }

    /*@Override
    protected boolean onDraw(ShapeRenderer renderer, float parentAlpha) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw
        for (int i = 0; i < NUM_POINTS; i++) {
            renderer.setColor(1, 1, 1, mA[i]);
            renderer.circle(mX[i], mY[i], mR[i]);
        }

        return false;
    }*/
}
