package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameOverScreen implements Screen {

    final Drop game;
    final int tiempoSobrevivido;

    public GameOverScreen(final Drop game, int tiempoSobrevivido) {
        this.game = game;
        this.tiempoSobrevivido = tiempoSobrevivido;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();
        game.font.draw(game.batch, "¡GAME OVER!", 2, 3f);
        game.font.draw(game.batch, "Sobreviviste: " + tiempoSobrevivido + " segundos", 1.5f, 2.5f);
        game.font.draw(game.batch, "Toca para volver al menú", 1.5f, 2f);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
