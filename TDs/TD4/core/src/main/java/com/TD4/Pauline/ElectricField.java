package com.TD4.Pauline;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class ElectricField {
    private float _x, _y;
    private float _width, _height;
    private float _speed;
    private Rectangle _hitbox;

    private final Animation<TextureRegion> _animation;
    private float _animationTimer = 0f;

    public ElectricField(Animation<TextureRegion> animation, float x, float y, float width, float height, float speed) {
        this._animation = animation;
        this._x = x;
        this._y = y;
        this._width = width;
        this._height = height;
        this._speed = speed;

        float hitboxWidth = width * 0.6f;
        float hitboxHeight = height * 0.7f;

        float hitboxX = x + (width - hitboxWidth) / 2;
        float hitboxY = y + (height - hitboxHeight) / 2;

        this._hitbox = new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    public void update(float delta) {
        _x -= _speed * delta;
        _hitbox.setPosition(_x + (_width - _hitbox.width) / 2, _y + (_height - _hitbox.height) / 2);
        _animationTimer += delta;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = _animation.getKeyFrame(_animationTimer, true);

        batch.draw(currentFrame,
            _x, _y,
            _width / 2, _height / 2,
            _width, _height,
            1, 1,
            90
        );
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(_hitbox.x, _hitbox.y, _hitbox.width, _hitbox.height);
    }

    public boolean collidesWith(Cosmonaute cosmo) {
        return Intersector.overlaps(cosmo.getCollisionCircle(), _hitbox);
    }

    public boolean isOutOfScreen() {
        return _x + _width < 0;
    }
}
