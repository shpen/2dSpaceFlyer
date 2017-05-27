package com.artem.topdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class ShapeActor extends Actor {
    private static final ShapeRenderer sRenderer = new ShapeRenderer();

    @Override
    public final void draw(Batch batch, float parentAlpha) {
        // Setup
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        sRenderer.identity();

        onDraw(sRenderer, parentAlpha);

        // Tear down
        Gdx.gl.glDisable(GL20.GL_BLEND);
        sRenderer.end();
        batch.begin();
    }

    protected abstract void onDraw(ShapeRenderer renderer, float parentAlpha);
}
