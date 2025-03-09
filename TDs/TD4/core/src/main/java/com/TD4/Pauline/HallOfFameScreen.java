package com.TD4.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.Comparator;

public class HallOfFameScreen extends ScreenAdapter {
    private static final String PREFS_NAME = "game_prefs";
    private static final String KEY_PSEUDO = "player_pseudo";
    private static final String KEY_SCORES = "saved_scores";

    private static final boolean DEBUG = true;
    private static final String TAG = "SpaceWarriorApp";

    private final StartScreen _startScreen;
    private Stage _stage;
    private Viewport _viewport;
    private OrthographicCamera _camera;
    private SpriteBatch _batch;
    private BitmapFont _font;
    private Skin _skin;

    public HallOfFameScreen(StartScreen startScreen) {
        this._startScreen = startScreen;
    }

    @Override
    public void show() {
        if (DEBUG) Gdx.app.log(TAG, "🟢 Chargement du Hall of Fame...");

        _camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), _camera);
        _stage = new Stage(_viewport);
        Gdx.input.setInputProcessor(_stage);

        _batch = new SpriteBatch();
        _font = new BitmapFont();
        _font.getData().setScale(2f);

        try {
            _skin = new Skin(Gdx.files.internal("uiskin.json"));
            if (DEBUG) Gdx.app.log(TAG, "✅ Skin chargé avec succès.");
        } catch (Exception e) {
            Gdx.app.error(TAG, "❌ ERREUR: Impossible de charger 'uiskin.json' !");
        }

        Image backgroundImage = new Image(new TextureRegionDrawable(_startScreen.getAtlas().findRegion("Background-layer")));
        backgroundImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _stage.addActor(backgroundImage);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        _stage.addActor(rootTable);



        Label title = new Label("Hall of Fame", _skin, "default");
        title.setFontScale(2f);
        title.setColor(Color.GOLD);
        rootTable.add(title).padBottom(10).padTop(40);
        rootTable.row();

        Table scoreTable = new Table();
        ScrollPane scrollPane = new ScrollPane(scoreTable, _skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setOverscroll(false, true);
        scrollPane.setStyle(new ScrollPane.ScrollPaneStyle());

        loadScores(scoreTable);

        rootTable.add(scrollPane).expand().fill().height(500);
        rootTable.row().padTop(10);

        TextButton.TextButtonStyle backButtonStyle = new TextButton.TextButtonStyle();
        backButtonStyle.up = new TextureRegionDrawable(_startScreen.getAtlas().findRegion("Blank Button-2"));
        backButtonStyle.down = new TextureRegionDrawable(_startScreen.getAtlas().findRegion("Blank Button"));
        BitmapFont smallFont = new BitmapFont();
        smallFont.getData().setScale(1.5f);
        backButtonStyle.font = smallFont;

        TextButton backButton = new TextButton("Retour", backButtonStyle);

        backButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                if (DEBUG) Gdx.app.log(TAG, "🔄 Retour à StartScreen...");

                Gdx.input.setInputProcessor(null);
                _stage.clear();

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        _startScreen.getGame().setScreen(new StartScreen(_startScreen));
                    }
                }, 0.1f);
            }
        });

        rootTable.add(backButton).width(180).height(60).padTop(20).padBottom(20);

        if (DEBUG) Gdx.app.log(TAG, "✅ Hall of Fame affiché avec succès !");
    }

    private void loadScores(Table table) {
        Array<String> scores = getSavedScores();

        if (scores.size == 0) {
            if (DEBUG) Gdx.app.log(TAG, "⚠️ Aucun score enregistré !");
            Label emptyLabel = new Label("Aucun score disponible.", _skin);
            emptyLabel.setFontScale(1.5f);
            emptyLabel.setColor(Color.RED);
            table.row();
            table.add(emptyLabel).pad(10);
            return;
        }

        int rank = 1;
        for (String entry : scores) {
            Label scoreEntry = new Label(rank + ". " + entry, _skin);
            scoreEntry.setFontScale(1.5f);
            scoreEntry.setColor(Color.WHITE);
            table.row();
            table.add(scoreEntry).pad(5);

            if (DEBUG) Gdx.app.log(TAG, "🏅 Ajouté: " + entry);
            rank++;
        }
    }

    private Array<String> getSavedScores() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        String rawScores = prefs.getString(KEY_SCORES, "");

        Array<String> scores = new Array<>();
        if (!rawScores.isEmpty()) {
            String[] entries = rawScores.split(";");
            for (String entry : entries) {
                scores.add(entry);
            }
        }

        scores.sort(Comparator.comparingInt(s -> -Integer.parseInt(s.split(" - ")[1])));
        return scores;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        _stage.act(delta);
        _stage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        _stage.dispose();
        _batch.dispose();
        _font.dispose();
        _skin.dispose();
        if (DEBUG) Gdx.app.log(TAG, "🗑️ HallOfFameScreen nettoyé.");
    }
}
