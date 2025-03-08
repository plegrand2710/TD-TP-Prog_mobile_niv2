package com.TD4.Pauline;

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

    private boolean _isDead = false;
    private float _fireTimer = 0;
    private boolean _isFiring = false;
    private Animation<TextureRegion> _fireAnimation;
    private float _fireAnimationTimer = 0;

    private Animation<TextureRegion> _deathAnimation;
    private float _deathTimer = 0;

    private final GameScreen _gameScreen;

    private static final String TAG = "SpaceWarriorApp";

    private final Circle _collisionCircle;
    private float _x = 0, _y = 0;
    private float _xSpeed = 0, _ySpeed = 0;
    private float _animationTimer = 0;
    private static final float SCALE_FACTOR = 0.3f;
    private final Animation<TextureRegion> _animation;
    private final TextureAtlas _atlas;

    public Cosmonaute(TextureAtlas atlas, GameScreen gameScreen) {
        this._gameScreen = gameScreen;
        _atlas = atlas;
        ArrayList<TextureRegion> frames = new ArrayList<>();

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
            }
        }

        deathFrames.sort(Comparator.comparing(TextureRegion::toString));

        if (!deathFrames.isEmpty()) {
            _deathAnimation = new Animation<>(0.05f, deathFrames.toArray(new TextureRegion[0]));
            _deathAnimation.setPlayMode(Animation.PlayMode.NORMAL);
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
            _fireAnimation = new Animation<>(0.05f, fireFrames.toArray(new TextureRegion[0]));
            _fireAnimation.setPlayMode(Animation.PlayMode.NORMAL);
            Gdx.app.log(TAG, "Fire animation loaded with " + fireFrames.size() + " frames.");
        } else {
            Gdx.app.error(TAG, "No fire animation frames found in the atlas!");
        }

        _animation = new Animation<>(FRAME_DURATION, frames.toArray(new TextureRegion[0]));
        _animation.setPlayMode(Animation.PlayMode.LOOP);

        _collisionCircle = new Circle(_x, _y, COLLISION_RADIUS);
    }

    public Circle getCollisionCircle() {
        return _collisionCircle;
    }

    public void startFiring() {
        _isFiring = true;
        _fireTimer = 0;
    }

    public void reset() {
        _isDead = false;
        _deathTimer = 0;
    }

    public void fire() {
        _isFiring = true;
        _fireAnimationTimer = 0;
    }

    public void update(float delta) {
        if (_isDead) {
            _deathTimer += delta;
            if (_deathAnimation != null && _deathAnimation.isAnimationFinished(_deathTimer)) {
                _gameScreen.setGameOver();
            }
            return;
        }

        if (_isFiring) {
            _fireAnimationTimer += delta;
            if (_fireAnimation.isAnimationFinished(_fireAnimationTimer)) {
                _isFiring = false;
            }
        }

        _animationTimer += delta;
        _x += _xSpeed * delta;
        _y += _ySpeed * delta;
        _collisionCircle.setPosition(_x, _y);
    }

    public void setVelocity(float vx, float vy) {
        this._xSpeed = vx;
        this._ySpeed = vy;
    }

    public void moveBy(float dx, float dy) {
        this._x += dx;
        this._y += dy;
        _collisionCircle.setPosition(_x, _y);
    }

    public void setPosition(float x, float y) {
        this._x = x;
        this._y = y;
        _collisionCircle.setPosition(x, y);
    }

    public float getX() { return _x; }
    public float getY() { return _y; }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (_isDead) {
            if (_deathAnimation != null && _deathAnimation.isAnimationFinished(_deathTimer)) {
                return;
            }
            currentFrame = _deathAnimation.getKeyFrame(_deathTimer);
        } else if (_isFiring && _fireAnimation != null) {
            currentFrame = _fireAnimation.getKeyFrame(_fireAnimationTimer);
        } else {
            currentFrame = _animation.getKeyFrame(_animationTimer);
        }

        float drawX = _collisionCircle.x - (currentFrame.getRegionWidth() * SCALE_FACTOR) / 2;
        float drawY = _collisionCircle.y - (currentFrame.getRegionHeight() * SCALE_FACTOR) / 2;

        batch.draw(currentFrame, drawX, drawY, currentFrame.getRegionWidth() * SCALE_FACTOR, currentFrame.getRegionHeight() * SCALE_FACTOR);
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(_collisionCircle.x, _collisionCircle.y, _collisionCircle.radius);
    }

    public void updateWithGyro() {
        try {
            float threshold = 5f;
            float ax = Gdx.input.getAccelerometerX();
            float ay = Gdx.input.getAccelerometerY();
            float az = Gdx.input.getAccelerometerZ();

            float pitch = MathUtils.atan2(-ax, (float) Math.sqrt(ay * ay + az * az)) * MathUtils.radiansToDegrees;
            float roll = MathUtils.atan2(ay, az) * MathUtils.radiansToDegrees;

            float speed = 200f;
            if (Math.abs(pitch) > Math.abs(roll)) {
                float newYVelocity = (pitch > threshold ? speed : (pitch < -threshold ? -speed : 0));
                setVelocity(0, newYVelocity);
            } else {
                float newXVelocity = (roll > threshold ? speed : (roll < -threshold ? -speed : 0));
                setVelocity(newXVelocity, 0);
            }

        } catch (Exception e) {
            Gdx.app.error(TAG, "Error in updateWithGyro(): " + e.getMessage(), e);
        }
    }

    public Missile tirer() {
        fire();
        TextureRegion bulletRegion = _atlas.findRegion("Bullet (1)");
        return new Missile(_x, _y, 400, 0, true, bulletRegion);
    }

    public void die() {
        if (_isDead) return;
        _isDead = true;
        _deathTimer = 0;
    }
}
