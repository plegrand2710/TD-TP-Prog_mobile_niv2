package com.TD3.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

public class GameScreen extends ScreenAdapter {
    private static final boolean DEBUG = true;
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

    private final StartScreen startScreen;

    private Music backgroundMusic;
    private Music gameOverMusic;
    private Music explosionMusic;



    private Cosmonaute cosmonaute;
    private Array<Planete> planetes = new Array<Planete>();
    private int score = 0;

    private TextureAtlas atlas;
    private TextureRegion backgroundRegion;
    private Array<TextureRegion> planetRegions;
    private TextureRegion energyRegion;
    private TextureRegion alienRegion;
    private TextureRegion shootButtonRegion;
    private TextureRegion gameOverRegion;

    private final Array<ElectricField> electricFields = new Array<>();
    private float electricFieldSpawnTimer = 0f;
    private float electricFieldSpawnInterval = 8f;
    private Animation<TextureRegion> electricFieldAnimation;

    private boolean isDying = false;


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
    private float enemyMissileSpawnTimer = 0f;
    private float enemyMissileSpawnInterval = 8f;

    private final Array<Roquet> enemyRockets = new Array<>();
    private Animation<TextureRegion> enemyRocketAnimation;

    private float deathTimer = 0f;
    private static final float DEATH_ANIMATION_DURATION = 1.5f;

    private Animation<TextureRegion> alienDeathAnimation;
    private Animation<TextureRegion> alienFlyAnimation;

    private Animation<TextureRegion> explosionAnimation;

    public GameScreen(StartScreen startScreen, boolean useGyroscope) {
        this.startScreen = startScreen;

        this.useGyroscope = useGyroscope;
        WORLD_WIDTH = Gdx.graphics.getWidth();
        WORLD_HEIGHT = Gdx.graphics.getHeight();
        if (DEBUG) Gdx.app.log(TAG, "GameScreen constructor called with useGyroscope=" + useGyroscope);

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("ambianceSoundSpace.wav"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.9f);
        backgroundMusic.play();
    }

