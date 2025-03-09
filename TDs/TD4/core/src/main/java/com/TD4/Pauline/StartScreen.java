package com.TD4.Pauline;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.audio.Music;

public class StartScreen extends ScreenAdapter {
    private static final boolean _DEBUG = true;
    private static final String _TAG = "SpaceWarriorApp";

    private float _WORLD_WIDTH;
    private float _WORLD_HEIGHT;

    private final Game _game;

    private Music _backgroundMusic;

    private Stage _stage;
    private TextureAtlas _atlas;
    private TextureRegion _backgroundRegion;
    private TextureRegion _logoRegion;
    private TextureRegion _guiBoxRegion;
    private TextureRegion _exitButtonRegion;
    private TextureRegion _blankButtonRegion;

    private BitmapFont _font;

    private static final float _GYRO_BUTTON_SCALE = 1.5f;
    private static final float _TOUCHPAD_BUTTON_SCALE = 1.5f;
    private static final float _EXIT_BUTTON_SCALE = 1.2f;

    public StartScreen(Game game, TextureAtlas atlas, BitmapFont font,
                       TextureRegion backgroundRegion, TextureRegion logoRegion,
                       TextureRegion guiBoxRegion, TextureRegion exitButtonRegion, TextureRegion blankButtonRegion) {
        this._game = game;
        this._atlas = atlas;
        this._font = font;
        this._backgroundRegion = backgroundRegion;
        this._logoRegion = logoRegion;
        this._guiBoxRegion = guiBoxRegion;
        this._exitButtonRegion = exitButtonRegion;
        this._blankButtonRegion = blankButtonRegion;

        this._WORLD_WIDTH = Gdx.graphics.getWidth();
        this._WORLD_HEIGHT = Gdx.graphics.getHeight();

        _stage = new Stage(new FitViewport(_WORLD_WIDTH, _WORLD_HEIGHT));
        Gdx.input.setInputProcessor(_stage);

        _backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("ambianceSound1.wav"));
        _backgroundMusic.setLooping(true);
        _backgroundMusic.setVolume(0.9f);
        _backgroundMusic.play();

        Gdx.app.log(_TAG, "✅ StartScreen initialisé avec succès !");

    }

    public StartScreen(StartScreen existingScreen) {
        this(
            existingScreen._game,
            existingScreen._atlas,
            existingScreen._font,
            existingScreen._backgroundRegion,
            existingScreen._logoRegion,
            existingScreen._guiBoxRegion,
            existingScreen._exitButtonRegion,
            existingScreen._blankButtonRegion
        );
    }

    @Override
    public void show() {
        Image background = new Image(_backgroundRegion);
        background.setSize(_WORLD_WIDTH, _WORLD_HEIGHT);
        _stage.addActor(background);

        Image guiBox = new Image(_guiBoxRegion);
        float guiBoxScaleFactor = 0.35f;
        float guiBoxWidth = _WORLD_WIDTH * guiBoxScaleFactor;
        float guiBoxHeight = guiBoxWidth * ((float) _guiBoxRegion.getRegionHeight() / _guiBoxRegion.getRegionWidth());
        guiBox.setSize(guiBoxWidth, guiBoxHeight);
        guiBox.setPosition(_WORLD_WIDTH / 2 - guiBoxWidth / 2, (_WORLD_HEIGHT - guiBoxHeight) / 2);
        _stage.addActor(guiBox);

        Image logo = new Image(_logoRegion);
        float logoWidth = _WORLD_WIDTH * 0.2f;
        float logoHeight = logoWidth * ((float) _logoRegion.getRegionHeight() / _logoRegion.getRegionWidth());
        logo.setSize(logoWidth, logoHeight);
        logo.setPosition(_WORLD_WIDTH / 2, 3 * _WORLD_HEIGHT / 4, Align.center);
        _stage.addActor(logo);

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(_blankButtonRegion);
        buttonStyle.font = _font;

        TextButton gyroButton = createTextButton("Gyroscope", buttonStyle, _GYRO_BUTTON_SCALE);
        gyroButton.setPosition(_WORLD_WIDTH / 2 - gyroButton.getWidth() / 2, _WORLD_HEIGHT / 2 - gyroButton.getHeight() / 2);
        _stage.addActor(gyroButton);

        TextButton touchPadButton = createTextButton("TouchPad", buttonStyle, _TOUCHPAD_BUTTON_SCALE);
        touchPadButton.setPosition(_WORLD_WIDTH / 2 - touchPadButton.getWidth() / 2, gyroButton.getY() - touchPadButton.getHeight() * 1.5f);
        _stage.addActor(touchPadButton);

        ImageButton exitButton = createImageButton(_exitButtonRegion, _EXIT_BUTTON_SCALE);
        exitButton.setPosition(_WORLD_WIDTH / 2 - exitButton.getWidth() / 2, touchPadButton.getY() - exitButton.getHeight() * 1.5f);
        _stage.addActor(exitButton);

        gyroButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener() {
            @Override
            public void tap(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int count, int button) {
                if (_DEBUG) Gdx.app.log(_TAG, "Gyroscope button tapped.");
        _game.setScreen(new GameScreen(StartScreen.this, true));
            }
        });

        touchPadButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener() {
            @Override
            public void tap(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int count, int button) {
                if (_DEBUG) Gdx.app.log(_TAG, "TouchPad button tapped.");
                _game.setScreen(new GameScreen(StartScreen.this, false));
            }
        });

        exitButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener() {
            @Override
            public void tap(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int count, int button) {
                if (_DEBUG) Gdx.app.log(_TAG, "Exit button tapped.");
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void hide() {
        Gdx.app.log(_TAG, "🛑 StartScreen caché (hide() appelé).");
        Gdx.input.setInputProcessor(null);
        if (_backgroundMusic != null) {
            _backgroundMusic.stop();
        }
    }

    public Stage getStage(){
        return _stage;
    }
    private TextButton createTextButton(String text, TextButtonStyle style, float scaleFactor) {
        float buttonWidth = (_blankButtonRegion.getRegionWidth() / 3f) * scaleFactor;
        float buttonHeight = (_blankButtonRegion.getRegionHeight() / 3f) * scaleFactor;

        TextButton button = new TextButton(text, style);
        button.setSize(buttonWidth, buttonHeight);
        return button;
    }

    public Game getGame() {
        return _game;
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
        _stage.act(delta);
        _stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        _WORLD_WIDTH = width;
        _WORLD_HEIGHT = height;
        _stage.getViewport().update(width, height, true);
        if (_DEBUG) Gdx.app.log(_TAG, "StartScreen resize() called: width=" + width + ", height=" + height);
    }

    public void setScreen(ScreenAdapter screen) {
        if (screen == null) {
            Gdx.app.error(_TAG, "❌ ERREUR: setScreen() appelé avec un écran null !");
            return;
        }

        Gdx.app.log(_TAG, "🔄 Changement d'écran vers " + screen.getClass().getSimpleName());
        _game.setScreen(screen);
    }
    @Override
    public void dispose() {
        if (_DEBUG) Gdx.app.log(_TAG, "StartScreen dispose() called.");
        if (_DEBUG) Gdx.app.log(_TAG, "🗑️ Nettoyage de StartScreen...");

        if (_backgroundMusic != null) {
            _backgroundMusic.dispose();
            _backgroundMusic = null;
            Gdx.app.log(_TAG, "🎵 Musique d'ambiance supprimée.");
        }

        if (_stage != null) _stage.dispose();
        if (_atlas != null) _atlas.dispose();
        if (_font != null) _font.dispose();
    }
}
