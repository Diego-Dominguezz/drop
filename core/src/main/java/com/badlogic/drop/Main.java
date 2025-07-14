package com.badlogic.drop;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main implements ApplicationListener {
    Texture backgroundTexture;
    Texture bucketTexture;
    Texture dropTexture;
    Sound dropSound;
    Music music;

    Sprite bucketSprite;

    SpriteBatch spriteBatch;
    FitViewport viewport;
    Vector2 touchPos;
    Array<Sprite> dropSprites;

    float dropTimer;
    float dropSpeed = 2f;

    @Override
    public void create() {
        // Prepare your application here.
        backgroundTexture = new Texture("background.png");
        bucketTexture = new Texture("bucket.png");
        dropTexture = new Texture("drop.png");
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.play();

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5); // Set the viewport size to match

        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(1, 1);
        bucketSprite.setPosition(0f, 0f);

        touchPos = new Vector2();
        dropSprites = new Array<Sprite>();
        createDroplet();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height
        // are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a
        // normal size before updating.
        if (width <= 0 || height <= 0)
            return;
        viewport.update(width, height, true);

        // Resize your application here. The parameters represent the new window size.
    }

    @Override
    public void render() {
        // Draw your application here.
        input();
        logic();
        draw();
    }

    private void input() {
        // Handle user input here.
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isTouched()) {
            // Example: Play a sound when the screen is touched.
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            bucketSprite.setCenterX(touchPos.x);
        }

        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            bucketSprite.translateX(speed * delta);
        } else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            bucketSprite.translateX(-speed * delta);
        }
    }

    private void logic() {
        // Update your application logic here.

        float worldWidth = viewport.getWorldWidth();
        float delta = Gdx.graphics.getDeltaTime();

        // Clamp bucket position
        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, worldWidth - bucketSprite.getWidth()));

        // Update drop timer and create new drops
        dropTimer += delta;
        if (dropTimer > 1f) { // Create a new drop every second
            createDroplet();
            dropTimer = 0;
        }

        // Move drops and check for collisions
        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i);
            dropSprite.translateY(-dropSpeed * delta);

            // Check collision with bucket
            if (dropSprite.getBoundingRectangle().overlaps(bucketSprite.getBoundingRectangle())) {
                dropSound.play();
                dropSprites.removeIndex(i);
            }
            // Remove drops that fall off screen
            else if (dropSprite.getY() + dropSprite.getHeight() < 0) {
                dropSprites.removeIndex(i);
            }
        }
    }

    private void draw() {
        // Draw your application here.
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        bucketSprite.draw(spriteBatch);

        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(spriteBatch);
        }

        spriteBatch.end();
    }

    private void createDroplet() {
        float dropWidth = 1;
        float dropHeight = 1;
        float worldHeight = viewport.getWorldHeight();
        float worldWidth = viewport.getWorldWidth();
        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setY(worldHeight);
        dropSprite.setX(MathUtils.random(0, worldWidth - dropWidth));
        dropSprites.add(dropSprite);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
        backgroundTexture.dispose();
        bucketTexture.dispose();
        dropTexture.dispose();
        dropSound.dispose();
        music.dispose();
        spriteBatch.dispose();
    }
}