    @Override
    public void resize(int width, int height) {
        if (!useGyroscope && uiStage != null) {
            float touchpadSize = WORLD_WIDTH * 0.2f;
            touchpad.setBounds(15, 15, 200, 200);
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

        planetRegions = new Array<>();

        for (TextureAtlas.AtlasRegion region : atlas.getRegions()) {
            if (region.name.startsWith("Planet (")) {
                planetRegions.add(region);
                if (DEBUG) Gdx.app.log(TAG, "Loaded planet: " + region.name);
            }
        }

        if (planetRegions.size == 0) {
            Gdx.app.error(TAG, "No planet textures found!");
        }

        energyRegion = atlas.findRegion("Electric Obstacles (1)");
        if (energyRegion == null && DEBUG) Gdx.app.log(TAG, "Failed to load energyRegion!");

        Array<TextureRegion> alienFlyFrames = new Array<>();
        for (TextureAtlas.AtlasRegion region : atlas.getRegions()) {
            if (region.name.startsWith("Alien Fly")) {
                alienFlyFrames.add(region);
            }
        }

        if (alienFlyFrames.size > 0) {
            alienFlyAnimation = new Animation<>(0.1f, alienFlyFrames, Animation.PlayMode.LOOP);
        } else {
            Gdx.app.error("GameScreen", "No alien fly animation frames found!");
        }


        shootButtonRegion = atlas.findRegion("Blank Button");
        if (shootButtonRegion == null && DEBUG) Gdx.app.log(TAG, "Failed to load shootButtonRegion!");

        Array<TextureRegion> enemyRocketFrames = new Array<>();
        for (TextureAtlas.AtlasRegion region : atlas.getRegions()) {
            if (region.name.startsWith("Rocket (")) {
                enemyRocketFrames.add(region);
            }
        }

        enemyRocketFrames.sort(Comparator.comparing(TextureRegion::toString));
        enemyRocketAnimation = new Animation<>(0.1f, enemyRocketFrames, Animation.PlayMode.LOOP);

        gameOverRegion = atlas.findRegion("Game Over GUI");

        if (gameOverRegion == null && DEBUG) {
            Gdx.app.log(TAG, "Failed to load Game Over GUI!");
        }

        Array<TextureRegion> explosionFrames = new Array<>();
        for (TextureAtlas.AtlasRegion region : atlas.getRegions()) {
            if (region.name.startsWith("Rocket Explode")) {
                explosionFrames.add(region);
            }
        }

        Array<TextureRegion> alienDeathFrames = new Array<>();
        for (TextureAtlas.AtlasRegion region : atlas.getRegions()) {
            if (region.name.startsWith("Alien Die")) {
                alienDeathFrames.add(region);
            }
        }

        if (alienDeathFrames.size > 0) {
            alienDeathAnimation = new Animation<>(0.1f, alienDeathFrames, Animation.PlayMode.NORMAL);
            Gdx.app.log(TAG, "Alien death animation loaded with " + alienDeathFrames.size + " frames.");
        } else {
            Gdx.app.error(TAG, "No alien death animation frames found in the atlas!");
        }


        if (explosionFrames.size > 0) {
            explosionAnimation = new Animation<>(0.1f, explosionFrames, Animation.PlayMode.NORMAL);
            Gdx.app.log(TAG, "Explosion animation loaded with " + explosionFrames.size + " frames.");
        } else {
            Gdx.app.error(TAG, "No explosion animation frames found in the atlas!");
        }

        Array<TextureRegion> electricFieldFrames = new Array<>();
        for (TextureAtlas.AtlasRegion region : atlas.getRegions()) {
            if (region.name.startsWith("Electric Obstacles")) {
                electricFieldFrames.add(region);
            }
        }

        if (electricFieldFrames.size > 0) {
            electricFieldAnimation = new Animation<>(0.1f, electricFieldFrames, Animation.PlayMode.LOOP);
            Gdx.app.log(TAG, "Electric field animation loaded with " + electricFieldFrames.size + " frames.");
        } else {
            Gdx.app.error(TAG, "No electric field animation frames found!");
        }


        cosmonaute = new Cosmonaute(atlas, this);
        cosmonaute.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);
        if (DEBUG) {
            Gdx.app.log(TAG, "Cosmonaute initialized at position -> X: " + (WORLD_WIDTH / 4) + ", Y: " + (WORLD_HEIGHT / 2));
        }

        if (DEBUG) {
            if (backgroundRegion != null) {
                Gdx.app.log(TAG, "Background dimensions -> Width: " + backgroundRegion.getRegionWidth() + ", Height: " + backgroundRegion.getRegionHeight());
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

        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle();
        style.background = skin.getDrawable("border-circle");
        style.knob = skin.getDrawable("touchpad-knob");

        touchpad = new Touchpad(10, style);
        touchpad.setBounds(15, 15, 200, 200);
        uiStage.addActor(touchpad);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);

        if (DEBUG) Gdx.app.log(TAG, "Touchpad initialized with circular transparent style.");
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
        if (isDying) {
            deathTimer += delta;
            Gdx.app.log(TAG, "Death timer: " + deathTimer);

            cosmonaute.update(delta);

            if (deathTimer >= DEATH_ANIMATION_DURATION) {
                setGameOver();
            }
            return;
        }

        if (Gdx.input.justTouched()) {
            if (!isTouchpadTouched()) {
                Missile missile = cosmonaute.tirer();
                playerMissiles.add(missile);
                if (DEBUG) Gdx.app.log(TAG, "Screen touched, missile fired.");
            }
        }

        if (Gdx.input.justTouched() && !isTouchpadTouched()) {
            Missile missile = cosmonaute.tirer();
            playerMissiles.add(missile);
            cosmonaute.startFiring();
            if (DEBUG) Gdx.app.log(TAG, "Screen touched, missile fired.");
        }

        updateCosmonaute(delta);
        updatePlanetes(delta);
        updateScore();
        updateAliens(delta);
        updateMissiles(delta);
        checkCollisions();
        updateLevel(delta);
        updateElectricField(delta);
    }

    private void updateElectricField(float delta) {
        electricFieldSpawnTimer += delta;
        if (electricFieldSpawnTimer >= electricFieldSpawnInterval) {
            spawnElectricField();
            electricFieldSpawnTimer = 0f;
        }

        for (int i = electricFields.size - 1; i >= 0; i--) {
            ElectricField field = electricFields.get(i);
            field.update(delta);
            if (field.isOutOfScreen()) {
                electricFields.removeIndex(i);
            }
        }
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



    public void setGameOver() {
        if (DEBUG) Gdx.app.log(TAG, "Game Over triggered. Transitioning to GameOverScreen.");
        startScreen.setScreen(new GameOverScreen( startScreen, atlas, score, useGyroscope));
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

        if (cosmonaute.getCollisionCircle().x - cosmonaute.getCollisionCircle().radius <= 0 ||
            cosmonaute.getCollisionCircle().x + cosmonaute.getCollisionCircle().radius >= WORLD_WIDTH ||
            cosmonaute.getCollisionCircle().y - cosmonaute.getCollisionCircle().radius <= 0 ||
            cosmonaute.getCollisionCircle().y + cosmonaute.getCollisionCircle().radius >= WORLD_HEIGHT) {

            if (DEBUG) Gdx.app.log(TAG, "Collision: Cosmonaute hit the screen border.");
            triggerDeath();
            return;
        }

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
            if (aliens.get(i).isFinishedExploding()) {
                aliens.removeIndex(i);
                if (DEBUG) Gdx.app.log(TAG, "Alien removed after death animation.");
            }
        }


        for (int i = aliens.size - 1; i >= 0; i--) {
            Alien alien = aliens.get(i);
            for (int j = playerMissiles.size - 1; j >= 0; j--) {
                Missile m = playerMissiles.get(j);
                TextureRegion currentFrame = alien.getCurrentRegion();
                float alienRadius = (currentFrame.getRegionWidth() * alien.getScale()) / 2f;

                com.badlogic.gdx.math.Circle alienCircle = new com.badlogic.gdx.math.Circle(
                    alien.getX() + alienRadius,
                    alien.getY() + (currentFrame.getRegionHeight() * alien.getScale()) / 2f,
                    alienRadius
                );

                if (m.getCollisionCircle().overlaps(alienCircle)) {
                    if (DEBUG) Gdx.app.log(TAG, "Alien hit by missile.");
                    alien.die();
                    playerMissiles.removeIndex(j);
                    break;
                }
            }
        }
    }

    private void spawnElectricField() {
        float x = WORLD_WIDTH;
        float y = MathUtils.random(50, WORLD_HEIGHT - 100);
        float width = 140;
        float height = 160;
        float speed = 150f;

        electricFields.add(new ElectricField(electricFieldAnimation, x, y, width, height, speed));
        if (DEBUG) Gdx.app.log(TAG, "Electric field spawned at (" + x + ", " + y + ").");
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
            spawnEnemyRocket();
            enemyMissileSpawnTimer = 0f;
        }
        for (Roquet rocket : enemyRockets) {
            rocket.update(delta);
        }
        for (int i = enemyRockets.size - 1; i >= 0; i--) {
            Roquet rocket = enemyRockets.get(i);
            if (rocket.getCollisionRect().x + rocket.getCollisionRect().width < 0) {
                enemyRockets.removeIndex(i);
            }
        }

    }

