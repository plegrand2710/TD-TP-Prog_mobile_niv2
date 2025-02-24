package com.TD3.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {
    private static final boolean DEBUG = false;
    private static final String TAG = "SpaceWarriorApp";

    private float WORLD_WIDTH;
    private float WORLD_HEIGHT;
    private static final float GAP_BETWEEN_PLANETES = 200f;
    private static final float TOUCHPAD_SPEED = 300f;

    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private OrthographicCamera camera;
    private BitmapFont bitmapFont;
    private GlyphLayout glyphLayout;
    private SpriteBatch batch;

    private Cosmonaute cosmonaute;
    private Array<Planete> planetes = new Array<Planete>();
    private int score = 0;

    private TextureAtlas atlas;
    private TextureRegion backgroundRegion;
    private TextureRegion planetRegion1;
    private TextureRegion planetRegion2;
    private TextureRegion energyRegion;
    private TextureRegion alienRegion;
    private TextureRegion shootButtonRegion;
    private Skin skin;

    private boolean useGyroscope;

    private Stage uiStage;
    private Touchpad touchpad;
    private float levelTimer = 0f;
    private int currentStage = 1;
    private float stageMessageTime = 0f;
    private String stageMessage = "";
    private float scrollSpeedFactor = 1.0f;

    private final Array<Alien> aliens = new Array<Alien>();
    private float alienSpawnTimer = 0f;
    private float alienSpawnInterval = 10f;

    private final Array<Missile> playerMissiles = new Array<Missile>();
    private final Array<Missile> enemyMissiles = new Array<Missile>();
    private float enemyMissileSpawnTimer = 0f;
    private float enemyMissileSpawnInterval = 8f;

    public GameScreen(boolean useGyroscope) {
        this.useGyroscope = useGyroscope;
        WORLD_WIDTH = Gdx.graphics.getWidth();
        WORLD_HEIGHT = Gdx.graphics.getHeight();
        if (DEBUG) Gdx.app.log(TAG, "GameScreen constructor called with useGyroscope=" + useGyroscope);
    }

    @Override
    public void resize(int width, int height) {
        if (!useGyroscope && uiStage != null) {
            float touchpadSize = WORLD_WIDTH * 0.2f;
            touchpad.setBounds(WORLD_WIDTH - touchpadSize - 20, 20, touchpadSize, touchpadSize);
        }
        viewport.update(width, height, true);
        if (DEBUG) Gdx.app.log(TAG, "GameScreen resize() called: width=" + width + ", height=" + height);
    }

    @Override
    public void show() {
        if (DEBUG) Gdx.app.log(TAG, "GameScreen show() called.");
        camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        camera.update();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        bitmapFont = new BitmapFont();
        glyphLayout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        atlas = new TextureAtlas(Gdx.files.internal("space_warrior.atlas"));
        if (DEBUG) Gdx.app.log(TAG, "Atlas loaded from space_warrior.atlas");

        backgroundRegion = atlas.findRegion("Background-layer");
        if (backgroundRegion == null && DEBUG) Gdx.app.log(TAG, "Failed to load background region!");

        planetRegion1 = atlas.findRegion("Planet (1)");
        if (planetRegion1 == null && DEBUG) Gdx.app.log(TAG, "Failed to load planetRegion1!");

        planetRegion2 = atlas.findRegion("Planet (4)");
        if (planetRegion2 == null && DEBUG) Gdx.app.log(TAG, "Failed to load planetRegion2!");

        energyRegion = atlas.findRegion("Electric Obstacles (1)");
        if (energyRegion == null && DEBUG) Gdx.app.log(TAG, "Failed to load energyRegion!");

        alienRegion = atlas.findRegion("Alien Fly (6)");
        if (alienRegion == null && DEBUG) Gdx.app.log(TAG, "Failed to load alienRegion!");

        shootButtonRegion = atlas.findRegion("Blank Button");
        if (shootButtonRegion == null && DEBUG) Gdx.app.log(TAG, "Failed to load shootButtonRegion!");

        cosmonaute = new Cosmonaute(atlas);
        cosmonaute.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);
        if (DEBUG) {
            Gdx.app.log(TAG, "Cosmonaute initialized at position -> X: " + (WORLD_WIDTH / 4) + ", Y: " + (WORLD_HEIGHT / 2));
        }

        if (DEBUG) {
            if (backgroundRegion != null) {
                Gdx.app.log(TAG, "Background dimensions -> Width: " + backgroundRegion.getRegionWidth() + ", Height: " + backgroundRegion.getRegionHeight());
            }
            if (planetRegion1 != null) {
                Gdx.app.log(TAG, "Planet (1) dimensions -> Width: " + planetRegion1.getRegionWidth() + ", Height: " + planetRegion1.getRegionHeight());
            }
            if (alienRegion != null) {
                Gdx.app.log(TAG, "Alien Fly (6) dimensions -> Width: " + alienRegion.getRegionWidth() + ", Height: " + alienRegion.getRegionHeight());
            }
        }
        if (!useGyroscope) {
            setupTouchpad();
        }
        createNewPlanete();
    }

    private void setupTouchpad() {
        uiStage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Touchpad.TouchpadStyle style = skin.get(Touchpad.TouchpadStyle.class);

        touchpad = new Touchpad(10, style);
        touchpad.setBounds(WORLD_WIDTH - 215, 15, 200, 200);
        uiStage.addActor(touchpad);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);

        if (DEBUG) Gdx.app.log(TAG, "Touchpad initialized with uiskin.json.");
    }

    @Override
    public void render(float delta) {
        update(delta);
        clearScreen();
        draw();
        if (!useGyroscope && uiStage != null) {
            uiStage.act(delta);
            uiStage.draw();
        }
    }

    private void update(float delta) {
        if (Gdx.input.justTouched()) {
            if (!isTouchpadTouched()) {
                Missile missile = cosmonaute.tirer();
                playerMissiles.add(missile);
                if (DEBUG) Gdx.app.log(TAG, "Screen touched, missile fired.");
            }
        }

        updateCosmonaute(delta);
        updatePlanetes(delta);
        updateScore();
        updateAliens(delta);
        updateMissiles(delta);
        checkCollisions();
        updateLevel(delta);
    }

    private boolean isTouchpadTouched() {
        if (touchpad != null) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
            return touchpad.getX() <= touchX && touchX <= touchpad.getX() + touchpad.getWidth()
                && touchpad.getY() <= touchY && touchY <= touchpad.getY() + touchpad.getHeight();
        }
        return false;
    }


    private void updateCosmonaute(float delta) {
        if (useGyroscope) {
            cosmonaute.updateWithGyro();
        } else if (touchpad != null) {
            float vx = touchpad.getKnobPercentX() * TOUCHPAD_SPEED;
            float vy = touchpad.getKnobPercentY() * TOUCHPAD_SPEED;
            cosmonaute.setVelocity(vx, vy);
        }
        cosmonaute.update(delta);
        blockCosmonaute();
    }

    private void updatePlanetes(float delta) {
        for (Planete p : planetes) {
            p.update(delta * scrollSpeedFactor);
        }
        checkIfNewPlaneteIsNeeded();
        removePlanetesIfPassed();
    }

    private void updateAliens(float delta) {
        alienSpawnTimer += delta;
        if (alienSpawnTimer >= alienSpawnInterval) {
            spawnAlien();
            alienSpawnTimer = 0f;
        }
        for (Alien alien : aliens) {
            alien.update(delta);
        }
        for (int i = aliens.size - 1; i >= 0; i--) {
            Alien a = aliens.get(i);
            float effectiveWidth = a.getRegion().getRegionWidth() * a.getScale();
            if (a.getX() < -effectiveWidth || a.getX() > WORLD_WIDTH) {
                aliens.removeIndex(i);
            }
        }
    }

    private void updateMissiles(float delta) {
        for (Missile m : playerMissiles) {
            m.update(delta);
        }
        for (int i = playerMissiles.size - 1; i >= 0; i--) {
            Missile m = playerMissiles.get(i);
            if (m.getCollisionCircle().x > WORLD_WIDTH) {
                playerMissiles.removeIndex(i);
            }
        }
        enemyMissileSpawnTimer += delta;
        if (enemyMissileSpawnTimer >= enemyMissileSpawnInterval) {
            spawnEnemyMissile();
            enemyMissileSpawnTimer = 0f;
        }
        for (Missile m : enemyMissiles) {
            m.update(delta);
        }
        for (int i = enemyMissiles.size - 1; i >= 0; i--) {
            Missile m = enemyMissiles.get(i);
            if (m.getCollisionCircle().x < 0) {
                enemyMissiles.removeIndex(i);
            }
        }
    }

    private void spawnAlien() {
        float x = WORLD_WIDTH;
        float y = MathUtils.random(0, WORLD_HEIGHT - alienRegion.getRegionHeight() * 0.5f);
        float speed = 100 + (currentStage - 1) * 20;
        float verticalSpeed = (currentStage >= 3) ? MathUtils.random(-50, 50) + (currentStage - 2) * 10 : 0f;
        aliens.add(new Alien(alienRegion, x, y, speed, true, 0.2f, verticalSpeed));
        if (DEBUG) Gdx.app.log(TAG, "Alien spawned at (" + x + ", " + y + ").");
    }

    private void spawnEnemyMissile() {
        float x = WORLD_WIDTH;
        float y = MathUtils.random(0, WORLD_HEIGHT);
        float speedX = -300;
        float speedY = 0;
        TextureRegion enemyMissileRegion = atlas.findRegion("Bullet (3)");
        enemyMissiles.add(new Missile(x, y, speedX, speedY, false, enemyMissileRegion));
        if (DEBUG) Gdx.app.log(TAG, "Enemy missile spawned at (" + x + ", " + y + ").");
    }

    private void checkCollisions() {
        for (Alien alien : aliens) {
            if (alien.collidesWith(cosmonaute)) {
                if (DEBUG) Gdx.app.log(TAG, "Collision: Cosmonaute hit by alien.");
                restart();
                return;
            }
        }
        for (Missile m : enemyMissiles) {
            if (m.getCollisionCircle().overlaps(cosmonaute.getCollisionCircle())) {
                if (DEBUG) Gdx.app.log(TAG, "Collision: Cosmonaute hit by enemy missile.");
                restart();
                return;
            }
        }
        for (int i = aliens.size - 1; i >= 0; i--) {
            Alien alien = aliens.get(i);
            for (int j = playerMissiles.size - 1; j >= 0; j--) {
                Missile m = playerMissiles.get(j);
                float alienRadius = (alien.getRegion().getRegionWidth() * alien.getScale()) / 2f;
                com.badlogic.gdx.math.Circle alienCircle = new com.badlogic.gdx.math.Circle(
                    alien.getX() + alienRadius,
                    alien.getY() + alien.getRegion().getRegionHeight() * alien.getScale() / 2,
                    alienRadius);
                if (m.getCollisionCircle().overlaps(alienCircle)) {
                    if (DEBUG) Gdx.app.log(TAG, "Alien hit by missile.");
                    aliens.removeIndex(i);
                    playerMissiles.removeIndex(j);
                    break;
                }
            }
        }
    }

    private void updateLevel(float delta) {
        levelTimer += delta;
        if (levelTimer >= 60f) {
            levelTimer = 0f;
            stageMessage = "Stage " + currentStage;
            stageMessageTime = 3f;
            currentStage++;
            scrollSpeedFactor += 0.2f;
            alienSpawnInterval = MathUtils.clamp(alienSpawnInterval - 0.5f, 5f, 10f);
            if (DEBUG) Gdx.app.log(TAG, "Level up: " + stageMessage);
        }
        if (stageMessageTime > 0) {
            stageMessageTime -= delta;
            if (stageMessageTime < 0) stageMessageTime = 0;
        }
    }

    private void checkIfNewPlaneteIsNeeded() {
        if (planetes.size == 0) {
            createNewPlanete();
        } else {
            Planete p = planetes.peek();
            if (p.getX() < WORLD_WIDTH - GAP_BETWEEN_PLANETES) {
                createNewPlanete();
            }
        }
    }

    private void createNewPlanete() {
        if (DEBUG) Gdx.app.log(TAG, "Starting planet creation...");

        TextureRegion chosen = MathUtils.randomBoolean() ? planetRegion1 : planetRegion2;

        if (DEBUG) {
            String chosenPlanet = (chosen == planetRegion1) ? "Planet (1)" : "Planet (4)";
            Gdx.app.log(TAG, "Random planet selected -> " + chosenPlanet);
        }

        if (chosen == null) {
            if (DEBUG) Gdx.app.error(TAG, "Failed to choose a valid planet texture region!");
            return;
        }

        Planete p = new Planete(chosen, energyRegion);

        if (energyRegion == null) {
            if (DEBUG) Gdx.app.error(TAG, "Energy region is null! The obstacle might not display correctly.");
        } else if (DEBUG) {
            Gdx.app.log(TAG, "Energy region loaded correctly.");
        }

        float positionX = WORLD_WIDTH + Planete.WIDTH;
        float positionY = MathUtils.random(0, WORLD_HEIGHT - chosen.getRegionHeight());
        p.setPosition(positionX);

        if (DEBUG) {
            Gdx.app.log(TAG, "Setting planet position -> X: " + positionX + ", Y: " + positionY);
        }

        planetes.add(p);

        if (DEBUG) {
            Gdx.app.log(TAG, "New planet successfully created and added to the list.");
            Gdx.app.log(TAG, "Planet texture dimensions -> Width: " + chosen.getRegionWidth() + ", Height: " + chosen.getRegionHeight());
            Gdx.app.log(TAG, "Total number of planets -> " + planetes.size);
        }

        if (planetes.size > 0 && DEBUG) {
            Planete lastPlanet = planetes.peek();
            Gdx.app.log(TAG, "Last added planet position -> X: " + lastPlanet.getX());
        }
    }

    private void removePlanetesIfPassed() {
        if (planetes.size > 0) {
            Planete p = planetes.first();
            if (p.getX() < -Planete.WIDTH) {
                planetes.removeValue(p, true);
                if (DEBUG) Gdx.app.log(TAG, "Planet removed.");
            }
        }
    }

    private void updateScore() {
        if (planetes.size > 0) {
            Planete p = planetes.first();
            if (p.getX() < cosmonaute.getX() && !p.isPointClaimed()) {
                p.markPointClaimed();
                score++;
                if (DEBUG) Gdx.app.log(TAG, "Score updated: " + score);
            }
        }
    }

    private void blockCosmonaute() {
        float clampedX = MathUtils.clamp(cosmonaute.getX(), 0, WORLD_WIDTH);
        float clampedY = MathUtils.clamp(cosmonaute.getY(), 0, WORLD_HEIGHT);
        cosmonaute.setPosition(clampedX, clampedY);
    }

    private void restart() {
        if (DEBUG) Gdx.app.log(TAG, "Restarting game.");
        cosmonaute.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);
        cosmonaute.setVelocity(0, 0);
        planetes.clear();
        aliens.clear();
        playerMissiles.clear();
        enemyMissiles.clear();
        score = 0;
        levelTimer = 0f;
        currentStage = 1;
        scrollSpeedFactor = 1.0f;
        alienSpawnInterval = 10f;
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw() {
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();

        batch.draw(backgroundRegion, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        for (Planete p : planetes) {
            p.draw(batch);
        }
        for (Alien a : aliens) {
            a.draw(batch);
        }
        for (Missile m : playerMissiles) {
            m.draw(batch);
        }
        for (Missile m : enemyMissiles) {
            m.draw(batch);
        }

        cosmonaute.draw(batch);

        glyphLayout.setText(bitmapFont, Integer.toString(score));
        bitmapFont.draw(batch, Integer.toString(score),
            (viewport.getWorldWidth() - glyphLayout.width) / 2,
            (4 * viewport.getWorldHeight() / 5) - glyphLayout.height / 2);

        if (stageMessageTime > 0) {
            glyphLayout.setText(bitmapFont, stageMessage);
            bitmapFont.draw(batch, stageMessage, (WORLD_WIDTH - glyphLayout.width) / 2, (WORLD_HEIGHT + glyphLayout.height) / 2);
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        bitmapFont.dispose();
        shapeRenderer.dispose();
        atlas.dispose();
        if (uiStage != null)
            uiStage.dispose();
    }
}
