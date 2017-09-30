package com.me.rpggame;

/**
 * CLASSE PRINCIPALE
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

import com.badlogic.gdx.Game;
import com.me.rpggame.screens.SplashScreen;

public class RpgGame extends Game {   

	    
	@Override
	public void create() {		        
				
		setScreen(new SplashScreen(this));  
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void render() {		        
		super.render();		
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);		
	}

	@Override
	public void pause() {
		super.pause();
	}
	

	@Override
	public void resume() {
		super.resume();
	}
}