    private void spawnAlien() {
        Gdx.app.log(TAG, "spawnAlien() called");
        if (alienFlyAnimation == null) Gdx.app.error(TAG, "alienFlyAnimation is NULL");
        if (alienDeathAnimation == null) Gdx.app.error(TAG, "alienDeathAnimation is NULL");

        float x = WORLD_WIDTH;

        if (alienRegion == null) {
            Gdx.app.error(TAG, "alienRegion is NULL! Using default height.");
        } else {
            Gdx.app.log(TAG, "alienRegion dimensions -> Width: " + alienRegion.getRegionWidth() + ", Height: " + alienRegion.getRegionHeight());
        }

        float y = MathUtils.random(0, WORLD_HEIGHT - (alienRegion != null ? alienRegion.getRegionHeight() * 0.5f : 50));

        float speed = 100 + (currentStage - 1) * 20;

        float verticalSpeed = (currentStage >= 3) ? MathUtils.random(-50, 50) + (currentStage - 2) * 10 : 0f;

        if (alienFlyAnimation == null || alienDeathAnimation == null) {
            Gdx.app.error(TAG, "Alien animations are null! Skipping spawn.");
            return;
        }
        aliens.add(new Alien(alienFlyAnimation, x, y, speed, true, 0.2f, verticalSpeed, alienDeathAnimation));

        if (DEBUG) Gdx.app.log(TAG, "Alien spawned at (" + x + ", " + y + ").");
    }


