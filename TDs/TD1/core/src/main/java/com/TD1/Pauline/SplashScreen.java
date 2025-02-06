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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SplashScreen implements Screen {
    private Game game;
    private Stage stage;
    private Skin skin;
    private Preferences prefs;

    public SplashScreen(final Game game) {
        this.game = game;
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
        // (Optionnel) Vous pouvez définir un arrière-plan semi-transparent pour la table via le skin si souhaité
        table.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.5f)));
        stage.addActor(table);

        // Création des éléments d'interface avec un style agrandi
        Label titleLabel = new Label("Choose Control Mode", skin);
        titleLabel.setFontScale(2f); // Augmente la taille de la police
        TextButton touchpadButton = new TextButton("Touchpad", skin);
        TextButton gyroscopeButton = new TextButton("Gyroscope", skin);
        Label scoreLabel = new Label("Top Scores:\n1. " + prefs.getInteger("score1", 0) +
            "\n2. " + prefs.getInteger("score2", 0) +
            "\n3. " + prefs.getInteger("score3", 0), skin);
        scoreLabel.setFontScale(1.5f);

        // Ajout des listeners aux boutons
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

        // Organisation des éléments dans la table
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
