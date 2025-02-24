package com.TD3.Pauline;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Planete {
    private static final float MAX_SPEED = 100f;
    private static final float COLLISION_RECT_WIDTH = 13f;
    private static final float COLLISION_RECT_HEIGHT = 447f;
    private static final float COLLISION_CIRCLE_RADIUS = 33f;
    private static final float HEIGHT_OFFSET = -400f;
    public static final float WIDTH = COLLISION_CIRCLE_RADIUS * 2;

    private float x = 0;
    private boolean pointClaimed = false;
    private float rotation = 0f;

    private final Circle floorCircle;
    private final Rectangle floorRect;
    private final Circle ceilingCircle;
    private final Rectangle ceilingRect;
    private final float y;

    private final TextureRegion planetRegion;
    private final TextureRegion energyRegion;

    public Planete(TextureRegion planetRegion, TextureRegion energyRegion) {
        this.planetRegion = planetRegion;
        this.energyRegion = energyRegion;
        this.y = MathUtils.random(HEIGHT_OFFSET);
        floorRect = new Rectangle(x, y, COLLISION_RECT_WIDTH, COLLISION_RECT_HEIGHT);
        floorCircle = new Circle(x + floorRect.width / 2, y + floorRect.height, COLLISION_CIRCLE_RADIUS);
        ceilingRect = new Rectangle(x, floorCircle.y + 225f, COLLISION_RECT_WIDTH, COLLISION_RECT_HEIGHT);
        ceilingCircle = new Circle(x + ceilingRect.width / 2, ceilingRect.y, COLLISION_CIRCLE_RADIUS);
    }

    public void update(float delta) {
        setPosition(x - (MAX_SPEED * delta));
        rotation += 20 * delta;
    }

    public boolean isCosmonauteColliding(Cosmonaute cosmo) {
        return Intersector.overlaps(cosmo.getCollisionCircle(), ceilingCircle) ||
            Intersector.overlaps(cosmo.getCollisionCircle(), floorCircle) ||
            Intersector.overlaps(cosmo.getCollisionCircle(), ceilingRect) ||
            Intersector.overlaps(cosmo.getCollisionCircle(), floorRect);
    }

    public void setPosition(float x) {
        this.x = x;
        floorCircle.setX(x + floorRect.width / 2);
        ceilingCircle.setX(x + ceilingRect.width / 2);
        floorRect.setX(x);
        ceilingRect.setX(x);
    }

    public float getX() { return x; }
    public boolean isPointClaimed() { return pointClaimed; }
    public void markPointClaimed() { pointClaimed = true; }

    public void draw(SpriteBatch batch) {
        float drawX = floorCircle.x - planetRegion.getRegionWidth() / 2;
        float drawY = floorRect.y + COLLISION_CIRCLE_RADIUS;
        batch.draw(planetRegion, drawX, drawY, planetRegion.getRegionWidth()/2f, planetRegion.getRegionHeight()/2f,
            planetRegion.getRegionWidth(), planetRegion.getRegionHeight(), 1, 1, rotation);
        float energyX = ceilingCircle.x - energyRegion.getRegionWidth() / 2;
        float energyY = ceilingRect.y - COLLISION_CIRCLE_RADIUS;
        batch.draw(energyRegion, energyX, energyY);
    }

    public void drawDebug(ShapeRenderer sr) {
        sr.circle(floorCircle.x, floorCircle.y, floorCircle.radius);
        sr.rect(floorRect.x, floorRect.y, floorRect.width, floorRect.height);
        sr.circle(ceilingCircle.x, ceilingCircle.y, ceilingCircle.radius);
        sr.rect(ceilingRect.x, ceilingRect.y, ceilingRect.width, ceilingRect.height);
    }
}
