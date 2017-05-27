package com.artem.topdown;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.HashSet;

public class TopDownGame extends ApplicationAdapter {
    public static final float PHYSICS_TO_PIXEL_SCALE = 10f;
    public static final float PIXEL_TO_PHYSICS_SCALE = 1 / PHYSICS_TO_PIXEL_SCALE;

    private static final float WORLD_SIZE = 2000f;
    private static final float ASPECT_RATIO = 16f / 9f;
    private static final float VIEWPORT_SIZE = 300f;
    private static final float CAMERA_FOLLOW_SPEED = 0.1f;

    private HashSet<PhysicsActor> mRemovePhysicsActors;

    private World mWorld;
    private Box2DDebugRenderer mDebugRenderer;

    private Stage mStage;

    private PlayerActor mPlayer;

    private int mSpawnCounter;

    public static ShapeRenderer mShapeRenderer;

    @Override
    public void create() {
        mRemovePhysicsActors = new HashSet<PhysicsActor>();

        Gdx.gl.glClearColor(0, 0, 0, 1);

        Box2D.init();
        mWorld = new World(new Vector2(0, 0), true);
        mDebugRenderer = new Box2DDebugRenderer();
        mWorld.setContactListener(mContactListener);

        mShapeRenderer = new ShapeRenderer();

        mStage = new Stage(new ExtendViewport(VIEWPORT_SIZE, VIEWPORT_SIZE * ASPECT_RATIO));
        Gdx.input.setInputProcessor(mStage);

        mStage.addActor(new BackgroundActor(WORLD_SIZE));

        mPlayer = new PlayerActor(mWorld, WORLD_SIZE / 2, WORLD_SIZE / 2);

        // Create some random background elements
        fillGravityActors();
        fillNpcs();

        mStage.addActor(mPlayer);
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

        if(mSpawnCounter++ == 100) {
            mSpawnCounter = 0;
            mStage.addActor(new NpcActor(mWorld, mPlayer, (float) Math.random() * WORLD_SIZE, (float) Math.random() * WORLD_SIZE));
        }

        mStage.act();

        // Follow player
        Vector3 camPos = mStage.getCamera().position;
        camPos.slerp(new Vector3(mPlayer.getX(), mPlayer.getY(), 0), CAMERA_FOLLOW_SPEED);
        mStage.getCamera().update();

        mStage.draw();

        //mDebugRenderer.render(mWorld, mStage.getCamera().combined.scl(PHYSICS_TO_PIXEL_SCALE));
        mWorld.step(1/60f, 6, 2);
    }

    @Override
    public void dispose() {
        mStage.dispose();
    }

    private void fillGravityActors() {
        for (int i = 0; i < 40; i++) {
            Actor box = new GravityActor(mWorld, (float) Math.random() * WORLD_SIZE, (float) Math.random() * WORLD_SIZE);
            mStage.addActor(box);
        }
    }

    private void fillNpcs() {
        for (int i = 0; i < 10; i++) {
            Actor npc = new NpcActor(mWorld, mPlayer, (float) Math.random() * WORLD_SIZE, (float) Math.random() * WORLD_SIZE);
            mStage.addActor(npc);
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
            if (a instanceof NpcActor) npc = (NpcActor) a;
            if (b instanceof NpcActor) npc = (NpcActor) b;
            if (a instanceof PlayerActor) player = (PlayerActor) a;
            if (b instanceof PlayerActor) player = (PlayerActor) b;
            if (a instanceof GravityActor) gravity = (GravityActor) a;
            if (b instanceof GravityActor) gravity = (GravityActor) b;

            // Player hit something. Game over
            if (player != null && (gravity != null || npc != null)) {
                create();
            // Npc hit a gravity object.
            } else if (npc != null && gravity != null) {
                mRemovePhysicsActors.add(npc);
            } // Don't care about anything else
        }

        @Override
        public void endContact(Contact contact) {

        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {

        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    };
}
