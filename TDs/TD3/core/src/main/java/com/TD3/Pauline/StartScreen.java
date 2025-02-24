package com.TD3.Pauline;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class StartScreen extends ScreenAdapter {
    private static final boolean DEBUG = false;
    private static final String TAG = "SpaceWarriorApp";

    private float WORLD_WIDTH;
    private float WORLD_HEIGHT;

    private Stage stage;
    private TextureAtlas atlas;
    private TextureRegion backgroundRegion;
    private TextureRegion titleRegion;
    private Skin skin;
    private final Game game;

    public StartScreen(Game game) {
        this.game = game;
        this.WORLD_WIDTH = Gdx.graphics.getWidth();
        this.WORLD_HEIGHT = Gdx.graphics.getHeight();
        if (DEBUG) Gdx.app.log(TAG, "StartScreen constructor called.");
    }

    @Override
    public void show() {
        float buttonWidth = WORLD_WIDTH * 0.3f;
        float buttonHeight = WORLD_HEIGHT * 0.1f;

        if (DEBUG) Gdx.app.log(TAG, "StartScreen show() called.");
        stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        Gdx.input.setInputProcessor(stage);

        atlas = new TextureAtlas(Gdx.files.internal("space_warrior.atlas"));
        if (DEBUG) Gdx.app.log(TAG, "récupération de l'atlas.");

        backgroundRegion = atlas.findRegion("Game Background");
        titleRegion = atlas.findRegion("Game Logo");

        Image background = new Image(backgroundRegion);
        background.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        stage.addActor(background);

        Image title = new Image(titleRegion);
        float originalWidth = titleRegion.getRegionWidth();
        float originalHeight = titleRegion.getRegionHeight();
        float scaleFactor = WORLD_WIDTH / (originalWidth * 2);

        title.setSize(originalWidth * scaleFactor, originalHeight * scaleFactor);
        title.setPosition(WORLD_WIDTH / 2, 3 * WORLD_HEIGHT / 4, Align.center);
        stage.addActor(title);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        TextButton gyroButton = new TextButton("Gyroscope", skin);
        gyroButton.setSize(buttonWidth, buttonHeight);
        gyroButton.setPosition(WORLD_WIDTH / 4 - buttonWidth / 2, WORLD_HEIGHT / 4 - buttonHeight / 2);
        gyroButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if (DEBUG) Gdx.app.log(TAG, "Gyroscope button tapped.");
                game.setScreen(new GameScreen(true));
                dispose();
            }
        });
        stage.addActor(gyroButton);

        TextButton touchButton = new TextButton("TouchPad", skin);
        touchButton.setSize(buttonWidth, buttonHeight);
        touchButton.setPosition(3 * WORLD_WIDTH / 4 - buttonWidth / 2, WORLD_HEIGHT / 4 - buttonHeight / 2);
        touchButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if (DEBUG) Gdx.app.log(TAG, "TouchPad button tapped.");
                game.setScreen(new GameScreen(false));
                dispose();
            }
        });
        stage.addActor(touchButton);
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
        if (DEBUG) Gdx.app.log(TAG, "StartScreen dispose() called. Starting resource cleanup.");

        if (stage != null) {
            stage.dispose();
            if (DEBUG) Gdx.app.log(TAG, "Stage resources disposed successfully.");
        } else {
            if (DEBUG) Gdx.app.log(TAG, "Stage was already null or not initialized.");
        }

        if (atlas != null) {
            atlas.dispose();
            if (DEBUG) Gdx.app.log(TAG, "Atlas resources disposed successfully.");
        } else {
            if (DEBUG) Gdx.app.log(TAG, "Atlas was already null or not initialized.");
        }

        if (skin != null) {
            skin.dispose();
            if (DEBUG) Gdx.app.log(TAG, "Skin resources disposed successfully.");
        } else {
            if (DEBUG) Gdx.app.log(TAG, "Skin was already null or not initialized.");
        }

        if (DEBUG) Gdx.app.log(TAG, "Resource cleanup completed.");
    }
}
