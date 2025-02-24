package com.TD3.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;

public class Cosmonaute {
    private static final float FRAME_DURATION = 0.25f;
    private static final float COLLISION_RADIUS = 24f;

    private static final boolean DEBUG = false;
    private static final boolean DEBUG1 = true;

    private static final String TAG = "SpaceWarriorApp";

    private final Circle collisionCircle;
    private float x = 0, y = 0;
    private float xSpeed = 0, ySpeed = 0;
    private float animationTimer = 0;
    private static final float SCALE_FACTOR = 0.3f;
    private final Animation<TextureRegion> animation;
    private final TextureAtlas _atlas;

    public Cosmonaute(TextureAtlas atlas) {
        _atlas = atlas;
        ArrayList<TextureRegion> frames = new ArrayList<TextureRegion>();

        if (DEBUG1) {
            Gdx.app.log(TAG, "Loading animation frames from atlas...");
        }

        int i = 0;
        for (TextureAtlas.AtlasRegion region : _atlas.getRegions()) {
            if (region.name.startsWith("Character Fly")) {
                frames.add(region);

                if (DEBUG1) {
                    Gdx.app.log(TAG, "Frame " + i + " -> Name: " + region.name +
                        ", X: " + region.getRegionX() + ", Y: " + region.getRegionY() +
                        ", Width: " + region.getRegionWidth() + ", Height: " + region.getRegionHeight());
                }
                i++;
            }
        }

        if (frames.isEmpty()) {
            if (DEBUG1) {
                Gdx.app.error(TAG, "No frames found for animation. Check atlas naming convention.");
            }
        } else {
            if (DEBUG1) {
                Gdx.app.log(TAG, "Total frames loaded into animation -> " + frames.size());
            }
        }

        animation = new Animation<TextureRegion>(FRAME_DURATION, frames.toArray(new TextureRegion[frames.size()]));
        animation.setPlayMode(Animation.PlayMode.LOOP);

        if (DEBUG1) {
            Gdx.app.log(TAG, "Animation created with frame duration -> " + FRAME_DURATION + " seconds per frame");
        }

        collisionCircle = new Circle(x, y, COLLISION_RADIUS);

        if (DEBUG1) {
            Gdx.app.log(TAG, "Collision circle initialized -> Center X: " + x + ", Center Y: " + y + ", Radius: " + COLLISION_RADIUS);
        }
    }

    public Circle getCollisionCircle() {
        return collisionCircle;
    }

    public void update(float delta) {
        animationTimer += delta;
        x += xSpeed * delta;
        y += ySpeed * delta;
        collisionCircle.setPosition(x, y);
    }

    public void setVelocity(float vx, float vy) {
        this.xSpeed = vx;
        this.ySpeed = vy;
    }

    public void moveBy(float dx, float dy) {
        this.x += dx;
        this.y += dy;
        collisionCircle.setPosition(x, y);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        collisionCircle.setPosition(x, y);
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(animationTimer);

        if (DEBUG) {
            boolean isAnimationFinished = animation.isAnimationFinished(animationTimer);
            Gdx.app.log(TAG, "Fetching current frame from animation");
            Gdx.app.log(TAG, "Animation Timer -> " + animationTimer);
            Gdx.app.log(TAG, "Is Animation Finished? -> " + isAnimationFinished);
            Gdx.app.log(TAG, "Current Frame Region -> X: " + currentFrame.getRegionX() + ", Y: " + currentFrame.getRegionY());
            Gdx.app.log(TAG, "Current Frame Dimensions -> Width: " + currentFrame.getRegionWidth() + ", Height: " + currentFrame.getRegionHeight());
        }

        float drawX = collisionCircle.x - (currentFrame.getRegionWidth() * SCALE_FACTOR) / 2;
        float drawY = collisionCircle.y - (currentFrame.getRegionHeight() * SCALE_FACTOR) / 2;

        batch.draw(
            currentFrame,
            drawX,
            drawY,
            currentFrame.getRegionWidth() * SCALE_FACTOR,
            currentFrame.getRegionHeight() * SCALE_FACTOR
        );

        if (DEBUG) {
            Gdx.app.log(TAG, "Drawing Cosmonaute Frame at -> X: " + drawX + ", Y: " + drawY);
            Gdx.app.log(TAG, "Collision Circle -> Center X: " + collisionCircle.x + ", Y: " + collisionCircle.y + ", Radius: " + collisionCircle.radius);
        }

    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(collisionCircle.x, collisionCircle.y, collisionCircle.radius);
    }

    public void updateWithGyro() {
        try {
            float threshold = 5f;

            float ax = Gdx.input.getAccelerometerX();
            float ay = Gdx.input.getAccelerometerY();
            float az = Gdx.input.getAccelerometerZ();

            if (DEBUG) Gdx.app.log(TAG, "Accelerometer readings -> X: " + ax + ", Y: " + ay + ", Z: " + az);

            float pitch = MathUtils.atan2(-ax, (float) Math.sqrt(ay * ay + az * az)) * MathUtils.radiansToDegrees;
            float roll = MathUtils.atan2(ay, az) * MathUtils.radiansToDegrees;

            if (DEBUG) Gdx.app.log(TAG, "Calculated angles -> Pitch: " + pitch + ", Roll: " + roll);

            float speed = 200f;
            if (Math.abs(pitch) > Math.abs(roll)) {
                float newYVelocity = (pitch > threshold ? speed : (pitch < -threshold ? -speed : 0));
                setVelocity(0, newYVelocity);
                if (DEBUG) Gdx.app.log(TAG, "Vertical movement -> New Y Velocity: " + newYVelocity);
            } else {
                float newXVelocity = (roll > threshold ? speed : (roll < -threshold ? -speed : 0));
                setVelocity(newXVelocity, 0);
                if (DEBUG) Gdx.app.log(TAG, "Horizontal movement -> New X Velocity: " + newXVelocity);
            }

            if (DEBUG) {
                Gdx.app.log(TAG, "Updated velocities -> X: " + xSpeed + ", Y: " + ySpeed);
                Gdx.app.log(TAG, "Cosmonaute position -> X: " + x + ", Y: " + y);
            }

        } catch (Exception e) {
            Gdx.app.error(TAG, "Error in updateWithGyro(): " + e.getMessage(), e);
        }
    }

    public Missile tirer() {
        TextureRegion bulletRegion = _atlas.findRegion("Bullet (1)");
        return new Missile(x, y, 400, 0, true, bulletRegion);
    }
}
