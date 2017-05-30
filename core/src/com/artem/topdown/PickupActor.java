package com.artem.topdown;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class PickupActor extends PhysicsActor {
    private static final Color COLOR = new Color(0.7f, 1f, 0.2f, 1f);

    private static final float OUTER_RADIUS = 20f;
    private static final float INNER_RADIUS = 4f;
    private static final int NUM_LAYERS = 2;
    private static final int NUM_POLYGON_SIDES = 6;

    private static final float FULL_CIRCLE_DEG = 360f;

    private static final float ANIMATION_RATE = 0.003f;

    private float mAnimationCounter;

    public PickupActor(World world, float x, float y) {
        super(x, y, INNER_RADIUS * 2, INNER_RADIUS * 2, world, BodyDef.BodyType.StaticBody, true, 0f);
    }

    @Override
    protected void onDraw(ShapeRenderer renderer, float parentAlpha) {
        mAnimationCounter += ANIMATION_RATE;
        if (mAnimationCounter > 1) mAnimationCounter = 0;

        for (int i = 0; i < NUM_LAYERS; i++) {
            renderer.identity();
            renderer.translate(getX() + getWidth() / 2, getY() + getHeight() / 2, 0);
            renderer.rotate(0, 0, 1, mAnimationCounter * (float) Math.pow(-1, i) * FULL_CIRCLE_DEG);

            renderer.begin(ShapeRenderer.ShapeType.Line);

            Color color = COLOR.cpy();
            color.a = (i + 1) / (float) (NUM_LAYERS + 1);
            renderer.setColor(color);
            renderer.circle(0, 0, OUTER_RADIUS - (OUTER_RADIUS - INNER_RADIUS) * i / (float) NUM_LAYERS, NUM_POLYGON_SIDES);

            renderer.end();
        }

        renderer.identity();
        renderer.translate(getX() + getWidth() / 2, getY() + getHeight() / 2, 0);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(COLOR);
        renderer.circle(0, 0, INNER_RADIUS, NUM_POLYGON_SIDES);
        renderer.end();
    }
}
