package com.artem.topdown;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import static com.artem.topdown.TopDownGame.PHYSICS_TO_PIXEL_SCALE;
import static com.artem.topdown.TopDownGame.PIXEL_TO_PHYSICS_SCALE;

public class GravityActor extends PhysicsActor {
    private static final int NUM_OUTER_LINES = 4;
    private static final float OUTER_LINES_SPACING = 20f;
    private static final float ANIMATION_RATE = 0.02f;

    private final boolean mAttract;
    private final float mGravity;
    private final float mOuterLinesSpacing;
    private final float mMaxOuterLineDistance;
    
    private float mAnimationCounter;

    public GravityActor(World world, float x, float y) {
        super(x, y, world,
                generateBodyDef(x, y),
                generateFixtureDef((float) Math.random() * 40f + 10f));
        float radius = getBody().getFixtureList().first().getShape().getRadius() * PHYSICS_TO_PIXEL_SCALE;
        setSize(radius*2, radius*2);
        //setOrigin(radius + getX(), radius + getY());
        mAttract = Math.random() > 0.5f;
        mGravity = 15f * radius * (mAttract ? 0.7f : -1.5f);
        setColor(mAttract ? 1 : 0, 0.7f, mAttract ? 0 : 1, 1f);

        mOuterLinesSpacing = OUTER_LINES_SPACING * radius / 50;
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
        // Create physics body
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(x * PIXEL_TO_PHYSICS_SCALE, y * PIXEL_TO_PHYSICS_SCALE);
        def.fixedRotation = false;
        return def;
    }

    private static FixtureDef generateFixtureDef(float radius) {
        // Create physics collision shape
        CircleShape shape = new CircleShape();
        shape.setRadius(radius * PIXEL_TO_PHYSICS_SCALE);    //.setAsBox(getWidth() / 2 * PIXEL_TO_PHYSICS_SCALE, getHeight() / 2 * PIXEL_TO_PHYSICS_SCALE);
        FixtureDef def = new FixtureDef();
        def.shape = shape;
        def.density = 0f;
        return def;
    }
}
