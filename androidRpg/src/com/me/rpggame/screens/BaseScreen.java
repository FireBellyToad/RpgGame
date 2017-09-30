package com.me.rpggame.screens;

/**
 * CLASSE BASE DEGLI SCHERMI
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.me.rpggame.RpgGame;

public abstract class BaseScreen implements Screen {

    public static final float SCREEN_WIDTH = 480;
    public static final float SCREEN_HEIGHT = 800;      
    
    protected RpgGame game;	

    protected Stage stage;
    
    public BaseScreen(RpgGame game){
    	this.game=game;
    	stage= new Stage(SCREEN_WIDTH, SCREEN_HEIGHT, true);
    }

	@Override
	public void render(float delta) {
	
		//Disegna lo stage e l'immagine
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		//Funzione di resize
		stage.setViewport(SCREEN_WIDTH, SCREEN_HEIGHT, true);
	}


	@Override
	public void show() {
        // set the stage as the input processor
        Gdx.input.setInputProcessor( stage );

	}

	@Override
	public void hide() {
		dispose();

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		stage.clear();
		stage.dispose();
	}

}
