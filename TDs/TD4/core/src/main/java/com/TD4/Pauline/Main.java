package com.TD4.Pauline;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Box2D;

public class Main extends Game {
    public static final boolean _DEBUG = true;
    public static final String _TAG = "SpaceWarriorApp";

    private TextureAtlas _atlas;
    private BitmapFont _font;
    private TextureRegion _backgroundRegion, _logoRegion, _guiBoxRegion, _exitButtonRegion, _blankButtonRegion;

    @Override
    public void create() {
        Box2D.init();

        Gdx.app.log(_TAG, "create() called. Starting application.");

        _atlas = new TextureAtlas(Gdx.files.internal("space_warrior.atlas"));
        _font = new BitmapFont();
        _font.getData().setScale(1.2f);

        _backgroundRegion = _atlas.findRegion("Game Background");
        _guiBoxRegion = _atlas.findRegion("GUI box");
        _logoRegion = _atlas.findRegion("Game Logo");
        _exitButtonRegion = _atlas.findRegion("Exit Button");
        _blankButtonRegion = _atlas.findRegion("Blank Button-2");

        if (_backgroundRegion == null) Gdx.app.log(_TAG, "❌ ERREUR: backgroundRegion est null");
        if (_guiBoxRegion == null) Gdx.app.log(_TAG, "❌ ERREUR: guiBoxRegion est null");
        if (_logoRegion == null) Gdx.app.log(_TAG, "❌ ERREUR: logoRegion est null");
        if (_exitButtonRegion == null) Gdx.app.log(_TAG, "❌ ERREUR: exitButtonRegion est null");
        if (_blankButtonRegion == null) Gdx.app.log(_TAG, "❌ ERREUR: blankButtonRegion est null");

        Gdx.app.log(_TAG, "setscreen() called. Starting application.");

        setScreen(new StartScreen(this, _atlas, _font, _backgroundRegion, _logoRegion, _guiBoxRegion, _exitButtonRegion, _blankButtonRegion));
    }

    public TextureAtlas get_atlas() {
        return _atlas;
    }

    public BitmapFont get_font() {
        return _font;
    }

    public TextureRegion get_backgroundRegion() {
        return _backgroundRegion;
    }

    public TextureRegion get_logoRegion() {
        return _logoRegion;
    }

    public TextureRegion get_guiBoxRegion() {
        return _guiBoxRegion;
    }

    public TextureRegion get_exitButtonRegion() {
        return _exitButtonRegion;
    }

    public TextureRegion get_blankButtonRegion() {
        return _blankButtonRegion;
    }
    @Override
    public void dispose() {
        super.dispose();
        Gdx.app.log(_TAG, "🛑 Fermeture de l'application, nettoyage des ressources.");
        _atlas.dispose();
        _font.dispose();
    }
}
