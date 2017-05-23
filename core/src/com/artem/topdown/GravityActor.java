package com.artem.topdown;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import static com.artem.topdown.TopDownGame.PIXEL_TO_PHYSICS_SCALE;

/**
 * Created by Artem on 5/19/2017.
 */

public class GravityActor extends PhysicsActor {
    private final float mGravity;

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

    public GravityActor(World world, float x, float y) {
        super(new Texture("circle.png"), x, y, world,
                generateBodyDef(x, y),
                generateFixtureDef(new Texture("circle.png").getWidth() / 2));
        boolean attract = Math.random() > 0.5f;
        mGravity = 1.5f * getWidth() * (attract ? 1 : -1);
        setColor(attract ? 1 : 0, 0, attract ? 0 : 1, 1);
    }

    public float getGravity() {
        return mGravity;
    }
}
