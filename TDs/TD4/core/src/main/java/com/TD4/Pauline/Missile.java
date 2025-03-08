package com.TD4.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.Timer;

public class Missile {
    private float _x, _y, _speedX, _speedY;
    private final boolean _fromPlayer;
    private final TextureRegion _region;
    private final Circle _collisionCircle;
    private static final float _RADIUS = 10f;
    private static final float _SCALE_FACTOR = 0.5f;
    private Music _backgroundMusic;

    public Missile(float x, float y, float speedX, float speedY, boolean fromPlayer, TextureRegion region) {
        this._x = x;
        this._y = y;
        this._speedX = speedX;
        this._speedY = speedY;
        this._fromPlayer = fromPlayer;
        this._region = region;
        _collisionCircle = new Circle(x, y, _RADIUS);
        _backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("fireSound.wav"));
        _backgroundMusic.setLooping(false);
        _backgroundMusic.setVolume(1.2f);
        _backgroundMusic.play();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (_backgroundMusic != null) {
                    _backgroundMusic.stop();
                    _backgroundMusic.dispose();
                    _backgroundMusic = null;
                }
            }
        }, 5);
    }

    public void update(float delta) {
        _x += _speedX * delta;
        _y += _speedY * delta;
        _collisionCircle.setPosition(_x, _y);
    }

    public void draw(SpriteBatch batch) {
        float width = _region.getRegionWidth() * _SCALE_FACTOR;
        float height = _region.getRegionHeight() * _SCALE_FACTOR;
        batch.draw(_region, _x - width / 2f, _y - height / 2f, width, height);
    }

    public Circle getCollisionCircle() {
        return _collisionCircle;
    }

    public boolean isFromPlayer() {
        return _fromPlayer;
    }
}
