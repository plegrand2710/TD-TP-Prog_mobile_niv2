package com.TD3.Pauline;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.audio.Music;

public class StartScreen extends ScreenAdapter {
    private static final boolean DEBUG = false;
    private static final String TAG = "SpaceWarriorApp";

    private float WORLD_WIDTH;
    private float WORLD_HEIGHT;

    private final Game game;

    private Music backgroundMusic;

    private Stage stage;
    private TextureAtlas atlas;
    private TextureRegion backgroundRegion;
    private TextureRegion logoRegion;
    private TextureRegion guiBoxRegion;
    private TextureRegion exitButtonRegion;
    private TextureRegion blankButtonRegion;

    private BitmapFont font;
    private StartScreen startScreen;

    private static final float GYRO_BUTTON_SCALE = 1.5f;
    private static final float TOUCHPAD_BUTTON_SCALE = 1.5f;
    private static final float EXIT_BUTTON_SCALE = 1.2f;

    public StartScreen(Game game, TextureAtlas atlas, BitmapFont font,
                       TextureRegion backgroundRegion, TextureRegion logoRegion,
                       TextureRegion guiBoxRegion, TextureRegion exitButtonRegion, TextureRegion blankButtonRegion) {
        this.game = game;
        this.atlas = atlas;
        this.font = font;
        this.backgroundRegion = backgroundRegion;
        this.logoRegion = logoRegion;
        this.guiBoxRegion = guiBoxRegion;
        this.exitButtonRegion = exitButtonRegion;
        this.blankButtonRegion = blankButtonRegion;

        this.WORLD_WIDTH = Gdx.graphics.getWidth();
        this.WORLD_HEIGHT = Gdx.graphics.getHeight();

        stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        this.startScreen = this;
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("ambianceSound1.wav"));
        backgroundMusic.setLooping(true);  // 🔁 Répétition automatique
        backgroundMusic.setVolume(0.9f);   // 🔊 Volume à 50%
        backgroundMusic.play();

        Gdx.app.log(TAG, "✅ StartScreen initialisé avec succès !");

    }

    public StartScreen(StartScreen existingScreen) {
        this(
            existingScreen.game,
            existingScreen.atlas,
            existingScreen.font,
            existingScreen.backgroundRegion,
            existingScreen.logoRegion,
            existingScreen.guiBoxRegion,
            existingScreen.exitButtonRegion,
            existingScreen.blankButtonRegion
        );
    }

    @Override
    public void show() {
        if (DEBUG) Gdx.app.log(TAG, "🎬 StartScreen show() appelé.");


        Image background = new Image(backgroundRegion);
        background.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        stage.addActor(background);

        Image guiBox = new Image(guiBoxRegion);
        float guiBoxScaleFactor = 0.35f;
        float guiBoxWidth = WORLD_WIDTH * guiBoxScaleFactor;
        float guiBoxHeight = guiBoxWidth * ((float) guiBoxRegion.getRegionHeight() / guiBoxRegion.getRegionWidth());
        guiBox.setSize(guiBoxWidth, guiBoxHeight);
        guiBox.setPosition(WORLD_WIDTH / 2 - guiBoxWidth / 2, (WORLD_HEIGHT - guiBoxHeight) / 2);
        stage.addActor(guiBox);

        Image logo = new Image(logoRegion);
        float logoWidth = WORLD_WIDTH * 0.2f;
        float logoHeight = logoWidth * ((float) logoRegion.getRegionHeight() / logoRegion.getRegionWidth());
        logo.setSize(logoWidth, logoHeight);
        logo.setPosition(WORLD_WIDTH / 2, 3 * WORLD_HEIGHT / 4, Align.center);
        stage.addActor(logo);

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(blankButtonRegion);
        buttonStyle.font = font;

        TextButton gyroButton = createTextButton("Gyroscope", buttonStyle, GYRO_BUTTON_SCALE);
        gyroButton.setPosition(WORLD_WIDTH / 2 - gyroButton.getWidth() / 2, WORLD_HEIGHT / 2 - gyroButton.getHeight() / 2);
        stage.addActor(gyroButton);

        TextButton touchPadButton = createTextButton("TouchPad", buttonStyle, TOUCHPAD_BUTTON_SCALE);
        touchPadButton.setPosition(WORLD_WIDTH / 2 - touchPadButton.getWidth() / 2, gyroButton.getY() - touchPadButton.getHeight() * 1.5f);
        stage.addActor(touchPadButton);

        ImageButton exitButton = createImageButton(exitButtonRegion, EXIT_BUTTON_SCALE);
        exitButton.setPosition(WORLD_WIDTH / 2 - exitButton.getWidth() / 2, touchPadButton.getY() - exitButton.getHeight() * 1.5f);
        stage.addActor(exitButton);

        gyroButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener() {
            @Override
            public void tap(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int count, int button) {
                if (DEBUG) Gdx.app.log(TAG, "Gyroscope button tapped.");
        game.setScreen(new GameScreen(StartScreen.this, true));
            }
        });

        touchPadButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener() {
            @Override
            public void tap(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int count, int button) {
                if (DEBUG) Gdx.app.log(TAG, "TouchPad button tapped.");
                game.setScreen(new GameScreen(StartScreen.this, false));
            }
        });

        exitButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener() {
            @Override
            public void tap(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int count, int button) {
                if (DEBUG) Gdx.app.log(TAG, "Exit button tapped.");
                Gdx.app.exit();
            }
        });
        if (DEBUG) Gdx.app.log(TAG, "j'ai fini show...");

    }

    @Override
    public void hide() {
        Gdx.app.log(TAG, "🛑 StartScreen caché (hide() appelé).");
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }
    private TextButton createTextButton(String text, TextButtonStyle style, float scaleFactor) {
        float buttonWidth = (blankButtonRegion.getRegionWidth() / 3f) * scaleFactor;
        float buttonHeight = (blankButtonRegion.getRegionHeight() / 3f) * scaleFactor;

        TextButton button = new TextButton(text, style);
        button.setSize(buttonWidth, buttonHeight);
        return button;
    }

    public Game getGame() {
        return game;
    }
    private ImageButton createImageButton(TextureRegion region, float scaleFactor) {
        float buttonWidth = (region.getRegionWidth() / 3f) * scaleFactor;
        float buttonHeight = (region.getRegionHeight() / 3f) * scaleFactor;

        ImageButton button = new ImageButton(new TextureRegionDrawable(region));
        button.setSize(buttonWidth, buttonHeight);
        return button;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        WORLD_WIDTH = width;
        WORLD_HEIGHT = height;
        stage.getViewport().update(width, height, true);
        if (DEBUG) Gdx.app.log(TAG, "StartScreen resize() called: width=" + width + ", height=" + height);
    }

    public void setScreen(ScreenAdapter screen) {
        if (screen == null) {
            Gdx.app.error(TAG, "❌ ERREUR: setScreen() appelé avec un écran null !");
            return;
        }

        Gdx.app.log(TAG, "🔄 Changement d'écran vers " + screen.getClass().getSimpleName());
        game.setScreen(screen);
    }
    @Override
    public void dispose() {
        if (DEBUG) Gdx.app.log(TAG, "StartScreen dispose() called.");
        if (DEBUG) Gdx.app.log(TAG, "🗑️ Nettoyage de StartScreen...");

        if (backgroundMusic != null) {
            backgroundMusic.dispose();
            backgroundMusic = null;
            Gdx.app.log(TAG, "🎵 Musique d'ambiance supprimée.");
        }

        if (stage != null) stage.dispose();
        if (atlas != null) atlas.dispose();
        if (font != null) font.dispose();
    }
}
