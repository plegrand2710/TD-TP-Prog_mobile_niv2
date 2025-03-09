package com.TD4.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;

public class Missile {
    private final boolean _fromPlayer;
    private final TextureRegion _region;
    private static final float _SCALE_FACTOR = 0.5f;
    private static final float _RADIUS = 10f;
    private GameScreen _gameScreen;
    private Body _body;
    private Music _backgroundMusic;
    private boolean _isDestroyed = false;
    private Music _explosionMusic;


    public Missile(float x, float y, float speedX, float speedY, boolean fromPlayer, TextureRegion region, World world, GameScreen gameScreen) {
        this._gameScreen = gameScreen;
        this._fromPlayer = fromPlayer;
        this._region = region;

        createBody(x, y, speedX, speedY, world);

        playFireSound();

    }

    private void createBody(float x, float y, float speedX, float speedY, World world) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x / 100f, y / 100f);
        bodyDef.bullet = true;

        _body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(_RADIUS / 100f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;
        fixtureDef.isSensor = true;
        _body.createFixture(fixtureDef);
        shape.dispose();

        _body.setLinearVelocity(speedX / 100f, speedY / 100f);
        _body.setUserData(this);
        _body.setSleepingAllowed(false);

    }

    private void playFireSound() {
        _backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("fireSound.wav"));
        _backgroundMusic.setLooping(false);
        _backgroundMusic.setVolume(1.2f);
        _backgroundMusic.play();

    }

    public void die() {
        if (!_isDestroyed) {
            _isDestroyed = true;
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
            Gdx.app.log("SpaceWarriorApp1", "missile is dying, animation started.");
        }
    }

    private void playExplosionSound() {
        _explosionMusic = Gdx.audio.newMusic(Gdx.files.internal("explosionSound.wav"));
        _explosionMusic.setLooping(false);
        _explosionMusic.setVolume(1.2f);
        _explosionMusic.setPosition(3f);
        _explosionMusic.play();
    }

    public void draw(SpriteBatch batch) {
        if (_isDestroyed) return;

        float drawX = _body.getPosition().x * 100 - (_region.getRegionWidth() * _SCALE_FACTOR) / 2;
        float drawY = _body.getPosition().y * 100 - (_region.getRegionHeight() * _SCALE_FACTOR) / 2;

        batch.draw(_region, drawX, drawY, _region.getRegionWidth() * _SCALE_FACTOR, _region.getRegionHeight() * _SCALE_FACTOR);
    }

    public boolean getisDestroyed() {
        return _isDestroyed;
    }

    public Body getBody() {
        return _body;
    }

}
