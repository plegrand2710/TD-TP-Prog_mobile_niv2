package com.TD4.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Comparator;


public class GameScreen extends ScreenAdapter {
    private static final boolean _DEBUG = true;
    private static final String _TAG = "SpaceWarriorApp";
    private static final String _TAG1 = "SpaceWarriorAppTAG1";

    private float _WORLD_WIDTH;
    private float _WORLD_HEIGHT;
    private static final float _GAP_BETWEEN_PLANETES = 200f;
    private static final float _TOUCHPAD_SPEED = 300f;

    private ShapeRenderer _shapeRenderer;
    private Viewport _viewport;
    private OrthographicCamera _camera;
    private BitmapFont _bitmapFont;
    private GlyphLayout _glyphLayout;
    private SpriteBatch _batch;

    private final StartScreen _startScreen;

    private Music _backgroundMusic;
    private Music _gameOverMusic;
    private Music _explosionMusic;

    private World _world;
    private Box2DDebugRenderer _debugRenderer;

    private Cosmonaute _cosmonaute;
    private Array<Planete> _planetes = new Array<Planete>();
    private int _score = 0;

    private TextureAtlas _atlas;
    private TextureRegion _backgroundRegion;
    private Array<TextureRegion> _planetRegions;
    private TextureRegion _energyRegion;
    private TextureRegion _alienRegion;
    private TextureRegion _shootButtonRegion;
    private TextureRegion _gameOverRegion;

    private final Array<ElectricField> _electricFields = new Array<>();
    private float _electricFieldSpawnTimer = 0f;
    private float _electricFieldSpawnInterval = 8f;
    private Animation<TextureRegion> _electricFieldAnimation;

    private boolean _isDying = false;

    private final Array<Body> toRemove = new Array<>();

    private Skin _skin;

    private boolean _useGyroscope;

    private Stage _uiStage;
    private Touchpad _touchpad;
    private float _levelTimer = 0f;
    private int _currentStage = 1;
    private float _stageMessageTime = 0f;
    private String _stageMessage = "";
    private float _scrollSpeedFactor = 1.0f;

    private final Array<Alien> _aliens = new Array<Alien>();
    private float _alienSpawnTimer = 0f;
    private float _alienSpawnInterval = 10f;

    private final Array<Missile> _playerMissiles = new Array<Missile>();
    private float _enemyMissileSpawnTimer = 0f;
    private float _enemyMissileSpawnInterval = 8f;

    private final Array<Roquet> _enemyRockets = new Array<>();
    private Animation<TextureRegion> _enemyRocketAnimation;

    private float _deathTimer = 0f;
    private static final float _DEATH_ANIMATION_DURATION = 1.5f;

    private Animation<TextureRegion> _alienDeathAnimation;
    private Animation<TextureRegion> _alienFlyAnimation;

    private Animation<TextureRegion> _explosionAnimation;

    private Box2DDebugRenderer debugRenderer;

    public GameScreen(StartScreen startScreen, boolean useGyroscope) {
        this._startScreen = startScreen;
        this._useGyroscope = useGyroscope;
        _WORLD_WIDTH = Gdx.graphics.getWidth();
        _WORLD_HEIGHT = Gdx.graphics.getHeight();

        if (_DEBUG) Gdx.app.log(_TAG, "GameScreen constructor called with useGyroscope=" + useGyroscope);

        _backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("ambianceSoundSpace.wav"));
        _backgroundMusic.setLooping(true);
        _backgroundMusic.setVolume(0.9f);
        _backgroundMusic.play();

        if (_DEBUG) Gdx.app.log(_TAG, "🎵 Musique activée");

        _world = new World(new Vector2(0, 0), true);
        debugRenderer = new Box2DDebugRenderer();
        if (_DEBUG) Gdx.app.log(_TAG, "🌍 Monde Box2D créé avec succès");
        _batch = new SpriteBatch();
        _world.setContactListener(new GameScreen.SpaceContactListener());

