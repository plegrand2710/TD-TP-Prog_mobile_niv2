package com.TD1.Pauline;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {
    private static final float MOVE_TIME = 0.5F;
    private static final int GRID_CELL = 32;
    private static final int SNAKE_MOVEMENT = GRID_CELL;
    private static final float WORLD_WIDTH = 640;
    private static final float WORLD_HEIGHT = 480;
    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;
    private static final int POINTS_PER_APPLE = 20;
    private static final String GAME_OVER_TEXT = "Game Over... Tap space to restart!";

    private ShapeRenderer shapeRenderer;
    private BitmapFont bitmapFont;
    private Viewport viewport;
    private Camera camera;
    private SpriteBatch batch;
    private Texture snakeHead;
    private Texture snakeBody;
    private Texture apple;
    private boolean appleAvailable = false;
    private float appleX, appleY;
    private float timer = MOVE_TIME;
    private float snakeX = 0, snakeY = 0;
    private float snakeXBeforeUpdate = 0, snakeYBeforeUpdate = 0;
    private int snakeDirection = RIGHT;
    private boolean directionSet = false;
    private int score = 0;
    private Array<BodyPart> bodyParts = new Array<>();
    private GlyphLayout layout = new GlyphLayout();
    private String controlMode;

    private enum STATE {
        PLAYING, GAME_OVER
    }
    private STATE state = STATE.PLAYING;

    public GameScreen(Game game, String controlMode) {
        this.controlMode = controlMode;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        bitmapFont = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        snakeHead = new Texture(Gdx.files.internal("snakehead.png"));
        snakeBody = new Texture(Gdx.files.internal("snakeBody.png"));
        apple = new Texture(Gdx.files.internal("apple.png"));
    }

    @Override
    public void render(float delta) {
        clearScreen();
        if (state == STATE.PLAYING) {
            queryInput();
            updateSnake(delta);
            checkAppleCollision();
            checkAndPlaceApple();
        } else if (state == STATE.GAME_OVER) {
            checkForRestart();
        }
        draw();
    }

    private void queryInput() {
        // Pour tester, on utilise les flèches du clavier, quelle que soit la valeur de controlMode.
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && snakeDirection != LEFT && !directionSet) {
            snakeDirection = RIGHT;
            directionSet = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && snakeDirection != RIGHT && !directionSet) {
            snakeDirection = LEFT;
            directionSet = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP) && snakeDirection != DOWN && !directionSet) {
            snakeDirection = UP;
            directionSet = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && snakeDirection != UP && !directionSet) {
            snakeDirection = DOWN;
            directionSet = true;
        }
    }

    private void checkForRestart() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            doRestart();
        }
    }

    private void doRestart() {
        state = STATE.PLAYING;
        bodyParts.clear();
        snakeDirection = RIGHT;
        directionSet = false;
        timer = MOVE_TIME;
        snakeX = 0;
        snakeY = 0;
        appleAvailable = false;
        score = 0;
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(snakeHead, snakeX, snakeY);
        for (BodyPart bodyPart : bodyParts) {
            bodyPart.draw(batch);
        }
        if (appleAvailable) {
            batch.draw(apple, appleX, appleY);
        }
        if (state == STATE.GAME_OVER) {
            layout.setText(bitmapFont, GAME_OVER_TEXT);
            bitmapFont.draw(batch, GAME_OVER_TEXT, viewport.getWorldWidth() / 2 - layout.width / 2,
                viewport.getWorldHeight() / 2 - layout.height / 2);
        }
        batch.end();
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

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public void draw(Batch batch) {
            if (!(x == snakeX && y == snakeY)) {
                batch.draw(texture, x, y);
            }
        }
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
                break;
            case LEFT:
                snakeX -= SNAKE_MOVEMENT;
                break;
            case UP:
                snakeY += SNAKE_MOVEMENT;
                break;
            case DOWN:
                snakeY -= SNAKE_MOVEMENT;
                break;
        }
    }

    private void checkForOutOfBounds() {
        if (snakeX < 0 || snakeX >= viewport.getWorldWidth() || snakeY < 0 || snakeY >= viewport.getWorldHeight()) {
            state = STATE.GAME_OVER;
        }
    }

    private void updateBodyPartsPosition() {
        if (bodyParts.size > 0) {
            float prevX = snakeXBeforeUpdate;
            float prevY = snakeYBeforeUpdate;
            for (int i = 0; i < bodyParts.size; i++) {
                BodyPart part = bodyParts.get(i);
                float tempX = part.getX();
                float tempY = part.getY();
                part.updateBodyPosition(prevX, prevY);
                prevX = tempX;
                prevY = tempY;
            }
        }
    }

    private void checkSnakeBodyCollision() {
        for (BodyPart part : bodyParts) {
            if (snakeX == part.getX() && snakeY == part.getY()) {
                state = STATE.GAME_OVER;
                break;
            }
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

    private void addToScore() {
        score += POINTS_PER_APPLE;
    }

    private void checkAndPlaceApple() {
        if (!appleAvailable) {
            do {
                appleX = MathUtils.random((int) (viewport.getWorldWidth() / SNAKE_MOVEMENT) - 1) * SNAKE_MOVEMENT;
                appleY = MathUtils.random((int) (viewport.getWorldHeight() / SNAKE_MOVEMENT) - 1) * SNAKE_MOVEMENT;
                appleAvailable = true;
            } while (appleX == snakeX && appleY == snakeY);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        snakeHead.dispose();
        snakeBody.dispose();
        apple.dispose();
        bitmapFont.dispose();
        shapeRenderer.dispose();
    }
}
