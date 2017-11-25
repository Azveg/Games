package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.RunnerGame;

public class DesktopLauncher {
	/**
	 * Провести рефакторинк
	 * Доделать:
	 * 1. прозрачность текстур +
	 * 5. улучшить качество текстур
	 * 3. добавить анимацию прыжка
	 * 6. добавить анимацию смерти
	 * 4. добавить анимацию врагов
	 * 7. добавить уровни
	 * 8. добавить монеты
	 * 9. добавить изменение ланшафта
	 * 11. тайл мап???
	 * 12. при переходе в меню из игры продолжает играть музыка(это минус)
	 * 13. все кнопки должны храниться в своих группах по смыслу
	 */
	public static void main (String[] arg) {
		System.setProperty("user.name","Public");
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new RunnerGame(), config);
		config.width = 1280;
		config.height = 720;
	}
}
