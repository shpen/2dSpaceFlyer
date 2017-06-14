package com.artem.topdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.HashSet;

/**
 * Created by Artem on 6/12/2017.
 */

public class GravityGroup extends Group {
    @Override
    protected void drawChildren(Batch batch, float parentAlpha) {
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        ShapeActor.sRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Actor actor : getChildren()) {
            ((GravityActor) actor).drawCenter(ShapeActor.sRenderer);
        }
        ShapeActor.sRenderer.end();

        ShapeActor.sRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (Actor actor : getChildren()) {
            ((GravityActor) actor).drawOuter(ShapeActor.sRenderer);
        }
        ShapeActor.sRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
    }
}
