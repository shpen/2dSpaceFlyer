package com.artem.topdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import static com.artem.topdown.TopDownGame.PHYSICS_TO_PIXEL_SCALE;
import static com.artem.topdown.TopDownGame.PIXEL_TO_PHYSICS_SCALE;

/**
 * Created by Artem on 5/19/2017.
 */

public class GravityActor extends PhysicsActor {
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

    private final float mGravity;
    private final ShapeRenderer mRenderer;

    public GravityActor(World world, float x, float y) {
        super(new Texture("circle.png"), x, y, world,
                generateBodyDef(x, y),
                generateFixtureDef((float) Math.random() * 40f + 10f));
        float radius = getBody().getFixtureList().first().getShape().getRadius() * PHYSICS_TO_PIXEL_SCALE;
        setSize(radius*2, radius*2);
        //setOrigin(radius + getX(), radius + getY());
        mAttract = Math.random() > 0.5f;
        mGravity = 10f * radius * (mAttract ? 0.7f : -1.5f);
        setColor(mAttract ? 1 : 0, 0.7f, mAttract ? 0 : 1, 1f);

        mOuterLinesSpacing = OUTER_LINES_SPACING * radius / 50;
        mMaxOuterLineDistance = mOuterLinesSpacing * NUM_OUTER_LINES;
        mRenderer = new ShapeRenderer();
    }

    private static final int NUM_OUTER_LINES = 4;
    private static final float OUTER_LINES_SPACING = 20f;
    private static final float ANIMATION_RATE = 0.02f;

    private final boolean mAttract;
    private final float mOuterLinesSpacing;
    private final float mMaxOuterLineDistance;

    private float mAnimationCounter;

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //super.draw(batch, parentAlpha);

        // Setup
        float centerX = getX() + getWidth() / 2;
        float centerY = getY() + getHeight() / 2;

        mAnimationCounter += ANIMATION_RATE;
        if (mAnimationCounter > 1f) mAnimationCounter = 0;

        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        mRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        // Draw solid circle
        mRenderer.setColor(getColor());
        mRenderer.begin(ShapeRenderer.ShapeType.Filled);
        mRenderer.circle(centerX, centerY, getWidth() / 2);
        mRenderer.end();

        // Draw outer animating lines
        for (int i = 0; i < NUM_OUTER_LINES; i++) {
            float distance = mOuterLinesSpacing * (i + mAnimationCounter);
            if (mAttract) distance = mMaxOuterLineDistance - distance;
            mRenderer.setColor(getColor().cpy().add(0, 0, 0, -distance / mMaxOuterLineDistance));
            mRenderer.begin(ShapeRenderer.ShapeType.Line);
            mRenderer.circle(centerX, centerY, getWidth() / 2 + distance);
            mRenderer.end();
        }

        // Tear down
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
    }

    public float getGravity() {
        return mGravity;
    }
}
