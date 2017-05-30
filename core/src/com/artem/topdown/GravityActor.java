package com.artem.topdown;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import static com.artem.topdown.TopDownGame.PIXEL_TO_PHYSICS_SCALE;

public class GravityActor extends PhysicsActor {
    private static final float SIZE_RANGE_PX = 60f;
    private static final float SIZE_MIN_PX = 20f;

    private static final float BASE_FACTOR = 20f;
    private static final float ATTRACT_FACTOR = 0.9f;
    private static final float REPEL_FACTOR = -1.5f;

    private static final int NUM_OUTER_LINES = 4;
    private static final float OUTER_LINES_SPACING_FACTOR = 0.4f;
    private static final float ANIMATION_RATE = 0.02f;

    private static final Color ATTRACT_COLOR = new Color(1, 0.7f, 0, 1);
    private static final Color REPEL_COLOR = new Color(0, 0.7f, 1, 1);

    private final boolean mAttract;
    private final float mGravity;
    private final float mOuterLinesSpacing;
    private final float mMaxOuterLineDistance;
    
    private float mAnimationCounter;

    public GravityActor(World world, float x, float y) {
        super(world,
                generateBodyDef(x, y),
                generateFixtureDef((float) Math.random() * SIZE_RANGE_PX + SIZE_MIN_PX));

        float radius = getWidth() / 2;
        mAttract = Math.random() > 0.5f;
        mGravity = BASE_FACTOR * radius * (mAttract ? ATTRACT_FACTOR : REPEL_FACTOR);
        setColor(mAttract ? ATTRACT_COLOR : REPEL_COLOR);

        mOuterLinesSpacing = OUTER_LINES_SPACING_FACTOR * radius;
        mMaxOuterLineDistance = mOuterLinesSpacing * NUM_OUTER_LINES;
    }

    @Override
    protected void onDraw(ShapeRenderer renderer, float parentAlpha) {
        float centerX = getX() + getWidth() / 2;
        float centerY = getY() + getHeight() / 2;

        mAnimationCounter += ANIMATION_RATE;
        if (mAnimationCounter > 1f) mAnimationCounter = 0;
        
        // Draw solid circle
        renderer.setColor(getColor());
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.circle(centerX, centerY, getWidth() / 2);
        renderer.end();

        // Draw outer animating lines
        for (int i = 0; i < NUM_OUTER_LINES; i++) {
            float distance = mOuterLinesSpacing * (i + mAnimationCounter);
            if (mAttract) distance = mMaxOuterLineDistance - distance;
            renderer.setColor(getColor().cpy().add(0, 0, 0, -distance / mMaxOuterLineDistance));
            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.circle(centerX, centerY, getWidth() / 2 + distance);
            renderer.end();
        }
    }

    public float getGravity() {
        return mGravity;
    }

    private static BodyDef generateBodyDef(float x, float y) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(x * PIXEL_TO_PHYSICS_SCALE, y * PIXEL_TO_PHYSICS_SCALE);
        def.fixedRotation = false;
        return def;
    }

    private static FixtureDef generateFixtureDef(float radius) {
        CircleShape shape = new CircleShape();
        shape.setRadius(radius * PIXEL_TO_PHYSICS_SCALE);
        FixtureDef def = new FixtureDef();
        def.shape = shape;
        def.isSensor = true;
        def.density = 0f;
        return def;
    }
}
