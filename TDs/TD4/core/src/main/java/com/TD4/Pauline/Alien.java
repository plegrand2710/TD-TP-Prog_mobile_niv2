package com.TD4.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.utils.Timer;

public class Alien {
    private static final float _WORLD_HEIGHT = 640f;
    private static final float _DEATH_ANIMATION_DURATION = 0.5f;

    private GameScreen _gameScreen;
    private boolean _movingLeft;
    private float _speed, _verticalSpeed, _scale;
    private boolean _isDying = false;
    private float _deathTimer = 0f;
    private float _animationTimer = 0f;

    private Music _explosionMusic;

    private final Animation<TextureRegion> _deathAnimation;
    private final Animation<TextureRegion> _flyAnimation;

    private Body _body;

    public Alien(Animation<TextureRegion> flyAnimation, float x, float y, float speed, boolean movingLeft, float scale, float verticalSpeed, Animation<TextureRegion> deathAnimation, GameScreen gameScreen) {
        Gdx.app.log("SpaceWarriorApp", "Constructeur Alien");

        _gameScreen = gameScreen;
        _flyAnimation = flyAnimation;
        _speed = speed;
        _movingLeft = movingLeft;
        _scale = scale;
        _verticalSpeed = verticalSpeed;
        _deathAnimation = deathAnimation;

        createBody(x, y);
    }

    public Alien(Animation<TextureRegion> flyAnimation, float x, float y, float speed, boolean movingLeft, Animation<TextureRegion> deathAnimation, GameScreen gameScreen) {
        this(flyAnimation, x, y, speed, movingLeft, 0.2f, 0f, deathAnimation, gameScreen);
    }

    private void createBody(float x, float y) {
        World world = _gameScreen.getWorld();
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x / 100f, y / 100f);

        _body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(15f / 100f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.3f;
        fixtureDef.isSensor = false;

        _body.createFixture(fixtureDef);
        shape.dispose();

        _body.setUserData(this);
        _body.setSleepingAllowed(false);
    }

    public void die() {
        if (!_isDying) {
            _isDying = true;
            _deathTimer = 0f;
            playExplosionSound();

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

    private void playExplosionSound() {
        _explosionMusic = Gdx.audio.newMusic(Gdx.files.internal("explosionSound.wav"));
        _explosionMusic.setLooping(false);
        _explosionMusic.setVolume(1.2f);
        _explosionMusic.setPosition(3f);
        _explosionMusic.play();
    }

    public boolean isFinishedExploding() {
        return _isDying && _deathTimer >= _DEATH_ANIMATION_DURATION;
    }

    public void update(float delta) {
        if (_isDying) {
            _deathTimer += delta;
            return;
        }

        _animationTimer += delta;
        float velocityX = (_movingLeft ? -_speed : _speed) / 100f;
        float velocityY = _verticalSpeed / 100f;

        _body.setLinearVelocity(velocityX, velocityY);

        if (MathUtils.randomBoolean(0.01f)) {
            _verticalSpeed = MathUtils.random(-50, 50);
        }
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (_isDying) {
            currentFrame = (_deathAnimation != null) ? _deathAnimation.getKeyFrame(_deathTimer, false) : _flyAnimation.getKeyFrame(_animationTimer, true);
        } else {
            currentFrame = _flyAnimation.getKeyFrame(_animationTimer, true);
        }

        float drawX = _body.getPosition().x * 100 - (currentFrame.getRegionWidth() * _scale) / 2;
        float drawY = _body.getPosition().y * 100 - (currentFrame.getRegionHeight() * _scale) / 2;

        batch.draw(currentFrame, drawX, drawY, currentFrame.getRegionWidth() * _scale, currentFrame.getRegionHeight() * _scale);
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(_body.getPosition().x * 100, _body.getPosition().y * 100, 15f);
    }

    public float getX() { return _body.getPosition().x * 100; }
    public float getY() { return _body.getPosition().y * 100; }
    public float getScale() { return _scale; }

    public Body getBody() {
        return _body;
    }
    public TextureRegion getCurrentRegion() {
        if (_flyAnimation == null) {
            Gdx.app.error("Alien", "flyAnimation is null!");
            return null;
        }
        return _flyAnimation.getKeyFrame(_animationTimer, true);
    }
}
