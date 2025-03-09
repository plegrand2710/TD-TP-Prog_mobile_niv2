//package com.TD4.Pauline;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.g2d.Sprite;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.physics.box2d.*;
//
//public class Planete2 {
//    private Body body;
//    private Sprite sprite;
//
//    public Planete2(World world, TextureAtlas atlas, float x, float y) {
//        sprite = new Sprite(atlas.findRegion("Planet (1)")); // Charge l'image depuis l'atlas
//        sprite.setSize(100, 100); // Taille du sprite
//
//        BodyDef bodyDef = new BodyDef();
//        bodyDef.type = BodyDef.BodyType.StaticBody;
//        bodyDef.position.set(x / 100f, y / 100f);
//
//        body = world.createBody(bodyDef);
//
//        CircleShape shape = new CircleShape();
//        shape.setRadius(50 / 100f);
//
//        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.shape = shape;
//        fixtureDef.density = 1f;
//        fixtureDef.friction = 0.5f;
//        fixtureDef.restitution = 0.3f;
//
//        body.createFixture(fixtureDef);
//        shape.dispose();
//
//        body.setUserData(this);
//        body.setSleepingAllowed(false);
//    }
//
//    public void draw(SpriteBatch batch) {
//        sprite.setPosition((body.getPosition().x * 100) - sprite.getWidth() / 2,
//            (body.getPosition().y * 100) - sprite.getHeight() / 2);
//        sprite.draw(batch);
//        Gdx.app.log("DRAW", "Planète X: " + sprite.getX() + " Y: " + sprite.getY());
//    }
//
//    public Body getBody() {
//        return body;
//    }
//}
