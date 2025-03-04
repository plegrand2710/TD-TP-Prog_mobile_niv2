package com.TD3.Pauline;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class ElectricField {
    private float x, y;
    private float width, height;
    private float speed;
    private Rectangle hitbox;

    private final Animation<TextureRegion> animation;
    private float animationTimer = 0f;

    public ElectricField(Animation<TextureRegion> animation, float x, float y, float width, float height, float speed) {
        this.animation = animation;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;

        float hitboxWidth = width * 0.6f;
        float hitboxHeight = height * 0.7f;

        float hitboxX = x + (width - hitboxWidth) / 2;
        float hitboxY = y + (height - hitboxHeight) / 2;

        this.hitbox = new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    public void update(float delta) {
        x -= speed * delta;
        hitbox.setPosition(x + (width - hitbox.width) / 2, y + (height - hitbox.height) / 2);
        animationTimer += delta;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(animationTimer, true);

        batch.draw(currentFrame,
            x, y,
            width / 2, height / 2,
            width, height,
            1, 1,
            90
        );
    }



    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public boolean collidesWith(Cosmonaute cosmo) {
        return Intersector.overlaps(cosmo.getCollisionCircle(), hitbox);
    }

    public boolean isOutOfScreen() {
        return x + width < 0;
    }
}

