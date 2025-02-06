package com.TD1.Pauline;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;

public class SplashScreen1 implements Screen {
    private Game game;
    private Stage stage;
    private Skin skin;
    private Preferences prefs;

    // Utilisation d'une constante pour les logs
    public static final String APP_TAG = "SplashScreen";

    public SplashScreen1(final Game game) {
        this.game = game;
        Gdx.app.log(APP_TAG, "Constructor called");
    }

    @Override
    public void show() {
        Gdx.app.log(APP_TAG, "show() called");
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Gdx.app.log(APP_TAG, "Stage created and input processor set");

        // Création d'un skin minimal en code
        BitmapFont font = new BitmapFont(); // police par défaut
        skin = new Skin();
        skin.add("default-font", font);

        // Crée un pixmap 1x1 rempli de blanc pour servir de drawable de base
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        pixmap.dispose();

        // Définition du style par défaut pour les labels
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        // Définition du style pour les boutons
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        // "up" : bouton en état normal
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        // "down" : bouton pressé
        textButtonStyle.down = skin.newDrawable("white", Color.LIGHT_GRAY);
        // "over" : bouton survolé (si applicable)
        textButtonStyle.over = skin.newDrawable("white", Color.GRAY);
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.WHITE;
        skin.add("default", textButtonStyle);

        Gdx.app.log(APP_TAG, "Minimal skin created successfully");

        // Chargement des préférences
        prefs = Gdx.app.getPreferences("GameScores");
        Gdx.app.log(APP_TAG, "Preferences loaded");

        // Création de la table pour organiser l'UI
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        Gdx.app.log(APP_TAG, "Table created and added to stage");

        // Création des éléments d'interface
        Label titleLabel = new Label("Choose Control Mode", skin);
        titleLabel.setAlignment(Align.center);
        Gdx.app.log(APP_TAG, "Title label created");

        TextButton touchpadButton = new TextButton("Touchpad", skin);
        TextButton gyroscopeButton = new TextButton("Gyroscope", skin);
        Gdx.app.log(APP_TAG, "Buttons created");

        Label scoreLabel = new Label("Top Scores:\n1. " + prefs.getInteger("score1", 0) +
            "\n2. " + prefs.getInteger("score2", 0) +
            "\n3. " + prefs.getInteger("score3", 0), skin);
        scoreLabel.setAlignment(Align.center);
        Gdx.app.log(APP_TAG, "Score label created");

        // Ajout des listeners pour les boutons
        touchpadButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log(APP_TAG, "Touchpad button pressed");
                game.setScreen(new GameScreen(game, "touchpad"));
                return true;
            }
        });
        Gdx.app.log(APP_TAG, "Touchpad button listener added");

        gyroscopeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log(APP_TAG, "Gyroscope button pressed");
                game.setScreen(new GameScreen(game, "gyroscope"));
                return true;
            }
        });
        Gdx.app.log(APP_TAG, "Gyroscope button listener added");

        // Organisation des éléments dans la table
        table.add(titleLabel).padBottom(20).row();
        table.add(touchpadButton).padBottom(10).row();
        table.add(gyroscopeButton).padBottom(20).row();
        table.add(scoreLabel).padTop(10);
        Gdx.app.log(APP_TAG, "UI elements added to table");
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
        Gdx.app.log(APP_TAG, "resize() called with width: " + width + " height: " + height);
    }

    @Override
    public void pause() {
        Gdx.app.log(APP_TAG, "pause() called");
    }

    @Override
    public void resume() {
        Gdx.app.log(APP_TAG, "resume() called");
    }

    @Override
    public void hide() {
        Gdx.app.log(APP_TAG, "hide() called");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log(APP_TAG, "dispose() called");
        stage.dispose();
        skin.dispose();
    }
}
