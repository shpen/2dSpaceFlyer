package com.artem.topdown;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.HashSet;

/**
 * Created by Artem on 5/30/2017.
 */

public class PickupHintActor extends ShapeActor {
    private static final Color RING_COLOR = new Color(0.7f, 1f, 0.2f, 0.2f);
    private static final Color ARROW_COLOR = new Color(0.7f, 1f, 0.2f, 1f);

    private final Camera mCamera;

    private final HashSet<PickupActor> mPickupActors;

    public PickupHintActor(Camera camera) {
        mCamera = camera;
        mPickupActors = new HashSet<PickupActor>();
    }

    public void addPickupActor(PickupActor actor) {
        mPickupActors.add(actor);
    }

    public void removePickupActor(PickupActor actor) {
        mPickupActors.remove(actor);
    }

    @Override
    protected void onDraw(ShapeRenderer renderer, float parentAlpha) {
        float radius = Math.min(mCamera.viewportWidth, mCamera.viewportHeight) / 2 * 0.9f;

        // Draw full circle
        renderer.setColor(RING_COLOR);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.circle(mCamera.position.x, mCamera.position.y, radius);
        renderer.end();

        // Draw arrows to each pickup
        for (PickupActor pickup : mPickupActors) {
            Vector2 direction = new Vector2(mCamera.position.x, mCamera.position.y).sub(new Vector2(pickup.getX(), pickup.getY()));
            if (direction.len() < radius) continue;

            float angle = direction.angle();

            renderer.setColor(ARROW_COLOR);
            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.identity();
            renderer.translate(mCamera.position.x, mCamera.position.y, 0);
            renderer.rotate(0, 0, 1, angle + 90);
            renderer.translate(0, radius, 0);
            renderer.line(0, 0, 0, 10);
            renderer.end();
        }
    }
}
