package com.TD1.Pauline;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;

public class SplashScreen implements Screen {
    private Game game;
    private Stage stage;
    private Skin skin;
    private Preferences prefs;
    private ScoresFileAdapter scoreAdapter;

    public SplashScreen(final Game game) {
        this.game = game;
        this.scoreAdapter = new ScoresFileAdapter();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        try {
            skin = new Skin(Gdx.files.internal("uiskin.json"));
        } catch (Exception e) {
        }

        prefs = Gdx.app.getPreferences("GameScores");


        if (!Gdx.files.internal("background.png").exists()) {
            System.out.println("background.png introuvable !");
        } else {
            System.out.println("background.png trouvé !");
        }
        Texture bgTexture = new Texture(Gdx.files.internal("background.png"));
        Image background = new Image(bgTexture);
        background.setFillParent(true);
        stage.addActor(background);

        Table table = new Table();
        table.setFillParent(true);
        table.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.5f)));
        stage.addActor(table);

        Label titleLabel = new Label("Choose Control Mode", skin);
        titleLabel.setFontScale(2f);
        TextButton touchpadButton = new TextButton("Touchpad", skin);
        TextButton gyroscopeButton = new TextButton("Gyroscope", skin);

        ArrayList<String> topScores = scoreAdapter.getTop3Scores();
        StringBuilder scoreText = new StringBuilder("Top Scores:\n");
        for (String scoreEntry : topScores) {
            scoreText.append(scoreEntry).append("\n");
        }
        Label scoreLabel = new Label(scoreText.toString(), skin);
        scoreLabel.setFontScale(1.5f);

        touchpadButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen(game, "touchpad"));
                return true;
            }
        });

        gyroscopeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen(game, "gyroscope"));
                return true;
            }
        });

        table.add(titleLabel).padBottom(40).expandX().row();
        table.add(touchpadButton).padBottom(20).width(300).height(60).row();
        table.add(gyroscopeButton).padBottom(40).width(300).height(60).row();
        table.add(scoreLabel).expandX();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
