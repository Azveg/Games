package com.mygdx.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    //ссылка на игровой экран
    private GameScreen gameScreen;
    //тестура игрока
    private TextureRegion[][] texture;
    private Vector2 position;
    private Vector2 velocity;
    //время бега спрайта
    private float time;
    private float score;
    private float doubleJumpTime;
    private Rectangle hitArea;
    private Sound jumpSound;
    private float angle;
    private boolean jumpRequest;

    /**
     * размер текстуры персонажа
     */
    private final int WIDTH = 107;
    private final int HEIGHT = 200;

    public boolean isDoubleJumpAvaible(){
        return doubleJumpTime <= 0.0f;
    }

    /**
     * кто-то запрашивает прыжок
     */
    public void tryToJump(){
        jumpRequest = true;
    }

    public Vector2 getPosition(){
        return position;
    }

    public Rectangle getHitArea() {
        return hitArea;
    }

    public float getScore() {
        return score;
    }

    public float getTime() {
        return time;
    }

    /**
     * Создание персонажа
     * @param gameScreen ссылка на игровой экран
     * @param jumpSound звук прыжка
     */
    public Player(GameScreen gameScreen, Sound jumpSound){
        //говорим, что он знаетна каком экране находится
        this.gameScreen = gameScreen;
        this.texture = gameScreen.getAtlas().findRegion("terInv").split(WIDTH, HEIGHT);

        //задаем положение спрайта
        this.position = new Vector2(0, 27);

        //считем что спрайт движется вправо по x со скоростью 240 px/sec
        this.velocity = new Vector2(240.0f, 0.0f);

        //начинаем рисовать прямоуг со второй четверти текстуры спрайта: WIDTH / 4
        //ширина прямоугольника равен половине ширины текстуры спрайта: WIDTH / 2;
        this.hitArea = new Rectangle(position.x + WIDTH/2 , position.y + HEIGHT/2 , WIDTH /2 , HEIGHT* 0.8f);

        this.score = 0;
        this.jumpSound = jumpSound;
    }

    /**
     *отрисовка спрайта
     */
    public void render(SpriteBatch batch){
        int frame = (int)(time / 0.1f); //0.1f - частота кадров
        frame = frame % 6; //за счет деления по модулю получаем кадр от 1 до 6
        batch.draw(texture[0][frame], gameScreen.getPlayerAnchor(), position.y,
                WIDTH /2, HEIGHT /2, WIDTH, HEIGHT, 1, 1, angle);
    }

    /**
     * обговление спрайта
     * @param dt
     */
    public void update(float dt){
        //крутим спрайта(анимация)
        if (angle > 0.0f){
            if (angle > 360){
                angle += 15.0f * dt;
            } else {
                angle += 280.0f * dt;
            }
        }
         //если в воздухе, то уменьшаем скорость по y
        if (position.y > gameScreen.getGroundHeight()) {
            velocity.y  -= 720.0f * dt;
        } else {
            angle = 0.0f;
            position.y = gameScreen.getGroundHeight();
            velocity.y = 0.0f;
             // изменение кадров спрайта
            time +=  velocity.x * dt / 300.0f;
        }
        if (doubleJumpTime > 0.0f){
            doubleJumpTime -= dt;
        }
        if (jumpRequest && ((position.y <= gameScreen.getGroundHeight()) || doubleJumpTime <= 0.0f)) {
            //если клик по экрану, то задаем скорсть по y
            velocity.y = 620.0f;
            jumpSound.play();
            if (position.y > gameScreen.getGroundHeight()){
                doubleJumpTime = 5.0f;
                angle = 1.0f;
            }
        }

        //меняем координаты персонажа
        position.mulAdd(velocity, dt);
        //для повышения сложности игры увеличиваем скорость спрайта
        velocity.x += 3.0f * dt;
        hitArea.setPosition(position.x + WIDTH/2, position.y );
        score += velocity.x * dt/3.0f;
        jumpRequest = false;
    }

    /**
     * перезапуск игрока
     */
    public void restart(){
        position.set(0, gameScreen.getGroundHeight());
        score = 0.0f;
        doubleJumpTime = 0.0f;
        velocity.set(240.0f, 0.0f);
        hitArea.setPosition(position.x + WIDTH/2, position.y + HEIGHT/2);
    }
}
