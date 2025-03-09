//package com.TD4.Pauline;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.ScreenAdapter;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.*;
//import com.badlogic.gdx.utils.Array;
//import com.badlogic.gdx.utils.viewport.FitViewport;
//import com.badlogic.gdx.utils.viewport.Viewport;
//
//public class GameScreen2 extends ScreenAdapter {
//    private static final String _TAG = "SpaceWarriorApp";
//
//    private static final float WORLD_WIDTH = 960;
//    private static final float WORLD_HEIGHT = 544;
//
//    private OrthographicCamera camera;
//    private Viewport viewport;
//    private SpriteBatch batch;
//    private TextureAtlas atlas;
//    private TextureRegion _planetRegions;
//    private World world;
//    private Box2DDebugRenderer debugRenderer;
//
//    private Cosmonaute cosmonaute;
//    private final Array<Planete> planetes = new Array<>();
//
//    private final Array<Body> toRemove = new Array<>();
//
//    public GameScreen2(TextureAtlas atlas) {
//        this.atlas = atlas;
//    }
//
//    @Override
//    public void show() {
//        world = new World(new Vector2(0, 0), true);
//        debugRenderer = new Box2DDebugRenderer();
//        _planetRegions = atlas.findRegion("Planet (1)");
//        camera = new OrthographicCamera();
//        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
//        batch = new SpriteBatch();
//
//        cosmonaute = new Cosmonaute(atlas, this);
//        planetes.add(new Planete(_planetRegions, this));
//
//        world.setContactListener(new SpaceContactListener());
//
//        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
//        camera.update();
//    }
//
//    @Override
//    public void render(float delta) {
//        update(delta);
//        clearScreen();
//        draw();
//        debugRenderer.render(world, camera.combined);
//        batch.setProjectionMatrix(camera.combined);
//    }
//
//    private void update(float delta) {
//        world.step(delta, 6, 2);
//        updateCosmonauteWithGyro();
//        cleanDestroyedBodies();
//    }
//
//    private void updateCosmonauteWithGyro() {
//        float ax = Gdx.input.getAccelerometerX(); // Mouvement gauche-droite
//        float ay = Gdx.input.getAccelerometerY(); // Mouvement haut-bas
//
//        float speedMultiplier = 2.0f; // Ajuste la réactivité du mouvement
//
//        Vector2 velocity = new Vector2(-ax * speedMultiplier, ay * speedMultiplier);
//        cosmonaute.getBody().setLinearVelocity(velocity);
//
//        // 📏 Empêcher le Cosmonaute de sortir de l'écran
//        float cosmoX = cosmonaute.getBody().getPosition().x;
//        float cosmoY = cosmonaute.getBody().getPosition().y;
//        float cosmoRadius = 0.5f; // Rayon approximatif
//
//        if (cosmoX - cosmoRadius < 0) {
//            cosmonaute.getBody().setTransform(cosmoRadius, cosmoY, 0);
//        } else if (cosmoX + cosmoRadius > WORLD_WIDTH / 100f) {
//            cosmonaute.getBody().setTransform((WORLD_WIDTH / 100f) - cosmoRadius, cosmoY, 0);
//        }
//
//        if (cosmoY - cosmoRadius < 0) {
//            cosmonaute.getBody().setTransform(cosmoX, cosmoRadius, 0);
//        } else if (cosmoY + cosmoRadius > WORLD_HEIGHT / 100f) {
//            cosmonaute.getBody().setTransform(cosmoX, (WORLD_HEIGHT / 100f) - cosmoRadius, 0);
//        }
//    }
//    private void cleanDestroyedBodies() {
//        for (Body body : toRemove) {
//            world.destroyBody(body);
//        }
//        toRemove.clear();
//    }
//
//    private void clearScreen() {
//        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//    }
//
//    public World getWorld(){
//        return world;
//    }
//    private void draw() {
//        batch.setProjectionMatrix(camera.combined);
//        batch.begin();
//        cosmonaute.draw(batch);
//        for (Planete p : planetes) {
//            p.draw(batch);
//        }
//        batch.end();
//    }
//
//    private class SpaceContactListener implements ContactListener {
//        @Override
//        public void beginContact(Contact contact) {
//            Object a = contact.getFixtureA().getBody().getUserData();
//            Object b = contact.getFixtureB().getBody().getUserData();
//
//            if (a instanceof Cosmonaute && b instanceof Planete) {
//                Gdx.app.log(_TAG, "💀 Collision: Cosmonaute touché par une Planète !");
//                toRemove.add(((Cosmonaute) a).getBody());
//            } else if (b instanceof Cosmonaute && a instanceof Planete) {
//                Gdx.app.log(_TAG, "💀 Collision: Cosmonaute touché par une Planète !");
//                toRemove.add(((Cosmonaute) b).getBody());
//            }
//        }
//
//        @Override
//        public void endContact(Contact contact) {}
//
//        @Override
//        public void preSolve(Contact contact, Manifold oldManifold) {}
//
//        @Override
//        public void postSolve(Contact contact, ContactImpulse impulse) {}
//    }
//}
