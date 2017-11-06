package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;


public class InputHandler {

    public static boolean isClicked(){
        //если кликнул мышью
        return Gdx.input.justTouched();
    }

    public static boolean isPressed(){
        //если зажал кнопку мыши
        return Gdx.input.isTouched();
    }

    public static Vector2 getMousePosition(){
        //возвращает координаты мыши
        return new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
    }

    //отлавливать события кнопки
    public static boolean d1(){
        return Gdx.input.isKeyPressed(Input.Keys.B);
    }
}
