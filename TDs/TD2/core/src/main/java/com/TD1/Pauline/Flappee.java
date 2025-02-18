package com.TD1.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;

public class Flappee {

    private static final int TILE_WIDTH = 118;
    private static final int TILE_HEIGHT = 118;
    private static final float FRAME_DURATION = 0.25F;
    private static final float COLLISION_RADIUS = 24f;

    private final Circle collisionCircle;

    private float x = 0;
    private float y = 0;

    private float xSpeed = 0;
    private float ySpeed = 0;

    private float animationTimer = 0;

    private final Animation animation;

    public Flappee(Texture flappeeTexture) {
        TextureRegion[][] flappeeTextures = new TextureRegion(flappeeTexture).split(TILE_WIDTH, TILE_HEIGHT);
        animation = new Animation(FRAME_DURATION, flappeeTextures[0][0], flappeeTextures[0][1]);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        collisionCircle = new Circle(x, y, COLLISION_RADIUS);
    }

    public Circle getCollisionCircle() {
        return collisionCircle;
    }

    public void update(float delta) {
        animationTimer += delta;
        x += xSpeed * delta;
        y += ySpeed * delta;
        updateCollisionCircle();
    }

    public void setVelocity(float vx, float vy) {
        this.xSpeed = vx;
        this.ySpeed = vy;
    }

    public void moveBy(float dx, float dy) {
        this.x += dx;
        this.y += dy;
        updateCollisionCircle();
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateCollisionCircle();
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion flappeeTexture = (TextureRegion) animation.getKeyFrame(animationTimer);
        float textureX = collisionCircle.x - flappeeTexture.getRegionWidth() / 2;
        float textureY = collisionCircle.y - flappeeTexture.getRegionHeight() / 2;
        batch.draw(flappeeTexture, textureX, textureY);
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(collisionCircle.x, collisionCircle.y, collisionCircle.radius);
    }

    private void updateCollisionCircle() {
        collisionCircle.setX(x);
        collisionCircle.setY(y);
    }

    public void updateWithGyro() {
        float thresholdDegrees = 5f;
        float ax = Gdx.input.getAccelerometerX();
        float ay = Gdx.input.getAccelerometerY();
        float az = Gdx.input.getAccelerometerZ();
        float pitch = MathUtils.atan2(-ax, (float) Math.sqrt(ay * ay + az * az)) * MathUtils.radiansToDegrees;
        float roll = MathUtils.atan2(ay, az) * MathUtils.radiansToDegrees;
        float speed = 200f;

        if (Math.abs(pitch) > Math.abs(roll)) {
            if (pitch > thresholdDegrees) {
                setVelocity(0, speed);
            } else if (pitch < -thresholdDegrees) {
                setVelocity(0, -speed);
            } else {
                setVelocity(0, 0);
            }
        } else {
            if (roll > thresholdDegrees) {
                setVelocity(speed, 0);
            } else if (roll < -thresholdDegrees) {
                setVelocity(-speed, 0);
            } else {
                setVelocity(0, 0);
            }
        }
    }
}
