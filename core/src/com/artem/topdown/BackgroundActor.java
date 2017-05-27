package com.artem.topdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Artem on 5/24/2017.
 */

public class BackgroundActor extends Actor {
    private static final int NUM_POINTS = 200;

    private final ShapeRenderer mRenderer;

    private final float[] mX;
    private final float[] mY;
    private final float[] mA;
    private final float[] mR;

    public BackgroundActor(float size) {
        mRenderer = TopDownGame.mShapeRenderer;//new ShapeRenderer();

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
    public void draw(Batch batch, float parentAlpha) {
        // Setup
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        mRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        mRenderer.identity();
        mRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw
        for (int i = 0; i < NUM_POINTS; i++) {
            mRenderer.setColor(1, 1, 1, mA[i]);
            mRenderer.circle(mX[i], mY[i], mR[i]);
        }

        // Tear down
        mRenderer.end();
        batch.begin();
    }
}
