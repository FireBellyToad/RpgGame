package com.faust.rpggame.screens;

/**
 * CLASSE DELLA SCHERMATA DEL MENU'
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.faust.rpggame.RpgGame;

public class MenuScreen extends BaseScreen {	
	
	//---------------- PULSANTI A SCHERMO ----------------//
	
	final private int BUTTON_NUMBERS = 2;
	final private int BUTTON_PLAY = 0;
	final private int BUTTON_OPTIONS = 1;
	
	boolean ispressed=false;
		
	private Texture titleTexture;		//Texture del titolo
	private Image titleImage;     		//Immagine del titolo
	private Image newGameButtonImage;   //Immagine del pulsante di nuova partita
	private Image optionButtonImage;    //Immagine del pulsante del menù opzioni 
	
	private Texture buttonsSheet;		//Texture dei pulsanti		
	private TextureRegion[] buttonsRegions=new TextureRegion[BUTTON_NUMBERS];	//Region dei pulsanti
	
	private Table rootTable;			//Tabella principale
	private Table titleTable;			//Tabella del titolo
	private Table menuTable;			//Tabella del menù
	
	
	public MenuScreen(RpgGame game){		
		super(game);	
		rootTable=new Table();	
		titleTable=new Table();
		menuTable=new Table();

        Gdx.input.setCatchBackKey(true);  
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		

		//Se il tasto back è premuto chiude il gioco
		if (Gdx.input.isKeyPressed(Keys.BACK))
			System.exit(0);
		
		//Fa agire i pulsanti
		stage.act();
		
		super.render(delta);

	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

	}

	@Override
	public void show() {
		super.show();
		

		// Carica le texture
		titleTexture =new Texture(Gdx.files.internal("data/title.png"));
		titleTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		buttonsSheet =new Texture(Gdx.files.internal("data/buttons.png"));
		buttonsSheet.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		TextureRegion tmp[][] = TextureRegion.split(buttonsSheet,buttonsSheet.getWidth(),  buttonsSheet.getHeight() / 2);              
		
		buttonsRegions[BUTTON_PLAY]=tmp[0][0];
		buttonsRegions[BUTTON_OPTIONS]=tmp[1][0];
		
		//Crea l'immagine dalla texture		
		titleImage =new Image(titleTexture);
		newGameButtonImage =new Image(buttonsRegions[BUTTON_PLAY]);
		optionButtonImage =new Image(buttonsRegions[BUTTON_OPTIONS]);
		
		//Setta l'origine delle due immagini
		newGameButtonImage.setOrigin(buttonsRegions[BUTTON_PLAY].getRegionWidth()/2,buttonsRegions[BUTTON_PLAY].getRegionHeight()/2);	
		optionButtonImage.setOrigin(buttonsRegions[BUTTON_OPTIONS].getRegionWidth()/2,buttonsRegions[BUTTON_OPTIONS].getRegionHeight()/2);		
		
		//Crea l'effetto di animazione di tasto premuto	per entrambi i tasti	
		newGameButtonImage.addListener(new InputListener() {			
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	        		newGameButtonImage.addAction(Actions.sequence( Actions.scaleTo(0.95f, 0.95f),Actions.delay( 0.15f ),Actions.scaleTo(1f, 1f), Actions.delay( 0.15f ),new Action() {
	                    @Override
	                    public boolean act(
	                        float delta )
	                    {                
	                    	//Cambio lo schermo alla fine dell'animazione
	                        game.setScreen( new GameScreen( game ));
	                        return true;
	                    }
	                } ));
	                return true;
	        }
	        
		});

		optionButtonImage.addListener(new InputListener() {			
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
        			//TODO cambio della schermata
	        		optionButtonImage.addAction(Actions.sequence( Actions.scaleTo(0.95f, 0.95f),Actions.delay( 0.15f ),Actions.scaleTo(1f, 1f)));
	                return true;
	        }
	        
		});
				
        //Aggiunge le tabelle con dentro gli attori nello stage
        stage.addActor(rootTable);

        rootTable.setFillParent(true);
        rootTable.add(titleTable).expandY().top();
        rootTable.row();
        rootTable.add(menuTable).expandY();
        
        titleTable.add(titleImage).top().spaceTop(50);
        menuTable.add(newGameButtonImage);
        menuTable.row();
        menuTable.add(optionButtonImage).spaceTop(50);
        
		

	}

	@Override
	public void dispose() {
		super.dispose();
		titleTexture.dispose();
		titleImage=null;
		buttonsSheet.dispose();
		buttonsSheet=null;
	}

}
