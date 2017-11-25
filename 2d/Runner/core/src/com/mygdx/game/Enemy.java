package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {

    /**
     * перечисление объектов класса
     * объекты используют единый конструктор класса
     */
    public enum Type {
        CACTUS, BIRD
    }


    /**
     *@param texture - текстура врага
     *@param position - координаты врага
     *@param velocity - скорость врага
     *@param hitArea - хинт(область для сталкновения) врага
     */
    private TextureRegion texture;
    private Vector2 position;
    private Vector2 velocity;
    private Rectangle hitArea;

    public int getWIDTH() {
        return texture.getRegionWidth();
    }

    public Rectangle getHitArea(){
        return hitArea;
    }

    public Vector2 getPosition(){
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    /**
     * прямоугольник находится в тех же координатах
     * и с тем же размером, что и кактус т.е привязка квадрата к кактусу
     * @param texture - тип текстуры врага
     * @param position - позиция врага
     */
    public Enemy(TextureRegion texture, Vector2 position){
        this.texture = texture;
        this.position = position;
        this.velocity = new Vector2(0,0);
        this.hitArea = new Rectangle(position.x + texture.getRegionWidth()/2, position.y + texture.getRegionHeight()/2,
                texture.getRegionWidth()* 0.7f, texture.getRegionHeight() * 0.8f);
    }

    /**
     * если меняем положение врага
     */
//    public void setPosition(float x, float y){
//        position.set(x, y);
//        hitArea.setWidth(texture.getRegionWidth() * 0.8f);
//        hitArea.setHeight(texture.getRegionHeight() * 0.8f);
//        hitArea.setPosition(position.x + texture.getRegionWidth()/2 , position.y + texture.getRegionHeight()/2);
//    }

    /**
     * craete police
     */
    public void setupPolice(TextureRegion texture, float x, float y, float vx, float vy){
        this.texture = texture;
        this.position.set(x,y);
        this.velocity.set(vx, vy);
        hitArea.setWidth(texture.getRegionWidth() * 0.7f);
        hitArea.setHeight(texture.getRegionHeight() * 0.8f);
    }

    /**
     *create bird
     */
    public void setupBird(TextureRegion texture, float x, float y, float vx, float vy){
        this.texture = texture;
        this.position.set(x,y);
        this.velocity.set(vx, vy);
        hitArea.setWidth(texture.getRegionWidth() * 0.8f);
        hitArea.setHeight(texture.getRegionHeight() * 0.8f);
    }

    /**
     * measuring enemy move
     */
    public void update(float dt){
        position.mulAdd(velocity, dt);
        hitArea.setPosition(position);
    }

    /**
     *отрисовка врагов
     */
    public void render (SpriteBatch batch, float worldX){
        batch.draw(texture, position.x - worldX, position.y);
    }
}
