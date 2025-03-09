//package com.TD4.Pauline;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.g2d.Sprite;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.physics.box2d.*;
//
//public class Cosmonaute {
//    private Body body;
//    private Sprite sprite;
//
//    public Cosmonaute(World world, TextureAtlas atlas, float x, float y) {
//        sprite = new Sprite(atlas.findRegion("Character Fly (3)")); // Charge l'image depuis l'atlas
//        sprite.setSize(50, 50); // Taille du sprite
//
//        BodyDef bodyDef = new BodyDef();
//        bodyDef.type = BodyDef.BodyType.DynamicBody;
//        bodyDef.position.set(x / 100f, y / 100f);
//
//        body = world.createBody(bodyDef);
//
//        CircleShape shape = new CircleShape();
//        shape.setRadius(20 / 100f);
//
//        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.shape = shape;
//        fixtureDef.density = 1f;
//        fixtureDef.friction = 0.5f;
//        fixtureDef.restitution = 0f;
//
//        body.createFixture(fixtureDef);
//        shape.dispose();
//        body.setType(BodyDef.BodyType.DynamicBody);
//        body.setUserData(this);
//        body.setSleepingAllowed(false);
//    }
//
//    public void draw(SpriteBatch batch) {
//        sprite.setPosition((body.getPosition().x * 100) - sprite.getWidth() / 2,
//            (body.getPosition().y * 100) - sprite.getHeight() / 2);
//        sprite.draw(batch);
//        Gdx.app.log("DRAW", "Cosmonaute X: " + sprite.getX() + " Y: " + sprite.getY());
//    }
//
//    public Body getBody() {
//        return body;
//    }
//}
