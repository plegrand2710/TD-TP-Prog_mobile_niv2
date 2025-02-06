package com.TD1.Pauline;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class BodyPart {
    private float x, y;
    private Texture texture;

    public BodyPart(Texture texture) {
        this.texture = texture;
    }

    public void updateBodyPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void draw(Batch batch) {
        batch.draw(texture, x, y);
    }
}
