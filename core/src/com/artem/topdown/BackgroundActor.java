package com.artem.topdown;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Artem on 5/24/2017.
 */

public class BackgroundActor extends Actor {
    private static final Texture sTex = new Texture("circle.png");

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (int i = 0; i < 600; i += 40) {
            for (int j = 0; j < 600; j += 40) {
                batch.draw(sTex, i, j, 1, 1);
            }
        }
    }
}
