package com.TD4.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

public class Missile {
    private final boolean _fromPlayer;
    private final TextureRegion _region;
    private static final float _SCALE_FACTOR = 0.5f;
    private static final float _RADIUS = 10f;

    private Body _body;
    private Music _backgroundMusic;
    private boolean _isDestroyed = false;

    public Missile(float x, float y, float speedX, float speedY, boolean fromPlayer, TextureRegion region, World world) {
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

        Gdx.app.postRunnable(() -> {
            try {
                Thread.sleep(5000);
                if (_backgroundMusic != null) {
                    _backgroundMusic.stop();
                    _backgroundMusic.dispose();
                    _backgroundMusic = null;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void update(float delta) {
        if (_isDestroyed) return;

        float posX = _body.getPosition().x * 100;
        float posY = _body.getPosition().y * 100;

        if (posX > Gdx.graphics.getWidth() || posX < 0 || posY > Gdx.graphics.getHeight() || posY < 0) {
            destroy(_body.getWorld());
        }
    }

    public void draw(SpriteBatch batch) {
        if (_isDestroyed) return;

        float drawX = _body.getPosition().x * 100 - (_region.getRegionWidth() * _SCALE_FACTOR) / 2;
        float drawY = _body.getPosition().y * 100 - (_region.getRegionHeight() * _SCALE_FACTOR) / 2;

        batch.draw(_region, drawX, drawY, _region.getRegionWidth() * _SCALE_FACTOR, _region.getRegionHeight() * _SCALE_FACTOR);
    }

    /** 🚀 Supprime le missile du monde Box2D proprement */
    public void destroy(World world) {
        if (!_isDestroyed) {
            world.destroyBody(_body);
            _isDestroyed = true;
        }
    }

    public boolean isDestroyed() {
        return _isDestroyed;
    }
    public Body getBody() {
        return _body;
    }

    public boolean isFromPlayer() {
        return _fromPlayer;
    }
}
