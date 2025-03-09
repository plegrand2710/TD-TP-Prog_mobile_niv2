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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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

        Table table = new Table();
        table.setFillParent(true);
        _stage.addActor(table);

        Label title = new Label("🏆 Hall of Fame 🏆", _skin, "default");
        title.setFontScale(2f);
        title.setColor(Color.GOLD);
        table.add(title).padBottom(20);
        table.row();

        if (DEBUG) Gdx.app.log(TAG, "📥 Chargement des scores...");
        loadScores(table);
        if (DEBUG) Gdx.app.log(TAG, "✅ Scores chargés avec succès.");

        TextButton backButton = new TextButton("Retour", _skin);
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

        table.row().padTop(20);
        table.add(backButton);

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
        String rawScores = prefs.getString(KEY_SCORES, ""); // Récupère la liste de scores

        Array<String> scores = new Array<>();
        if (!rawScores.isEmpty()) {
            String[] entries = rawScores.split(";");
            for (String entry : entries) {
                scores.add(entry);
            }
        }

        // Tri du plus grand au plus petit
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
        // Laisser le Game appeler hide() puis dispose() pour nettoyer l'écran
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
