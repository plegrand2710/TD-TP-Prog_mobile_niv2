package com.TD3.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;

public class Cosmonaute {
    private static final float FRAME_DURATION = 0.25f;
    private static final float COLLISION_RADIUS = 24f;

    private final Circle collisionCircle;
    private float x = 0, y = 0;
    private float xSpeed = 0, ySpeed = 0;
    private float animationTimer = 0;
    private final Animation<TextureRegion> animation;
    // Pour créer les tirs depuis l'atlas
    private final TextureAtlas atlas;

    public Cosmonaute(TextureAtlas atlas) {
        this.atlas = atlas;
        // On suppose que l’animation de propulsion utilise toutes les régions nommées "cosmonaute"
        animation = new Animation<TextureRegion>(FRAME_DURATION, atlas.findRegions("cosmonaute"));
        animation.setPlayMode(Animation.PlayMode.LOOP);
        collisionCircle = new Circle(x, y, COLLISION_RADIUS);
    }

    public Circle getCollisionCircle() {
        return collisionCircle;
    }

    public void update(float delta) {
        animationTimer += delta;
        x += xSpeed * delta;
        y += ySpeed * delta;
        updateCollisionCircle();
    }

    public void setVelocity(float vx, float vy) {
        this.xSpeed = vx;
        this.ySpeed = vy;
    }

    public void moveBy(float dx, float dy) {
        this.x += dx;
        this.y += dy;
        updateCollisionCircle();
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateCollisionCircle();
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(animationTimer);
        float drawX = collisionCircle.x - currentFrame.getRegionWidth() / 2;
        float drawY = collisionCircle.y - currentFrame.getRegionHeight() / 2;
        batch.draw(currentFrame, drawX, drawY);
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(collisionCircle.x, collisionCircle.y, collisionCircle.radius);
    }

    private void updateCollisionCircle() {
        collisionCircle.setPosition(x, y);
    }

    public void updateWithGyro() {
        float threshold = 5f;
        float ax = Gdx.input.getAccelerometerX();
        float ay = Gdx.input.getAccelerometerY();
        float az = Gdx.input.getAccelerometerZ();
        float pitch = MathUtils.atan2(-ax, (float)Math.sqrt(ay * ay + az * az)) * MathUtils.radiansToDegrees;
        float roll = MathUtils.atan2(ay, az) * MathUtils.radiansToDegrees;
        float speed = 200f;
        if (Math.abs(pitch) > Math.abs(roll)) {
            setVelocity(0, (pitch > threshold ? speed : (pitch < -threshold ? -speed : 0)));
        } else {
            setVelocity((roll > threshold ? speed : (roll < -threshold ? -speed : 0)), 0);
        }
    }

    // Le cosmonaute tire un missile (tirs déclenchés par touch anywhere ou bouton)
    public Missile tirer() {
        // Joue le son de tir
        Sound tirSound = Gdx.audio.newSound(Gdx.files.internal("tir.wav"));
        tirSound.play();
        // Crée un missile partant du cosmonaute vers la droite
        // Ici, on utilise la région "missilePlayer" depuis l'atlas
        TextureRegion missileRegion = atlas.findRegion("missilePlayer");
        return new Missile(x, y, 400, 0, true, missileRegion);
    }
}
