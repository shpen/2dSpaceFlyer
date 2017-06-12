package com.artem.topdown;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.HashSet;

public class TopDownGame extends ApplicationAdapter {
    public static final float PHYSICS_TO_PIXEL_SCALE = 10f;
    public static final float PIXEL_TO_PHYSICS_SCALE = 1 / PHYSICS_TO_PIXEL_SCALE;

    private static final float WORLD_SIZE = 2000f;

    private static final float ASPECT_RATIO = 16f / 9f;
    private static final float VIEWPORT_SIZE = 300f;
    private static final float CAMERA_FOLLOW_SPEED = 0.1f;

    private static final float BASE_GAME_SPEED = 1/60f;
    private static final float GAME_SPEED_ADD = BASE_GAME_SPEED / 10000f;

    private HashSet<PhysicsActor> mRemovePhysicsActors;

    private World mWorld;
    private Grid mGrid;
    private Box2DDebugRenderer mDebugRenderer;

    private Stage mStage;

    private PlayerActor mPlayer;
    private PickupHintActor mPickupHintActor;

    private float mGameSpeed;
    private int mSpawnCounter;

    public static ShapeRenderer mShapeRenderer;

    private HashSet<PickupActor> mPickups;
    private HashSet<GravityActor> mGravities;

    @Override
    public void create() {
        mGameSpeed = BASE_GAME_SPEED;

        mRemovePhysicsActors = new HashSet<PhysicsActor>();
        mPickups = new HashSet<PickupActor>();
        mGravities = new HashSet<GravityActor>();

        Gdx.gl.glClearColor(0, 0, 0, 1);

        Box2D.init();
        mWorld = new World(new Vector2(0, 0), true);
        mDebugRenderer = new Box2DDebugRenderer();
        mWorld.setContactListener(mContactListener);

        // Create grid before any other physics object, so we can detect them properly
        mGrid = new Grid(mWorld, WORLD_SIZE * PIXEL_TO_PHYSICS_SCALE);

        mShapeRenderer = new ShapeRenderer();

        float width = VIEWPORT_SIZE;
        float height = VIEWPORT_SIZE;
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            width *= ASPECT_RATIO;
        } else {
            height *= ASPECT_RATIO;
        }
        mStage = new Stage(new ExtendViewport(width, height));
        Gdx.input.setInputProcessor(mStage);

        mStage.addActor(new BackgroundActor(WORLD_SIZE));

        mPlayer = new PlayerActor(mWorld, WORLD_SIZE / 2, WORLD_SIZE / 2);
        mStage.addActor(mPlayer);
        mStage.getCamera().position.set(mPlayer.getX(), mPlayer.getY(), 0);

        // Create some random background elements
        fillGravityActors();
        fillNpcs();

