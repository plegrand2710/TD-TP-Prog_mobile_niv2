package com.TD3.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.Timer;

public class Missile {
    private float x, y, speedX, speedY;
    private final boolean fromPlayer;
    private final TextureRegion region;
    private final Circle collisionCircle;
    private static final float RADIUS = 10f;
    private static final float SCALE_FACTOR = 0.5f;
    private Music backgroundMusic;


    public Missile(float x, float y, float speedX, float speedY, boolean fromPlayer, TextureRegion region) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.fromPlayer = fromPlayer;
        this.region = region;
        collisionCircle = new Circle(x, y, RADIUS);
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("fireSound.wav"));
        backgroundMusic.setLooping(false);  // 🔁 Répétition automatique
        backgroundMusic.setVolume(1.2f);   // 🔊 Volume à 50%
        backgroundMusic.play();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (backgroundMusic != null) {
                    backgroundMusic.stop();
                    backgroundMusic.dispose();
                    backgroundMusic = null;
                }
            }
        }, 5);
    }

    public void update(float delta) {
        x += speedX * delta;
        y += speedY * delta;
        collisionCircle.setPosition(x, y);
    }

    public void draw(SpriteBatch batch) {
        float width = region.getRegionWidth() * SCALE_FACTOR;
        float height = region.getRegionHeight() * SCALE_FACTOR;
        batch.draw(region, x - width / 2f, y - height / 2f, width, height);
    }

    public Circle getCollisionCircle() {
        return collisionCircle;
    }

    public boolean isFromPlayer() {
        return fromPlayer;
    }
}
