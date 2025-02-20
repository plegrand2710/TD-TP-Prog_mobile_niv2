package com.TD1.Pauline;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Monster {
    private float x;
    private float y;
    private float speed;
    private float verticalSpeed;
    private Texture texture;
    private boolean movingLeft;
    private float scale;
    private static final float WORLD_HEIGHT = 640f;

    public Monster(Texture texture, float x, float y, float speed, boolean movingLeft, float scale, float verticalSpeed) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.verticalSpeed = verticalSpeed;
        this.movingLeft = movingLeft;
        this.scale = scale;
    }

    public Monster(Texture texture, float x, float y, float speed, boolean movingLeft) {
        this(texture, x, y, speed, movingLeft, 0.2f, 0f);
    }

    public void update(float delta) {
        if (movingLeft) {
            x -= speed * delta;
        } else {
            x += speed * delta;
        }
        y += verticalSpeed * delta;
        float monsterHeight = texture.getHeight() * scale;
        if (y < 0) {
            y = 0;
            verticalSpeed = -verticalSpeed;
        } else if (y + monsterHeight > WORLD_HEIGHT) {
            y = WORLD_HEIGHT - monsterHeight;
            verticalSpeed = -verticalSpeed;
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, texture.getWidth() * scale, texture.getHeight() * scale);
    }

    public boolean collidesWith(Flappee flappee) {
        float monsterRadius = (texture.getWidth() * scale) / 2f;
        float dx = (x + monsterRadius) - flappee.getX();
        float dy = (y + monsterRadius) - flappee.getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance < (monsterRadius + flappee.getCollisionCircle().radius);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getVerticalSpeed() {
        return verticalSpeed;
    }

    public void setVerticalSpeed(float verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public boolean isMovingLeft() {
        return movingLeft;
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
