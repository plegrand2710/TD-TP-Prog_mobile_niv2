package com.TD4.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.*;

public class Roquet {
    private final GameScreen _gameScreen;
    private final Animation<TextureRegion> _animation;
    private float _animationTimer = 0f;
    private float _scaleFactor;
    private static final float _COLLISION_WIDTH = 100f;
    private static final float _COLLISION_LENGTH = 500f;

    private final Animation<TextureRegion> _explosionAnimation;
    private boolean _isExploding = false;
    private float _explosionTimer = 0f;

    private Body _body;

    public Roquet(float x, float y, float speedX, float speedY, Animation<TextureRegion> animation, Animation<TextureRegion> explosionAnimation, GameScreen gameScreen) {
        this._gameScreen = gameScreen;
        this._animation = animation;
        this._explosionAnimation = explosionAnimation;
        this._scaleFactor = 0.2f;

        createBody(x, y, speedX, speedY);
    }

    private void createBody(float x, float y, float speedX, float speedY) {
        World world = _gameScreen.getWorld();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x / 100f, y / 100f);
        bodyDef.linearVelocity.set(speedX / 100f, speedY / 100f);

        _body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((_COLLISION_LENGTH * _scaleFactor) / 200f, (_COLLISION_WIDTH * _scaleFactor) / 200f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.3f;
        fixtureDef.isSensor = false;

        _body.createFixture(fixtureDef);
        shape.dispose();

        _body.setUserData(this);
        _body.setSleepingAllowed(false);

    }

    public void update(float delta) {
        if (_isExploding) {
            _explosionTimer += delta;
            return;
        }

        _animationTimer += delta;
    }

    public void explode() {
        if (!_isExploding) {
            _isExploding = true;
            _explosionTimer = 0f;

            _body.setLinearVelocity(0, 0);
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

        float drawX = _body.getPosition().x * 100 - width / 2;
        float drawY = _body.getPosition().y * 100 - height / 2;

        batch.draw(currentFrame, drawX, drawY, width, height);
    }

    public void drawDebug(ShapeRenderer sr) {
        sr.setColor(Color.PINK);

        if (!_isExploding) {
            sr.rect(_body.getPosition().x * 100 - (_COLLISION_LENGTH * _scaleFactor) / 2,
                _body.getPosition().y * 100 - (_COLLISION_WIDTH * _scaleFactor) / 2,
                _COLLISION_LENGTH * _scaleFactor, _COLLISION_WIDTH * _scaleFactor);
        } else {
            float explosionRadius = (_COLLISION_WIDTH * 1.5f) / 2;
            sr.setColor(Color.ORANGE);
            sr.circle(_body.getPosition().x * 100, _body.getPosition().y * 100, explosionRadius);
        }
    }

    public boolean isFinishedExploding() {
        return _isExploding && _explosionAnimation.isAnimationFinished(_explosionTimer);
    }

    public Body getBody() {
        return _body;
    }

    public float getWidth() {
        TextureRegion currentFrame = _animation.getKeyFrame(_animationTimer);
        return currentFrame.getRegionWidth() * _scaleFactor;
    }

    public void drawExplosion(SpriteBatch batch) {
        if (!_isExploding) return;

        TextureRegion explosionFrame = _explosionAnimation.getKeyFrame(_explosionTimer);
        float explosionSize = _COLLISION_WIDTH * 1.5f;

        float drawX = _body.getPosition().x * 100 - explosionSize / 2;
        float drawY = _body.getPosition().y * 100 - explosionSize / 2;

        batch.draw(explosionFrame, drawX, drawY, explosionSize, explosionSize);
    }

    /** ✅ Vérifie la collision avec un missile */
    public boolean collidesWith(Missile missile) {
        boolean collision = _gameScreen.checkCollision(_body, missile.getBody());
        if (collision) {
            explode();
        }
        return collision;
    }

    /** 🗑️ Supprime proprement le missile de Box2D */
    public void destroy(World world) {
        if (_body != null) {
            world.destroyBody(_body);
            _body = null;
        }
    }
}
