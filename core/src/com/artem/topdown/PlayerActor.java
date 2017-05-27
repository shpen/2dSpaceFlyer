package com.artem.topdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class PlayerActor extends PhysicsActor {

    private static final float ROTATION_SPEED = (float) Math.PI;
    private static final float FORWARD_ACCEL = 20.0f;
    private static final float MOVE_SPEED = 10.0f;

    private final ShapeRenderer mRenderer;
    private final float[] mVerts;

    public PlayerActor(World world, float x, float y) {
        super(new Texture("player.png"), x, y, world, BodyDef.BodyType.DynamicBody, 100f);
        setMaxVelocity(MOVE_SPEED);
        getBody().setAngularDamping(100);

        mRenderer = new ShapeRenderer();
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
    public void draw(Batch batch, float parentAlpha) {
        //super.draw(batch, parentAlpha);

        // Setup
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        mRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        mRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw
        mRenderer.setColor(Color.WHITE);
        mRenderer.identity();
        mRenderer.translate(getX() + getWidth() / 2, getY() + getHeight() / 2, 0);
        mRenderer.rotate(0, 0, 1, (float) Math.toDegrees(getBody().getAngle()));
        mRenderer.triangle(mVerts[0], mVerts[1], mVerts[2], mVerts[3], mVerts[6], mVerts[7]);
        mRenderer.triangle(mVerts[2], mVerts[3], mVerts[4], mVerts[5], mVerts[6], mVerts[7]);

        // Teardown
        mRenderer.end();
        batch.begin();
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
        getBody().applyAngularImpulse(angularVelocity * 10, true);

        float angle = getBody().getAngle();
        Vector2 accelVel = new Vector2(0, 1).rotateRad(angle).scl(FORWARD_ACCEL);
        getBody().applyLinearImpulse(accelVel, getBody().getWorldCenter(), true);
    }
}
