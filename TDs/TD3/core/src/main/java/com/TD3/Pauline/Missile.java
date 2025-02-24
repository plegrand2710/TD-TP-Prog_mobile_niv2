package com.TD3.Pauline;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;

public class Missile {
    private float x, y, speedX, speedY;
    private final boolean fromPlayer;
    private final TextureRegion region;
    private final Circle collisionCircle;
    private static final float RADIUS = 10f;

    public Missile(float x, float y, float speedX, float speedY, boolean fromPlayer, TextureRegion region) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.fromPlayer = fromPlayer;
        this.region = region;
        collisionCircle = new Circle(x, y, RADIUS);
    }

    public void update(float delta) {
        x += speedX * delta;
        y += speedY * delta;
        collisionCircle.setPosition(x, y);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(region, x - region.getRegionWidth() / 2f, y - region.getRegionHeight() / 2f);
    }

    public Circle getCollisionCircle() {
        return collisionCircle;
    }

    public boolean isFromPlayer() {
        return fromPlayer;
    }
}
