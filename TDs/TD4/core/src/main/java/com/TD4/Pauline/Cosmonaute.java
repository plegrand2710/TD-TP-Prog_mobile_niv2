package com.TD4.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
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

import java.util.ArrayList;
import java.util.Comparator;

public class Cosmonaute {
    private static final float FRAME_DURATION = 0.25f;
    private static final float COLLISION_RADIUS = 50f;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG1 = true;
    private static final String TAG = "SpaceWarriorApp";
    private static final String TAG1 = "SpaceWarriorApp2";

    private static final float SCALE_FACTOR = 0.3f;

    private boolean _isDead = false;
    private boolean _isFiring = false;
    private float _fireAnimationTimer = 0;
    private float _deathTimer = 0;
    private float _animationTimer = 0;

    private final GameScreen _gameScreen;
    private final Animation<TextureRegion> _animation;
    private final TextureAtlas _atlas;
    private Animation<TextureRegion> _fireAnimation;
    private Animation<TextureRegion> _deathAnimation;

    private Body _body;

    public Cosmonaute(TextureAtlas atlas, GameScreen gameScreen) {
        this._gameScreen = gameScreen;
        _atlas = atlas;

        createBody();
        ArrayList<TextureRegion> frames = new ArrayList<>();
        loadAnimations(frames);
        _animation = new Animation<>(FRAME_DURATION, frames.toArray(new TextureRegion[0]));
        _animation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public float getRadius() {
        return COLLISION_RADIUS / 100f;
    }

    private void createBody() {
        World world = _gameScreen.getWorld();
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(1.5f, 2f);

        _body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(COLLISION_RADIUS / 100f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.2f;

        _body.createFixture(fixtureDef);
        shape.dispose();

        _body.setUserData(this);
        _body.setSleepingAllowed(false);

    }

    private void loadAnimations(ArrayList<TextureRegion> frames) {
        if (DEBUG1) Gdx.app.log(TAG, "Loading animation frames from atlas...");

        for (TextureAtlas.AtlasRegion region : _atlas.getRegions()) {
            if (region.name.startsWith("Character Fly")) {
                frames.add(region);
            }
        }

        ArrayList<TextureRegion> deathFrames = new ArrayList<>();
        for (TextureAtlas.AtlasRegion region : _atlas.getRegions()) {
            if (region.name.startsWith("Character Death")) {
                deathFrames.add(region);
            }
        }
        deathFrames.sort(Comparator.comparing(TextureRegion::toString));

        if (!deathFrames.isEmpty()) {
            _deathAnimation = new Animation<>(0.05f, deathFrames.toArray(new TextureRegion[0]));
            _deathAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        } else {
            Gdx.app.error(TAG, "No death animation frames found in the atlas!");
        }

        ArrayList<TextureRegion> fireFrames = new ArrayList<>();
        for (TextureAtlas.AtlasRegion region : _atlas.getRegions()) {
            if (region.name.startsWith("Character Fire")) {
                fireFrames.add(region);
            }
        }
        fireFrames.sort(Comparator.comparing(TextureRegion::toString));

        if (!fireFrames.isEmpty()) {
            _fireAnimation = new Animation<>(0.05f, fireFrames.toArray(new TextureRegion[0]));
            _fireAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        } else {
            Gdx.app.error(TAG, "No fire animation frames found in the atlas!");
        }
    }

    public Body getBody() {
        return _body;
    }

    public void update(float delta) {
        _animationTimer += delta;

        if (_isDead) {
            _deathTimer += delta;
            if (_deathAnimation != null && _deathAnimation.isAnimationFinished(_deathTimer)) {
                _gameScreen.setGameOver();
            }
            return;
        }

        if (_isFiring) {
            _fireAnimationTimer += delta;
            if (_fireAnimation.isAnimationFinished(_fireAnimationTimer)) {
                _isFiring = false;
            }
        }
    }

    public void setVelocity(float vx, float vy) {
        _body.setLinearVelocity(vx / 100f, vy / 100f);
    }

    public void setPosition(float x, float y) {
        _body.setTransform(x / 100f, y / 100f, _body.getAngle());
    }

    public float getX() {
        return _body.getPosition().x * 100;
    }

    public float getY() {
        return _body.getPosition().y * 100;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (_isDead) {
            currentFrame = _deathAnimation.getKeyFrame(_deathTimer);
        } else if (_isFiring && _fireAnimation != null) {
            currentFrame = _fireAnimation.getKeyFrame(_fireAnimationTimer);
        } else {
            currentFrame = _animation.getKeyFrame(_animationTimer);
        }

        float drawX = getX() - (currentFrame.getRegionWidth() * SCALE_FACTOR) / 2;
        float drawY = getY() - (currentFrame.getRegionHeight() * SCALE_FACTOR) / 2;

        batch.draw(currentFrame, drawX, drawY, currentFrame.getRegionWidth() * SCALE_FACTOR, currentFrame.getRegionHeight() * SCALE_FACTOR);
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.circle(getX(), getY(), COLLISION_RADIUS);
        shapeRenderer.end();
    }

    public void updateWithGyro() {
        float threshold = 5f;
        float ax = Gdx.input.getAccelerometerX();
        float ay = Gdx.input.getAccelerometerY();
        float az = Gdx.input.getAccelerometerZ();

        float pitch = MathUtils.atan2(-ax, (float) Math.sqrt(ay * ay + az * az)) * MathUtils.radiansToDegrees;
        float roll = MathUtils.atan2(ay, az) * MathUtils.radiansToDegrees;

        float speed = 200f;
        if (Math.abs(pitch) > Math.abs(roll)) {
            float newYVelocity = (pitch > threshold ? speed : (pitch < -threshold ? -speed : 0));
            setVelocity(0, newYVelocity);
        } else {
            float newXVelocity = (roll > threshold ? speed : (roll < -threshold ? -speed : 0));
            setVelocity(newXVelocity, 0);
        }
    }

    public Missile tirer() {
        _isFiring = true;

        TextureRegion bulletRegion = _atlas.findRegion("Bullet (1)");
        float startX = _body.getPosition().x * 100;

        float startY = _body.getPosition().y * 100;
        if (DEBUG1) Gdx.app.log(TAG1, "position :" + startX + " - " + startY);

        return new Missile(startX, startY, 400, 0, true, bulletRegion, _gameScreen.getWorld(), _gameScreen);
    }


    public void die() {
        if (_isDead) return;
        _isDead = true;
        _deathTimer = 0;
    }
}
