package com.faust.rpggame.screens;

/**
 * CLASSE DELLA SCHERMATA CON IMMAGINE
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.faust.rpggame.RpgGame;

public class SplashScreen extends BaseScreen {
	
	
	private Texture splashTexture;
	private Image splashImage;     
	
	
	public SplashScreen(RpgGame game){
		super(game);		
	}

	@Override
	public void render(float delta) {		

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0f, 0f, 0f, 1.0f);
		
		//Permette all'immagine di eseguire le sue azioni (transizione)
		stage.act(delta);
		
		super.render(delta);
	}


	@Override
	public void show() {
				
		// Carica la texture
		splashTexture =new Texture(Gdx.files.internal("data/splash.png"));
		splashTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		//Crea l'immagine dalla texture		
		splashImage =new Image(splashTexture);
				
		splashImage.setOrigin(splashTexture.getWidth()/2,splashTexture.getHeight()/2);
		splashImage.setPosition(stage.getWidth()/2-splashTexture.getWidth()/2,stage.getHeight()/2-splashTexture.getHeight()/2);
		
        splashImage.getColor().a = 0f;

        // Configura l'effetto di transizione. 4 azioni: fadeIn, ritarda, fadeOut, apri il menu
        splashImage.addAction( sequence( fadeIn( 0.75f ), delay( 2f ), fadeOut( 0.75f ), new Action() {
                    @Override
                    public boolean act(
                        float delta )
                    {                    	
                        game.setScreen( new MenuScreen( game ));
                        return true;
                    }
                } ) );
        
        //Aggiunge l'attore Immagine allo stage
        stage.addActor(splashImage);		

	}


	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		super.dispose();
		splashTexture.dispose();
		splashImage=null;
	}

}
