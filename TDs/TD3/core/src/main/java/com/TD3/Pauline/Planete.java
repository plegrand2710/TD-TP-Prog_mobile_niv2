package com.TD3.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Planete {
    private static final float MAX_SPEED = 100f;

    private float x = 0;
    private boolean pointClaimed = false;
    private float rotation = 0f;
    private final String name;  // Ajout du nom de la planète


    private final Circle collisionCircle;
    private final Rectangle collisionRect;

    private final TextureRegion planetRegion;
    private final TextureRegion energyRegion;
    private float scaleFactor = 1.0f;

    public Planete(TextureRegion planetRegion, TextureRegion energyRegion) {
        this.planetRegion = planetRegion;
        this.energyRegion = energyRegion;

        if (planetRegion instanceof TextureAtlas.AtlasRegion) {
            this.name = ((TextureAtlas.AtlasRegion) planetRegion).name;
        } else {
            this.name = "Unknown Planet"; // Nom par défaut si pas dans un atlas
        }
        float radius = (planetRegion.getRegionWidth() / 2f) * scaleFactor;
        collisionCircle = new Circle(0, 0, radius);

        float width = planetRegion.getRegionWidth() * scaleFactor;
        float height = planetRegion.getRegionHeight() * scaleFactor;
        collisionRect = new Rectangle(0, 0, width, height);
    }

    public void update(float delta) {
        setPosition(x - (MAX_SPEED * delta), collisionCircle.y);
        rotation += 20 * delta;
    }

    public void setScale(float scale) {
        this.scaleFactor = scale;
        collisionCircle.setRadius((planetRegion.getRegionWidth() / 2f) * scaleFactor);
        collisionRect.setSize(
            planetRegion.getRegionWidth() * scaleFactor,
            planetRegion.getRegionHeight() * scaleFactor
        );
    }

    public void setPosition(float x, float y) {
        this.x = x;
        collisionCircle.setPosition( x, y);
        collisionRect.setPosition(x - collisionRect.width / 2f, y - collisionRect.height / 2f);
    }

    public void randomizePositionAndSize(float worldWidth, float worldHeight) {
        float positionX = worldWidth + planetRegion.getRegionWidth() * scaleFactor;
        float positionY = MathUtils.random(0, worldHeight - planetRegion.getRegionHeight() * scaleFactor);

        setPosition(positionX, positionY);
    }

    public void draw(SpriteBatch batch) {
        float drawX = collisionRect.x;
        float drawY = collisionRect.y;

        batch.draw(
            planetRegion,
            drawX,
            drawY,
            planetRegion.getRegionWidth() * scaleFactor,
            planetRegion.getRegionHeight() * scaleFactor
        );


        if (energyRegion != null) {
            float energyX = collisionCircle.x - (energyRegion.getRegionWidth() / 2) * scaleFactor;
            float energyY = collisionCircle.y - (energyRegion.getRegionHeight() / 2) * scaleFactor;

            batch.draw(
                energyRegion,
                energyX,
                energyY,
                energyRegion.getRegionWidth() * scaleFactor,
                energyRegion.getRegionHeight() * scaleFactor
            );
        }
    }

    public void drawDebug(ShapeRenderer sr) {
        sr.setColor(Color.YELLOW);
        sr.circle(collisionCircle.x, collisionCircle.y, collisionCircle.radius);

        sr.setColor(Color.RED);
        sr.rect(collisionRect.x, collisionRect.y, collisionRect.width, collisionRect.height);
    }

    public boolean isCosmonauteColliding(Cosmonaute cosmo) {
        return Intersector.overlaps(cosmo.getCollisionCircle(), collisionCircle)
            || Intersector.overlaps(cosmo.getCollisionCircle(), collisionRect);
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
