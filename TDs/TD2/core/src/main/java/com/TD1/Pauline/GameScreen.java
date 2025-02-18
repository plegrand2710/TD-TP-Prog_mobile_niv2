package com.TD1.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {
    private static final boolean DEBUG = true;
    private static final String TAG = "MYAPP";
    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 640;
    private static final float GAP_BETWEEN_FLOWERS = 200F;

    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private Camera camera;
    private BitmapFont bitmapFont;
    private GlyphLayout glyphLayout;
    private SpriteBatch batch;

    private Flappee flappee;
    private Array<Flower> flowers = new Array<Flower>();
    private int score = 0;

    private Texture background;
    private Texture flowerBottom;
    private Texture flowerTop;
    private Texture flappeeTexture;

    private boolean useGyroscope;
    private float gyroscopeSensitivity = 5.0f;

    private boolean joystickActive = false;
    private float joystickCenterX;
    private float joystickCenterY;
    private final float JOYSTICK_MAX_DISTANCE = 50f;
    private final float JOYSTICK_SPEED_FACTOR = 3.0f;

    public GameScreen(boolean useGyroscope) {
        this.useGyroscope = useGyroscope;
        if (DEBUG) Gdx.app.log(TAG, "GameScreen constructor called with useGyroscope=" + useGyroscope);
    }

    @Override
    public void resize(int width, int height) {
        if (DEBUG) Gdx.app.log(TAG, "GameScreen resize() called: width=" + width + ", height=" + height);
        super.resize(width, height);
        viewport.update(width, height);
    }

    @Override
    public void show() {
        if (DEBUG) Gdx.app.log(TAG, "GameScreen show() called.");
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        bitmapFont = new BitmapFont();
        glyphLayout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        background = new Texture(Gdx.files.internal("bg.png"));
        flowerBottom = new Texture(Gdx.files.internal("flowerBottom.png"));
        flowerTop = new Texture(Gdx.files.internal("flowerTop.png"));
        flappeeTexture = new Texture(Gdx.files.internal("bee.png"));
        if (DEBUG) {
            Gdx.app.log(TAG, "Resources loaded: bg.png, flowerBottom.png, flowerTop.png, bee.png");
        }

        flappee = new Flappee(flappeeTexture);
        flappee.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);
        if (DEBUG) Gdx.app.log(TAG, "Flappee initialized at (" + (WORLD_WIDTH / 4) + ", " + (WORLD_HEIGHT / 2) + ")");
    }

    @Override
    public void render(float delta) {
        update(delta);
        clearScreen();
        draw();
    }

    private void update(float delta) {
        updateFlappee(delta);
        updateFlowers(delta);
        updateScore();
        handleCollisions();
    }

    private void handleCollisions() {
        for (Flower flower : flowers) {
            if (flower.isFlappeeColliding(flappee)) {
                if (flappee.getX() - flappee.getCollisionCircle().radius <= 0) {
                    if (DEBUG) Gdx.app.log(TAG, "Collision: Flappee crushed at left boundary. Restarting.");
                    restart();
                    return;
                } else {
                    if (DEBUG) Gdx.app.log(TAG, "Collision detected, applying bounce.");
                    flappee.setPosition(flappee.getX() - 30, flappee.getY());
                    flappee.setVelocity(-100, 0);
                }
            }
        }
    }

    private void restart() {
        if (DEBUG) Gdx.app.log(TAG, "Restarting game.");
        flappee.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);
        flappee.setVelocity(0, 0);
        flowers.clear();
        score = 0;
    }

    private void updateFlappee(float delta) {
        if (useGyroscope) {
            flappee.updateWithGyro();
        } else {
            if (Gdx.input.isTouched()) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                if (!joystickActive && touchPos.x < WORLD_WIDTH / 2 && touchPos.y < WORLD_HEIGHT / 2) {
                    joystickActive = true;
                    joystickCenterX = touchPos.x;
                    joystickCenterY = touchPos.y;
                }
                if (joystickActive) {
                    float dx = touchPos.x - joystickCenterX;
                    float dy = touchPos.y - joystickCenterY;
                    float distance = (float) Math.sqrt(dx * dx + dy * dy);
                    if (distance > JOYSTICK_MAX_DISTANCE) {
                        float ratio = JOYSTICK_MAX_DISTANCE / distance;
                        dx *= ratio;
                        dy *= ratio;
                    }
                    float vx = dx * JOYSTICK_SPEED_FACTOR;
                    float vy = dy * JOYSTICK_SPEED_FACTOR;
                    flappee.setVelocity(vx, vy);
                }
            } else {
                joystickActive = false;
                flappee.setVelocity(0, 0);
            }
        }
        flappee.update(delta);
        blockFlappeeLeavingTheWorld();
    }

    private void updateScore() {
        Flower flower = flowers.first();
        if (flower.getX() < flappee.getX() && !flower.isPointClaimed()) {
            flower.markPointClaimed();
            score++;
            if (DEBUG) Gdx.app.log(TAG, "Score updated: " + score);
        }
    }

    private void blockFlappeeLeavingTheWorld() {
        flappee.setPosition(
            MathUtils.clamp(flappee.getX(), 0, WORLD_WIDTH),
            MathUtils.clamp(flappee.getY(), 0, WORLD_HEIGHT)
        );
    }

    private void updateFlowers(float delta) {
        for (Flower flower : flowers) {
            flower.update(delta);
        }
        checkIfNewFlowerIsNeeded();
        removeFlowersIfPassed();
    }

    private void checkIfNewFlowerIsNeeded() {
        if (flowers.size == 0) {
            createNewFlower();
        } else {
            Flower flower = flowers.peek();
            if (flower.getX() < WORLD_WIDTH - GAP_BETWEEN_FLOWERS) {
                createNewFlower();
            }
        }
    }

    private void createNewFlower() {
        Flower newFlower = new Flower(flowerBottom, flowerTop);
        newFlower.setPosition(WORLD_WIDTH + Flower.WIDTH);
        flowers.add(newFlower);
        if (DEBUG) Gdx.app.log(TAG, "New flower created at x=" + (WORLD_WIDTH + Flower.WIDTH));
    }

    private void removeFlowersIfPassed() {
        if (flowers.size > 0) {
            Flower firstFlower = flowers.first();
            if (firstFlower.getX() < -Flower.WIDTH) {
                flowers.removeValue(firstFlower, true);
                if (DEBUG) Gdx.app.log(TAG, "Flower removed.");
            }
        }
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw() {
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();
        batch.draw(background, 0, 0);
        drawFlowers();
        flappee.draw(batch);
        drawScore();
        batch.end();
    }

    private void drawScore() {
        String scoreAsString = Integer.toString(score);
        glyphLayout.setText(bitmapFont, scoreAsString);
        bitmapFont.draw(batch, scoreAsString,
            (viewport.getWorldWidth() - glyphLayout.width) / 2,
            (4 * viewport.getWorldHeight() / 5) - glyphLayout.height / 2);
    }

    private void drawFlowers() {
        for (Flower flower : flowers) {
            flower.draw(batch);
        }
    }
}
