package com.TD4.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Planete {
    private GameScreen _gameScreen;
    private static final float _MAX_SPEED = 100f;
    private boolean _pointClaimed = false;
    private float _rotation = 0f;
    private final String _name;
    private final TextureRegion _planetRegion;
    private float _scaleFactor;
    private Body _body;

    public Planete(TextureRegion planetRegion, GameScreen gameScreen, float scaleFactor) {
        this._planetRegion = planetRegion;
        _gameScreen = gameScreen;
        _scaleFactor = scaleFactor;
        if (planetRegion instanceof TextureAtlas.AtlasRegion) {
            this._name = ((TextureAtlas.AtlasRegion) planetRegion).name;
        } else {
            this._name = "Unknown Planet";
        }

        createBody();
    }

    private void createBody() {
        float textureWidth = _planetRegion.getRegionWidth() * _scaleFactor;

        float radius = ((textureWidth/ 100) / 2f) ;
        Gdx.app.log("DEBUG", "creation corps planete with Radius: " + radius + " texture width : " + textureWidth + "sacel factor : " + _scaleFactor);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 0);

        _body = _gameScreen.getWorld().createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.1f;

        _body.createFixture(fixtureDef);
        shape.dispose();

        _body.setUserData(this);
        _body.setSleepingAllowed(false);
    }

    public void update(float delta) {
        _rotation += 20 * delta;

        float newX = _body.getPosition().x * 100 - (_MAX_SPEED * delta);
        _body.setTransform(newX / 100f, _body.getPosition().y, 0);
    }

    public void setScale(float scale) {
        this._scaleFactor = scale;
    }

    public void setPosition(float x, float y) {
        _body.setTransform(x / 100f, y / 100f, 0);
    }

    public void randomizePositionAndSize(float worldWidth, float worldHeight) {
        float positionX = worldWidth + _planetRegion.getRegionWidth() * _scaleFactor;
        float positionY = MathUtils.random(0, worldHeight - _planetRegion.getRegionHeight() * _scaleFactor);
        setPosition(positionX, positionY);
    }

    public void draw(SpriteBatch batch) {
        float drawX = _body.getPosition().x * 100 - (_planetRegion.getRegionWidth() * _scaleFactor) / 2;
        float drawY = _body.getPosition().y * 100 - (_planetRegion.getRegionHeight() * _scaleFactor) / 2;

        batch.draw(
            _planetRegion,
            drawX, drawY,
            _planetRegion.getRegionWidth() * _scaleFactor,
            _planetRegion.getRegionHeight() * _scaleFactor
        );
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        float centerX = _body.getPosition().x * 100;
        float centerY = _body.getPosition().y * 100;
        float radius = _body.getFixtureList().first().getShape().getRadius() * 100;

        shapeRenderer.circle(centerX, centerY, radius);
        shapeRenderer.end();
    }

    public float getX() {
        return _body.getPosition().x * 100;
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

    /** 🔥 Supprime la planète du monde Box2D proprement */
    public void destroy(World world) {
        if (_body != null) {
            world.destroyBody(_body);
            _body = null;
        }
    }
}
