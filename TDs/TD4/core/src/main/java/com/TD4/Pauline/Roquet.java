package com.TD4.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class Roquet {
    private float _x, _y, _speedX, _speedY;
    private final Animation<TextureRegion> _animation;
    private float _animationTimer = 0f;
    private float _scaleFactor;
    private final Rectangle _collisionRect;
    private static final float _COLLISION_WIDTH = 100f;
    private static final float _COLLISION_LENGTH = 500f;

    private Animation<TextureRegion> _explosionAnimation;
    private boolean _isExploding = false;
    private float _explosionTimer = 0f;


    public Roquet(float x, float y, float speedX, float speedY, Animation<TextureRegion> animation, Animation<TextureRegion> explosionAnimation) {
        this._x = x;
        this._y = y;
        this._speedX = speedX;
        this._speedY = speedY;
        this._animation = animation;
        this._explosionAnimation = explosionAnimation;
        this._scaleFactor = 0.2f;

        this._collisionRect = new Rectangle(
            x - (_COLLISION_LENGTH * _scaleFactor) / 2,
            y - (_COLLISION_WIDTH * _scaleFactor) / 2,
            _COLLISION_LENGTH * _scaleFactor,
            _COLLISION_WIDTH * _scaleFactor
        );
    }


    public void update(float delta) {
        if (_isExploding) {
            _explosionTimer += delta;
            return;
        }

        _x += _speedX * delta;
        _y += _speedY * delta;
        _animationTimer += delta;
        _collisionRect.setPosition(
            _x - _collisionRect.width / 2f,
            _y - _collisionRect.height / 2f
        );
    }

    public void explode() {
        if (!_isExploding) {
            _isExploding = true;
            _explosionTimer = 0f;
            _speedX = 0;
            _speedY = 0;
        }
    }

    public boolean isExploding() {
        return _isExploding;
    }

    public void draw(SpriteBatch batch) {
        if (_isExploding) return;

        TextureRegion currentFrame = _animation.getKeyFrame(_animationTimer);
        float width = currentFrame.getRegionWidth() * _scaleFactor;
        float height = currentFrame.getRegionHeight() * _scaleFactor;
        batch.draw(currentFrame, _x - width / 2, _y - height / 2, width, height);
    }

    public void drawDebug(ShapeRenderer sr) {
        sr.setColor(Color.PINK);

        if (!_isExploding) {
            sr.rect(_collisionRect.x, _collisionRect.y, _collisionRect.width, _collisionRect.height);
        } else {
            float explosionRadius = _collisionRect.width * 0.5f;
            sr.setColor(Color.ORANGE);
            sr.circle(_x, _y, explosionRadius);
        }
    }

    public boolean isFinishedExploding() {
        return _isExploding && _explosionAnimation.isAnimationFinished(_explosionTimer);
    }

    public void drawExplosion(SpriteBatch batch) {
        if (!_isExploding) return;

        TextureRegion explosionFrame = _explosionAnimation.getKeyFrame(_explosionTimer);
        float explosionSize = _COLLISION_WIDTH * 1.5f;
        batch.draw(explosionFrame, _x - explosionSize / 2, _y - explosionSize / 2, explosionSize, explosionSize);
    }

    public boolean collidesWith(Missile missile) {
        boolean collision = Intersector.overlaps(missile.getCollisionCircle(), this.getCollisionRect());
        if (collision) {
            explode();
        }
        return collision;
    }

    public Rectangle getCollisionRect() {
        return _collisionRect;
    }
}
