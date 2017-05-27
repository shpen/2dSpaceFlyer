package com.artem.topdown;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import static com.artem.topdown.TopDownGame.PHYSICS_TO_PIXEL_SCALE;
import static com.artem.topdown.TopDownGame.PIXEL_TO_PHYSICS_SCALE;

public class PhysicsActor extends BasicActor {

    private final World mWorld;
    private final Body mBody;
    private final Fixture mFixture;

    private float mMaxVelocity = -1;

    public PhysicsActor(Texture texture, float x, float y, World world, BodyDef bodyDef, FixtureDef fixtureDef) {
        super(texture);
        setPosition(x, y);

        mWorld = world;
        mBody = mWorld.createBody(bodyDef);
        mBody.setUserData(this);

        mFixture = mBody.createFixture(fixtureDef);
        mFixture.setUserData(this);
        fixtureDef.shape.dispose();
    }

    public PhysicsActor(Texture texture, float x, float y, World world, BodyDef.BodyType bodyType, float density) {
        super(texture);
        setPosition(x, y);

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
        mFixture = mBody.createFixture(boxShape, density);
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
            Array<Body> bodies = new Array<Body>();
            mWorld.getBodies(bodies);
            for (Body body : bodies) {
                if (body.getUserData() instanceof GravityActor) {
                    GravityActor actor = (GravityActor) body.getUserData();

                    Vector2 direction = body.getPosition().sub(mBody.getPosition());
                    float distanceSqr = direction.len2();
                    Vector2 gravity = direction.nor().scl(actor.getGravity()).scl(1 / distanceSqr);
                    mBody.applyLinearImpulse(gravity, mBody.getWorldCenter(), true);
                }
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
        //mWorld.destroyBody(mBody);
        return super.remove();
    }

    protected void setVelocity(Vector2 vec) {
        mBody.setLinearVelocity(vec);
    }

    protected Body getBody() {
        return mBody;
    }

    protected void enableCollision(boolean enable) {
        Filter filter = mFixture.getFilterData();
        filter.maskBits = (short) (enable ? -1 : 0);
        mFixture.setFilterData(filter);
    }
}
