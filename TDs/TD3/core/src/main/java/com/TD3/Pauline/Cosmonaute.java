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
import java.util.Comparator;

public class Cosmonaute {
    private static final float FRAME_DURATION = 0.25f;
    private static final float COLLISION_RADIUS = 50f;

    private static final boolean DEBUG = false;
    private static final boolean DEBUG1 = true;

    private boolean isDead = false;
    private float fireTimer = 0;
    private boolean isFiring = false;
    private Animation<TextureRegion> fireAnimation;
    private float fireAnimationTimer = 0;


    private Animation<TextureRegion> deathAnimation;
    private float deathTimer = 0;

    private final GameScreen gameScreen;

    private static final String TAG = "SpaceWarriorApp";

    private final Circle collisionCircle;
    private float x = 0, y = 0;
    private float xSpeed = 0, ySpeed = 0;
    private float animationTimer = 0;
    private static final float SCALE_FACTOR = 0.3f;
    private final Animation<TextureRegion> animation;
    private final TextureAtlas _atlas;


    public Cosmonaute(TextureAtlas atlas, GameScreen gameScreen) {
        this.gameScreen = gameScreen;
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

        ArrayList<TextureRegion> deathFrames = new ArrayList<>();
        for (TextureAtlas.AtlasRegion region : _atlas.getRegions()) {
            if (region.name.startsWith("Character Death")) {
                deathFrames.add(region);

                if (DEBUG1) {
                    Gdx.app.log(TAG, "Frame death " + i + " -> Name: " + region.name +
                        ", X: " + region.getRegionX() + ", Y: " + region.getRegionY() +
                        ", Width: " + region.getRegionWidth() + ", Height: " + region.getRegionHeight());
                }
            }
        }

        deathFrames.sort(Comparator.comparing(TextureRegion::toString));

        if (!deathFrames.isEmpty()) {
            deathAnimation = new Animation<>(0.05f, deathFrames.toArray(new TextureRegion[0]));
            deathAnimation.setPlayMode(Animation.PlayMode.NORMAL);

            Gdx.app.log(TAG, "Death animation frames count: " + deathAnimation.getKeyFrames().length);
            TextureRegion[] framesDeath = deathAnimation.getKeyFrames();
            for (int j = 0; j < framesDeath.length; j++) {
                TextureRegion frame = framesDeath[j];
                Gdx.app.log(TAG, "Death Animation Frame " + j + " -> Width: " + frame.getRegionWidth() +
                    ", Height: " + frame.getRegionHeight());
            }


            Gdx.app.log(TAG, "Death animation loaded with " + deathFrames.size() + " frames.");
        } else {
            Gdx.app.error(TAG, "No death animation frames found in the atlas!");
        }


        ArrayList<TextureRegion> fireFrames = new ArrayList<>();
        for (TextureAtlas.AtlasRegion region : _atlas.getRegions()) {
            if (region.name.startsWith("Character Fire")) {
                fireFrames.add(region);
            }
        }

        fireFrames.sort(Comparator.comparing(TextureRegion::toString));

        if (!fireFrames.isEmpty()) {
            fireAnimation = new Animation<TextureRegion>(0.05f, fireFrames.toArray(new TextureRegion[fireFrames.size()]));
            fireAnimation.setPlayMode(Animation.PlayMode.NORMAL);
            Gdx.app.log(TAG, "Fire animation loaded with " + fireFrames.size() + " frames.");
        } else {
            Gdx.app.error(TAG, "No fire animation frames found in the atlas!");
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

    public void startFiring() {
        isFiring = true;
        fireTimer = 0;
    }

    public void reset() {
        isDead = false;
        deathTimer = 0;
    }

    public void fire() {
        isFiring = true;
        fireAnimationTimer = 0;
    }


    public void update(float delta) {
        if (isDead) {
            deathTimer += delta;

            if (DEBUG1) {
                Gdx.app.log(TAG, "Death animation time: " + deathTimer + " / " + deathAnimation.getAnimationDuration());
            }

            Gdx.app.log(TAG, "Death animation frames count: " + deathAnimation.getKeyFrames().length);
            TextureRegion[] frames = deathAnimation.getKeyFrames();
            for (int j = 0; j < frames.length; j++) {
                TextureRegion frame = frames[j];
                Gdx.app.log(TAG, "Death Animation Frame " + j + " -> Width: " + frame.getRegionWidth() +
                    ", Height: " + frame.getRegionHeight());
            }

            if (deathAnimation != null && deathAnimation.isAnimationFinished(deathTimer)) {
                Gdx.app.log(TAG, "Death animation finished. Triggering game over.");
                gameScreen.setGameOver();
            }
            return;
        }

        if (isFiring) {
            fireAnimationTimer += delta;
            if (fireAnimation.isAnimationFinished(fireAnimationTimer)) {
                isFiring = false;
            }
        }

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
        TextureRegion currentFrame;

        if (isDead) {
            if (deathAnimation != null && deathAnimation.isAnimationFinished(deathTimer)) {
                return;
            }
            currentFrame = deathAnimation.getKeyFrame(deathTimer);
        } else if (isFiring && fireAnimation != null) {
            currentFrame = fireAnimation.getKeyFrame(fireAnimationTimer);
        } else {
            currentFrame = animation.getKeyFrame(animationTimer);
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
        fire();
        TextureRegion bulletRegion = _atlas.findRegion("Bullet (1)");
        return new Missile(x, y, 400, 0, true, bulletRegion);
    }


    public void die() {
        if (isDead) return;
        isDead = true;
        deathTimer = 0;

        if (DEBUG1) {
            Gdx.app.log(TAG, "Cosmonaute died, starting death animation.");
        }
    }


}

