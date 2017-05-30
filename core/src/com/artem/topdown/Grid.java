package com.artem.topdown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.HashSet;

public class Grid {
    public static class Cell {
        int x;
        int y;
    }

    private static final float GRID_SIZE = 50f;

    private final Fixture[][] mCells;
    private final HashSet<Actor>[][] mActors;

    private Cell mPlayerCell;

    public Grid(World world, float worldSize) {
        int dimension = (int) (worldSize / GRID_SIZE) + 1;
        mCells = new Fixture[dimension][dimension];
        mActors = new HashSet[dimension][dimension];

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(GRID_SIZE / 2, GRID_SIZE / 2);
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                bodyDef.position.set(i * GRID_SIZE, j * GRID_SIZE);
                Body body = world.createBody(bodyDef);

                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.isSensor = true;
                fixtureDef.shape = shape;
                mCells[i][j] = body.createFixture(fixtureDef);

                Cell cell = new Cell();
                cell.x = i;
                cell.y = j;
                mCells[i][j].setUserData(cell);

                mActors[i][j] = new HashSet<Actor>();
            }
        }
        shape.dispose();
    }

    public void onActorContact(Actor actor, Grid.Cell cell, boolean enter) {
        HashSet<Actor> actors = mActors[cell.x][cell.y];
        //Gdx.app.log("artem", String.format("onActorContact: cell=%d,%d actor=%s enter=%b", cell.x, cell.y, actor, enter));

        if (actor instanceof PlayerActor) {
            if (enter) updateActorCell(cell);
            return;
        }

        if (!enter) {
            actors.remove(actor);
        } else {
            actors.add(actor);
            if (mPlayerCell == null) {
                actor.setVisible(false);
            } else {
                if (Math.abs(cell.x - mPlayerCell.x) <= 1 && Math.abs(cell.y - mPlayerCell.y) <= 1) {
                    actor.setVisible(true);
                } else {
                    actor.setVisible(false);
                }
            }
        }
    }

    private void updateActorCell(Cell cell) {
        if (mPlayerCell != null) {
            changeVisibilityAroundCell(mPlayerCell, false);
        }
        mPlayerCell = cell;
        changeVisibilityAroundCell(mPlayerCell, true);
    }

    private void changeVisibilityAroundCell(Cell cell, boolean visible) {
        for (int i = cell.x - 1; i <= cell.x + 1; i++) {
            for (int j = cell.y - 1; j <= cell.y + 1; j++) {
                // Detect out of bounds
                if (i < 0 || j < 0 || i >= mCells.length || j >= mCells.length) continue;

                for (Actor actor : mActors[i][j]) {
                    actor.setVisible(visible);
                }
            }
        }
    }
}
