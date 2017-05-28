package com.artem.topdown;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class JetActor extends ShapeActor {
    private static final float INITIAL_DISTANCE = 5f;
    private static final float MAX_DISTANCE = 5f;

    private static final float WIDTH = 8f;
    private static final float HEIGHT = 0.5f;

    private final float mX;
    private final float mY;
    private final float mAngle;
    private final Color mColor;

    private float mDistance;

    public JetActor(float x, float y, float angle, Color color) {
        mX = x;
        mY = y;
        mAngle = angle;
        mColor = color;
    }

    @Override
    protected void onDraw(ShapeRenderer renderer, float parentAlpha) {
        float inversePercent = 1 - mDistance / MAX_DISTANCE;
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        Color color = mColor.cpy();
        color.a = inversePercent;
        renderer.setColor(color);
        renderer.identity();
        renderer.translate(mX, mY, 0);
        renderer.rotate(0, 0, 1, mAngle);
        renderer.translate(0, -mDistance - INITIAL_DISTANCE, 0);
        float width = WIDTH * inversePercent;
        renderer.rect(-width / 2, -HEIGHT / 2, width * inversePercent, HEIGHT * inversePercent);
        //renderer.line(-WIDTH, -HEIGHT / 2, 0, INITIAL_DISTANCE - HEIGHT / 2);
        //renderer.line(0, INITIAL_DISTANCE - HEIGHT / 2, WIDTH, -HEIGHT / 2);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        mDistance += 0.15f;
        if (mDistance > MAX_DISTANCE) remove();
    }
}