    private void spawnEnemyRocket() {
        float x = WORLD_WIDTH;
        float y = MathUtils.random(0, WORLD_HEIGHT);
        float speedX = -300;
        float speedY = MathUtils.random(-50, 50);
        Roquet rocket = new Roquet(x, y, speedX, speedY, enemyRocketAnimation, explosionAnimation);
        enemyRockets.add(rocket);
        if (DEBUG) Gdx.app.log(TAG, "Enemy rocket spawned at (" + x + ", " + y + ").");
    }


    private void checkCollisions() {
        for (Alien alien : aliens) {
            if (alien.collidesWith(cosmonaute)) {
                if (DEBUG) Gdx.app.log(TAG, "Collision: Cosmonaute hit by alien.");
                triggerDeath();
                return;
            }
        }

        for (ElectricField field : electricFields) {
            if (field.collidesWith(cosmonaute)) {
                if (DEBUG) Gdx.app.log(TAG, "Collision: Cosmonaute hit by electric field.");
                triggerDeath();
                return;
            }
        }


        for (Roquet rocket : enemyRockets) {
            if (Intersector.overlaps(cosmonaute.getCollisionCircle(), rocket.getCollisionRect())) {
                if (DEBUG) Gdx.app.log(TAG, "Collision: Cosmonaute hit by enemy rocket.");
                triggerDeath();
                return;
            }
        }

        for (int i = aliens.size - 1; i >= 0; i--) {
            Alien alien = aliens.get(i);
            for (int j = playerMissiles.size - 1; j >= 0; j--) {
                Missile m = playerMissiles.get(j);
                TextureRegion currentFrame = alien.getCurrentRegion();
                if (currentFrame == null) return;

                float alienWidth = currentFrame.getRegionWidth() * alien.getScale();
                float alienHeight = currentFrame.getRegionHeight() * alien.getScale();

                com.badlogic.gdx.math.Circle alienCircle = new com.badlogic.gdx.math.Circle(
                    alien.getX() + (alienWidth / 2f),
                    alien.getY() + (alienHeight / 2f),
                    Math.min(alienWidth, alienHeight) / 3f
                );

                if (m.getCollisionCircle().overlaps(alienCircle)) {
                    if (DEBUG) Gdx.app.log(TAG, "Alien hit by missile.");
                    alien.die();
                    playerMissiles.removeIndex(j);
                    break;
                }
            }
        }


        for (Planete p : planetes) {
            if (p.isCosmonauteColliding(cosmonaute)) {
                if (DEBUG) Gdx.app.log(TAG, "Collision: Cosmonaute hit by planet or electric obstacle.");
                triggerDeath();
                return;
            }
        }

        for (int i = enemyRockets.size - 1; i >= 0; i--) {
            Roquet rocket = enemyRockets.get(i);
            for (int j = playerMissiles.size - 1; j >= 0; j--) {
                Missile missile = playerMissiles.get(j);
                if (rocket.collidesWith(missile)) {
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
                    rocket.explode();
                    playerMissiles.removeIndex(j);
                    if (DEBUG) Gdx.app.log(TAG, "Rocket hit! Explosion triggered.");
                    break;
                }
            }
        }

        for (int i = enemyRockets.size - 1; i >= 0; i--) {
            if (enemyRockets.get(i).isFinishedExploding()) {
                enemyRockets.removeIndex(i);
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
        if (planetRegions.size == 0) return;

        TextureRegion chosen = planetRegions.random();

        if (DEBUG) Gdx.app.log(TAG, "Random planet selected");

        Planete p = new Planete(chosen, null);
        float scaleFactor = MathUtils.random(0.3f, 0.6f);
        p.setScale(scaleFactor);

        float positionX = WORLD_WIDTH + chosen.getRegionWidth() * scaleFactor;
        float positionY = MathUtils.random(0, WORLD_HEIGHT - chosen.getRegionHeight() * scaleFactor);
        p.setPosition(positionX, positionY);

        p.randomizePositionAndSize(WORLD_WIDTH, WORLD_HEIGHT);

        planetes.add(p);
    }


    private void removePlanetesIfPassed() {
        if (planetes.size > 0) {
            Planete p = planetes.first();
            if (p.getX() < -(p.getWidth())) {
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
        for (ElectricField field : electricFields) {
            field.draw(batch);
        }

        for (int i = enemyRockets.size - 1; i >= 0; i--) {
            Roquet rocket = enemyRockets.get(i);

            if (rocket.isExploding()) {
                rocket.drawExplosion(batch);
            } else {
                rocket.draw(batch);
            }

            if (rocket.isFinishedExploding()) {
                enemyRockets.removeIndex(i);
            }
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

        drawDebug();

    }

    @Override
    public void dispose() {
        batch.dispose();
        bitmapFont.dispose();
        shapeRenderer.dispose();
        atlas.dispose();
        if (uiStage != null)
            uiStage.dispose();

        if (backgroundMusic != null) {
            backgroundMusic.dispose();
            backgroundMusic = null;
            Gdx.app.log(TAG, "🎵 Musique d'ambiance supprimée.");
        }
        Gdx.app.log(TAG, "🗑️ GameScreen.dispose() appelé !");

    }

    private void triggerDeath() {
        if (isDying) return;

        isDying = true;
        deathTimer = 0f;
        cosmonaute.die();

        if (backgroundMusic != null) {
            backgroundMusic.dispose();
            backgroundMusic = null;
            Gdx.app.log(TAG, "🎵 Musique d'ambiance supprimée.");
        }
        gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("gameOverSound.wav"));
        gameOverMusic.setLooping(false);
        gameOverMusic.setVolume(0.9f);
        gameOverMusic.setPosition(2f);
        gameOverMusic.play();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (gameOverMusic != null) {
                    gameOverMusic.stop();
                    gameOverMusic.dispose();
                    gameOverMusic = null;
                }
            }
        }, 5);
        if (DEBUG) Gdx.app.log(TAG, "Death triggered. Waiting for animation to finish.");
    }

    private void drawDebug() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.circle(cosmonaute.getCollisionCircle().x, cosmonaute.getCollisionCircle().y, cosmonaute.getCollisionCircle().radius);

        for (Roquet rocket : enemyRockets) {
            rocket.drawDebug(shapeRenderer);
        }

        shapeRenderer.setColor(Color.BLUE);
        for (Missile missile : playerMissiles) {
            shapeRenderer.circle(missile.getCollisionCircle().x, missile.getCollisionCircle().y, missile.getCollisionCircle().radius);
        }

        for (Planete planete : planetes) {
            planete.drawDebug(shapeRenderer);
        }

        for (ElectricField electricField : electricFields) {
            electricField.drawDebug(shapeRenderer);
        }

        shapeRenderer.setColor(Color.RED);
        for (Alien alien : aliens) {
            alien.drawDebug(shapeRenderer);
        }

        shapeRenderer.end();
    }

}
