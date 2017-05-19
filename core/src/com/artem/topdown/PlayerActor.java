package com.artem.topdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class PlayerActor extends PhysicsActor {

    private static final float ROTATION_SPEED = (float) Math.PI;
    private static final float FORWARD_ACCEL = 0.2f;
    private static final float MOVE_SPEED = 5.0f;

    public PlayerActor(World world, float x, float y) {
        super(new Texture("player.png"), x, y, world, BodyDef.BodyType.DynamicBody, 100f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        float angularVelocity = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            angularVelocity += ROTATION_SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            angularVelocity -= ROTATION_SPEED;
        }
        getBody().setAngularVelocity(angularVelocity);

        float angle = getBody().getAngle();
        Vector2 curVel = getBody().getLinearVelocity();
        Vector2 accelVel = new Vector2(0, 1).rotateRad(angle).scl(FORWARD_ACCEL);
        Vector2 totalVel = curVel.add(accelVel);
        Vector2 cappedVel = totalVel.clamp(0, MOVE_SPEED);

        // Update physics model
        setVelocity(cappedVel);
        //setVelocity(accelVel.nor().scl(MOVE_SPEED));

        // Update visual model
        setRotation((float) Math.toDegrees(angle));
    }
}
