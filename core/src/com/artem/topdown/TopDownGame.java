package com.artem.topdown;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class TopDownGame extends ApplicationAdapter {
    private Stage mStage;

    private PlayerActor mPlayer;

    @Override
    public void create() {
        Gdx.gl.glClearColor(1, 1, 1, 1);

        mStage = new Stage(new FitViewport(128, 96));
        Gdx.input.setInputProcessor(mStage);

        // Create some random background elements
        fillBoxes();

        mPlayer = new PlayerActor();
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
        mStage.getCamera().position.x = mPlayer.getX();
        mStage.getCamera().position.y = mPlayer.getY();
        mStage.getCamera().update();

        mStage.draw();
    }

    @Override
    public void dispose() {
        mStage.dispose();
    }

    private void fillBoxes() {
        Texture tex = new Texture("box.png");
        for (int i = 0; i < 10; i++) {
            BasicActor box = new BasicActor(tex);
            box.setPosition((float) Math.random() * 100f, (float) Math.random() * 100f);
            mStage.addActor(box);
        }
    }
}
