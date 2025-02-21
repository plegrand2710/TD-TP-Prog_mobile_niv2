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
    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 640;

    private Stage stage;
    private TextureAtlas atlas;
    private TextureRegion backgroundRegion;
    private TextureRegion titleRegion;
    private Skin skin;
    private final Game game;

    public StartScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Charge l'atlas et récupère les régions pour le fond et le titre
        atlas = new TextureAtlas(Gdx.files.internal("space_warrior.atlas"));
        backgroundRegion = atlas.findRegion("background");
        titleRegion = atlas.findRegion("title");

        Image background = new Image(backgroundRegion);
        stage.addActor(background);

        Image title = new Image(titleRegion);
        title.setPosition(WORLD_WIDTH / 2, 3 * WORLD_HEIGHT / 4, Align.center);
        stage.addActor(title);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Bouton Gyroscope
        TextButton gyroButton = new TextButton("Gyroscope", skin);
        gyroButton.setSize(150, 50);
        gyroButton.setPosition(WORLD_WIDTH / 4 - gyroButton.getWidth() / 2, WORLD_HEIGHT / 4 - gyroButton.getHeight() / 2);
        gyroButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.setScreen(new GameScreen(true));
                dispose();
            }
        });
        stage.addActor(gyroButton);

        // Bouton TouchPad
        TextButton touchButton = new TextButton("TouchPad", skin);
        touchButton.setSize(150, 50);
        touchButton.setPosition(3 * WORLD_WIDTH / 4 - touchButton.getWidth() / 2, WORLD_HEIGHT / 4 - touchButton.getHeight() / 2);
        touchButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.setScreen(new GameScreen(false));
                dispose();
            }
        });
        stage.addActor(touchButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        atlas.dispose();
        skin.dispose();
    }
}
