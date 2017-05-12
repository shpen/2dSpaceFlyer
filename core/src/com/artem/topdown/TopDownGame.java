package com.artem.topdown;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class TopDownGame extends ApplicationAdapter {
    public static final float PHYSICS_TO_PIXEL_SCALE = 10f;
    public static final float PIXEL_TO_PHYSICS_SCALE = 1 / PHYSICS_TO_PIXEL_SCALE;

    private static final float CAMERA_FOLLOW_SPEED = 0.1f;

    private World mWorld;
    private Box2DDebugRenderer mDebugRenderer;

    private Stage mStage;

    private PlayerActor mPlayer;

    @Override
    public void create() {
        Gdx.gl.glClearColor(0, 0, 0, 1);

        Box2D.init();
        mWorld = new World(new Vector2(0, 0), true);
        mDebugRenderer = new Box2DDebugRenderer();

        mStage = new Stage(new FitViewport(128, 96));
        Gdx.input.setInputProcessor(mStage);

        // Create some random background elements
        fillBoxes();

        mPlayer = new PlayerActor(mWorld, 20, 20);
        mStage.addActor(mPlayer);
    }

    @Override
    public void resize(int width, int height) {
        mStage.getViewport().update(width, height);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mStage.act();

        // Follow player
        Vector3 camPos = mStage.getCamera().position;
        camPos.slerp(new Vector3(mPlayer.getX(), mPlayer.getY(), 0), CAMERA_FOLLOW_SPEED);
        mStage.getCamera().update();

        mStage.draw();

        mDebugRenderer.render(mWorld, mStage.getCamera().combined.scl(PHYSICS_TO_PIXEL_SCALE));
        mWorld.step(1/60f, 6, 2);
    }

    @Override
    public void dispose() {
        mStage.dispose();
    }

    private void fillBoxes() {
        Texture tex = new Texture("box.png");
        for (int i = 0; i < 10; i++) {
            Actor box = new PhysicsActor(tex, (float) Math.random() * 100f, (float) Math.random() * 100f,
                    mWorld, BodyDef.BodyType.StaticBody);
            mStage.addActor(box);
        }
    }
}
