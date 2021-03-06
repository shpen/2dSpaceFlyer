package com.artem.topdown;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import static com.artem.topdown.TopDownGame.PHYSICS_TO_PIXEL_SCALE;
import static com.artem.topdown.TopDownGame.PIXEL_TO_PHYSICS_SCALE;

public abstract class PhysicsActor extends ShapeActor {

    private final World mWorld;
    private final Body mBody;
    private final Fixture mFixture;

    private float mMaxVelocity = -1;

    public PhysicsActor(World world, BodyDef bodyDef, FixtureDef fixtureDef) {
        setPosition(bodyDef.position.x, bodyDef.position.y);
        float size = fixtureDef.shape.getRadius() * 2 * PHYSICS_TO_PIXEL_SCALE;
        setSize(size, size);

        mWorld = world;
        mBody = mWorld.createBody(bodyDef);
        mBody.setUserData(this);

        mFixture = mBody.createFixture(fixtureDef);
        mFixture.setUserData(this);
        fixtureDef.shape.dispose();
    }

    public PhysicsActor(float x, float y, float width, float height, World world, BodyDef.BodyType bodyType, boolean sensor, float density) {
        setPosition(x * PIXEL_TO_PHYSICS_SCALE, y * PIXEL_TO_PHYSICS_SCALE);
        setSize(width, height);

        mWorld = world;

        // Create physics body
        BodyDef def = new BodyDef();
        def.type = bodyType;
        def.position.set(x * PIXEL_TO_PHYSICS_SCALE, y * PIXEL_TO_PHYSICS_SCALE);
        def.fixedRotation = false;
        mBody = mWorld.createBody(def);
        mBody.setUserData(this);

        // Create physics collision shape
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(getWidth() / 2 * PIXEL_TO_PHYSICS_SCALE, getHeight() / 2 * PIXEL_TO_PHYSICS_SCALE);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = sensor;
        fixtureDef.shape = boxShape;
        fixtureDef.density = density;
        mFixture = mBody.createFixture(fixtureDef);
        mFixture.setFriction(0);
        mFixture.setUserData(this);
        boxShape.dispose();
    }

    protected void setMaxVelocity(float velocity) {
        mMaxVelocity = velocity;
    }

    @Override
    public final void act(float delta) {
        super.act(delta);

        // Let sub-classes try to act, then we can modify behavior as needed
        doAct(delta);

        // Apply local gravity
        if (mBody.getType() == BodyDef.BodyType.DynamicBody) {
            for (GravityActor actor : TopDownGame.mGravities) {
                Vector2 direction = actor.getBody().getPosition().sub(mBody.getPosition());
                float distanceFactor = (float) Math.pow(direction.len2(), 0.9);
                Vector2 gravity = direction.nor().scl(actor.getGravity()).scl(1 / distanceFactor);
                mBody.applyLinearImpulse(gravity, mBody.getWorldCenter(), true);
            }
        }

        if (mMaxVelocity > 0) {
            mBody.setLinearVelocity(mBody.getLinearVelocity().clamp(0, mMaxVelocity));
        }

        // Update visual model
        setPosition(mBody.getPosition().x, mBody.getPosition().y);
        setRotation((float) Math.toDegrees(mBody.getAngle()));
    }

    protected void doAct(float delta) {}

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x * PHYSICS_TO_PIXEL_SCALE - getWidth() / 2, y * PHYSICS_TO_PIXEL_SCALE - getHeight() / 2);
    }

    @Override
    public boolean remove() {
        mWorld.destroyBody(mBody);
        return super.remove();
    }

    protected Body getBody() {
        return mBody;
    }
}
