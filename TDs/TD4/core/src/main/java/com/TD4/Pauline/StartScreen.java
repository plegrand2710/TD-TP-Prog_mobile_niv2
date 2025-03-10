package com.TD4.Pauline;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
    private TextField _pseudoField;
    private Preferences _preferences;
    private Skin _skin;

    private float _tablePosition;
    private Image _logo;
    private TextButton _gyroButton;
    private TextButton _touchPadButton;
    private ImageButton _exitButton;
    private TextButton _hallOfFameButton;

    private static final String PREFS_NAME = "game_prefs";
    private static final String KEY_PSEUDO = "player_pseudo";
    private static final String KEY_SCORE = "saved_scores";

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

        setUpGuiBox();

        setUpLogo();

        setUpTextField();

        setUpButtons();

        setUpListeners();
    }

    private void setUpGuiBox(){
        Image guiBox = new Image(_guiBoxRegion);
        float guiBoxScaleFactor = 0.35f;
        float guiBoxWidth = _WORLD_WIDTH * guiBoxScaleFactor;
        float guiBoxHeight = guiBoxWidth * ((float) _guiBoxRegion.getRegionHeight() / _guiBoxRegion.getRegionWidth());
        guiBox.setSize(guiBoxWidth, guiBoxHeight);
        guiBox.setPosition(_WORLD_WIDTH / 2 - guiBoxWidth / 2, (_WORLD_HEIGHT - guiBoxHeight) / 2);
        _stage.addActor(guiBox);
    }

    private void setUpLogo(){
        _logo = new Image(_logoRegion);
        float logoWidth = _WORLD_WIDTH * 0.2f;
        float logoHeight = logoWidth * ((float) _logoRegion.getRegionHeight() / _logoRegion.getRegionWidth());
        _logo.setSize(logoWidth, logoHeight);
        _logo.setPosition(_WORLD_WIDTH / 2, 3.1f * _WORLD_HEIGHT / 4, Align.center);
        _stage.addActor(_logo);
    }

    private void setUpTextField(){
        _preferences = Gdx.app.getPreferences(PREFS_NAME);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = _font;
        textFieldStyle.fontColor = Color.WHITE;

        textFieldStyle.background = new TextureRegionDrawable(_atlas.findRegion("Blank Button"));
        _skin = new Skin(Gdx.files.internal("uiskin.json"));

        Label pseudoLabel = new Label("Pseudo :", _skin);
        _pseudoField = new TextField("", textFieldStyle);
        textFieldStyle.background.setLeftWidth(40);
        _pseudoField.setText(_preferences.getString(KEY_PSEUDO, ""));
        _pseudoField.setMaxLength(20);
        Table table = new Table();
        table.setFillParent(true);
        _tablePosition = 1.5f * _WORLD_HEIGHT / 4;
        table.top().padTop(_tablePosition);

        table.add(pseudoLabel).padRight(10);
        table.add(_pseudoField).width(250).height(60);

        _stage.addActor(table);

    }

    private void setUpButtons(){
        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(_blankButtonRegion);
        buttonStyle.down = new TextureRegionDrawable(_atlas.findRegion("Blank Button"));
        buttonStyle.font = _font;

        _gyroButton = createTextButton("Gyroscope", buttonStyle, _GYRO_BUTTON_SCALE);
        _gyroButton.setPosition(_WORLD_WIDTH / 2 - _gyroButton.getWidth() / 2, (_tablePosition - _gyroButton.getHeight() / 2) +60);
        _stage.addActor(_gyroButton);

        _touchPadButton = createTextButton("TouchPad", buttonStyle, _TOUCHPAD_BUTTON_SCALE);
        _touchPadButton.setPosition(_WORLD_WIDTH / 2 - _touchPadButton.getWidth() / 2, (_gyroButton.getY() - _touchPadButton.getHeight())-20);
        _stage.addActor(_touchPadButton);

        _exitButton = createImageButton(_exitButtonRegion, _EXIT_BUTTON_SCALE);
        _exitButton.setPosition(
            _logo.getX() + _logo.getWidth() + _exitButton.getWidth() - 100,
            _logo.getY() + _logo.getHeight() + _exitButton.getHeight() - 125
        );
        _stage.addActor(_exitButton);

        _hallOfFameButton = createTextButton("Hall of Fame", buttonStyle, _GYRO_BUTTON_SCALE);
        _hallOfFameButton.setPosition(_WORLD_WIDTH / 2 - _hallOfFameButton.getWidth() / 2, (_touchPadButton.getY() - _hallOfFameButton.getHeight())-20);

        _stage.addActor(_hallOfFameButton);

    }

    private void setUpListeners(){
        _gyroButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener() {
            @Override
            public void tap(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int count, int button) {
                if (_DEBUG) Gdx.app.log(_TAG, "Gyroscope button tapped.");
                savePseudo();
                _game.setScreen(new GameScreen(StartScreen.this, true));
            }
        });

        _touchPadButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener() {
            @Override
            public void tap(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int count, int button) {
                if (_DEBUG) Gdx.app.log(_TAG, "TouchPad button tapped.");
                savePseudo();
                _game.setScreen(new GameScreen(StartScreen.this, false));
            }
        });

        _exitButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener() {
            @Override
            public void tap(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int count, int button) {
                if (_DEBUG) Gdx.app.log(_TAG, "Exit button tapped.");
                Gdx.app.exit();
            }
        });

        _hallOfFameButton.addListener(event -> {
            _game.setScreen(new HallOfFameScreen(this));
            return true;
        });
    }

    public void savePseudo() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putString(KEY_PSEUDO, _pseudoField.getText().trim());
        prefs.flush();
    }

    public String getSavedPseudo() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        return prefs.getString(KEY_PSEUDO, "Joueur");
    }

    public void saveScore(int score) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putInteger(KEY_SCORE, score);
        prefs.flush();
    }

    public int getSavedScore() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        return prefs.getInteger(KEY_SCORE, 0);
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

    public TextureAtlas getAtlas(){
        return _atlas;
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
