package com.TD4.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;

public class Alien {
    private float _x, _y, _speed, _verticalSpeed;
    private boolean _movingLeft;
    private float _scale;
    private static final float _WORLD_HEIGHT = 640f;
    private Music _explosionMusic;
    private boolean _isDying = false;
    private float _deathTimer = 0f;
    private static final float _DEATH_ANIMATION_DURATION = 0.5f;
    private final Animation<TextureRegion> _deathAnimation;
    private final Animation<TextureRegion> _flyAnimation;
    private float _animationTimer = 0f;

    public Alien(Animation<TextureRegion> flyAnimation, float x, float y, float speed, boolean movingLeft, float scale, float verticalSpeed, Animation<TextureRegion> deathAnimation) {
        Gdx.app.log("SpaceWarriorApp", "constructeur ok.");

        this._flyAnimation = flyAnimation;
        this._x = x;
        this._y = y;
        this._speed = speed;
        this._verticalSpeed = verticalSpeed;
        this._movingLeft = movingLeft;
        this._scale = scale;
        this._deathAnimation = deathAnimation;
    }

    public Alien(Animation<TextureRegion> flyAnimation, float x, float y, float speed, boolean movingLeft, Animation<TextureRegion> deathAnimation) {
        this(flyAnimation, x, y, speed, movingLeft, 0.2f, 0f, deathAnimation);
    }

    public void die() {
        if (!_isDying) {
            _isDying = true;
            _deathTimer = 0f;
            _explosionMusic = Gdx.audio.newMusic(Gdx.files.internal("explosionSound.wav"));
            _explosionMusic.setLooping(false);
            _explosionMusic.setVolume(1.2f);
            _explosionMusic.setPosition(3f);
            _explosionMusic.play();

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (_explosionMusic != null) {
                        _explosionMusic.stop();
                        _explosionMusic.dispose();
                        _explosionMusic = null;
                    }
                }
            }, 5);
            Gdx.app.log("Alien", "Alien is dying, animation started.");
        }
    }

    public boolean isFinishedExploding() {
        return _isDying && _deathTimer >= _DEATH_ANIMATION_DURATION;
    }

    public boolean collidesWith(Cosmonaute cosmo) {
        TextureRegion region = getCurrentRegion();
        float alienWidth = region.getRegionWidth() * _scale;
        float alienHeight = region.getRegionHeight() * _scale;

        float centerX = _x + alienWidth / 2;
        float centerY = _y + alienHeight / 2;

        float alienRadius = Math.min(alienWidth, alienHeight) / 3f;

        float dx = centerX - cosmo.getX();
        float dy = centerY - cosmo.getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        return distance < (alienRadius + cosmo.getCollisionCircle().radius);
    }

    public void update(float delta) {
        if (_isDying) {
            _deathTimer += delta;
            return;
        }

        _animationTimer += delta;
        _x += (_movingLeft ? -_speed : _speed) * delta;
        _y += _verticalSpeed * delta;

        if (MathUtils.randomBoolean(0.01f)) {
            _verticalSpeed = MathUtils.random(-50, 50);
        }
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;
        Gdx.app.log("SpaceWarriorApp", "Drawing alien at (" + _x + ", " + _y + ") with animation.");

        if (_isDying) {
            if (_deathAnimation != null) {
                currentFrame = _deathAnimation.getKeyFrame(_deathTimer, false);
            } else {
                Gdx.app.error("Alien", "deathAnimation is null! Falling back to flyAnimation.");
                currentFrame = _flyAnimation.getKeyFrame(_animationTimer, true);
            }
        } else {
            currentFrame = _flyAnimation.getKeyFrame(_animationTimer, true);
        }

        batch.draw(currentFrame, _x, _y, currentFrame.getRegionWidth() * _scale, currentFrame.getRegionHeight() * _scale);
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);

        TextureRegion region = getCurrentRegion();
        float alienWidth = region.getRegionWidth() * _scale;
        float alienHeight = region.getRegionHeight() * _scale;

        float centerX = _x + alienWidth / 2;
        float centerY = _y + alienHeight / 2;
        float alienRadius = Math.min(alienWidth, alienHeight) / 3f;

        shapeRenderer.circle(centerX, centerY, alienRadius);
    }

    public float getX() { return _x; }
    public float getY() { return _y; }
    public float getScale() { return _scale; }

    public TextureRegion getCurrentRegion() {
        if (_flyAnimation == null) {
            Gdx.app.error("Alien", "flyAnimation is null!");
            return null;
        }
        return _flyAnimation.getKeyFrame(_animationTimer, true);
    }
}
