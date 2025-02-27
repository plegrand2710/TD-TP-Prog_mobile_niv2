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

public class StartScreen extends ScreenAdapter {
    private static final boolean DEBUG = true;
    private static final String TAG = "SpaceWarriorApp";

    private float WORLD_WIDTH;
    private float WORLD_HEIGHT;

    private Stage stage;
    private TextureAtlas atlas;
    private TextureRegion backgroundRegion;
    private TextureRegion logoRegion;
    private TextureRegion guiBoxRegion;
    private TextureRegion exitButtonRegion;
    private TextureRegion blankButtonRegion;

    private BitmapFont font;
    private final Game game;

    private static final float GYRO_BUTTON_SCALE = 1.5f;
    private static final float TOUCHPAD_BUTTON_SCALE = 1.5f;
    private static final float EXIT_BUTTON_SCALE = 1.2f;


    public StartScreen(Game game) {
        this.game = game;
        WORLD_WIDTH = Gdx.graphics.getWidth();
        WORLD_HEIGHT = Gdx.graphics.getHeight();
    }

    @Override
    public void show() {
        if (DEBUG) Gdx.app.log(TAG, "StartScreen show() called.");
        stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        atlas = new TextureAtlas(Gdx.files.internal("space_warrior.atlas"));
        backgroundRegion = atlas.findRegion("Game Background");
        guiBoxRegion = atlas.findRegion("GUI box");
        logoRegion = atlas.findRegion("Game Logo");
        exitButtonRegion = atlas.findRegion("Exit Button");
        blankButtonRegion = atlas.findRegion("Blank Button-2");

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);

        Image background = new Image(backgroundRegion);
        background.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        stage.addActor(background);

        Image guiBox = new Image(guiBoxRegion);
        float guiBoxOriginalWidth = guiBoxRegion.getRegionWidth();
        float guiBoxOriginalHeight = guiBoxRegion.getRegionHeight();
        float guiBoxScaleFactor = 0.35f;
        float guiBoxWidth = WORLD_WIDTH * guiBoxScaleFactor;
        float guiBoxHeight = guiBoxWidth * (guiBoxOriginalHeight / guiBoxOriginalWidth);
        guiBox.setSize(guiBoxWidth, guiBoxHeight);

        float verticalOffset = (WORLD_HEIGHT - guiBoxHeight) / 2;
        guiBox.setPosition(WORLD_WIDTH / 2 - guiBoxWidth / 2, verticalOffset);
        stage.addActor(guiBox);

        Image logo = new Image(logoRegion);
        float logoOriginalWidth = logoRegion.getRegionWidth();
        float logoOriginalHeight = logoRegion.getRegionHeight();
        float logoScaleFactor = 0.2f;
        float logoWidth = WORLD_WIDTH * logoScaleFactor;
        float logoHeight = logoWidth * (logoOriginalHeight / logoOriginalWidth);
        logo.setSize(logoWidth, logoHeight);
        logo.setPosition(WORLD_WIDTH / 2, 3 * WORLD_HEIGHT / 4, Align.center);
        stage.addActor(logo);

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(blankButtonRegion);
        buttonStyle.font = font;

        TextButton gyroButton = createTextButton("Gyroscope", buttonStyle, GYRO_BUTTON_SCALE);
        gyroButton.setPosition(WORLD_WIDTH / 2 - gyroButton.getWidth() / 2,
            WORLD_HEIGHT / 2 - gyroButton.getHeight() / 2);
        stage.addActor(gyroButton);

        TextButton touchPadButton = createTextButton("TouchPad", buttonStyle, TOUCHPAD_BUTTON_SCALE);
        touchPadButton.setPosition(WORLD_WIDTH / 2 - touchPadButton.getWidth() / 2,
            gyroButton.getY() - touchPadButton.getHeight() * 1.5f);
        stage.addActor(touchPadButton);

        ImageButton exitButton = createImageButton(exitButtonRegion, EXIT_BUTTON_SCALE);
        exitButton.setPosition(WORLD_WIDTH / 2 - exitButton.getWidth() / 2,
            touchPadButton.getY() - exitButton.getHeight() * 1.5f);
        stage.addActor(exitButton);

        gyroButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener() {
            @Override
            public void tap(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int count, int button) {
                if (DEBUG) Gdx.app.log(TAG, "Gyroscope button tapped.");
                game.setScreen(new GameScreen(true));
                dispose();
            }
        });

        touchPadButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener() {
            @Override
            public void tap(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int count, int button) {
                if (DEBUG) Gdx.app.log(TAG, "TouchPad button tapped.");
                game.setScreen(new GameScreen(false));
                dispose();
            }
        });

        exitButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener() {
            @Override
            public void tap(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int count, int button) {
                if (DEBUG) Gdx.app.log(TAG, "Exit button tapped.");
                Gdx.app.exit();
            }
        });


    }

    private TextButton createTextButton(String text, TextButtonStyle style, float scaleFactor) {
        float buttonWidth = (blankButtonRegion.getRegionWidth() / 3f) * scaleFactor;
        float buttonHeight = (blankButtonRegion.getRegionHeight() / 3f) * scaleFactor;

        TextButton button = new TextButton(text, style);
        button.setSize(buttonWidth, buttonHeight);
        return button;
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

    @Override
    public void dispose() {
        if (DEBUG) Gdx.app.log(TAG, "StartScreen dispose() called.");
        if (stage != null) stage.dispose();
        if (atlas != null) atlas.dispose();
        if (font != null) font.dispose();
    }
}
