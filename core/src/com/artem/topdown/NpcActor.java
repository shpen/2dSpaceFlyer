package com.artem.topdown;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class NpcActor extends PhysicsActor {
    private static final float MAX_SPEED_PHYS = 6f;
    private static final float FORWARD_ACCELERATION_PHYS = 20f;
    private static final float ROTATION_FORCE_PHYS = 15f;
    private static final float DENSITY_PHYS = 25f;
    private static final float ANGULAR_DAMP_PHYS = 100f;

    private static final float PLAYER_ANGLE_TOLERANCE_RAD = 0.1f;

    private static final float WIDTH_PX = 7f;
    private static final float HEIGHT_PX = 7f;

    private final PlayerActor mPlayer;

    public NpcActor(World world, PlayerActor player, float x, float y) {
        super(x, y, WIDTH_PX, HEIGHT_PX, world, BodyDef.BodyType.DynamicBody, false, DENSITY_PHYS);
        mPlayer = player;
        getBody().setAngularDamping(ANGULAR_DAMP_PHYS);
        setMaxVelocity(MAX_SPEED_PHYS);
    }

    @Override
    protected boolean onDraw(ShapeRenderer renderer, float parentAlpha) {
        renderer.setColor(Color.RED);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.translate(getX() + getWidth() / 2, getY() + getHeight() / 2, 0);
        renderer.rotate(0, 0, 1, getRotation());
        renderer.rect(-getWidth() / 2, -getHeight() / 2, getWidth(), getHeight());

        return true;
    }

    @Override
    protected void doAct(float delta) {
        Vector2 playerDir = new Vector2(mPlayer.getX() - getX(), mPlayer.getY() - getY());
        float playerAngle = playerDir.angleRad();
        float selfAngle = (getBody().getAngle() + (float) Math.PI / 2) % ((float) Math.PI * 2);
        if (selfAngle > Math.PI) selfAngle -= Math.PI * 2;
        float angularImpulse = 0;
        float angleDelta = getAngleDelta(selfAngle, playerAngle);
        //Gdx.app.log("NpcActor", "playerAngle: " + playerAngle + " selfAngle: " + selfAngle + " angleDelta: " + angleDelta);
        if (angleDelta < -PLAYER_ANGLE_TOLERANCE_RAD) {
            angularImpulse = ROTATION_FORCE_PHYS;
        } else if (angleDelta > PLAYER_ANGLE_TOLERANCE_RAD) {
            angularImpulse = -ROTATION_FORCE_PHYS;
        }
        getBody().applyAngularImpulse(angularImpulse, true);

        float angle = getBody().getAngle();
        Vector2 accelVel = new Vector2(0, 1).rotateRad(angle).scl(FORWARD_ACCELERATION_PHYS);
        getBody().applyLinearImpulse(accelVel, getBody().getWorldCenter(), true);
    }

    /**
     * Complex math stolen from StackOverflow to get angle delta in a usable range in radians.
     */
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
