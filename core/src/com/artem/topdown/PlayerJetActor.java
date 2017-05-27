package com.artem.topdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PlayerJetActor extends Actor{
    private static final float INITIAL_DISTANCE = 5f;
    private static final float MAX_DISTANCE = 5f;

    private static final float WIDTH = 5f;
    private static final float HEIGHT = 0.5f;

    private final float mX;
    private final float mY;
    private final float mAngle;

    private final ShapeRenderer mRenderer;

    private float mDistance;

    public PlayerJetActor(float x, float y, float angle) {
        mX = x;
        mY = y;
        mAngle = angle;
        mRenderer = new ShapeRenderer();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Setup
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        mRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        mRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw
        float inversePercent = 1 - mDistance / MAX_DISTANCE;
        mRenderer.setColor(new Color(1, 1, 1, inversePercent));
        mRenderer.identity();
        mRenderer.translate(mX, mY, 0);
        mRenderer.rotate(0, 0, 1, mAngle);
        mRenderer.translate(0, -mDistance - INITIAL_DISTANCE, 0);
        float width = WIDTH * inversePercent;
        mRenderer.rect(-width / 2, -HEIGHT / 2, width * inversePercent, HEIGHT * inversePercent);
        //mRenderer.line(-WIDTH, -HEIGHT / 2, 0, INITIAL_DISTANCE - HEIGHT / 2);
        //mRenderer.line(0, INITIAL_DISTANCE - HEIGHT / 2, WIDTH, -HEIGHT / 2);

        // Teardown
        mRenderer.end();
        batch.begin();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        mDistance += 0.15f;
        if (mDistance > MAX_DISTANCE) remove();
    }
}
