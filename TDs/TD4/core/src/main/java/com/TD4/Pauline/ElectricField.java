package com.TD4.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.*;

public class ElectricField {
    private final float _width, _height;
    private final float _speed;
    private final Animation<TextureRegion> _animation;
    private float _animationTimer = 0f;

    private Body _body;

    public ElectricField(Animation<TextureRegion> animation, float x, float y, float width, float height, float speed, World world) {
        this._animation = animation;
        this._width = width;
        this._height = height;
        this._speed = speed;

        createBody(x, y, width, height, world);
    }

    /** 🔧 Crée le corps Box2D pour le champ électrique */
    private void createBody(float x, float y, float width, float height, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x / 100f, y / 100f);

        _body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((width * 0.6f) / 200f, (height * 0.7f) / 200f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;

        _body.createFixture(fixtureDef);
        shape.dispose();

        _body.setLinearVelocity(-_speed / 100f, 0);
        _body.setUserData(this);
        _body.setSleepingAllowed(false);

    }

    public void update(float delta) {
        _animationTimer += delta;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = _animation.getKeyFrame(_animationTimer, true);
        float drawX = _body.getPosition().x * 100 - _width / 2;
        float drawY = _body.getPosition().y * 100 - _height / 2;

        batch.draw(currentFrame, drawX, drawY, _width, _height);
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(
            (_body.getPosition().x * 100) - (_width * 0.6f) / 2,
            (_body.getPosition().y * 100) - (_height * 0.7f) / 2,
            _width * 0.6f,
            _height * 0.7f
        );
    }

    /** 📌 Plus besoin de `collidesWith()` → Box2D gère ça via `ContactListener` */

    public boolean isOutOfScreen() {
        return _body.getPosition().x * 100 + _width < 0;
    }

    /** 🚀 Supprime le champ du monde physique proprement */
    public void destroy(World world) {
        world.destroyBody(_body);
    }
}
