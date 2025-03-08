package com.TD3.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;

import javax.swing.text.html.HTML;

public class Alien {
    private float x, y, speed, verticalSpeed;
    private boolean movingLeft;
    private float scale;
    private static final float WORLD_HEIGHT = 640f;
    private Music explosionMusic;
    private boolean isDying = false;
    private float deathTimer = 0f;
    private static final float DEATH_ANIMATION_DURATION = 0.5f;
    private final Animation<TextureRegion> deathAnimation;
    private final Animation<TextureRegion> flyAnimation;
    private float animationTimer = 0f;

    public Alien(Animation<TextureRegion> flyAnimation, float x, float y, float speed, boolean movingLeft, float scale, float verticalSpeed, Animation<TextureRegion> deathAnimation) {
        Gdx.app.log("SpaceWarriorApp", "constructeur ok.");

        this.flyAnimation = flyAnimation;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.verticalSpeed = verticalSpeed;
        this.movingLeft = movingLeft;
        this.scale = scale;
        this.deathAnimation = deathAnimation;
    }

    public Alien(Animation<TextureRegion> flyAnimation, float x, float y, float speed, boolean movingLeft, Animation<TextureRegion> deathAnimation) {
        this(flyAnimation, x, y, speed, movingLeft, 0.2f, 0f, deathAnimation);
    }

    public void die() {
        if (!isDying) {
            isDying = true;
            deathTimer = 0f;
            explosionMusic = Gdx.audio.newMusic(Gdx.files.internal("explosionSound.wav"));
            explosionMusic.setLooping(false);
            explosionMusic.setVolume(1.2f);
            explosionMusic.setPosition(3f);
            explosionMusic.play();

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (explosionMusic != null) {
                        explosionMusic.stop();
                        explosionMusic.dispose();
                        explosionMusic = null;
                    }
                }
            }, 5);
            Gdx.app.log("Alien", "Alien is dying, animation started.");
        }
    }

    public boolean isFinishedExploding() {
        return isDying && deathTimer >= DEATH_ANIMATION_DURATION;
    }

    public boolean collidesWith(Cosmonaute cosmo) {
        TextureRegion region = getCurrentRegion();
        float alienWidth = region.getRegionWidth() * scale;
        float alienHeight = region.getRegionHeight() * scale;

        float centerX = x + alienWidth / 2;
        float centerY = y + alienHeight / 2;

        float alienRadius = Math.min(alienWidth, alienHeight) / 3f;

        float dx = centerX - cosmo.getX();
        float dy = centerY - cosmo.getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        return distance < (alienRadius + cosmo.getCollisionCircle().radius);
    }

    public void update(float delta) {
        if (isDying) {
            deathTimer += delta;
            return;
        }

        animationTimer += delta;
        x += (movingLeft ? -speed : speed) * delta;
        y += verticalSpeed * delta;

        if (MathUtils.randomBoolean(0.01f)) {
            verticalSpeed = MathUtils.random(-50, 50);
        }
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;
        Gdx.app.log("SpaceWarriorApp", "Drawing alien at (" + x + ", " + y + ") with animation.");

        if (isDying) {
            if (deathAnimation != null) {
                currentFrame = deathAnimation.getKeyFrame(deathTimer, false);
            } else {
                Gdx.app.error("Alien", "deathAnimation is null! Falling back to flyAnimation.");
                currentFrame = flyAnimation.getKeyFrame(animationTimer, true);
            }
        } else {
            currentFrame = flyAnimation.getKeyFrame(animationTimer, true);
        }

        batch.draw(currentFrame, x, y, currentFrame.getRegionWidth() * scale, currentFrame.getRegionHeight() * scale);
    }


    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);

        TextureRegion region = getCurrentRegion();
        float alienWidth = region.getRegionWidth() * scale;
        float alienHeight = region.getRegionHeight() * scale;

        float centerX = x + alienWidth / 2;
        float centerY = y + alienHeight / 2;
        float alienRadius = Math.min(alienWidth, alienHeight) / 3f;

        shapeRenderer.circle(centerX, centerY, alienRadius);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public TextureRegion getCurrentRegion() {
        if (flyAnimation == null) {
            Gdx.app.error("Alien", "flyAnimation is null!");
            return null;
        }
        return flyAnimation.getKeyFrame(animationTimer, true);
    }

    public float getScale() { return scale; }
}
