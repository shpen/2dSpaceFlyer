package com.artem.topdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class PlayerActor extends PhysicsActor {
    private static final float MOVE_SPEED = 5.0f;

    private static final float DASH_SPEED_SCALE = 3.0f;
    private static final float DASH_DURATION = 0.15f;

    private boolean mIsDashing;
    private float mDashTime;

    public PlayerActor(World world, float x, float y) {
        super(new Texture("player.png"), x, y, world, BodyDef.BodyType.DynamicBody);
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) && !mIsDashing) {
            mDashTime = 0;
            mIsDashing = true;
            enableCollision(false);
        } else if (mIsDashing && mDashTime < DASH_DURATION) {
            mDashTime += delta;
        } else {
            mIsDashing = false;
            enableCollision(true);
        }

        float speed = MOVE_SPEED;
        if (mIsDashing) {
            speed *= DASH_SPEED_SCALE;
            getColor().a = 0.5f;
        } else {
            getColor().a = 1f;
        }

        //Gdx.app.log("artem", "dir: " + direction);
        /*if (!direction.isZero()) {
            Vector2 curPos = new Vector2(getX(), getY());
            Vector2 pos = direction.nor().scl(MOVE_SPEED).add(getX(), getY());
            setPosition(pos.x, pos.y);

            for (Actor actor: getStage().getActors()) {
                if (actor != this && actor instanceof BasicActor && overlaps((BasicActor) actor)) {
                    Gdx.app.log("artem", "player overlaps with " + actor);
                    setPosition(curPos.x, curPos.y);
                    break;
                }
            }
        }*/

        direction.nor().scl(speed);
        setVelocity(direction);
    }

    /*private InputListener mInputListener = new InputListener() {
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (keycode == Input.Keys.SHIFT_LEFT) {

            }

            return super.keyDown(event, keycode);
        }
    };*/
}
