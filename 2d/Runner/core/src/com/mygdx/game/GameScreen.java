package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class GameScreen implements Screen{
    private RunnerGame runnerGame;
    private SpriteBatch batch;
    private TextureAtlas atlas;
    private TextureAtlas atlasIcons;
    private TextureRegion butJump;
    private TextureRegion butPause;
    private TextureRegion textureBackground;
    private TextureRegion texturePolice;
    private TextureRegion textureBird;
    private TextureRegion textureSand;
    private BitmapFont font32;
    private BitmapFont font96;
    //планка земли(нужно для ввода прыжка спрайта)
    private float groundHeight = 27.0f;
    //координата спрайта по x
    private float playerAnchor = 200.0f;
    private Player player;
    private Enemy[] enemies;
    private boolean gameover;
    private float time;
    private Music music;
    private Sound playerJumpSound;
    private Stage stage;
    private Skin skin;
    private boolean paused;
    private Group endGameGroup;
    private Enemy.Type type;

    public float getPlayerAnchor(){
        return playerAnchor;
    }

    public float getGroundHeight(){
        return groundHeight;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public GameScreen(RunnerGame runnerGame, SpriteBatch batch) {
        this.runnerGame = runnerGame;
        this.batch = batch;
    }

    /**
     * add elements on window
     */
    @Override
    public void show() {
        CreateTexture();
        iconsGameScreen();
        addMusicGameScreen();
        //блок подключения звука
        addSoundGameScreen();
        createEnemy();
        gameover = false;

        FontGenerate fontGenerate = new FontGenerate().invoke();
        FreeTypeFontGenerator generator = fontGenerate.getGenerator();
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = fontGenerate.getParameter();

        //передаем параметры в наш шрифт
        font32 = generator.generateFont(parameter);
        parameter.size = 96;
        font96 = generator.generateFont(parameter);

        generator.dispose();

        HighScoreSystem.createTable();
        HighScoreSystem.loadTable();

        //сбрасываем процессор обработки экрана
        Gdx.input.setInputProcessor(null);

        paused = false;
        createGUI();
    }

    /**
     * paint spaite and enemys
     * @param delta
     */
    @Override
    public void render(float delta) {

        update(delta);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(textureBackground, 0, 0);


        //при каждом вызове render() рисуем 8 кусков с разным начальным положением по х
        //из-за смещения текстуры появляется эффект движения
        for (int i = 0; i < 8; i++) {
            //так как идет деление по модулю значение player.getPosition.x будет меняться от 0 до 200
            //этим мы сделали привязку мира к спрайту
            batch.draw(textureSand, i * 200 - player.getPosition().x % 200, 0);
        }

        //рисуем спрайта
        player.render(batch);

        //рисуем врагов
        for (int i = 0; i < enemies.length; i++) {
            enemies[i].render(batch, player.getPosition().x - playerAnchor);
            enemies[i].getHitArea();
        }


        font32.draw(batch, "Top player " + HighScoreSystem.topPlayerName + ": " + HighScoreSystem.topPlayerScore, 50, 702);
        font32.draw(batch, "Score: " + (int)player.getScore(), 50, 652);

        if (player.isDoubleJumpAvaible()){
            font32.draw(batch, "Double jump", 0, 500, 1280, 1, false);
        }

        if (gameover){
            font96.draw(batch, "game over", 0, 382, 1280, 1, false);
            font32.setColor(1,1,1,0.5f + (float)Math.sin(time * 0.5f));
            font32.draw(batch, "tap to restart", 0, 292, 1280, 1, false);
            font32.setColor(1,1,1,1);
        }

        batch.end();
        stage.draw();
    }

    /**
     * операции метода идут только если игра не на паузе
     *игра идет пока gameover false
     */
    public void update(float dt){
        stage.act(dt);
        if (!paused) {
            time += dt;
            if (!gameover) {
                //обновление текстуры спрайта
                player.update(dt);
                //цикл создания дополнительных врагов
                addNewEnemy(dt);
                //цикл проверки столкновения
                checkOverLaps();
            }
        }
    }

    /**
     * create UI
     */
    public void createGUI(){

        stage = new Stage(runnerGame.getViewport(), batch);
        skin = new Skin(atlasIcons);

        //обработку каких событий будем производить
        Gdx.input.setInputProcessor(stage);

        //создаем стиль кнопки
        createStyleButton();

        //создаем игровые кнопки
        CreateGameButton createGameButton = new CreateGameButton().invoke();
        TextButton btnPause = createGameButton.getBtnPause();
        TextButton btnJump = createGameButton.getBtnJump();

        //создаем кнопки выходящие при gameOver
        CreateGameOverButton createGameOverButton = new CreateGameOverButton().invoke();
        TextButton btnExitToMenu = createGameOverButton.getBtnExitToMenu();
        TextButton btnRestart = createGameOverButton.getBtnRestart();

        //добавление кнопок на сцену
        stage.addActor(btnPause);
        stage.addActor(btnJump);

        //создаем группу для кнопок завершения
        endGameGroup = new Group();
        endGameGroup.addActor(btnExitToMenu);
        endGameGroup.addActor(btnRestart);
        endGameGroup.setVisible(false);
        endGameGroup.setPosition(100 , 110);

        //добавление группы на сцену
        stage.addActor(endGameGroup);

    }


    /**
     * create style button Game menu
     */
    private void createStyleButton() {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("blue_button00");
        textButtonStyle.font = font32;
        skin.add("tbs", textButtonStyle);
    }

    /**
     * create texture GameScreen
     */
    private void CreateTexture() {
        atlas = new TextureAtlas("runner.atlas");
        textureBackground = atlas.findRegion("sky");
        textureSand = atlas.findRegion("sand");
        texturePolice = atlas.findRegion("police");
        textureBird = atlas.findRegion("bird");
    }

    /**
     * create icons GameScreen
     */
    private void iconsGameScreen() {
        atlasIcons = new TextureAtlas("icons.atlas");
        butJump = atlasIcons.findRegion("blue_button00");
        butPause = atlasIcons.findRegion("blue_button00");
    }

    /**
     * add music for GameScreen
     */
    private void addMusicGameScreen() {
        music = Gdx.audio.newMusic(Gdx.files.internal("run1.mp3"));
        music.setLooping(true);
        music.setVolume(0.9f);
        music.play();
    }

    /**
     * add sound jump for GameScreen
     */
    private void addSoundGameScreen() {
        playerJumpSound = Gdx.audio.newSound(Gdx.files.internal("SFX_Jump_07.wav"));
        playerJumpSound.setVolume(0,0.1f);
        player = new Player(this, playerJumpSound);
    }

    /**
     * create enemy
     */
    private void createEnemy() {
        enemies = new Enemy[5];
        //первый кактус всегда рисуем в 1400 px
        enemies[0] = new Enemy(texturePolice, new Vector2(1400, groundHeight));
        for (int i = 1; i < 5; i++) {
            //все остальные будут зависить от предидущего
            enemies[i] = new Enemy(texturePolice,
                    //каждый следующий будет находиться от предидущего на рассотоянии
                    //от 600 до 900 px
                    new Vector2(enemies[i-1].getPosition().x + MathUtils.random(600, 900), groundHeight));
        }
    }

    /**
     * generate new enemy
     * @param index
     */
    public void generateEnemy(int index){
        int maxType = 0;
        if (time > 10.0f){
            maxType = 1;
        }
        type = Enemy.Type.values()[(int)(Math.random() * (maxType + 1))];
        switch (type){
            case CACTUS:
                enemies[index].setupPolice(texturePolice, getRightEnemy() + MathUtils.random(700, 1200), groundHeight, 0,0);
                break;
            case BIRD:
                enemies[index].setupBird(textureBird, getRightEnemy() + MathUtils.random(700, 1200), groundHeight + 250, -100,0);
                break;
        }
    }


    /**
     *при масштабировании viewport сохраняет начальные
     *координаты и сам пеесчитывает их с учетом масштабирования
     *для отрисовки используем циклы с учетом ширина экрана/ширина текстуры
     *для эрана - 1280 и тестуры 200
     */
    @Override
    public void resize(int width, int height) {
        //если изменили размер окна, то
        runnerGame.getViewport().update(width, height, true);
        runnerGame.getViewport().apply();
    }

    /**
     * @return координата самого правого кактуса
     * для зацикливания отрисовки врагов
     */
    public float getRightEnemy(){
        float maxValue = 0.0f;

        for (int i = 0; i < enemies.length; i++) {
            if (enemies[i].getPosition().x > maxValue) {
                maxValue = enemies[i].getPosition().x;
            }
        }
        return maxValue;
    }

    /**
     * create new enemy
     * @param dt - delta time
     */
    private void addNewEnemy(float dt) {
        for (int i = 0; i < enemies.length; i++) {
            enemies[i].update(dt);
            if (enemies[i].getPosition().x < player.getPosition().x - playerAnchor - enemies[i].getWIDTH()) {
                generateEnemy(i);
            }
        }
    }

    /**
     * check overlaps hero and enemys
     */
    private void checkOverLaps() {
        for (Enemy enemy : enemies) {

            if (enemy.getHitArea().overlaps(player.getHitArea())) {
                HighScoreSystem.updateTable("Player", (int) player.getScore());
                gameover = true;
                endGameGroup.setVisible(true);
                System.out.println("width enemy " + enemy.getHitArea().width + " height enemy " + enemy.getHitArea().height);
                System.out.println("width player " + player.getHitArea().width + " height player " + player.getHitArea().height);
                System.out.println();
                System.out.println("x enemy " + enemy.getHitArea().x + " y enemy " + enemy.getHitArea().y);
                System.out.println("x player " + player.getHitArea().x + " y player " + player.getHitArea().y);
                break;
            }
        }
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

    /**
     * перезапуск приложения
     */
    public void restart(){
        gameover = false;
        endGameGroup.setVisible(false);
        time = 0.0f;
        enemies[0].setupPolice(texturePolice,1400, groundHeight, 0,0);
        for (int i = 1; i < enemies.length; i++) {
            enemies[i].setupPolice(texturePolice,enemies[i-1].getPosition().x + MathUtils.random(800, 1200), groundHeight,0,0);
        }
        player.restart();
    }

    @Override
    public void dispose() {
        atlas.dispose();
        music.dispose();
        playerJumpSound.dispose();
        font32.dispose();
        font96.dispose();
    }

    /**
     * create font
     */
    public class FontGenerate {
        private FreeTypeFontGenerator generator;
        private FreeTypeFontGenerator.FreeTypeFontParameter parameter;

        public FreeTypeFontGenerator getGenerator() {
            return generator;
        }

        public FreeTypeFontGenerator.FreeTypeFontParameter getParameter() {
            return parameter;
        }

        public FontGenerate invoke() {
            //создаем генератор шрифтов и указываем шрифт
            generator = new FreeTypeFontGenerator(Gdx.files.internal("a_assuan_titulstrdst_bold.ttf"));
            //создаем генератор параметров шрифта
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

    /**
     * create game button
     */
    private class CreateGameButton {
        private TextButton btnPause;
        private TextButton btnJump;

        public TextButton getBtnPause() {
            return btnPause;
        }

        public TextButton getBtnJump() {
            return btnJump;
        }

        public CreateGameButton invoke() {
            btnPause = new TextButton("Pause", skin, "tbs");
            btnJump = new TextButton("Jump", skin, "tbs");
            btnPause.setPosition(1000, 100);
            btnJump.setPosition(1000, 200);

            //задаем действие при нажатии кнопки
            btnPause.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    paused = !paused;
                }
            });

            //задаем действие при нажатии кнопки
            btnJump.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    player.tryToJump();
                }
            });
            return this;
        }
    }

    /**
     * create game over button
     */
    private class CreateGameOverButton {
        private TextButton btnExitToMenu;
        private TextButton btnRestart;

        public TextButton getBtnExitToMenu() {
            return btnExitToMenu;
        }

        public TextButton getBtnRestart() {
            return btnRestart;
        }

        public CreateGameOverButton invoke() {
            btnExitToMenu = new TextButton("Menu", skin, "tbs");
            btnRestart = new TextButton("Restart", skin, "tbs");
            btnExitToMenu.setPosition(320, 300);
            btnRestart.setPosition(650, 300);

            //задаем действие при нажатии кнопки
            btnExitToMenu.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    runnerGame.switchScreens(RunnerGame.Screens.MENU);
                }
            });

            //задаем действие при нажатии кнопки
            btnRestart.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    restart();
                }
            });
            return this;
        }
    }
}
