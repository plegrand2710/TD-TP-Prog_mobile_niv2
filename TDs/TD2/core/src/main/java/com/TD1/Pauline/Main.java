package com.TD1.Pauline;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class Main extends Game {
    public static final boolean DEBUG = true;
    public static final String TAG = "MYAPP";

    @Override
    public void create() {
        if (DEBUG) {
            Gdx.app.log(TAG, "create() called. Starting application.");
        }
        setScreen(new StartScreen(this));
    }
}
