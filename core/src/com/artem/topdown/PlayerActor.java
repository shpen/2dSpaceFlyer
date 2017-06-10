package com.artem.topdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class PlayerActor extends PhysicsActor {

    private static final float ROTATION_SPEED = 60f;
    private static final float FORWARD_ACCEL = 30.0f;
    private static final float MOVE_SPEED = 10.0f;

    private final float[] mVerts;

    private float mAnimationCounter;

    public PlayerActor(World world, float x, float y) {
        super(x, y, 10, 10, world, BodyDef.BodyType.DynamicBody, false, 100f);
        setMaxVelocity(MOVE_SPEED);
        getBody().setAngularDamping(100);
        
        mVerts = new float[] {
                0f, 0f,
                0.5f, 1f,
                1f, 0f,
                0.5f, 0.3f,
                0f, 0f
        };
        for (int i = 0; i < mVerts.length; i++) {
            mVerts[i] -= 0.5f; // Center values on origin
            mVerts[i] *= 10f; // Scale up to fit proper game size
        }
    }

    @Override
    protected boolean onDraw(ShapeRenderer renderer, float parentAlpha) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.WHITE);
        renderer.translate(getX() + getWidth() / 2, getY() + getHeight() / 2, 0);
        renderer.rotate(0, 0, 1, (float) Math.toDegrees(getBody().getAngle()));
        renderer.triangle(mVerts[0], mVerts[1], mVerts[2], mVerts[3], mVerts[6], mVerts[7]);
        renderer.triangle(mVerts[2], mVerts[3], mVerts[4], mVerts[5], mVerts[6], mVerts[7]);

        return true;
    }

    @Override
    protected void doAct(float delta) {
        float angularVelocity = 0;

        boolean touchLeft = false;
        boolean touchRight = false;
        for (int i = 0; i < 5; i++) {
            if (Gdx.input.isTouched(i)) {
                if (Gdx.input.getX(i) < Gdx.graphics.getWidth() / 2) {
                    touchLeft = true;
                } else {
                    touchRight = true;
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT) || touchLeft) {
            angularVelocity += ROTATION_SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT) || touchRight) {
            angularVelocity -= ROTATION_SPEED;
        }
        //getBody().setAngularVelocity(angularVelocity);
        getBody().applyAngularImpulse(angularVelocity, true);

        float angle = getBody().getAngle();
        Vector2 accelVel = new Vector2(0, 1).rotateRad(angle).scl(FORWARD_ACCEL);
        getBody().applyLinearImpulse(accelVel, getBody().getWorldCenter(), true);

        mAnimationCounter += 0.1f;
        if (mAnimationCounter > 1) {
            mAnimationCounter = 0;
            Vector2 pos = new Vector2(getX() + getWidth() / 2, getY() + getHeight() / 2);
            //pos.add(new Vector2(0, -5).rotate(getRotation()));
            getStage().addActor(new PlayerJetActor(pos.x, pos.y, getRotation()));
        }
    }
}
