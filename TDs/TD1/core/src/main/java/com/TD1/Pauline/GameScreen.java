package com.TD1.Pauline;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {

    private static final float MOVE_TIME = 0.5f;
    private static final int GRID_CELL = 32;
    private static final int SNAKE_MOVEMENT = GRID_CELL;
    private static final int RIGHT = 0, LEFT = 1, UP = 2, DOWN = 3;
    private static final int POINTS_PER_APPLE = 20;
    private static final String GAME_OVER_TEXT = "Game Over... Cliquer pour rejouer!";
    private boolean DEBUG = true;
    private ShapeRenderer shapeRenderer;
    private BitmapFont bitmapFont;
    private Viewport viewport;
    private Camera camera;
    private SpriteBatch batch;
    private Texture snakeHead, snakeBody, apple;

    private boolean appleAvailable = false;
    private float appleX, appleY;
    private float timer = MOVE_TIME;
    private float snakeX = 0, snakeY = 0;
    private float snakeXBeforeUpdate = 0, snakeYBeforeUpdate = 0;
    private int snakeDirection = RIGHT;
    private boolean directionSet = false;
    private int score = 0;
    private Array<BodyPart> bodyParts = new Array<BodyPart>();
    private GlyphLayout layout = new GlyphLayout();
    private Game game;

    private enum STATE { PLAYING, GAME_OVER }
    private STATE state = STATE.PLAYING;

    private String controlMode;

    private Stage uiStage;
    private Touchpad touchpad;

    private float debugAngle = 0;
    private ScoresFileAdapter scoreAdapter;

    private int gridCellSize = GRID_CELL;
    private int reservedTouchpadWidth = 200;
    private int gridWidth;
    private int gridHeight;

    private boolean nameRequested = false;


    public GameScreen(Game game, String controlMode) {
        this.controlMode = controlMode;
        this.game = game;
        this.scoreAdapter = new ScoresFileAdapter();
    }

    @Override
    public void resize(int width, int height) {
        if ("touchpad".equalsIgnoreCase(controlMode)) {
            gridWidth = ((width - reservedTouchpadWidth) / gridCellSize) * gridCellSize;
        } else {
            gridWidth = (width / gridCellSize) * gridCellSize;
        }
        gridHeight = (height / gridCellSize) * gridCellSize;
        camera.viewportWidth = gridWidth;
        camera.viewportHeight = gridHeight;
        camera.position.set(gridWidth / 2, gridHeight / 2, 0);
        camera.update();

        viewport.update(width, height, true);
        if (uiStage != null) {
            uiStage.getViewport().update(width, height, true);
            touchpad.setBounds(width - 215, 15, 200, 200);

        }
    }



    @Override
    public void show() {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        if ("touchpad".equalsIgnoreCase(controlMode)) {
            gridWidth = ((screenWidth - reservedTouchpadWidth) / gridCellSize) * gridCellSize;
        } else {
            gridWidth = (screenWidth / gridCellSize) * gridCellSize;
        }
        gridHeight = (screenHeight / gridCellSize) * gridCellSize;

        camera = new OrthographicCamera(gridWidth, gridHeight);
        camera.position.set(gridWidth / 2, gridHeight / 2, 0);
        camera.update();
        viewport = new FitViewport(gridWidth, gridHeight, camera);

        bitmapFont = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        snakeHead = new Texture(Gdx.files.internal("snakehead.png"));
        snakeBody = new Texture(Gdx.files.internal("snakeBody.png"));
        apple = new Texture(Gdx.files.internal("apple.png"));

        if ("touchpad".equalsIgnoreCase(controlMode)) {
            setupTouchpad();
        }
    }



    @Override
    public void render(float delta) {
        switch (state) {
            case PLAYING:
                queryInput();
                updateSnake(delta);
                checkAppleCollision();
                checkAndPlaceApple();
                break;
            case GAME_OVER:
                checkForRestart();
                break;
        }
        clearScreen();
        drawGrid();
        draw();
        if ("touchpad".equalsIgnoreCase(controlMode) && uiStage != null) {
            uiStage.act(delta);
            uiStage.draw();
        }
        if ("gyroscope".equalsIgnoreCase(controlMode)) {
            drawDebugArrow();
        }
    }

    private void queryInput() {
        if ("gyroscope".equalsIgnoreCase(controlMode)) {
            float thresholdDegrees = 5f;

            float ax = Gdx.input.getAccelerometerX();
            float ay = Gdx.input.getAccelerometerY();
            float az = Gdx.input.getAccelerometerZ();

            float pitch = MathUtils.atan2(-ax, (float) Math.sqrt(ay * ay + az * az)) * MathUtils.radiansToDegrees;
            float roll = MathUtils.atan2(ay, az) * MathUtils.radiansToDegrees;

            if (Math.abs(pitch) > Math.abs(roll)) {
                if (pitch > thresholdDegrees) {
                    updateDirection(UP);
                    debugAngle = 90;
                } else if (pitch < -thresholdDegrees) {
                    updateDirection(DOWN);
                    debugAngle = 270;
                }
            } else {
                if (roll > thresholdDegrees) {
                    updateDirection(RIGHT);
                    debugAngle = 0;
                } else if (roll < -thresholdDegrees) {
                    updateDirection(LEFT);
                    debugAngle = 180;
                }
            }
        } else if ("touchpad".equalsIgnoreCase(controlMode)) {
            float knobX = touchpad.getKnobPercentX();
            float knobY = touchpad.getKnobPercentY();
            float threshold = 0.3f;
            if (Math.abs(knobX) > Math.abs(knobY)) {
                if (knobX > threshold) {
                    updateDirection(RIGHT);
                } else if (knobX < -threshold) {
                    updateDirection(LEFT);
                }
            } else {
                if (knobY > threshold) {
                    updateDirection(UP);
                } else if (knobY < -threshold) {
                    updateDirection(DOWN);
                }
            }
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) updateDirection(LEFT);
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) updateDirection(RIGHT);
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) updateDirection(UP);
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) updateDirection(DOWN);
        }
    }

    private void checkForRestart() {
        if (state == STATE.GAME_OVER && Gdx.input.justTouched() && !nameRequested) {
            nameRequested = true;
            Gdx.input.getTextInput(new Input.TextInputListener() {
                @Override
                public void input(String text) {
                    scoreAdapter.insertScore(text, score);
                    game.setScreen(new SplashScreen(game));
                    dispose();
                }

                @Override
                public void canceled() {
                    scoreAdapter.insertScore("Joueur", score);
                    game.setScreen(new SplashScreen(game));
                    dispose();
                }
            }, "Entrez votre nom", "", "Nom");
        }
    }

    private void updateDirection(int newSnakeDirection) {
        if (!directionSet && snakeDirection != newSnakeDirection) {
            directionSet = true;
            switch (newSnakeDirection) {
                case LEFT:
                    updateIfNotOppositeDirection(newSnakeDirection, RIGHT);
                    break;
                case RIGHT:
                    updateIfNotOppositeDirection(newSnakeDirection, LEFT);
                    break;
                case UP:
                    updateIfNotOppositeDirection(newSnakeDirection, DOWN);
                    break;
                case DOWN:
                    updateIfNotOppositeDirection(newSnakeDirection, UP);
                    break;
            }
        }
    }

    private void updateIfNotOppositeDirection(int newSnakeDirection, int oppositeDirection) {
        if ((snakeDirection != oppositeDirection) || bodyParts.size == 0)
            snakeDirection = newSnakeDirection;
    }

    private void updateSnake(float delta) {
        timer -= delta;
        if (timer <= 0) {
            timer = MOVE_TIME;
            moveSnake();
            checkForOutOfBounds();
            updateBodyPartsPosition();
            checkSnakeBodyCollision();
            directionSet = false;
        }
    }

    private void moveSnake() {
        snakeXBeforeUpdate = snakeX;
        snakeYBeforeUpdate = snakeY;
        switch (snakeDirection) {
            case RIGHT:
                snakeX += SNAKE_MOVEMENT;
                return;
            case LEFT:
                snakeX -= SNAKE_MOVEMENT;
                return;
            case UP:
                snakeY += SNAKE_MOVEMENT;
                return;
            case DOWN:
                snakeY -= SNAKE_MOVEMENT;
                return;
        }
    }

    private void checkForOutOfBounds() {
        if (snakeX >= gridWidth - reservedTouchpadWidth)
            snakeX = 0;
        if (snakeX < 0) {
            int columns = (gridWidth - reservedTouchpadWidth) / gridCellSize;
            snakeX = (columns - 0) * gridCellSize;
        }
        if (snakeY >= gridHeight)
            snakeY = 0;
        if (snakeY < 0)
            snakeY = gridHeight - gridCellSize;
    }


    private void updateBodyPartsPosition() {
        if (bodyParts.size > 0) {
            BodyPart bodyPart = bodyParts.removeIndex(0);
            bodyPart.updateBodyPosition(snakeXBeforeUpdate, snakeYBeforeUpdate);
            bodyParts.add(bodyPart);
        }
    }

    private void checkAndPlaceApple() {
        if (!appleAvailable) {
            do {
                appleX = MathUtils.random(((gridWidth - reservedTouchpadWidth) / gridCellSize) - 1) * gridCellSize;
                appleY = MathUtils.random((gridHeight / gridCellSize) - 1) * gridCellSize;
                appleAvailable = true;
            } while (appleX == snakeX && appleY == snakeY);
        }
    }


    private void checkAppleCollision() {
        if (appleAvailable && appleX == snakeX && appleY == snakeY) {
            BodyPart bodyPart = new BodyPart(snakeBody);
            bodyPart.updateBodyPosition(snakeX, snakeY);
            bodyParts.insert(0, bodyPart);
            addToScore();
            appleAvailable = false;
        }
    }

    private void checkSnakeBodyCollision() {
        for (BodyPart bodyPart : bodyParts) {
            if (bodyPart.x == snakeX && bodyPart.y == snakeY)
                state = STATE.GAME_OVER;
        }
    }

    private void addToScore() {
        score += POINTS_PER_APPLE;
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void drawGrid() {
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int x = 0; x < gridWidth - reservedTouchpadWidth; x += gridCellSize) {
            for (int y = 0; y < gridHeight; y += gridCellSize) {
                shapeRenderer.rect(x, y, gridCellSize, gridCellSize);
            }
        }
        shapeRenderer.end();
    }



    private void draw() {
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();
        batch.draw(snakeHead, snakeX, snakeY);
        for (BodyPart bodyPart : bodyParts) {
            bodyPart.draw(batch);
        }
        if (appleAvailable)
            batch.draw(apple, appleX, appleY);
        if (state == STATE.GAME_OVER) {
            layout.setText(bitmapFont, GAME_OVER_TEXT);
            bitmapFont.draw(batch, GAME_OVER_TEXT,
                gridWidth / 2 - layout.width / 2,
                gridHeight / 2 - layout.height / 2);
        }
        drawScore();
        batch.end();
    }

    private void drawScore() {
        if (state == STATE.PLAYING) {
            String scoreAsString = Integer.toString(score);
            layout.setText(bitmapFont, scoreAsString);
            bitmapFont.draw(batch, scoreAsString,
                gridWidth / 2 - layout.width / 2,
                (4 * gridHeight / 5) - layout.height / 2);
        }
    }



    private void drawDebugArrow() {
        if ("gyroscope".equalsIgnoreCase(controlMode) && DEBUG) {
            float debugX = gridWidth - 100;
            float debugY = gridHeight - 100;
            float arrowLength = 50;
            float rad = debugAngle * MathUtils.degreesToRadians;
            float tipX = debugX + arrowLength * MathUtils.cos(rad);
            float tipY = debugY + arrowLength * MathUtils.sin(rad);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rectLine(debugX, debugY, tipX, tipY, 5);
            shapeRenderer.end();
            batch.begin();
            bitmapFont.draw(batch, "Angle: " + (int) debugAngle, debugX - 40, debugY - 20);
            batch.end();
        }
    }


    private void setupTouchpad() {
        uiStage = new Stage(new ScreenViewport());

        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();

        Pixmap bgPixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(Color.GRAY);
        bgPixmap.fillCircle(50, 50, 50);
        Texture bgTexture = new Texture(bgPixmap);
        bgPixmap.dispose();

        Pixmap knobPixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
        knobPixmap.setColor(Color.DARK_GRAY);
        knobPixmap.fillCircle(25, 25, 25);
        Texture knobTexture = new Texture(knobPixmap);
        knobPixmap.dispose();

        touchpadStyle.background = new TextureRegionDrawable(bgTexture);
        touchpadStyle.knob = new TextureRegionDrawable(knobTexture);

        touchpad = new Touchpad(10, touchpadStyle);
        touchpad.setBounds(Gdx.graphics.getWidth() - 215, 15, 200, 200);
        uiStage.addActor(touchpad);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void dispose() {
        batch.dispose();
        snakeHead.dispose();
        snakeBody.dispose();
        apple.dispose();
        bitmapFont.dispose();
        shapeRenderer.dispose();
        if (uiStage != null)
            uiStage.dispose();
    }

    private class BodyPart {
        private float x, y;
        private Texture texture;

        public BodyPart(Texture texture) {
            this.texture = texture;
        }

        public void updateBodyPosition(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void draw(Batch batch) {
            if (!(x == snakeX && y == snakeY))
                batch.draw(texture, x, y);
        }
    }
}