        if (_DEBUG) Gdx.app.log(_TAG, "✅ Gestionnaire de collisions Box2D activé");
    }


    @Override
    public void resize(int width, int height) {
        if (!_useGyroscope && _uiStage != null) {
            float touchpadSize = _WORLD_WIDTH * 0.2f;
            _touchpad.setBounds(15, 15, 200, 200);
        }
        _viewport.update(width, height, true);
        if (_DEBUG) Gdx.app.log(_TAG, "GameScreen resize() called: width=" + width + ", height=" + height);
    }

    @Override
    public void show() {
        if (_DEBUG) Gdx.app.log(_TAG, "GameScreen show() called.");

        initializeCameraAndViewport();
        initializeRenderers();
        loadTexturesAndAnimations();
        setupCosmonaute();

        if (!_useGyroscope) {
            setupTouchpad();
        }

        createNewPlanete();
    }

    private void initializeCameraAndViewport() {
        _camera = new OrthographicCamera(_WORLD_WIDTH, _WORLD_HEIGHT);
        _camera.setToOrtho(false, _WORLD_WIDTH, _WORLD_HEIGHT);
        _camera.update();
        _viewport = new FitViewport(_WORLD_WIDTH, _WORLD_HEIGHT, _camera);
    }

    private void initializeRenderers() {
        _bitmapFont = new BitmapFont();
        _glyphLayout = new GlyphLayout();
        _shapeRenderer = new ShapeRenderer();
    }


    private void loadTexturesAndAnimations() {
        _atlas = new TextureAtlas(Gdx.files.internal("space_warrior.atlas"));
        if (_DEBUG) Gdx.app.log(_TAG, "Atlas loaded from space_warrior.atlas");

        loadBackground();
        loadPlanets();
        loadElectricFields();
        loadAliens();
        loadExplosions();
        loadMiscTextures();
    }

    private void loadBackground() {
        _backgroundRegion = _atlas.findRegion("Background-layer");
        if (_backgroundRegion == null && _DEBUG) Gdx.app.log(_TAG, "Failed to load background region!");
    }

    private void loadPlanets() {
        _planetRegions = new Array<>();
        for (TextureAtlas.AtlasRegion region : _atlas.getRegions()) {
            if (region.name.startsWith("Planet (")) {
                _planetRegions.add(region);
                if (_DEBUG) Gdx.app.log(_TAG, "Loaded planet: " + region.name);
            }
        }

        if (_planetRegions.size == 0) {
            Gdx.app.error(_TAG, "No planet textures found!");
        }
    }

    private void loadElectricFields() {
        _energyRegion = _atlas.findRegion("Electric Obstacles (1)");
        if (_energyRegion == null && _DEBUG) Gdx.app.log(_TAG, "Failed to load energyRegion!");

        Array<TextureRegion> electricFieldFrames = new Array<>();
        for (TextureAtlas.AtlasRegion region : _atlas.getRegions()) {
            if (region.name.startsWith("Electric Obstacles")) {
                electricFieldFrames.add(region);
            }
        }

        if (electricFieldFrames.size > 0) {
            _electricFieldAnimation = new Animation<>(0.1f, electricFieldFrames, Animation.PlayMode.LOOP);
            Gdx.app.log(_TAG, "Electric field animation loaded with " + electricFieldFrames.size + " frames.");
        } else {
            Gdx.app.error(_TAG, "No electric field animation frames found!");
        }
    }

    private void loadAliens() {
        Array<TextureRegion> alienFlyFrames = new Array<>();
        for (TextureAtlas.AtlasRegion region : _atlas.getRegions()) {
            if (region.name.startsWith("Alien Fly")) {
                alienFlyFrames.add(region);
            }
        }

        if (alienFlyFrames.size > 0) {
            _alienFlyAnimation = new Animation<>(0.1f, alienFlyFrames, Animation.PlayMode.LOOP);
        } else {
            Gdx.app.error("GameScreen", "No alien fly animation frames found!");
        }

        Array<TextureRegion> alienDeathFrames = new Array<>();
        for (TextureAtlas.AtlasRegion region : _atlas.getRegions()) {
            if (region.name.startsWith("Alien Die")) {
                alienDeathFrames.add(region);
            }
        }

        if (alienDeathFrames.size > 0) {
            _alienDeathAnimation = new Animation<>(0.1f, alienDeathFrames, Animation.PlayMode.NORMAL);
            Gdx.app.log(_TAG, "Alien death animation loaded with " + alienDeathFrames.size + " frames.");
        } else {
            Gdx.app.error(_TAG, "No alien death animation frames found in the atlas!");
        }
    }

    private void loadExplosions() {
        Array<TextureRegion> explosionFrames = new Array<>();
        for (TextureAtlas.AtlasRegion region : _atlas.getRegions()) {
            if (region.name.startsWith("Rocket Explode")) {
                explosionFrames.add(region);
            }
        }

        if (explosionFrames.size > 0) {
            _explosionAnimation = new Animation<>(0.1f, explosionFrames, Animation.PlayMode.NORMAL);
            Gdx.app.log(_TAG, "Explosion animation loaded with " + explosionFrames.size + " frames.");
        } else {
            Gdx.app.error(_TAG, "No explosion animation frames found in the atlas!");
        }
    }

    private void loadMiscTextures() {
        _shootButtonRegion = _atlas.findRegion("Blank Button");
        if (_shootButtonRegion == null && _DEBUG) Gdx.app.log(_TAG, "Failed to load shootButtonRegion!");

        _gameOverRegion = _atlas.findRegion("Game Over GUI");
        if (_gameOverRegion == null && _DEBUG) {
            Gdx.app.log(_TAG, "Failed to load Game Over GUI!");
        }

        Array<TextureRegion> enemyRocketFrames = new Array<>();
        for (TextureAtlas.AtlasRegion region : _atlas.getRegions()) {
            if (region.name.startsWith("Rocket (")) {
                enemyRocketFrames.add(region);
            }
        }

        enemyRocketFrames.sort(Comparator.comparing(TextureRegion::toString));
        _enemyRocketAnimation = new Animation<>(0.1f, enemyRocketFrames, Animation.PlayMode.LOOP);
    }

    private void setupCosmonaute() {
        _cosmonaute = new Cosmonaute(_atlas, this);
        _cosmonaute.setPosition(_WORLD_WIDTH / 4, _WORLD_HEIGHT / 2);

        if (_DEBUG) {
            Gdx.app.log(_TAG, "Cosmonaute initialized at position -> X: " + (_WORLD_WIDTH / 4) + ", Y: " + (_WORLD_HEIGHT / 2));
        }
    }

    private void setupTouchpad() {
        _uiStage = new Stage(new FitViewport(_WORLD_WIDTH, _WORLD_HEIGHT));
        _skin = new Skin(Gdx.files.internal("uiskin.json"));

        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle();
        style.background = _skin.getDrawable("border-circle");
        style.knob = _skin.getDrawable("touchpad-knob");

        _touchpad = new Touchpad(10, style);
        _touchpad.setBounds(15, 15, 200, 200);
        _uiStage.addActor(_touchpad);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(_uiStage);
        Gdx.input.setInputProcessor(multiplexer);

        if (_DEBUG) Gdx.app.log(_TAG, "Touchpad initialized with circular transparent style.");
    }


    @Override
    public void render(float delta) {
        update(delta);
        clearScreen();
        draw();
        if (!_useGyroscope && _uiStage != null) {
            _uiStage.act(delta);
            _uiStage.draw();
        }
        debugRenderer.render(_world, _camera.combined);

        //drawDebugWorld();
    }

    private void drawDebugWorld() {
        _shapeRenderer.setProjectionMatrix(_camera.combined);
        _shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(_camera.combined);
        _cosmonaute.drawDebug(shapeRenderer);
        for (Alien alien : _aliens) {
            alien.drawDebug(shapeRenderer);
        }
        for (ElectricField electricField : _electricFields) {
            electricField.drawDebug(shapeRenderer);
        }
        for (Missile missile : _playerMissiles) {
            //missile.drawDebug(shapeRenderer);
        }
        for (Roquet roquet : _enemyRockets) {
            roquet.drawDebug(shapeRenderer);
        }
        for (Planete planete : _planetes) {
            planete.drawDebug(shapeRenderer);
        }

        _shapeRenderer.setColor(Color.RED);

        Array<Body> bodies = new Array<>();
        _world.getBodies(bodies);
        for (Body body : bodies) {
            if (body.getUserData() == null) continue;

            Vector2 position = body.getPosition();

            if (body.getUserData() instanceof Cosmonaute) {
                _shapeRenderer.setColor(Color.BLUE);
            } else if (body.getUserData() instanceof Planete) {
                _shapeRenderer.setColor(Color.YELLOW);
            } else if (body.getUserData() instanceof Alien) {
                _shapeRenderer.setColor(Color.GREEN);
            } else if (body.getUserData() instanceof Missile) {
                _shapeRenderer.setColor(Color.WHITE);
            } else if (body.getUserData() instanceof Roquet) {
                _shapeRenderer.setColor(Color.PINK);
            } else if (body.getUserData() instanceof ElectricField) {
                _shapeRenderer.setColor(Color.ORANGE);
            }

            for (Fixture fixture : body.getFixtureList()) {
                if (fixture.getShape() instanceof CircleShape) {
                    CircleShape circle = (CircleShape) fixture.getShape();
                    float radius = circle.getRadius() * 100;
                    _shapeRenderer.circle(position.x * 100, position.y * 100, radius);
                } else if (fixture.getShape() instanceof PolygonShape) {
                    PolygonShape polygon = (PolygonShape) fixture.getShape();
                    drawPolygonShape(polygon, position);
                }
            }
        }
        _shapeRenderer.end();
    }

    private void drawPolygonShape(PolygonShape shape, Vector2 bodyPosition) {
        int vertexCount = shape.getVertexCount();
        Vector2[] vertices = new Vector2[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            vertices[i] = new Vector2();
            shape.getVertex(i, vertices[i]);
            vertices[i].scl(100);
            vertices[i].add(bodyPosition.x * 100, bodyPosition.y * 100);
        }

        for (int i = 0; i < vertexCount; i++) {
            Vector2 v1 = vertices[i];
            Vector2 v2 = vertices[(i + 1) % vertexCount];
            _shapeRenderer.line(v1, v2);
        }
    }

    private void update(float delta) {
        if (_isDying) {
            _deathTimer += delta;
            Gdx.app.log(_TAG, "Death timer: " + _deathTimer);

            _cosmonaute.update(delta);

            if (_deathTimer >= _DEATH_ANIMATION_DURATION) {
                setGameOver();
            }
            return;
        }

        _world.step(delta, 6, 2);

        if (Gdx.input.justTouched()) {
            if (!isTouchpadTouched()) {
                if (_DEBUG) Gdx.app.log(_TAG, "Screen touched");

                Missile missile = _cosmonaute.tirer();
                if (_DEBUG) Gdx.app.log(_TAG, "missile fired.");

                _playerMissiles.add(missile);

                if (_DEBUG) Gdx.app.log(_TAG, "Screen touched, missile fired.");
            }
        }

        updateCosmonaute(delta);
        updatePlanetes(delta);
        updateScore();
        updateAliens(delta);
        updateMissiles(delta);

        updateLevel(delta);
        updateElectricField(delta);

        cleanDestroyedBodies();
    }


    private void cleanDestroyedBodies() {
        for (Body body : toRemove) {
            _world.destroyBody(body);
        }
        toRemove.clear();
    }


    private void updateElectricField(float delta) {
        _electricFieldSpawnTimer += delta;
        if (_electricFieldSpawnTimer >= _electricFieldSpawnInterval) {
            spawnElectricField();
            _electricFieldSpawnTimer = 0f;
        }

        for (int i = _electricFields.size - 1; i >= 0; i--) {
            ElectricField field = _electricFields.get(i);
            field.update(delta);
            if (field.isOutOfScreen()) {
                _electricFields.removeIndex(i);
            }
        }
    }


    private boolean isTouchpadTouched() {
        if (_touchpad != null) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
            return _touchpad.getX() <= touchX && touchX <= _touchpad.getX() + _touchpad.getWidth()
                && _touchpad.getY() <= touchY && touchY <= _touchpad.getY() + _touchpad.getHeight();
        }
        return false;
    }



    public void setGameOver() {
        if (_DEBUG) Gdx.app.log(_TAG, "Game Over triggered. Transitioning to GameOverScreen.");
        _startScreen.setScreen(new GameOverScreen(_startScreen, _atlas, _score, _useGyroscope));
    }


    private void updateCosmonaute(float delta) {
        if (_useGyroscope) {
            _cosmonaute.updateWithGyro();
        } else if (_touchpad != null) {
            float vx = _touchpad.getKnobPercentX() * _TOUCHPAD_SPEED;
            float vy = _touchpad.getKnobPercentY() * _TOUCHPAD_SPEED;
            _cosmonaute.setVelocity(vx, vy);
        }

        _cosmonaute.update(delta);

        Body cosmoBody = _cosmonaute.getBody();
        float cosmoX = cosmoBody.getPosition().x;
        float cosmoY = cosmoBody.getPosition().y;
        float cosmoRadius = _cosmonaute.getRadius();

        boolean hitBorder = false;

        if (cosmoX - cosmoRadius <= 0) {
            cosmoBody.setTransform(cosmoRadius, cosmoY, 0);
            hitBorder = true;
        } else if (cosmoX + cosmoRadius >= _WORLD_WIDTH / 100f) {
            cosmoBody.setTransform((_WORLD_WIDTH / 100f) - cosmoRadius, cosmoY, 0);
            hitBorder = true;
        }

        if (cosmoY - cosmoRadius <= 0) {
            cosmoBody.setTransform(cosmoX, cosmoRadius, 0);
            hitBorder = true;
        } else if (cosmoY + cosmoRadius >= _WORLD_HEIGHT / 100f) {
            cosmoBody.setTransform(cosmoX, (_WORLD_HEIGHT / 100f) - cosmoRadius, 0);
            hitBorder = true;
        }

        if (hitBorder) {
            if (_DEBUG) Gdx.app.log(_TAG, "Collision: Cosmonaute hit the screen border.");
            triggerDeath();
        }
    }


    private void updatePlanetes(float delta) {
        for (Planete p : _planetes) {
            p.update(delta * _scrollSpeedFactor);
        }
        checkIfNewPlaneteIsNeeded();
        removePlanetesIfPassed();
    }


    private void updateAliens(float delta) {
        _alienSpawnTimer += delta;
        if (_alienSpawnTimer >= _alienSpawnInterval) {
            spawnAlien();
            _alienSpawnTimer = 0f;
        }

        for (Alien alien : _aliens) {
            alien.update(delta);
        }

        for (int i = _aliens.size - 1; i >= 0; i--) {
            if (_aliens.get(i).isFinishedExploding()) {
                _world.destroyBody(_aliens.get(i).getBody());
                _aliens.removeIndex(i);
                if (_DEBUG) Gdx.app.log(_TAG, "Alien removed after explosion.");
            }
        }
    }

    private void spawnElectricField() {
        float x = _WORLD_WIDTH;
        float y = MathUtils.random(50, _WORLD_HEIGHT - 100);
        float width = 140;
        float height = 160;
        float speed = 150f;

        _electricFields.add(new ElectricField(_electricFieldAnimation, x, y, width, height, speed, _world));
        if (_DEBUG) Gdx.app.log(_TAG, "Electric field spawned at (" + x + ", " + y + ").");
    }


    private void updateMissiles(float delta) {
        for (int i = _playerMissiles.size - 1; i >= 0; i--) {
            if (_playerMissiles.get(i).getisDestroyed()) {
                _world.destroyBody(_playerMissiles.get(i).getBody());
                _playerMissiles.removeIndex(i);
                if (_DEBUG) Gdx.app.log("SpaceWarriorApp1", "missile removed after explosion.");
            }
            else if (_playerMissiles.get(i).getBody().getPosition().x * 100 > _WORLD_WIDTH) {
                _world.destroyBody(_playerMissiles.get(i).getBody());
                _playerMissiles.removeIndex(i);
                if (_DEBUG) Gdx.app.log("SpaceWarriorApp1", "missile removed after leaving screen.");
            }
        }

        _enemyMissileSpawnTimer += delta;
        if (_enemyMissileSpawnTimer >= _enemyMissileSpawnInterval) {
            spawnEnemyRocket();
            _enemyMissileSpawnTimer = 0f;
        }

        for (Roquet rocket : _enemyRockets) {
            rocket.update(delta);
        }

        for (int i = _enemyRockets.size - 1; i >= 0; i--) {
            if (_enemyRockets.get(i).isFinishedExploding()) {
                _world.destroyBody(_enemyRockets.get(i).getBody());
                _enemyRockets.removeIndex(i);
                if (_DEBUG) Gdx.app.log("SpaceWarriorApp1", "roquet removed after explosion.");
            }
            else if (_enemyRockets.get(i).getBody().getPosition().x * 100 > _WORLD_WIDTH ||
                _enemyRockets.get(i).getBody().getPosition().x * 100 < 0) {
                _world.destroyBody(_enemyRockets.get(i).getBody());
                _enemyRockets.removeIndex(i);
                if (_DEBUG) Gdx.app.log("SpaceWarriorApp1", "🚀 Roquet removed after leaving screen (left or right).");
            }
        }
    }

    private void spawnAlien() {
        Gdx.app.log(_TAG, "spawnAlien() called");
        if (_alienFlyAnimation == null) Gdx.app.error(_TAG, "alienFlyAnimation is NULL");
        if (_alienDeathAnimation == null) Gdx.app.error(_TAG, "alienDeathAnimation is NULL");

        float x = _WORLD_WIDTH;

        if (_alienRegion == null) {
            Gdx.app.error(_TAG, "alienRegion is NULL! Using default height.");
        } else {
            Gdx.app.log(_TAG, "alienRegion dimensions -> Width: " + _alienRegion.getRegionWidth() + ", Height: " + _alienRegion.getRegionHeight());
        }

        float y = MathUtils.random(0, _WORLD_HEIGHT - (_alienRegion != null ? _alienRegion.getRegionHeight() * 0.5f : 50));

        float speed = 100 + (_currentStage - 1) * 20;

        float verticalSpeed = (_currentStage >= 3) ? MathUtils.random(-50, 50) + (_currentStage - 2) * 10 : 0f;

        if (_alienFlyAnimation == null || _alienDeathAnimation == null) {
            Gdx.app.error(_TAG, "Alien animations are null! Skipping spawn.");
            return;
        }
        _aliens.add(new Alien(_alienFlyAnimation, x, y, speed, true, 0.2f, verticalSpeed, _alienDeathAnimation, this));

        if (_DEBUG) Gdx.app.log(_TAG, "Alien spawned at (" + x + ", " + y + ").");
    }


    private void spawnEnemyRocket() {
        float x = _WORLD_WIDTH;
        float y = MathUtils.random(0, _WORLD_HEIGHT);
        float speedX = -300;
        float speedY = MathUtils.random(-50, 50);
        Roquet rocket = new Roquet(x, y, speedX, speedY, _enemyRocketAnimation, _explosionAnimation, this);
        _enemyRockets.add(rocket);
        if (_DEBUG) Gdx.app.log(_TAG, "Enemy rocket spawned at (" + x + ", " + y + ").");
    }


    private void updateLevel(float delta) {
        _levelTimer += delta;
        if (_levelTimer >= 60f) {
            _levelTimer = 0f;
            _stageMessage = "Stage " + _currentStage;
            _stageMessageTime = 3f;
            _currentStage++;
            _scrollSpeedFactor += 0.2f;
            _alienSpawnInterval = MathUtils.clamp(_alienSpawnInterval - 0.5f, 5f, 10f);
            if (_DEBUG) Gdx.app.log(_TAG, "Level up: " + _stageMessage);
        }
        if (_stageMessageTime > 0) {
            _stageMessageTime -= delta;
            if (_stageMessageTime < 0) _stageMessageTime = 0;
        }
    }

    private void checkIfNewPlaneteIsNeeded() {
        if (_planetes.size == 0) {
            createNewPlanete();
        } else {
            Planete p = _planetes.peek();
            if (p.getX() < _WORLD_WIDTH - _GAP_BETWEEN_PLANETES) {
                createNewPlanete();
            }
        }
    }

    private void createNewPlanete() {
        if (_planetRegions.size == 0) return;

        TextureRegion chosen = _planetRegions.random();
        float scaleFactor = MathUtils.random(0.3f, 0.6f);

        Planete p = new Planete(chosen, this, scaleFactor);
        p.setScale(scaleFactor);

        float positionX = _WORLD_WIDTH + chosen.getRegionWidth() * scaleFactor;
        float positionY = MathUtils.random(0, _WORLD_HEIGHT - chosen.getRegionHeight() * scaleFactor);
        p.setPosition(positionX, positionY);

        p.randomizePositionAndSize(_WORLD_WIDTH, _WORLD_HEIGHT);

        _planetes.add(p);
    }


    private void removePlanetesIfPassed() {
        if (_planetes.size > 0) {
            Planete p = _planetes.first();
            if (p.getX() < -(p.getWidth())) {
                _planetes.removeValue(p, true);
            }
        }
    }

    private void updateScore() {
        if (_planetes.size > 0) {
            Planete p = _planetes.first();
            if (p.getX() < _cosmonaute.getX() && !p.isPointClaimed()) {
                p.markPointClaimed();
                _score++;
                if (_DEBUG) Gdx.app.log(_TAG, "Score updated: " + _score);
            }
        }
    }


    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw() {
        _batch.setProjectionMatrix(_camera.projection);
        _batch.setTransformMatrix(_camera.view);
        _batch.begin();

        _batch.draw(_backgroundRegion, 0, 0, _WORLD_WIDTH, _WORLD_HEIGHT);

        for (Planete p : _planetes) {
            p.draw(_batch);
        }
        for (Alien a : _aliens) {
            a.draw(_batch);
        }
        for (Missile m : _playerMissiles) {
            m.draw(_batch);
        }
        for (ElectricField field : _electricFields) {
            field.draw(_batch);
        }

        for (int i = _enemyRockets.size - 1; i >= 0; i--) {
            Roquet rocket = _enemyRockets.get(i);

            if (rocket.isExploding()) {
                rocket.drawExplosion(_batch);
            } else {
                rocket.draw(_batch);
            }

            if (rocket.isFinishedExploding()) {
                _enemyRockets.removeIndex(i);
            }
        }

        _cosmonaute.draw(_batch);

        _glyphLayout.setText(_bitmapFont, Integer.toString(_score));
        _bitmapFont.draw(_batch, Integer.toString(_score),
            (_viewport.getWorldWidth() - _glyphLayout.width) / 2,
            (4 * _viewport.getWorldHeight() / 5) - _glyphLayout.height / 2);

        if (_stageMessageTime > 0) {
            _glyphLayout.setText(_bitmapFont, _stageMessage);
            _bitmapFont.draw(_batch, _stageMessage, (_WORLD_WIDTH - _glyphLayout.width) / 2, (_WORLD_HEIGHT + _glyphLayout.height) / 2);
        }

        _batch.end();


    }

    @Override
    public void dispose() {
        _batch.dispose();
        _bitmapFont.dispose();
        _shapeRenderer.dispose();
        _atlas.dispose();
        if (_uiStage != null)
            _uiStage.dispose();

        if (_backgroundMusic != null) {
            _backgroundMusic.dispose();
            _backgroundMusic = null;
            Gdx.app.log(_TAG, "🎵 Musique d'ambiance supprimée.");
        }
        Gdx.app.log(_TAG, "🗑️ GameScreen.dispose() appelé !");

    }

    public World getWorld() {
        return _world;
    }


    private void triggerDeath() {
        if (_isDying) return;

        _isDying = true;
        _deathTimer = 0f;
        _cosmonaute.die();

        if (_backgroundMusic != null) {
            _backgroundMusic.dispose();
            _backgroundMusic = null;
            Gdx.app.log(_TAG, "🎵 Musique d'ambiance supprimée.");
        }
        _gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("gameOverSound.wav"));
        _gameOverMusic.setLooping(false);
        _gameOverMusic.setVolume(0.9f);
        _gameOverMusic.setPosition(2f);
        _gameOverMusic.play();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (_gameOverMusic != null) {
                    _gameOverMusic.stop();
                    _gameOverMusic.dispose();
                    _gameOverMusic = null;
                }
            }
        }, 5);
        if (_DEBUG) Gdx.app.log(_TAG, "Death triggered. Waiting for animation to finish.");
    }


    private class SpaceContactListener implements ContactListener {
        @Override
        public void beginContact(Contact contact) {
            Object a = contact.getFixtureA().getBody().getUserData();
            Object b = contact.getFixtureB().getBody().getUserData();
            Gdx.app.log(_TAG, "🚀 Collision détectée entre " + a.getClass().getSimpleName() + " et " + b.getClass().getSimpleName());

            if (a instanceof Cosmonaute || b instanceof Cosmonaute) {
                Gdx.app.log(_TAG, "🚀 Collision détectée entre Cosmonaute et " +
                    (a instanceof Cosmonaute ? b.getClass().getSimpleName() : a.getClass().getSimpleName()));

                if (a instanceof Planete || b instanceof Planete) {
                    Gdx.app.log(_TAG1, "💀 Collision: Cosmonaute touché par une Planète !");
                    toRemove.add(((a instanceof Cosmonaute) ? ((Cosmonaute) a).getBody() : ((Cosmonaute) b).getBody()));
                    triggerDeath();
                }

                else if (a instanceof ElectricField || b instanceof ElectricField) {
                    Gdx.app.log(_TAG1, "⚡ Collision: Cosmonaute touché par un Champ Électrique !");
                    toRemove.add(((a instanceof Cosmonaute) ? ((Cosmonaute) a).getBody() : ((Cosmonaute) b).getBody()));
                    triggerDeath();
                }

                else if (a instanceof Roquet || b instanceof Roquet) {
                    Gdx.app.log(_TAG1, "💥 Collision: Cosmonaute touché par un Roquet !");
                    toRemove.add(((a instanceof Cosmonaute) ? ((Cosmonaute) a).getBody() : ((Cosmonaute) b).getBody()));
                    triggerDeath();
                }

                else if (a instanceof Alien || b instanceof Alien) {
                    Gdx.app.log(_TAG1, "👽 Collision: Cosmonaute touché par un Alien !");
                    toRemove.add(((a instanceof Cosmonaute) ? ((Cosmonaute) a).getBody() : ((Cosmonaute) b).getBody()));
                    triggerDeath();
                }
            }
            if (a instanceof Missile && b instanceof Roquet) {
                Gdx.app.log(_TAG1, "🔥 [DEBUG] Détection d'une collision: Missile -> Roquet !");
                Gdx.app.log(_TAG1, "🔥 [DEBUG] Roquet - Position: " + ((Roquet) b).getBody().getPosition());
                Gdx.app.log(_TAG1, "🔥 [DEBUG] Missile - Position: " + ((Missile) a).getBody().getPosition());

                Gdx.app.log(_TAG1, "🔥 Missile a détruit un Roquet !");
                ((Roquet) b).explode();

                Gdx.app.log(_TAG1, "🔥 [DEBUG] Roquet a explosé !");

                ((Missile) a).die();

            }
            else if (b instanceof Missile && a instanceof Roquet) {
                Gdx.app.log(_TAG1, "🔥 [DEBUG] Détection d'une collision: Missile -> Roquet !");
                Gdx.app.log(_TAG1, "🔥 [DEBUG] Roquet - Position: " + ((Roquet) a).getBody().getPosition());
                Gdx.app.log(_TAG1, "🔥 [DEBUG] Missile - Position: " + ((Missile) b).getBody().getPosition());

                Gdx.app.log(_TAG1, "🔥 Missile a détruit un Roquet !");
                ((Roquet) a).explode();

                Gdx.app.log(_TAG1, "🔥 [DEBUG] Roquet a explosé !");

                ((Missile) b).die();
            }

            if (a instanceof Missile && b instanceof Alien) {
                Gdx.app.log(_TAG1, "🎯 [DEBUG] Détection d'une collision: Missile -> Alien !");
                Gdx.app.log(_TAG1, "🎯 [DEBUG] Alien - Position: " + ((Alien) b).getBody().getPosition());
                Gdx.app.log(_TAG1, "🎯 [DEBUG] Missile - Position: " + ((Missile) a).getBody().getPosition());

                Gdx.app.log(_TAG1, "🎯 Missile a détruit un Alien !");
                ((Alien) b).die();
                Gdx.app.log(_TAG1, "🎯 [DEBUG] Alien marqué comme mort !");

                ((Missile) a).die();
            }
            else if (b instanceof Missile && a instanceof Alien) {
                Gdx.app.log(_TAG1, "🎯 [DEBUG] Détection d'une collision: Missile -> Alien !");
                Gdx.app.log(_TAG1, "🎯 [DEBUG] Alien - Position: " + ((Alien) a).getBody().getPosition());
                Gdx.app.log(_TAG1, "🎯 [DEBUG] Missile - Position: " + ((Missile) b).getBody().getPosition());

                Gdx.app.log(_TAG1, "🎯 Missile a détruit un Alien !");
                ((Alien) a).die();
                Gdx.app.log(_TAG1, "🎯 [DEBUG] Alien marqué comme mort !");

                ((Missile) b).die();
            }
        }

        @Override
        public void endContact(Contact contact) {}

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {}

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {}
    }
}
