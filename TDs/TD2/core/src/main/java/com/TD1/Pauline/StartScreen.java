package com.TD1.Pauline;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class StartScreen extends ScreenAdapter {

    private static final boolean DEBUG = true;
    private static final String TAG = "MYAPP";
    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 640;

    private Stage stage;
    private Texture backgroundTexture;
    private Texture titleTexture;
    private Skin skin;
    private final Game game;

    public StartScreen(Game game) {
        this.game = game;
        if (DEBUG) Gdx.app.log(TAG, "StartScreen constructor called.");
    }

    @Override
    public void show() {
        if (DEBUG) Gdx.app.log(TAG, "StartScreen show() called.");
        stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("bg.png"));
        if (DEBUG) Gdx.app.log(TAG, "Loaded bg.png");
        Image background = new Image(new TextureRegion(backgroundTexture));
        stage.addActor(background);

        titleTexture = new Texture(Gdx.files.internal("title.png"));
        if (DEBUG) Gdx.app.log(TAG, "Loaded title.png");
        Image title = new Image(new TextureRegion(titleTexture));
        title.setPosition(WORLD_WIDTH / 2, 3 * WORLD_HEIGHT / 4, Align.center);
        stage.addActor(title);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        if (DEBUG) Gdx.app.log(TAG, "Loaded skin");

        TextButton gyroButton = new TextButton("Gyroscope", skin);
        gyroButton.setSize(150, 50);
        gyroButton.setPosition(WORLD_WIDTH / 4 - gyroButton.getWidth() / 2, WORLD_HEIGHT / 4 - gyroButton.getHeight() / 2);
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
        touchButton.setSize(150, 50);
        touchButton.setPosition(3 * WORLD_WIDTH / 4 - touchButton.getWidth() / 2, WORLD_HEIGHT / 4 - touchButton.getHeight() / 2);
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
        clearScreen();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (DEBUG) Gdx.app.log(TAG, "StartScreen resize() called: width=" + width + ", height=" + height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (DEBUG) Gdx.app.log(TAG, "StartScreen dispose() called.");
        stage.dispose();
        backgroundTexture.dispose();
        titleTexture.dispose();
        skin.dispose();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
