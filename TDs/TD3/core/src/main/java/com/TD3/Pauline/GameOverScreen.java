package com.TD3.Pauline;

import com.badlogic.gdx.Game;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
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
    private boolean useGyroscope;
    private Stage stage;
    private ImageButton menuButton;
    private ShapeRenderer shapeRenderer;
    private TextureAtlas atlas;

    private float menuX, menuY, menuWidth, menuHeight;

    private Image restartOverlay;
    private Image menuOverlay;


    private float restartX, restartY, restartWidth, restartHeight;

    public GameOverScreen(StartScreen startScreen, TextureAtlas atlas, int finalScore, boolean useGyroscope) {
        this.startScreen = startScreen;
        this.atlas = atlas;
        this.gameOverRegion = atlas.findRegion("Game Over GUI");
        this.backgroundRegion = atlas.findRegion("Game Background");
        this.buttonRegion = atlas.findRegion("Blank Button-2");
        this.finalScore = finalScore;
        this.useGyroscope = useGyroscope;


        float WORLD_WIDTH = Gdx.graphics.getWidth();
        float WORLD_HEIGHT = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        batch = new SpriteBatch();
        bitmapFont = new BitmapFont();
        bitmapFont.getData().setScale(2f);

        glyphLayout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();

        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        setupUI();
    }

    private void setupUI() {
        float scaleFactor = 0.8f;
        float gameOverWidth = gameOverRegion.getRegionWidth() * scaleFactor;
        float gameOverHeight = gameOverRegion.getRegionHeight() * scaleFactor;
        float drawX = (viewport.getWorldWidth() - gameOverWidth) / 2;
        float drawY = (viewport.getWorldHeight() - gameOverHeight) / 2 ;
        TextureRegionDrawable clickedButtonDrawable = new TextureRegionDrawable(atlas.findRegion("Blank Button"));

        Image gameOverImage = new Image(new TextureRegionDrawable(gameOverRegion));
        gameOverImage.setSize(gameOverWidth, gameOverHeight);
        gameOverImage.setPosition(drawX, drawY);
        stage.addActor(gameOverImage);

        restartX = drawX + gameOverWidth * 0.21f;
        restartY = drawY + gameOverHeight * 0.20f;
        restartWidth = gameOverWidth * 0.605f;
        restartHeight = gameOverHeight * 0.15f;

        restartOverlay = new Image(new TextureRegionDrawable(atlas.findRegion("Blank Button")));
        restartOverlay.setSize(restartWidth, restartHeight);
        restartOverlay.setPosition(restartX, restartY);
        restartOverlay.setVisible(false);
        stage.addActor(restartOverlay);

        menuOverlay = new Image(new TextureRegionDrawable(atlas.findRegion("Blank Button")));
        menuOverlay.setSize(menuWidth, menuHeight);
        menuOverlay.setPosition(menuX, menuY + 100);
        menuOverlay.setVisible(false);
        stage.addActor(menuOverlay);


        menuWidth = 260;
        menuHeight = 150;
        menuX = viewport.getWorldWidth() / 2 - menuWidth / 2;
        menuY = drawY - menuHeight + 70;

        menuButton = new ImageButton(new TextureRegionDrawable(buttonRegion));
        menuButton.setSize(200, 100);
        menuButton.setPosition(viewport.getWorldWidth() / 2 - 100, drawY + 45);

        stage.addActor(menuButton);

        TextButton.TextButtonStyle textStyle = createTextStyle();
        TextButton menuTextButton = new TextButton("Menu", textStyle);
        menuTextButton.setSize(menuWidth, menuHeight);
        menuTextButton.setPosition(menuX, menuY + 100);

        ImageButton.ImageButtonStyle newStyle = new ImageButton.ImageButtonStyle(menuButton.getStyle());
        newStyle.imageUp = clickedButtonDrawable;
        menuTextButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Changement d'image du bouton...");

                menuButton.setStyle(newStyle);
                menuButton.invalidate();
                stage.act();
                useGyroscope = false;

                Game game = startScreen.getGame();
                StartScreen newScreen = new StartScreen(startScreen);

                Gdx.app.postRunnable(() -> game.setScreen(newScreen));
                Gdx.app.log(TAG, "✅ Retour à StartScreen !");

            }
        });

        stage.addActor(menuTextButton);

    }

    public void render(float delta) {
        clearScreen();

        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();

        if (backgroundRegion != null) {
            batch.draw(backgroundRegion, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        }

        TextButton.TextButtonStyle textStyle = createTextStyle();
        TextButton scoreText = new TextButton("Score: " + finalScore, textStyle);
        scoreText.setSize(200, 50);
        scoreText.setPosition(viewport.getWorldWidth() / 2 - 100, viewport.getWorldHeight() / 2 - 100);
        stage.addActor(scoreText);

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

                Gdx.app.log(TAG, "Affichage de l'overlay sur Restart...");

                restartOverlay.setVisible(true);
                stage.act();

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        startScreen.setScreen(new GameScreen(startScreen, useGyroscope));
                    }
                }, 0.5f);
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

    private TextButton.TextButtonStyle createTextStyle() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = bitmapFont;
        return style;
    }

    @Override
    public void dispose() {
        batch.dispose();
        bitmapFont.dispose();
        stage.dispose();
        shapeRenderer.dispose();
    }
}
