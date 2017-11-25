package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuScreen implements Screen {

    private RunnerGame runnerGame;
    private SpriteBatch batch;
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlasMenu;
    private TextureAtlas atlas;
    private TextureRegion textureBgMenu;
    private BitmapFont font32;
    private BitmapFont font96;
    private TextureRegion[][] t;
    private float time;

    public MenuScreen(RunnerGame runnerGame, SpriteBatch batch) {
        this.runnerGame = runnerGame;
        this.batch = batch;
    }

    @Override
    public void show() {
        atlas = new TextureAtlas("runner.atlas");
        atlasMenu = new TextureAtlas("runnerMenu.atlas");
        textureBgMenu = atlasMenu.findRegion("skyMenu");
        t = atlas.findRegion("terInv").split(107,200);

        CreateFontMenuScreen createFontMenuScreen = new CreateFontMenuScreen().invoke();
        FreeTypeFontGenerator generator = createFontMenuScreen.getGenerator();
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = createFontMenuScreen.getParameter();

        //передаем параметры в наш шрифт
        font32 = generator.generateFont(parameter);
        parameter.size = 96;
        font96 = generator.generateFont(parameter);

        generator.dispose();

        createGUI();
    }

    /**
     * создание интерфейса
     */
    public void createGUI(){
        stage = new Stage(runnerGame.getViewport(), batch);
        skin = new Skin(atlasMenu);

        //обработку каких событий будем производить
        Gdx.input.setInputProcessor(stage);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("buttons");

        textButtonStyle.font = font32;
        skin.add("tbs", textButtonStyle);

        CreateButtonMenuScreen createButtonMenuScreen = new CreateButtonMenuScreen().invoke();
        TextButton btnNewGame = createButtonMenuScreen.getBtnNewGame();
        TextButton btnExitGame = createButtonMenuScreen.getBtnExitGame();

        //добавление кнопок на сцену
        stage.addActor(btnNewGame);
        stage.addActor(btnExitGame);
        //действия при нажитии
    }

    /**
     *отрисовка экрана меню
     */
    @Override
    public void render(float delta) {
        update(delta);

        batch.begin();

        //отрисовка фона меню
        batch.draw(textureBgMenu, 0, 0);
        //движение спрайта в меню
        int frame = (int)(time / 0.1f); //0.1f - частота кадров
        frame = frame % 6; //за счет деления по модулю получаем кадр от 1 до 6

        //отрисовка спрайта
        batch.draw(t[0][frame],300,250);

        //вывод названия игры
        font96.draw(batch, "Ahmet-Runner-game", 0, 600, 1280, 1, false);

        batch.end();
        stage.draw();
    }

    /**
     * сцена должна реагировать на действия
     */
    public void update(float dt){
        stage.act(dt);
        time += dt;
    }

    /**
     * если изменили размер окна, то
     */
    @Override
    public void resize(int width, int height) {
        //
        runnerGame.getViewport().update(width, height, true);
        runnerGame.getViewport().apply();
    }

    /**
     * очистка памяти
     */
    @Override
    public void dispose() {
        font96.dispose();
        font32.dispose();
        atlasMenu.dispose();
        skin.dispose();
        stage.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    private class CreateFontMenuScreen {
        private FreeTypeFontGenerator generator;
        private FreeTypeFontGenerator.FreeTypeFontParameter parameter;

        public FreeTypeFontGenerator getGenerator() {
            return generator;
        }

        public FreeTypeFontGenerator.FreeTypeFontParameter getParameter() {
            return parameter;
        }

        public CreateFontMenuScreen invoke() {
            //создаем генератор параметров шрифта
            generator = new FreeTypeFontGenerator(Gdx.files.internal("a_assuan_titulstrdst_bold.ttf"));
            parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 32;
            parameter.borderColor = Color.BLACK;
            parameter.borderWidth = 2;
            parameter.shadowOffsetX = 3;
            parameter.shadowOffsetY = 3;
            parameter.shadowColor = Color.BLACK;
            return this;
        }
    }

    private class CreateButtonMenuScreen {
        private TextButton btnNewGame;
        private TextButton btnExitGame;

        public TextButton getBtnNewGame() {
            return btnNewGame;
        }

        public TextButton getBtnExitGame() {
            return btnExitGame;
        }

        public CreateButtonMenuScreen invoke() {
            btnNewGame = new TextButton("Start", skin, "tbs");
            btnExitGame = new TextButton("Exit", skin, "tbs");
            btnNewGame.setPosition(580, 400);
            btnExitGame.setPosition(580, 300);

            btnNewGame.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    runnerGame.switchScreens(RunnerGame.Screens.GAME);
                }
            });

            btnExitGame.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.exit();
                }
            });
            return this;
        }
    }
}
