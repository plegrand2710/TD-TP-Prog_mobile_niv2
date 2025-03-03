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

    private Animation<TextureRegion> explosionAnimation;
    private boolean isExploding = false;
    private float explosionTimer = 0f;
    private static final float EXPLOSION_DURATION = 0.5f;


    public Roquet(float x, float y, float speedX, float speedY, Animation<TextureRegion> animation, Animation<TextureRegion> explosionAnimation) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.animation = animation;
        this.explosionAnimation = explosionAnimation;
        this.scaleFactor = 0.2f;

        this.collisionRect = new Rectangle(
            x - (COLLISION_LENGTH * scaleFactor) / 2,
            y - (COLLISION_WIDTH * scaleFactor) / 2,
            COLLISION_LENGTH * scaleFactor,
            COLLISION_WIDTH * scaleFactor
        );
    }


    public void update(float delta) {
        if (isExploding) {
            explosionTimer += delta;
            return;
        }

        x += speedX * delta;
        y += speedY * delta;
        animationTimer += delta;
        collisionRect.setPosition(
            x - collisionRect.width / 2f,
            y - collisionRect.height / 2f
        );
    }

    public void explode() {
        if (!isExploding) {
            isExploding = true;
            explosionTimer = 0f;
            speedX = 0;
            speedY = 0;
        }
    }

    public boolean isExploding() {
        return isExploding;
    }

    public void draw(SpriteBatch batch) {
        if (isExploding) return;

        TextureRegion currentFrame = animation.getKeyFrame(animationTimer);
        float width = currentFrame.getRegionWidth() * scaleFactor;
        float height = currentFrame.getRegionHeight() * scaleFactor;
        batch.draw(currentFrame, x - width / 2, y - height / 2, width, height);
    }

    public void drawDebug(ShapeRenderer sr) {
        sr.setColor(Color.PINK);

        if (!isExploding) {
            sr.rect(collisionRect.x, collisionRect.y, collisionRect.width, collisionRect.height);
        } else {
            float explosionRadius = collisionRect.width * 0.5f;
            sr.setColor(Color.ORANGE);
            sr.circle(x, y, explosionRadius);
        }
    }

    public boolean isFinishedExploding() {
        return isExploding && explosionAnimation.isAnimationFinished(explosionTimer);
    }

    public void drawExplosion(SpriteBatch batch) {
        if (!isExploding) return;

        TextureRegion explosionFrame = explosionAnimation.getKeyFrame(explosionTimer);
        float explosionSize = COLLISION_WIDTH * 1.5f;
        batch.draw(explosionFrame, x - explosionSize / 2, y - explosionSize / 2, explosionSize, explosionSize);
    }

    public boolean collidesWith(Missile missile) {
        boolean collision = Intersector.overlaps(missile.getCollisionCircle(), this.getCollisionRect());
        if (collision) {
            explode();
        }
        return collision;
    }



    public Rectangle getCollisionRect() {
        return collisionRect;
    }
}
