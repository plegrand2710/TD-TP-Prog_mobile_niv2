package com.TD4.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;

public class Planete {
    private static final float _MAX_SPEED = 100f;
    private float _x = 0;
    private boolean _pointClaimed = false;
    private float _rotation = 0f;
    private final String _name;
    private final Circle _collisionCircle;
    private final TextureRegion _planetRegion;
    private float _scaleFactor = 1.0f;

    public Planete(TextureRegion planetRegion) {
        this._planetRegion = planetRegion;

        if (planetRegion instanceof TextureAtlas.AtlasRegion) {
            this._name = ((TextureAtlas.AtlasRegion) planetRegion).name;
        } else {
            this._name = "Unknown Planet";
        }

        float radius = (planetRegion.getRegionWidth() / 2f) * _scaleFactor;
        _collisionCircle = new Circle(0, 0, radius);
    }

    public void update(float delta) {
        setPosition(_x - (_MAX_SPEED * delta), _collisionCircle.y);
        _rotation += 20 * delta;
    }

    public void setScale(float scale) {
        this._scaleFactor = scale;
        _collisionCircle.setRadius((_planetRegion.getRegionWidth() / 2f) * _scaleFactor);
    }

    public void setPosition(float x, float y) {
        this._x = x;
        _collisionCircle.setPosition(x, y);
    }

    public void randomizePositionAndSize(float worldWidth, float worldHeight) {
        float positionX = worldWidth + _planetRegion.getRegionWidth() * _scaleFactor;
        float positionY = MathUtils.random(0, worldHeight - _planetRegion.getRegionHeight() * _scaleFactor);
        setPosition(positionX, positionY);
    }

    public void draw(SpriteBatch batch) {
        float drawX = _collisionCircle.x - (_planetRegion.getRegionWidth() * _scaleFactor) / 2;
        float drawY = _collisionCircle.y - (_planetRegion.getRegionHeight() * _scaleFactor) / 2;

        batch.draw(
            _planetRegion,
            drawX,
            drawY,
            _planetRegion.getRegionWidth() * _scaleFactor,
            _planetRegion.getRegionHeight() * _scaleFactor
        );
    }

    public void drawDebug(ShapeRenderer sr) {
        sr.setColor(Color.YELLOW);
        sr.circle(_collisionCircle.x, _collisionCircle.y, _collisionCircle.radius);
    }

    public boolean isCosmonauteColliding(Cosmonaute cosmo) {
        return Intersector.overlaps(cosmo.getCollisionCircle(), _collisionCircle);
    }

    public float getX() {
        return _x;
    }

    public boolean isPointClaimed() {
        return _pointClaimed;
    }

    public void markPointClaimed() {
        _pointClaimed = true;
    }

    public float getWidth() {
        return _planetRegion.getRegionWidth() * _scaleFactor;
    }
}
