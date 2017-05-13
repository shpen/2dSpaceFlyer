package com.artem.topdown;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Artem on 5/12/2017.
 */

public class NpcActor extends PhysicsActor {
    private static final float MOVE_SPEED = 3.0f;

    private final PlayerActor mPlayer;

    public NpcActor(World world, PlayerActor player, float x, float y) {
        super(new Texture("npc.png"), x, y, world, BodyDef.BodyType.DynamicBody);
        mPlayer = player;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        Vector2 playerDir = new Vector2(mPlayer.getX() - getX(), mPlayer.getY() - getY());
        playerDir.nor().scl(MOVE_SPEED);
        setVelocity(playerDir);
    }
}
