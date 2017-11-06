package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	BitmapFont bmf;
	private final int AST_COUNT = 1;
	private Asteroid[] asteroid = new Asteroid[AST_COUNT];
	private Random random = new Random();

	@Override
	public void create () {
		batch = new SpriteBatch();
		Asteroid.setMyTexture(new Texture("asteroid.png"));
		img = new Texture("bomb.png");
		//подключение шрифта
		bmf = new BitmapFont(Gdx.files.internal("myfont.fnt"), Gdx.files.internal("myfont.png"), false );

		for (int i = 0; i < AST_COUNT; i++) {
			asteroid[i] = new Asteroid(new Vector2(random.nextInt(800),3.0f * random.nextInt(600)),
					new Vector2(random.nextFloat()-0.5f, 3.0f * random.nextFloat()-0.5f));
		}
	}

	@Override
	public void render () {
		update();
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		batch.draw(img, InputHandler.getMousePosition().x - 16, InputHandler.getMousePosition().y - 16,
				16, 16, 32, 32, 1.0f, 1.0f,
				0.0f, 0, 0, 32, 32, false, false);

		for (int i = 0; i < AST_COUNT; i++) {
			asteroid[i].render(batch);
		}

		if (InputHandler.d1()){
			bmf.draw(batch, "hello world", 50, 50);
		}

		batch.end();
	}

	public void update(){
		for (int i = 0; i <AST_COUNT ; i++) {
			asteroid[i].update();
		}
	}
}
