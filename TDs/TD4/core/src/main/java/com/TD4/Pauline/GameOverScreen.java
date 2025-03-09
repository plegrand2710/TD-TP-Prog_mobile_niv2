package com.TD4.Pauline;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
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

    private final StartScreen _startScreen;
    private TextureRegion _gameOverRegion;
    private TextureRegion _backgroundRegion;
    private TextureRegion _buttonRegion;
    private SpriteBatch _batch;
    private BitmapFont _bitmapFont;
    private GlyphLayout _glyphLayout;
    private OrthographicCamera _camera;
    private Viewport _viewport;
    private final int _finalScore;
    private boolean _useGyroscope;
    private Stage _stage;
    private ImageButton _menuButton;
    private ShapeRenderer _shapeRenderer;
    private TextureAtlas _atlas;

    private float _scaleFactor;
    private float _gameOverWidth;
    private float _gameOverHeight;
    private float _drawX;
    private float _drawY;

    private float _menuX, _menuY, _menuWidth, _menuHeight;

    private Image _restartOverlay;
    private Image _menuOverlay;

    private Music _backgroundMusic;

    private float _restartX, _restartY, _restartWidth, _restartHeight;

    public GameOverScreen(StartScreen startScreen, TextureAtlas atlas, int finalScore, boolean useGyroscope) {
        this._startScreen = startScreen;
        this._atlas = atlas;
        this._finalScore = finalScore;
        this._useGyroscope = useGyroscope;

        initializeGameOverScreen();
        initializeCameraAndViewport();
        initializeRenderers();
        initializeStage();
        initializeBackgroundMusic();

        setupUI();
    }


    private void initializeGameOverScreen() {
        this._gameOverRegion = _atlas.findRegion("Game Over GUI");
        this._backgroundRegion = _atlas.findRegion("Game Background");
        this._buttonRegion = _atlas.findRegion("Blank Button-2");
    }

    private void initializeCameraAndViewport() {
        float WORLD_WIDTH = Gdx.graphics.getWidth();
        float WORLD_HEIGHT = Gdx.graphics.getHeight();

        _camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
        _camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        _viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, _camera);

        _scaleFactor = 0.8f;
        _gameOverWidth = _gameOverRegion.getRegionWidth() * _scaleFactor;
        _gameOverHeight = _gameOverRegion.getRegionHeight() * _scaleFactor;
        _drawX = (_viewport.getWorldWidth() - _gameOverWidth) / 2;
        _drawY = (_viewport.getWorldHeight() - _gameOverHeight) / 2 ;

        _menuWidth = 260;
        _menuHeight = 150;
        _menuX = _viewport.getWorldWidth() / 2 - _menuWidth / 2;
        _menuY = _drawY - _menuHeight + 70;
    }

    private void initializeRenderers() {
        _batch = new SpriteBatch();
        _bitmapFont = new BitmapFont();
        _bitmapFont.getData().setScale(2f);
        _glyphLayout = new GlyphLayout();
        _shapeRenderer = new ShapeRenderer();
    }

    private void initializeStage() {
        _stage = new Stage(_viewport);
        Gdx.input.setInputProcessor(_stage);
    }

    private void initializeBackgroundMusic() {
        _backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("ambianceSound1.wav"));
        _backgroundMusic.setLooping(true);
        _backgroundMusic.setVolume(0.9f);
        _backgroundMusic.setPosition(2f);
        _backgroundMusic.play();
    }


    private void setupUI() {
        setupGameOverImage();
        setupRestartOverlay();
        setupMenuOverlay();
        setupMenuButton();
        setupMenuTextButton();
    }


    private void setupGameOverImage() {
        Image gameOverImage = new Image(new TextureRegionDrawable(_gameOverRegion));
        gameOverImage.setSize(_gameOverWidth, _gameOverHeight);
        gameOverImage.setPosition(_drawX, _drawY);
        _stage.addActor(gameOverImage);
    }


    private void setupRestartOverlay() {
        _restartX = _drawX + _gameOverWidth * 0.21f;
        _restartY = _drawY + _gameOverHeight * 0.20f;
        _restartWidth = _gameOverWidth * 0.605f;
        _restartHeight = _gameOverHeight * 0.15f;

        _restartOverlay = new Image(new TextureRegionDrawable(_atlas.findRegion("Blank Button")));
        _restartOverlay.setSize(_restartWidth, _restartHeight);
        _restartOverlay.setPosition(_restartX, _restartY);
        _restartOverlay.setVisible(false);
        _stage.addActor(_restartOverlay);
    }

    private void setupMenuOverlay() {
        _menuOverlay = new Image(new TextureRegionDrawable(_atlas.findRegion("Blank Button")));
        _menuOverlay.setSize(_menuWidth, _menuHeight);
        _menuOverlay.setPosition(_menuX, _menuY + 100);
        _menuOverlay.setVisible(false);
        _stage.addActor(_menuOverlay);
    }

    private void setupMenuButton() {
        _menuButton = new ImageButton(new TextureRegionDrawable(_buttonRegion));
        _menuButton.setSize(200, 100);
        _menuButton.setPosition(_viewport.getWorldWidth() / 2 - 100, _drawY + 45);

        _stage.addActor(_menuButton);
    }

    private void setupMenuTextButton() {
        TextButton.TextButtonStyle textStyle = createTextStyle();
        TextButton menuTextButton = new TextButton("Menu", textStyle);
        menuTextButton.setSize(_menuWidth, _menuHeight);
        menuTextButton.setPosition(_menuX, _menuY + 100);

        TextureRegionDrawable clickedButtonDrawable = new TextureRegionDrawable(_atlas.findRegion("Blank Button"));
        ImageButton.ImageButtonStyle newStyle = new ImageButton.ImageButtonStyle(_menuButton.getStyle());
        newStyle.imageUp = clickedButtonDrawable;

        menuTextButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Changement d'image du bouton...");
                _menuButton.setStyle(newStyle);
                _menuButton.invalidate();
                _stage.act();

                handleMenuReturn();
            }
        });

        _stage.addActor(menuTextButton);
    }

    private void handleMenuReturn() {
        _useGyroscope = false;
        Game game = _startScreen.getGame();
        StartScreen newScreen = new StartScreen(_startScreen);
        Gdx.input.setInputProcessor(null);

        if (_backgroundMusic != null) {
            _backgroundMusic.dispose();
            _backgroundMusic = null;
            Gdx.app.log(TAG, "🎵 Musique d'ambiance supprimée.");
        }

        Gdx.app.postRunnable(() -> {
            game.setScreen(newScreen);
            Gdx.input.setInputProcessor(newScreen.getStage());
            Gdx.app.log(TAG, "✅ Retour à StartScreen !");
        });
    }


    public void render(float delta) {
        clearScreen();

        _batch.setProjectionMatrix(_camera.projection);
        _batch.setTransformMatrix(_camera.view);
        _batch.begin();

        if (_backgroundRegion != null) {
            _batch.draw(_backgroundRegion, 0, 0, _viewport.getWorldWidth(), _viewport.getWorldHeight());
        }

        TextButton.TextButtonStyle textStyle = createTextStyle();
        TextButton scoreText = new TextButton("Score: " + _finalScore, textStyle);
        scoreText.setSize(200, 50);
        scoreText.setPosition(_viewport.getWorldWidth() / 2 - 100, _viewport.getWorldHeight() / 2 - 100);
        _stage.addActor(scoreText);

        _batch.end();

        _stage.act(delta);
        _stage.draw();

        checkRestartClick();
        drawDebug();
    }

    private void checkRestartClick() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = _viewport.getWorldHeight() - Gdx.input.getY();

            if (touchX >= _restartX && touchX <= _restartX + _restartWidth &&
                touchY >= _restartY && touchY <= _restartY + _restartHeight) {

                Gdx.app.log(TAG, "Affichage de l'overlay sur Restart...");

                _restartOverlay.setVisible(true);
                _stage.act();

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        if (_backgroundMusic != null) {
                            _backgroundMusic.dispose();
                            _backgroundMusic = null;
                            Gdx.app.log(TAG, "🎵 Musique d'ambiance supprimée.");
                        }
                        _startScreen.setScreen(new GameScreen(_startScreen, _useGyroscope));
                    }
                }, 1f);
            }
        }
    }



    private void drawDebug() {
        _shapeRenderer.setProjectionMatrix(_camera.combined);
        _shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        _shapeRenderer.setColor(Color.RED);
        _shapeRenderer.rect(_restartX, _restartY, _restartWidth, _restartHeight);

        _shapeRenderer.end();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private TextButton.TextButtonStyle createTextStyle() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = _bitmapFont;
        return style;
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
        _batch.dispose();
        _bitmapFont.dispose();
        _stage.dispose();
        _shapeRenderer.dispose();
        if (_backgroundMusic != null) {
            _backgroundMusic.dispose();
            _backgroundMusic = null;
            Gdx.app.log(TAG, "🎵 Musique d'ambiance supprimée.");
        }
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        Gdx.app.log(TAG, "🛑 GameOverScreen caché (hide() appelé).");
    }
}
