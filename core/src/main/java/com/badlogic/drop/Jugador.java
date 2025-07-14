package com.badlogic.drop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Jugador {
    private Sprite planeSprite;
    private Texture flyTexture;
    private Texture deadTexture;
    private int vidas;
    private int vidasMaximas;
    private boolean estaMuerto;
    private Rectangle hitbox;

    public Jugador(float x, float y) {
        // Cargar texturas del avión
        flyTexture = new Texture("Plane/Fly (1).png");
        deadTexture = new Texture("Plane/Dead (1).png");

        // Configurar sprite
        planeSprite = new Sprite(flyTexture);
        planeSprite.setSize(1.2f, 1.2f);
        planeSprite.setPosition(x, y);

        // Configurar vidas
        vidasMaximas = 3;
        vidas = vidasMaximas;
        estaMuerto = false;

        // Configurar hitbox
        hitbox = new Rectangle();
        updateHitbox();
    }

    public void update() {
        updateHitbox();

        // Cambiar sprite si está muerto
        if (estaMuerto && planeSprite.getTexture() != deadTexture) {
            planeSprite.setTexture(deadTexture);
        }
    }

    private void updateHitbox() {
        hitbox.set(planeSprite.getX() - 0.5f, planeSprite.getY() - 0.5f,
                planeSprite.getWidth() - 0.5f, planeSprite.getHeight() - 0.5f);
    }

    public void mover(float deltaX, float deltaY) {
        if (!estaMuerto) {
            planeSprite.translate(deltaX, deltaY);
        }
    }

    public void setPosition(float x, float y) {
        if (!estaMuerto) {
            planeSprite.setPosition(x, y);
        }
    }

    public void setCenterX(float x) {
        if (!estaMuerto) {
            planeSprite.setCenterX(x);
        }
    }

    public void perderVida() {
        if (!estaMuerto) {
            vidas--;
            if (vidas <= 0) {
                estaMuerto = true;
            }
        }
    }

    public void restaurarVidas() {
        vidas = vidasMaximas;
        estaMuerto = false;
        planeSprite.setTexture(flyTexture);
    }

    public void draw(SpriteBatch batch) {
        planeSprite.draw(batch);
    }

    public void dispose() {
        flyTexture.dispose();
        deadTexture.dispose();
    }

    // Getters
    public float getX() {
        return planeSprite.getX();
    }

    public float getY() {
        return planeSprite.getY();
    }

    public float getWidth() {
        return planeSprite.getWidth();
    }

    public float getHeight() {
        return planeSprite.getHeight();
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public int getVidas() {
        return vidas;
    }

    public int getVidasMaximas() {
        return vidasMaximas;
    }

    public boolean estaMuerto() {
        return estaMuerto;
    }

    public Sprite getSprite() {
        return planeSprite;
    }
}
