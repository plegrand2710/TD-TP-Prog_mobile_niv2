package com.TD3.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class Roquet {
    private float x, y, speedX, speedY;
    private final Animation<TextureRegion> animation;
    private float animationTimer = 0f;
    private static final float RADIUS = 10f;
    private float scaleFactor;
    private final Rectangle collisionRect;
    private static final float COLLISION_WIDTH = 100f;
    private static final float COLLISION_LENGTH = 500f;

    public Roquet(float x, float y, float speedX, float speedY, Animation<TextureRegion> animation) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.animation = animation;
        this.scaleFactor = 0.2f;

        this.collisionRect = new Rectangle(
            x - (COLLISION_LENGTH * scaleFactor) / 2,
            y - (COLLISION_WIDTH * scaleFactor) / 2,
            COLLISION_LENGTH * scaleFactor,
            COLLISION_WIDTH * scaleFactor
        );
    }


    public void update(float delta) {
        x += speedX * delta;
        y += speedY * delta;
        animationTimer += delta;
        collisionRect.setPosition(
            x - collisionRect.width / 2f,
            y - collisionRect.height / 2f
        );
    }


    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(animationTimer);
        float width = currentFrame.getRegionWidth() * scaleFactor;
        float height = currentFrame.getRegionHeight() * scaleFactor;
        batch.draw(currentFrame, x - width / 2, y - height / 2, width, height);
    }

    public void drawDebug(ShapeRenderer sr) {
        sr.setColor(Color.PINK);
        Gdx.gl20.glLineWidth(5f);
        sr.rect(collisionRect.x, collisionRect.y, collisionRect.width, collisionRect.height);
        Gdx.gl20.glLineWidth(1f);
    }


    public boolean collidesWith(Missile missile) {
        return Intersector.overlaps(missile.getCollisionCircle(), this.getCollisionRect());
    }



    public Rectangle getCollisionRect() {
        return collisionRect;
    }
}
