package com.artem.topdown;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import static com.artem.topdown.TopDownGame.PHYSICS_TO_PIXEL_SCALE;
import static com.artem.topdown.TopDownGame.PIXEL_TO_PHYSICS_SCALE;

public class PhysicsActor extends BasicActor {

    private final World mWorld;
    private final Body mBody;
    private final Fixture mFixture;

    public PhysicsActor(Texture texture, float x, float y, World world, BodyDef.BodyType bodyType) {
        super(texture);
        setPosition(x, y);

        mWorld = world;

        // Create physics body
        BodyDef def = new BodyDef();
        def.type = bodyType;
        def.position.set(x * PIXEL_TO_PHYSICS_SCALE, y * PIXEL_TO_PHYSICS_SCALE);
        def.fixedRotation = true;
        mBody = mWorld.createBody(def);

        // Create physics collision shape
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(getWidth() / 2 * PIXEL_TO_PHYSICS_SCALE, getHeight() / 2 * PIXEL_TO_PHYSICS_SCALE);
        mFixture = mBody.createFixture(boxShape, bodyType == BodyDef.BodyType.StaticBody ? 0 : 1);
        boxShape.dispose();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        setPosition(mBody.getPosition().x, mBody.getPosition().y);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x * PHYSICS_TO_PIXEL_SCALE - getWidth() / 2, y * PHYSICS_TO_PIXEL_SCALE - getHeight() / 2);
    }

    public void setVelocity(Vector2 vec) {
        mBody.setLinearVelocity(vec);
    }

    public void enableCollision(boolean enable) {
        Filter filter = mFixture.getFilterData();
        filter.maskBits = (short) (enable ? -1 : 0);
        mFixture.setFilterData(filter);
    }
}