        mPickupHintActor = new PickupHintActor(mStage.getCamera());
        mStage.addActor(mPickupHintActor);
        fillPickups();
    }

    @Override
    public void resize(int width, int height) {
        mStage.getViewport().update(width, height);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for (PhysicsActor actor : mRemovePhysicsActors) {
            actor.remove();
        }
        mRemovePhysicsActors.clear();

        // Remove actors off map boundaries
        for (Actor actor : mStage.getActors()) {
            if (actor instanceof PlayerActor || actor instanceof NpcActor) {
                if (actor.getX() < 0 || actor.getX() > WORLD_SIZE
                        || actor.getY() < 0 || actor.getY() > WORLD_SIZE) {
                    if (actor instanceof PlayerActor) {
                        create();
                        return;
                    }
                    actor.remove();
                }
            }
        }

        if(mSpawnCounter++ == 100) {
            mSpawnCounter = 0;
            mStage.addActor(new NpcActor(mWorld, mPlayer, (float) Math.random() * WORLD_SIZE, (float) Math.random() * WORLD_SIZE));
        }

        if (mPickups.isEmpty()) fillPickups();

        mStage.act();

        // Follow player
        Vector3 camPos = mStage.getCamera().position;
        camPos.slerp(new Vector3(mPlayer.getX(), mPlayer.getY(), 0), CAMERA_FOLLOW_SPEED);

        // Prevent camera from moving off world
        float halfViewportWidth = mStage.getCamera().viewportWidth / 2;
        float halfViewportHeight = mStage.getCamera().viewportHeight / 2;
        camPos.x = MathUtils.clamp(camPos.x, halfViewportWidth, WORLD_SIZE - halfViewportWidth);
        camPos.y = MathUtils.clamp(camPos.y, halfViewportHeight, WORLD_SIZE - halfViewportHeight);

        mStage.getCamera().update();

        mShapeRenderer.setProjectionMatrix(mStage.getCamera().combined);
        mStage.draw();

        //mDebugRenderer.render(mWorld, mStage.getCamera().combined.scl(PHYSICS_TO_PIXEL_SCALE));
        mWorld.step(mGameSpeed, 6, 2);
        mGameSpeed += GAME_SPEED_ADD;
    }

    @Override
    public void dispose() {
        mStage.dispose();
    }

    private void fillGravityActors() {
        while (mGravities.size() < 40) {
            float x = (float) Math.random() * WORLD_SIZE;
            float y = (float) Math.random() * WORLD_SIZE;

            // We create the actor before distance testing, because it needs to generate a random radius
            GravityActor newActor = new GravityActor(mWorld, x, y);

            if (!isValidSpawn(x, y, newActor.getRadius())) {
                newActor.remove();
                continue;
            }

            mStage.addActor(newActor);
            mGravities.add(newActor);
        }
    }

    private boolean isValidSpawn(float x, float y, float radius) {
        // Don't spawn too close to edge
        float edgeLimit = 100f;
        if (x < edgeLimit || y < edgeLimit || x > WORLD_SIZE - edgeLimit || y > WORLD_SIZE - edgeLimit) return false;

        // Don't spawn too close to player start
        float playerDistanceLimit = 200f;
        if (Math.abs(x - mPlayer.getX()) < playerDistanceLimit || Math.abs(y - mPlayer.getY()) < playerDistanceLimit) return false;

        // Don't spawn too close too other gravity actors
        for (GravityActor actor : mGravities) {
            float dist = (float) Math.sqrt(Math.pow(x - actor.getX(), 2) + Math.pow(y - actor.getY(), 2));
            if (dist < (radius + actor.getRadius()) * 1.4) {
                return false;
            }
        }

        return true;
    }

    private void fillNpcs() {
        for (int i = 0; i < 100; i++) {
            float x;
            float y;
            do {
                x = (float) Math.random() * WORLD_SIZE;
                y = (float) Math.random() * WORLD_SIZE;
            } while (!isValidSpawn(x, y, 20));
            Actor npc = new NpcActor(mWorld, mPlayer, x, y);
            mStage.addActor(npc);
        }
    }

    private void fillPickups() {
        for (int i = 0; i < 5; i++) {
            float x;
            float y;
            do {
                x = (float) Math.random() * WORLD_SIZE;
                y = (float) Math.random() * WORLD_SIZE;
            } while (!isValidSpawn(x, y, 20));

            PickupActor pickup = new PickupActor(mWorld, x, y);
            mStage.addActor(pickup);
            mPickupHintActor.addPickupActor(pickup);
            mPickups.add(pickup);
        }
    }

    private ContactListener mContactListener = new ContactListener() {
        @Override
        public void beginContact(Contact contact) {
            Object a = contact.getFixtureA().getUserData();
            Object b = contact.getFixtureB().getUserData();
            NpcActor npc = null;
            PlayerActor player = null;
            GravityActor gravity = null;
            Grid.Cell gridCell = null;
            Actor other = null;
            PickupActor pickup = null;
            if (a instanceof NpcActor) npc = (NpcActor) a;
            if (b instanceof NpcActor) npc = (NpcActor) b;
            if (a instanceof PlayerActor) player = (PlayerActor) a;
            if (b instanceof PlayerActor) player = (PlayerActor) b;
            if (a instanceof GravityActor) gravity = (GravityActor) a;
            if (b instanceof GravityActor) gravity = (GravityActor) b;
            if (a instanceof Grid.Cell && !(b instanceof Grid.Cell)) {
                gridCell = (Grid.Cell) a;
                other = (Actor) b;
            }
            if (b instanceof Grid.Cell && !(a instanceof Grid.Cell)) {
                gridCell = (Grid.Cell) b;
                other = (Actor) a;
            }
            if (a instanceof PickupActor) pickup = (PickupActor) a;
            if (b instanceof PickupActor) pickup = (PickupActor) b;

            // Player hit something
            if (player != null && (gravity != null || npc != null || pickup != null)) {
                // Yay!
                if (pickup != null) {
                    mRemovePhysicsActors.add(pickup);
                    mPickups.remove(pickup);
                    mPickupHintActor.removePickupActor(pickup);

                // Oh no
                } else {
                    create();
                }

            // Npc hit a gravity object.
            } else if (npc != null && gravity != null) {
                mRemovePhysicsActors.add(npc);

            // Actor entered a cell
            } else if (gridCell != null) {
                mGrid.onActorContact(other, gridCell, true);
            }
            // Don't care about anything else
        }

        @Override
        public void endContact(Contact contact) {
            Object a = contact.getFixtureA().getUserData();
            Object b = contact.getFixtureB().getUserData();
            Grid.Cell gridCell = null;
            Actor other = null;
            if (a instanceof Grid.Cell) {
                gridCell = (Grid.Cell) a;
                other = (Actor) b;
            }
            if (b instanceof Grid.Cell) {
                gridCell = (Grid.Cell) b;
                other = (Actor) a;
            }

            // We only care about actors leaving a grid cell
            if (gridCell != null) {
                mGrid.onActorContact(other, gridCell, false);
            }
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {

        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    };
}
