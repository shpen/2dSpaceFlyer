package com.artem.topdown;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Artem on 5/19/2017.
 */

public class GravityActor extends PhysicsActor {
    private final float mGravity;

    public GravityActor(World world, float x, float y) {
        super(new Texture("box.png"), x, y, world, BodyDef.BodyType.StaticBody, 0f);
        mGravity = -10f;
    }

    public float getGravity() {
        return mGravity;
    }
}
