package com.artem.topdown;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class NpcActor extends PhysicsActor {
    private static final float MOVE_SPEED = 6f;
    private static final float FORWARD_ACCELERATION = 20f;
    private static final float ROTATION_SPEED = 15f;

    private static final float PLAYER_DISTANCE_LIMIT = 2000f;
    private static final float PLAYER_ANGLE_TOLERANCE = 0.1f;

    private final PlayerActor mPlayer;

    public NpcActor(World world, PlayerActor player, float x, float y) {
        super(x, y, 7, 7, world, BodyDef.BodyType.DynamicBody, 25f);
        mPlayer = player;
        getBody().setAngularDamping(100f);
        setMaxVelocity(MOVE_SPEED);
    }

    @Override
    protected void onDraw(ShapeRenderer renderer, float parentAlpha) {
        renderer.setColor(Color.RED);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.translate(getX() + getWidth() / 2, getY() + getHeight() / 2, 0);
        renderer.rotate(0, 0, 1, getRotation());
        renderer.rect(-getWidth() / 2, -getHeight() / 2, getWidth(), getHeight());
    }

    @Override
    protected void doAct(float delta) {
        Vector2 playerDir = new Vector2(mPlayer.getX() - getX(), mPlayer.getY() - getY());
        float playerAngle = playerDir.angleRad();
        float selfAngle = (getBody().getAngle() + (float) Math.PI / 2) % ((float) Math.PI * 2);
        if (selfAngle > Math.PI) selfAngle -= Math.PI * 2;
        float angularImpulse = 0;
        float acceleration = FORWARD_ACCELERATION;
        if (playerDir.len() < PLAYER_DISTANCE_LIMIT) {
            float angleDelta = getAngleDelta(selfAngle, playerAngle);
            //Gdx.app.log("NpcActor", "playerAngle: " + playerAngle + " selfAngle: " + selfAngle + " angleDelta: " + angleDelta);
            if (angleDelta < - PLAYER_ANGLE_TOLERANCE) {
                angularImpulse = ROTATION_SPEED;
            } else if (angleDelta > PLAYER_ANGLE_TOLERANCE) {
                angularImpulse = -ROTATION_SPEED;
            }

            acceleration *= 1;// - (Math.abs(angleDelta) / Math.PI);
        } else {

        }
        getBody().applyAngularImpulse(angularImpulse, true);


        /*getBody().applyLinearImpulse(Vector2.X.rotateRad(selfAngle).scl(acceleration),
                getBody().getWorldCenter(), true);*/

        float angle = getBody().getAngle();
        Vector2 accelVel = new Vector2(0, 1).rotateRad(angle).scl(acceleration);
        getBody().applyLinearImpulse(accelVel, getBody().getWorldCenter(), true);
    }

    private float getAngleDelta(float current, float target) {
        float diff = Math.abs(current - target) % ((float) Math.PI * 2);
        float delta = diff > ((float) Math.PI * 1) ? ((float) Math.PI * 2) - diff : diff;

        //calculate sign
        float sign = (current - target >= 0 && current - target <= ((float) Math.PI * 1)) ||
                (current - target <= -((float) Math.PI * 1) && current - target >= -((float) Math.PI * 2)) ? 1 : -1;
        delta *= sign;
        return delta;
    }
}
