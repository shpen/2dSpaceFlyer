package com.artem.topdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PlayerActor extends Actor {
    private static final float MOVE_SPEED = 1.0f;

    private final Sprite mSprite;

    public PlayerActor() {
        mSprite = new Sprite(new Texture("player.png"));
        setSize(5, 10);
        setPosition(20, 20);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        Vector2 direction = new Vector2();
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            direction.add(0, 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            direction.add(0, -1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            direction.add(-1, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            direction.add(1, 0);
        }

        Vector2 pos = direction.nor().scl(MOVE_SPEED).add(getX(), getY());
        setPosition(pos.x, pos.y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(mSprite, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}
