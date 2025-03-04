package com.TD3.Pauline;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Main extends Game {
    public static final boolean DEBUG = true;
    public static final String TAG = "SpaceWarriorApp";

    private TextureAtlas atlas;
    private BitmapFont font;
    private TextureRegion backgroundRegion, logoRegion, guiBoxRegion, exitButtonRegion, blankButtonRegion;

    @Override
    public void create() {
        Gdx.app.log(TAG, "create() called. Starting application.");

        atlas = new TextureAtlas(Gdx.files.internal("space_warrior.atlas"));
        font = new BitmapFont();
        font.getData().setScale(1.2f);

        backgroundRegion = atlas.findRegion("Game Background");
        guiBoxRegion = atlas.findRegion("GUI box");
        logoRegion = atlas.findRegion("Game Logo");
        exitButtonRegion = atlas.findRegion("Exit Button");
        blankButtonRegion = atlas.findRegion("Blank Button-2");

        // 🚀 Debug : Vérifier si les textures sont bien chargées
        if (backgroundRegion == null) Gdx.app.log(TAG, "❌ ERREUR: backgroundRegion est null");
        if (guiBoxRegion == null) Gdx.app.log(TAG, "❌ ERREUR: guiBoxRegion est null");
        if (logoRegion == null) Gdx.app.log(TAG, "❌ ERREUR: logoRegion est null");
        if (exitButtonRegion == null) Gdx.app.log(TAG, "❌ ERREUR: exitButtonRegion est null");
        if (blankButtonRegion == null) Gdx.app.log(TAG, "❌ ERREUR: blankButtonRegion est null");

        Gdx.app.log(TAG, "setscreen() called. Starting application.");

        setScreen(new StartScreen(this, atlas, font, backgroundRegion, logoRegion, guiBoxRegion, exitButtonRegion, blankButtonRegion));
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public BitmapFont getFont() {
        return font;
    }

    public TextureRegion getBackgroundRegion() {
        return backgroundRegion;
    }

    public TextureRegion getLogoRegion() {
        return logoRegion;
    }

    public TextureRegion getGuiBoxRegion() {
        return guiBoxRegion;
    }

    public TextureRegion getExitButtonRegion() {
        return exitButtonRegion;
    }

    public TextureRegion getBlankButtonRegion() {
        return blankButtonRegion;
    }
    @Override
    public void dispose() {
        super.dispose();
        Gdx.app.log(TAG, "🛑 Fermeture de l'application, nettoyage des ressources.");
        atlas.dispose();
        font.dispose();
    }
}
