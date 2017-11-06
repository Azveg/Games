package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Asteroid {

    private Vector2 position;
    private Vector2 velocity;

    public Asteroid(Vector2 position, Vector2 velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    //private float x, y, vx, vy;
    private static Texture myTexture;

    public static void setMyTexture(Texture myTexture) {
        Asteroid.myTexture = myTexture;
    }

    //paint asteroid
    public void render(SpriteBatch batch){
        batch.draw(myTexture, position.x - 16, position.y - 16);
    }

    //logic asteroid
    public void update(){
        position.add(velocity);
        MoveInLayout();

        if (InputHandler.isPressed()){
            //смена направления движения
            velocity = position.cpy().sub(InputHandler.getMousePosition()).nor().scl(-1.0f);
        }
    }

    private void MoveInLayout() {
        //-32 для плавного появления текстуры
        if (position.x > Gdx.graphics.getWidth())
            position.x = -myTexture.getWidth();

        if (position.x < - myTexture.getWidth())
            position.x = Gdx.graphics.getWidth();

        if (position.y > Gdx.graphics.getHeight())
            position.y = - myTexture.getHeight();

        if (position.y < - myTexture.getHeight())
            position.y = Gdx.graphics.getHeight();
    }
}
