package com.TD3.Pauline;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameOverScreen extends ScreenAdapter {
    private static final boolean DEBUG = true;
    private static final String TAG = "SpaceWarriorApp";

    private final StartScreen startScreen;
    private final TextureRegion gameOverRegion;
    private final TextureRegion backgroundRegion;
    private final TextureRegion buttonRegion;
    private final SpriteBatch batch;
    private final BitmapFont bitmapFont;
    private final GlyphLayout glyphLayout;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final int finalScore;
    private final boolean useGyroscope;
    private Stage stage;
    private ImageButton menuButton;
    private ShapeRenderer shapeRenderer;

    private float restartX, restartY, restartWidth, restartHeight;

    public GameOverScreen(StartScreen startScreen, TextureAtlas atlas, int finalScore, boolean useGyroscope) {
        this.startScreen = startScreen;
        this.gameOverRegion = atlas.findRegion("Game Over GUI");
        this.backgroundRegion = atlas.findRegion("Game Background");
        this.buttonRegion = atlas.findRegion("Blank Button");
        this.finalScore = finalScore;
        this.useGyroscope = useGyroscope;

        float WORLD_WIDTH = Gdx.graphics.getWidth();
        float WORLD_HEIGHT = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        batch = new SpriteBatch();
        bitmapFont = new BitmapFont();
        glyphLayout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();

        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        setupUI();
    }

    private void setupUI() {
        float scaleFactor = 0.6f;
        float gameOverWidth = gameOverRegion.getRegionWidth() * scaleFactor;
        float gameOverHeight = gameOverRegion.getRegionHeight() * scaleFactor;
        float drawX = (viewport.getWorldWidth() - gameOverWidth) / 2;
        float drawY = (viewport.getWorldHeight() - gameOverHeight) / 2 + 60;

        Image gameOverImage = new Image(new TextureRegionDrawable(gameOverRegion));
        gameOverImage.setSize(gameOverWidth, gameOverHeight);
        gameOverImage.setPosition(drawX, drawY);
        stage.addActor(gameOverImage);

        restartX = drawX + gameOverWidth * 0.21f;
        restartY = drawY + gameOverHeight * 0.20f;
        restartWidth = gameOverWidth * 0.605f;
        restartHeight = gameOverHeight * 0.15f;

        menuButton = new ImageButton(new TextureRegionDrawable(buttonRegion));
        menuButton.setSize(160, 50);
        menuButton.setPosition(viewport.getWorldWidth() / 2 - 80, drawY - 70);
        menuButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                //startScreen.setScreen(new StartScreen(startScreen));
            }
        });

        stage.addActor(menuButton);
    }

    @Override
    public void render(float delta) {
        clearScreen();

        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();

        if (backgroundRegion != null) {
            batch.draw(backgroundRegion, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        }

        glyphLayout.setText(bitmapFont, "Score: " + finalScore);
        bitmapFont.draw(batch, "Score: " + finalScore,
            (viewport.getWorldWidth() - glyphLayout.width) / 2,
            viewport.getWorldHeight() / 2 - 40);

        batch.end();

        stage.act(delta);
        stage.draw();

        checkRestartClick();
        drawDebug();
    }

    private void checkRestartClick() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = viewport.getWorldHeight() - Gdx.input.getY();

            if (touchX >= restartX && touchX <= restartX + restartWidth &&
                touchY >= restartY && touchY <= restartY + restartHeight) {
                startScreen.setScreen(new GameScreen(startScreen, useGyroscope));
            }
        }
    }

    private void drawDebug() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(restartX, restartY, restartWidth, restartHeight);

        shapeRenderer.end();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void dispose() {
        batch.dispose();
        bitmapFont.dispose();
        stage.dispose();
        shapeRenderer.dispose();
    }
}
