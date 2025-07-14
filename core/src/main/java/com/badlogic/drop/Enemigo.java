package com.badlogic.drop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Enemigo {
    private Sprite bulletSprite;
    private Array<Texture> bulletTextures;
    private Rectangle hitbox;
    private float velocidad;
    private boolean activo;
    private float animationTimer;
    private int currentFrame;
    private float frameTime = 0.1f; // Tiempo entre frames en segundos

    public Enemigo(float x, float y) {
        // Cargar todas las texturas de balas
        bulletTextures = new Array<>();
        bulletTextures.add(new Texture("Bullet/Bullet (1).png"));
        bulletTextures.add(new Texture("Bullet/Bullet (2).png"));
        bulletTextures.add(new Texture("Bullet/Bullet (3).png"));
        bulletTextures.add(new Texture("Bullet/Bullet (4).png"));
        bulletTextures.add(new Texture("Bullet/Bullet (5).png"));

        // Configurar sprite
        bulletSprite = new Sprite(bulletTextures.get(0));
        bulletSprite.setSize(0.3f, 0.5f); // Reducido de 0.5f, 0.8f
        bulletSprite.setPosition(x, y);

        // Configurar propiedades
        velocidad = 3f;
        activo = true;
        animationTimer = 0f;
        currentFrame = 0;

        // Configurar hitbox
        hitbox = new Rectangle();
        updateHitbox();
    }

    public void update(float delta) {
        if (activo) {
            // Actualizar animación
            animationTimer += delta;
            if (animationTimer >= frameTime) {
                currentFrame = (currentFrame + 1) % bulletTextures.size;
                bulletSprite.setTexture(bulletTextures.get(currentFrame));
                animationTimer = 0f;
            }
            
            // Mover la bala hacia la izquierda
            bulletSprite.translateX(-velocidad * delta);
            updateHitbox();

            // Desactivar si sale de la pantalla por la izquierda
            if (bulletSprite.getX() + bulletSprite.getWidth() < 0) {
                activo = false;
            }
        }
    }

    private void updateHitbox() {
        hitbox.set(bulletSprite.getX(), bulletSprite.getY(),
                bulletSprite.getWidth(), bulletSprite.getHeight());
    }

    public boolean colisionaCon(Rectangle otroRectangulo) {
        return activo && hitbox.overlaps(otroRectangulo);
    }

    public void desactivar() {
        activo = false;
    }

    public void draw(SpriteBatch batch) {
        if (activo) {
            bulletSprite.draw(batch);
        }
    }

    public void dispose() {
        for (Texture texture : bulletTextures) {
            texture.dispose();
        }
        bulletTextures.clear();
    }

    // Getters
    public float getX() {
        return bulletSprite.getX();
    }

    public float getY() {
        return bulletSprite.getY();
    }

    public float getWidth() {
        return bulletSprite.getWidth();
    }

    public float getHeight() {
        return bulletSprite.getHeight();
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public boolean estaActivo() {
        return activo;
    }

    public Sprite getSprite() {
        return bulletSprite;
    }
}
