package com.TD3.Pauline;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;

public class Planete {
    private static final float MAX_SPEED = 100f;
    private float x = 0;
    private boolean pointClaimed = false;
    private float rotation = 0f;
    private final String name;
    private final Circle collisionCircle;
    private final TextureRegion planetRegion;
    private float scaleFactor = 1.0f;

    public Planete(TextureRegion planetRegion) {
        this.planetRegion = planetRegion;

        if (planetRegion instanceof TextureAtlas.AtlasRegion) {
            this.name = ((TextureAtlas.AtlasRegion) planetRegion).name;
        } else {
            this.name = "Unknown Planet";
        }

        float radius = (planetRegion.getRegionWidth() / 2f) * scaleFactor;
        collisionCircle = new Circle(0, 0, radius);
    }

    public void update(float delta) {
        setPosition(x - (MAX_SPEED * delta), collisionCircle.y);
        rotation += 20 * delta;
    }

    public void setScale(float scale) {
        this.scaleFactor = scale;
        collisionCircle.setRadius((planetRegion.getRegionWidth() / 2f) * scaleFactor);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        collisionCircle.setPosition(x, y);
    }

    public void randomizePositionAndSize(float worldWidth, float worldHeight) {
        float positionX = worldWidth + planetRegion.getRegionWidth() * scaleFactor;
        float positionY = MathUtils.random(0, worldHeight - planetRegion.getRegionHeight() * scaleFactor);
        setPosition(positionX, positionY);
    }

    public void draw(SpriteBatch batch) {
        float drawX = collisionCircle.x - (planetRegion.getRegionWidth() * scaleFactor) / 2;
        float drawY = collisionCircle.y - (planetRegion.getRegionHeight() * scaleFactor) / 2;

        batch.draw(
            planetRegion,
            drawX,
            drawY,
            planetRegion.getRegionWidth() * scaleFactor,
            planetRegion.getRegionHeight() * scaleFactor
        );
    }

    public void drawDebug(ShapeRenderer sr) {
        sr.setColor(Color.YELLOW);
        sr.circle(collisionCircle.x, collisionCircle.y, collisionCircle.radius);
    }

    public boolean isCosmonauteColliding(Cosmonaute cosmo) {
        return Intersector.overlaps(cosmo.getCollisionCircle(), collisionCircle);
    }

    public float getX() {
        return x;
    }

    public boolean isPointClaimed() {
        return pointClaimed;
    }

    public void markPointClaimed() {
        pointClaimed = true;
    }

    public float getWidth() {
        return planetRegion.getRegionWidth() * scaleFactor;
    }
}
