package com.TD3.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
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
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {
    private static final boolean DEBUG = true;
    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 640;
    private static final float GAP_BETWEEN_PLANETES = 200f;
    private static final float TOUCHPAD_SPEED = 300f;

    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private Camera camera;
    private BitmapFont bitmapFont;
    private GlyphLayout glyphLayout;
    private SpriteBatch batch;

    // Utilisation de Cosmonaute au lieu de Flappee
    private Cosmonaute cosmonaute;
    private Array<Planete> planetes = new Array<Planete>();
    private int score = 0;

    // On charge l'atlas unique contenant tous les assets
    private TextureAtlas spaceAtlas;
    private TextureRegion backgroundRegion;

    // Récupération des régions pour les planètes et le champ d’énergie
    private TextureRegion planetRegion1; // "planet1" (space_warrior3)
    private TextureRegion planetRegion2; // "planet2" (space_warrior5)
    private TextureRegion energyRegion;  // "energy" (space_warrior4)

    // Région pour Alien
    private TextureRegion alienRegion;   // "alien" (space_warrior6)

    // Bouton de tir
    private TextureRegion shootButtonRegion; // "shootButton" (space_warrior2)

    private boolean useGyroscope;

    private Stage uiStage;
    private Touchpad touchpad;
    private ImageButton shootButton;

    private float levelTimer = 0f;
    private int currentStage = 1;
    private float stageMessageTime = 0f;
    private String stageMessage = "";
    private float scrollSpeedFactor = 1.0f;

    private final Array<Alien> aliens = new Array<Alien>();
    private float alienSpawnTimer = 0f;
    private float alienSpawnInterval = 10f;

    // Listes de missiles
    private final Array<Missile> playerMissiles = new Array<Missile>();
    private final Array<Missile> enemyMissiles = new Array<Missile>();
    private float enemyMissileSpawnTimer = 0f;
    private float enemyMissileSpawnInterval = 8f;

    public GameScreen(boolean useGyroscope) {
        this.useGyroscope = useGyroscope;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        if (!useGyroscope && uiStage != null) {
            touchpad.setBounds(WORLD_WIDTH - 215, 15, 200, 200);
            shootButton.setPosition(20, 20);
        }
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        bitmapFont = new BitmapFont();
        glyphLayout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        // Charge l'atlas contenant tous les assets
        spaceAtlas = new TextureAtlas(Gdx.files.internal("space_warrior.atlas"));

        // Récupère les régions depuis l’atlas
        backgroundRegion = spaceAtlas.findRegion("background");
        planetRegion1 = spaceAtlas.findRegion("planet1");
        planetRegion2 = spaceAtlas.findRegion("planet2");
        energyRegion = spaceAtlas.findRegion("energy");
        alienRegion = spaceAtlas.findRegion("alien");
        shootButtonRegion = spaceAtlas.findRegion("shootButton");

        // Crée le cosmonaute en lui passant l'atlas
        cosmonaute = new Cosmonaute(spaceAtlas);
        cosmonaute.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);

        if (!useGyroscope) {
            setupTouchpad();
            setupShootButton();
        }

        // Crée la première planète
        createNewPlanete();
    }

    private void setupTouchpad() {
        uiStage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle();
        // Ici, on crée des drawable simples à partir de régions de l’atlas si besoin
        // Pour cet exemple, nous utilisons le même drawable pour background et knob
        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(spaceAtlas.findRegion("touchpad_bg"));
        TextureRegionDrawable knobDrawable = new TextureRegionDrawable(spaceAtlas.findRegion("touchpad_knob"));
        style.background = bgDrawable;
        style.knob = knobDrawable;
        touchpad = new Touchpad(10, style);
        touchpad.setBounds(WORLD_WIDTH - 215, 15, 200, 200);
        uiStage.addActor(touchpad);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void setupShootButton() {
        shootButton = new ImageButton(new TextureRegionDrawable(shootButtonRegion));
        shootButton.setPosition(20, 20);
        shootButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener() {
            @Override
            public void tap(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int count, int button) {
                Missile missile = cosmonaute.tirer();
                playerMissiles.add(missile);
            }
        });
        uiStage.addActor(shootButton);
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
        // Déclenchement du tir par simple toucher
        if (Gdx.input.justTouched()) {
            Missile missile = cosmonaute.tirer();
            playerMissiles.add(missile);
        }
        updateCosmonaute(delta);
        updatePlanetes(delta);
        updateScore();
        updateAliens(delta);
        updateMissiles(delta);
        checkCollisions();
        updateLevel(delta);
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
    }

    private void spawnEnemyMissile() {
        float x = WORLD_WIDTH;
        float y = MathUtils.random(0, WORLD_HEIGHT);
        float speedX = -300;
        float speedY = 0;
        // Pour le missile ennemi, on utilise la région "missileEnemy" depuis l'atlas
        TextureRegion enemyMissileRegion = spaceAtlas.findRegion("missileEnemy");
        enemyMissiles.add(new Missile(x, y, speedX, speedY, false, enemyMissileRegion));
    }

    private void checkCollisions() {
        // Collision entre cosmonaute et aliens
        for (Alien alien : aliens) {
            if (alien.collidesWith(cosmonaute)) {
                restart();
                return;
            }
        }
        // Collision entre cosmonaute et missiles ennemis
        for (Missile m : enemyMissiles) {
            if (m.getCollisionCircle().overlaps(cosmonaute.getCollisionCircle())) {
                restart();
                return;
            }
        }
        // Collision entre missiles du joueur et aliens
        for (int i = aliens.size - 1; i >= 0; i--) {
            Alien alien = aliens.get(i);
            for (int j = playerMissiles.size - 1; j >= 0; j--) {
                Missile m = playerMissiles.get(j);
                // Création d'un cercle approximatif pour l'alien
                float alienRadius = (alien.getRegion().getRegionWidth() * alien.getScale()) / 2f;
                com.badlogic.gdx.math.Circle alienCircle = new com.badlogic.gdx.math.Circle(
                    alien.getX() + alienRadius,
                    alien.getY() + alien.getRegion().getRegionHeight() * alien.getScale() / 2,
                    alienRadius);
                if (m.getCollisionCircle().overlaps(alienCircle)) {
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
        }
        if (stageMessageTime > 0) {
            stageMessageTime -= delta;
            if (stageMessageTime < 0) stageMessageTime = 0;
        }
    }

    private void checkIfNewPlaneteIsNeeded() {
        if (planetes.size == 0) createNewPlanete();
        else {
            Planete p = planetes.peek();
            if (p.getX() < WORLD_WIDTH - GAP_BETWEEN_PLANETES) createNewPlanete();
        }
    }

    private void createNewPlanete() {
        // Choisit aléatoirement entre planetRegion1 et planetRegion2
        TextureRegion chosen = MathUtils.randomBoolean() ? planetRegion1 : planetRegion2;
        Planete p = new Planete(chosen, energyRegion);
        p.setPosition(WORLD_WIDTH + Planete.WIDTH);
        planetes.add(p);
    }

    private void removePlanetesIfPassed() {
        if (planetes.size > 0) {
            Planete p = planetes.first();
            if (p.getX() < -Planete.WIDTH) planetes.removeValue(p, true);
        }
    }

    private void updateScore() {
        if (planetes.size > 0) {
            Planete p = planetes.first();
            if (p.getX() < cosmonaute.getX() && !p.isPointClaimed()) {
                p.markPointClaimed();
                score++;
            }
        }
    }

    private void blockCosmonaute() {
        float clampedX = MathUtils.clamp(cosmonaute.getX(), 0, WORLD_WIDTH);
        float clampedY = MathUtils.clamp(cosmonaute.getY(), 0, WORLD_HEIGHT);
        cosmonaute.setPosition(clampedX, clampedY);
    }

    private void restart() {
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
        spaceAtlas.dispose();
        if (uiStage != null) uiStage.dispose();
    }
}
