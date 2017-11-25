package com.mygdx.game;

import com.badlogic.gdx.Gdx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

public class HighScoreSystem {

    public static String topPlayerName = "";
    public static int topPlayerScore = 0;

    /**
     * загрузка таблицы
     */
    public static void loadTable(){
        BufferedReader br = null;
        try {
            br = Gdx.files.local("highscore.txt").reader(8192);
            String data = br.readLine();
            topPlayerName = data.split(" ")[0];
            topPlayerScore = Integer.parseInt(data.split(" ")[1]);
        } catch (IOException e){

        }
    }

    /**
     * создание пустой таблицы
     */
    public static void  createTable(){
        Writer writer = null;
        try {
            writer = Gdx.files.local("highscore.txt").writer(false);
            writer.write("Unknown 0");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * обновление таблицы
     */
    public static void updateTable(String name, int score){
        if (score > topPlayerScore){
            Writer writer = null;
            try {
                writer = Gdx.files.local("highscore.txt").writer(false);
                writer.write(name + " " + score);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        loadTable();
    }
}
