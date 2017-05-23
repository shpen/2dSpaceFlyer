package com.artem.topdown;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Artem on 5/12/2017.
 */

public class NpcActor extends PhysicsActor {
    private static final float MOVE_SPEED = 1.5f;
    private static final float FORWARD_ACCEL = 10f;

    private final PlayerActor mPlayer;

    public NpcActor(World world, PlayerActor player, float x, float y) {
        super(new Texture("npc.png"), x, y, world, BodyDef.BodyType.DynamicBody, 25f);
        mPlayer = player;
        setMaxVelocity(MOVE_SPEED);
    }

    @Override
    protected void doAct(float delta) {
        float angle = new Vector2(mPlayer.getX() - getX(), mPlayer.getY() - getY()).angle();
        Vector2 accelVel = new Vector2(1, 0).rotate(angle).scl(FORWARD_ACCEL);
        getBody().applyLinearImpulse(accelVel, getBody().getWorldCenter(), true);
    }
}
