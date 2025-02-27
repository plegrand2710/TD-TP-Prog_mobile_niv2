package com.TD3.Pauline;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class Alien {
    private float x, y, speed, verticalSpeed;
    private final TextureRegion region;
    private boolean movingLeft;
    private float scale;
    private static final float WORLD_HEIGHT = 640f;

    public Alien(TextureRegion region, float x, float y, float speed, boolean movingLeft, float scale, float verticalSpeed) {
        this.region = region;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.verticalSpeed = verticalSpeed;
        this.movingLeft = movingLeft;
        this.scale = scale;
    }

    public Alien(TextureRegion region, float x, float y, float speed, boolean movingLeft) {
        this(region, x, y, speed, movingLeft, 0.2f, 0f);
    }

    public void update(float delta) {
        x += (movingLeft ? -speed : speed) * delta;
        y += verticalSpeed * delta;
        if (MathUtils.randomBoolean(0.01f)) {
            verticalSpeed = MathUtils.random(-50, 50);
        }
        float h = region.getRegionHeight() * scale;
        if (y < 0) { y = 0; verticalSpeed = -verticalSpeed; }
        else if (y + h > WORLD_HEIGHT) { y = WORLD_HEIGHT - h; verticalSpeed = -verticalSpeed; }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(region, x, y, region.getRegionWidth() * scale, region.getRegionHeight() * scale);
    }

    public boolean collidesWith(Cosmonaute cosmo) {
        float alienWidth = region.getRegionWidth() * scale;
        float alienHeight = region.getRegionHeight() * scale;

        float centerX = x + alienWidth / 2;
        float centerY = y + alienHeight / 2;

        float alienRadius = Math.min(alienWidth, alienHeight) / 3f;

        float dx = centerX - cosmo.getX();
        float dy = centerY - cosmo.getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        return distance < (alienRadius + cosmo.getCollisionCircle().radius);
    }



    public float getX() { return x; }
    public float getY() {
        return y;
    }
    public TextureRegion getRegion() { return region; }
    public float getScale() { return scale; }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);

        float alienWidth = region.getRegionWidth() * scale;
        float alienHeight = region.getRegionHeight() * scale;

        float centerX = x + alienWidth / 2;
        float centerY = y + alienHeight / 2;

        float alienRadius = Math.min(alienWidth, alienHeight) / 3f;

        shapeRenderer.circle(centerX, centerY, alienRadius);
    }

}
