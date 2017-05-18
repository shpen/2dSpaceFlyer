package com.artem.topdown;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class BasicActor extends Actor {
    private final Sprite mSprite;

    public BasicActor(Texture texture) {
        mSprite = new Sprite(texture);
        setSize(mSprite.getWidth(), mSprite.getHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(mSprite, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    public boolean overlaps(BasicActor other) {
        return mSprite.getBoundingRectangle().setPosition(getX(), getY())
                .overlaps(other.mSprite.getBoundingRectangle().setPosition(other.getX(), other.getY()));
    }
}
