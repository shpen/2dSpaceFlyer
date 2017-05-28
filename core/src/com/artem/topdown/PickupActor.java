package com.artem.topdown;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PickupActor extends ShapeActor {
    private static final Color COLOR = new Color(0.7f, 1f, 0.2f, 1f);

    private static final float OUTER_RADIUS = 20f;
    private static final float INNER_RADIUS = 4f;
    private static final int NUM_LAYERS = 2;

    private static final float ANIMATION_RATE = 0.003f;

    private final float mX;
    private final float mY;

    private float mAnimationCounter;

    public PickupActor(float x, float y) {
        mX = x;
        mY = y;
    }

    @Override
    protected void onDraw(ShapeRenderer renderer, float parentAlpha) {
        mAnimationCounter += ANIMATION_RATE;
        if (mAnimationCounter > 1) mAnimationCounter = 0;

        for (int i = 0; i < NUM_LAYERS; i++) {
            renderer.identity();
            renderer.translate(mX - OUTER_RADIUS / 2, mY - OUTER_RADIUS / 2, 0);
            renderer.rotate(0, 0, 1, mAnimationCounter * (float) Math.pow(-1, i) * 360);

            renderer.begin(ShapeRenderer.ShapeType.Line);

            Color color = COLOR.cpy();
            color.a = (i + 1) / (float) (NUM_LAYERS + 1);
            renderer.setColor(color);
            renderer.circle(0, 0, OUTER_RADIUS - (OUTER_RADIUS - INNER_RADIUS) * i / (float) NUM_LAYERS, 6);

            renderer.end();
        }

        renderer.identity();
        renderer.translate(mX - OUTER_RADIUS / 2, mY - OUTER_RADIUS / 2, 0);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(COLOR);
        renderer.circle(0, 0, INNER_RADIUS, 6);
        renderer.end();
    }
}
