package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    final Drop game;

    Texture backgroundTexture;
    Sound hitSound;
    Music music;

    Jugador jugador;
    Vector2 touchPos;
    Array<Enemigo> enemigos;
    float enemyTimer;
    boolean juegoTerminado;
    float tiempoSobrevivido;

    public GameScreen(final Drop game) {
        this.game = game;

        // Cargar fondo y sonidos
        backgroundTexture = new Texture("BG.png");
        hitSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.setVolume(0.5F);

        // Crear jugador (avión)
        jugador = new Jugador(3.5f, 0.5f);

        touchPos = new Vector2();
        enemigos = new Array<>();
        juegoTerminado = false;
        tiempoSobrevivido = 0f;
        enemyTimer = 0f;
    }

    @Override
    public void show() {
        music.play();
    }

    @Override
    public void render(float delta) {
        if (!juegoTerminado) {
            tiempoSobrevivido += delta;
        }

        input();
        logic();
        draw();
    }

    private void input() {
        if (juegoTerminado) {
            // Si el juego terminó, reiniciar con cualquier toque
            if (Gdx.input.isTouched()) {
                reiniciarJuego();
            }
            return;
        }

        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        // Movimiento horizontal
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            jugador.mover(speed * delta, 0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            jugador.mover(-speed * delta, 0);
        }
        
        // Movimiento vertical
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            jugador.mover(0, speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            jugador.mover(0, -speed * delta);
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            game.viewport.unproject(touchPos);
            jugador.setPosition(touchPos.x - jugador.getWidth()/2, touchPos.y - jugador.getHeight()/2);
        }
    }

    private void logic() {
        if (juegoTerminado)
            return;

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
        float delta = Gdx.graphics.getDeltaTime();

        // Actualizar jugador
        jugador.update();

        // Mantener jugador dentro de la pantalla
        if (jugador.getX() < 0) {
            jugador.setPosition(0, jugador.getY());
        } else if (jugador.getX() + jugador.getWidth() > worldWidth) {
            jugador.setPosition(worldWidth - jugador.getWidth(), jugador.getY());
        }
        
        if (jugador.getY() < 0) {
            jugador.setPosition(jugador.getX(), 0);
        } else if (jugador.getY() + jugador.getHeight() > worldHeight) {
            jugador.setPosition(jugador.getX(), worldHeight - jugador.getHeight());
        }

        // Crear nuevos enemigos (balas)
        enemyTimer += delta;
        float intervaloEnemigos = Math.max(0.3f, 1.5f - (tiempoSobrevivido * 0.02f)); // Se acelera con el tiempo
        if (enemyTimer > intervaloEnemigos) {
            crearEnemigo();
            enemyTimer = 0;
        }

        // Actualizar enemigos y verificar colisiones
        for (int i = enemigos.size - 1; i >= 0; i--) {
            Enemigo enemigo = enemigos.get(i);
            enemigo.update(delta);

            // Verificar colisión con el jugador
            if (enemigo.colisionaCon(jugador.getHitbox())) {
                hitSound.play();
                jugador.perderVida();
                enemigo.desactivar();

                // Verificar si el jugador murió
                if (jugador.estaMuerto()) {
                    juegoTerminado = true;
                }
            }

            // Remover enemigos inactivos
            if (!enemigo.estaActivo()) {
                enemigo.dispose();
                enemigos.removeIndex(i);
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        // Dibujar fondo
        game.batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);

        // Dibujar jugador
        jugador.draw(game.batch);

        // Dibujar enemigos
        for (Enemigo enemigo : enemigos) {
            enemigo.draw(game.batch);
        }

        // Dibujar UI
        game.font.draw(game.batch, "Vidas: " + jugador.getVidas(), 0.1f, worldHeight - 0.1f);
        game.font.draw(game.batch, "Tiempo: " + (int) tiempoSobrevivido + "s", 0.1f, worldHeight - 0.5f);

        if (juegoTerminado) {
            game.font.draw(game.batch, "¡GAME OVER!", worldWidth / 2 - 1, worldHeight / 2 + 0.5f);
            game.font.draw(game.batch, "Sobreviviste: " + (int) tiempoSobrevivido + " segundos", worldWidth / 2 - 1.5f,
                    worldHeight / 2);
            game.font.draw(game.batch, "Toca para reiniciar", worldWidth / 2 - 1, worldHeight / 2 - 0.5f);
        }

        game.batch.end();
    }

    private void crearEnemigo() {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
        float y = MathUtils.random(0f, worldHeight - 0.5f); // Ahora la Y es aleatoria
        float x = worldWidth; // Aparecen desde el borde derecho

        Enemigo nuevoEnemigo = new Enemigo(x, y);
        enemigos.add(nuevoEnemigo);
    }

    private void reiniciarJuego() {
        // Limpiar enemigos
        for (Enemigo enemigo : enemigos) {
            enemigo.dispose();
        }
        enemigos.clear();

        // Reiniciar jugador
        jugador.restaurarVidas();
        jugador.setPosition(3.5f, 0.5f);

        // Reiniciar variables
        juegoTerminado = false;
        tiempoSobrevivido = 0f;
        enemyTimer = 0f;
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
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
        backgroundTexture.dispose();
        hitSound.dispose();
        music.dispose();
        jugador.dispose();

        for (Enemigo enemigo : enemigos) {
            enemigo.dispose();
        }
    }
}
