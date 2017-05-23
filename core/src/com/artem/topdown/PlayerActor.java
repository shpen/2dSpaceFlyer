package com.artem.topdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class PlayerActor extends PhysicsActor {

    private static final float ROTATION_SPEED = (float) Math.PI;
    private static final float FORWARD_ACCEL = 10.0f;
    private static final float MOVE_SPEED = 4.0f;

    public PlayerActor(World world, float x, float y) {
        super(new Texture("player.png"), x, y, world, BodyDef.BodyType.DynamicBody, 100f);
        setMaxVelocity(MOVE_SPEED);
        getBody().setAngularDamping(100);
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
