package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class RunnerGame extends Game {

	/**
	 * создание списка эранов
	 */
	public enum Screens {
		MENU, GAME
	}

	private SpriteBatch batch;
	private GameScreen gameScreen;
	private MenuScreen menuScreen;
	private Viewport viewport;

	public Viewport getViewport() {
		return viewport;
	}

	/**
	 * создание области отрисовки, эранов, элемента корректного изменения размера экрана
	 * метод переключения экранов
	 */
	@Override
	public void create () {
		batch = new SpriteBatch();
		//созд экран, отдаем туда ссылку на игру
		gameScreen = new GameScreen(this, batch);
		menuScreen = new MenuScreen(this, batch);
		viewport = new FitViewport(1280, 720);
		switchScreens(Screens.MENU);
	}


	/**
	 * обновление экранов
	 */
	@Override
	public void render () {
		//dt - время между 2 кадрами
		float dt = Gdx.graphics.getDeltaTime();
		//update(dt);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		getScreen().render(dt);
	}

	/**
	 * выбор экрана
	 * @param type - тип экрана
	 */
	public void switchScreens (Screens type){
		//запоминаем экран
		Screen currentScreen = getScreen();
		//очищаем ресурсы текущего экрана
		if (currentScreen != getScreen()){
			currentScreen.dispose();
		}
		//поверяем куда хочет попасть юзер
		switch (type){
			case MENU: setScreen(menuScreen); break;
			case GAME: setScreen(gameScreen); break;
		}

	}

	@Override
	public void dispose () {
		batch.dispose();
		getScreen().dispose();

	}
}
