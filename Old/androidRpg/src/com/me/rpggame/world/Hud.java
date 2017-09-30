package com.me.rpggame.world;


/**
 * CLASSE PER L'HUD
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.me.rpggame.entities.EAction;
import com.me.rpggame.entities.Player;
import com.me.rpggame.inventory.Inventory;
import com.me.rpggame.screens.GameScreen;

public class Hud extends Group {
	
	//---------------- PULSANTI A SCHERMO ----------------//
	
	final private int BUTTONS_NUMBER = 4;
	final private int BUTTON_SEARCH = 0;
	final private int BUTTON_PICKUP = 1;
	final private int BUTTON_BACKPACK = 2;
	final private int BUTTON_PAUSE = 3;

	
	//---------------- TEXTURE E IMMAGINI PER L'HUD ----------------//

	private Texture hudTexture;			//Texture dello sfondo dell'hud		
	private Texture buttonsSheet;		//Texture dei pulsanti		
	private Texture startPauseSheet;	//Texture dell'immagine start pause
	
	private TextureRegion[] buttonsRegions=new TextureRegion[BUTTONS_NUMBER];	//Region dei pulsanti
	private TextureRegion[] startPauseRegions=new TextureRegion[2];				//Region dello start/pause
	
	private Image searchImage;  		//Immagine del pulsante "Cerca"
	private Image pickImage;  			//Immagine del pulsante "Raccogli"
	private Image packImage; 			//Immagine del pulsante "Zaino"
	private Image pauseImage; 			//Immagine del pulsante "Pausa"
	private Image hudImage;  			//Immagine dello sfondo dell'hud
	private Image startPauseImage;  	//Immagine dello sfondo dell'hud
	private GameScreen sc;				//Gestore del mondo
	private Inventory in;				//Gestore del mondo
	private Player player;
	

	private Table rootTable;
	private Table statsTable;
	private Table buttonTable;

	private Skin skin;
	
	private Label statBlock[]=new Label[5];
	
	/**Costruttore**/
	public Hud(GameScreen sc,Stage stage, Inventory in,Player player){
		super();
		
		
		this.in=in;
		this.sc=sc;
		this.player=player;
		
		
		initHud(stage);
		

	}
	
	/**Carica l'HUD e i pulsanti**/
	private void initHud(Stage stage) {
		
		setWidth(stage.getWidth());
		
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
				
		statBlock[0]=new Label("HP: "+player.getActualHp()+"/"+player.getMaxHp(),skin);		
		statBlock[2]=new Label("T: "+player.getToughness(),skin);
		statBlock[3]=new Label("A: "+player.getAgility(),skin);
		statBlock[4]=new Label("M: "+player.getMind(),skin);
		
		if(player.getMaxEp()!=0)
			statBlock[1]=new Label("EP: "+player.getActualEp()+"/"+player.getMaxEp(),skin);
				
		// Carica le texture
		hudTexture =new Texture(Gdx.files.internal("data/hudBack.png"));
		hudTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		//Carica le immagini dei pulsanti
		buttonsSheet =new Texture(Gdx.files.internal("data/hud.png"));
		buttonsSheet.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		//Carica le immagini dello start/pause
		startPauseSheet =new Texture(Gdx.files.internal("data/stop_pause.png"));
		startPauseSheet.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		//Divide le region della texture
		TextureRegion tmp[][] = TextureRegion.split(buttonsSheet,buttonsSheet.getWidth()/4,  buttonsSheet.getHeight() / 2);             
		
		buttonsRegions[BUTTON_SEARCH]=tmp[0][0];		
		buttonsRegions[BUTTON_PICKUP]=tmp[0][1];		
		buttonsRegions[BUTTON_BACKPACK]=tmp[0][2];	
		buttonsRegions[BUTTON_PAUSE]=tmp[0][3];		
		
		tmp=TextureRegion.split(startPauseSheet,startPauseSheet.getWidth()/2,  startPauseSheet.getHeight());
		
		startPauseRegions[0]=tmp[0][0];		
		startPauseRegions[1]=tmp[0][1];

		//Crea le immagini per i pulsanti dell'hud
		hudImage =new Image(hudTexture);
        this.addActor(hudImage);
		setHeight( hudImage.getHeight());

		rootTable=new Table();
		statsTable=new Table();
		buttonTable=new Table();
		
		this.addActor(rootTable);
		rootTable.setFillParent(true);
		rootTable.add(statsTable).expandX().left();
		rootTable.row();
		rootTable.add(buttonTable).padTop(20).expand().top().left();
		
		//Setta le posizioni per lo statblock
		for(int i=0;i<5;i++){
			if(statBlock[i]!=null){
				if(i<2)
					statBlock[i].setPosition(10+(i*140), 198);
				else
					statBlock[i].setPosition(10+(i*75)+100, 198);

			statsTable.add(statBlock[i]).height(20).padLeft(10).padRight(10);
			}
		}	
		
		initButtons();		
		
		
		//Crea le immagini per i pulsanti dell'hud
		startPauseImage =new Image(startPauseRegions[0]);
		startPauseImage.setVisible(false);
		startPauseImage.setPosition(0, 550);
		startPauseImage.setTouchable(null);
		this.addActor(startPauseImage);
        		
	}
	
	/**Inizializza i pulsanti**/
	private void initButtons() {		
		
		//Pulsante di Ricerca
		searchImage=createButton(BUTTON_SEARCH,new InputListener() {			
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	        	searchImage.addAction(Actions.sequence( Actions.scaleTo(0.95f, 0.95f),Actions.delay( 0.0125f ),Actions.scaleTo(1f, 1f), Actions.delay( 0.15f ),new Action() {
	                    @Override
	                    public boolean act(
	                        float delta )
	                    {                
	                    	//Permette al personaggio di cercare segreti
	                        player.addGameAction(EAction.SEARCH,6);
	                        return true;
	                    }
	                } ));
	                return true;
	        }	        
		});
		
		//Pulsante di raccolta degli oggetti
		pickImage=createButton(BUTTON_PICKUP,new InputListener() {			
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	        	pickImage.addAction(Actions.sequence( Actions.scaleTo(0.95f, 0.95f),Actions.delay( 0.0125f ),Actions.scaleTo(1f, 1f), Actions.delay( 0.15f ),new Action() {
	                    @Override
	                    public boolean act(
	                        float delta )
	                    {                
	                    	//Permette al personaggio di raccogliere oggetti
	                        player.addGameAction(EAction.PICK,2);
	                        return true;
	                    }
	                } ));
	                return true;
	        }	        
		});
				
		//Pulsante dell'Inventario
		packImage=createButton(BUTTON_BACKPACK,new InputListener() {			
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	        	packImage.addAction(Actions.sequence( Actions.scaleTo(0.95f, 0.95f),Actions.delay( 0.0125f ),Actions.scaleTo(1f, 1f), Actions.delay( 0.15f ),new Action() {
	                    @Override
	                    public boolean act(
	                        float delta )
	                    {                
	                    	//Permette al personaggio di raccogliere oggetti
	                        in.setVisible(!in.isVisible());
	                        return true;
	                    }
	                } ));
	                return true;
	        }	        
		});		
		
		
		//Pulsante della pausa
		pauseImage=createButton(BUTTON_PAUSE,new InputListener() {			
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	        	pauseImage.addAction(Actions.sequence( Actions.scaleTo(0.95f, 0.95f),Actions.delay( 0.0125f ),Actions.scaleTo(1f, 1f), Actions.delay( 0.15f ),new Action() {
	                    @Override
	                    public boolean act(		
	                        float delta )
	                    {                
	                    	//Permette al personaggio di raccogliere oggetti
	                        sc.togglePause();
	                        changeStartPause();
	                        return true;
	                    }
	                } ));
	                return true;
	        }	        
		});	
		
		buttonTable.add(searchImage).pad(10);
		buttonTable.add(pickImage).pad(10);
		buttonTable.add(packImage).pad(10);
		buttonTable.add(pauseImage).pad(10);
	}

	/**Crea un pulsante
	 * 
	 * @param buttonIndex indice del pulsante
	 * @param inputListener azione generata dal premere il pulsante
	 * @return il pulsante creato
	 */
	private Image createButton(int buttonIndex,InputListener inputListener) {
		//Crea le immagini per i pulsanti dell'hud
		Image buttonImage =new Image(buttonsRegions[buttonIndex]);
		buttonImage.setOrigin(buttonsRegions[buttonIndex].getRegionWidth()/2,buttonsRegions[buttonIndex].getRegionHeight()/2);
	
		//Crea l'effetto di animazione di tasto premuto	
		buttonImage.addListener(inputListener);
		
		return buttonImage;
	}

	/**Funzione per aggiornare le statistiche mostrate a schermo**/
	public void updateHUD(){
		
		statBlock[0].setText("HP: "+player.getActualHp()+"/"+player.getMaxHp());
		statBlock[2].setText("T: "+player.getToughness());
		statBlock[3].setText("A: "+player.getAgility());
		statBlock[4].setText("M: "+player.getMind());

		if(player.getMaxEp()!=0)
			statBlock[1].setText("EP: "+player.getActualEp()+"/"+player.getMaxEp());

	}	

	/**Gestisce l'animazione del simbolo Pausa/Play**/
	private void changeStartPause() {

		if(sc.isPaused()){
			//PAUSA
			if(!startPauseImage.isVisible())
				startPauseImage.setVisible(true);

			startPauseImage.clearActions();
			startPauseImage.addAction(fadeIn( 0f ));
			startPauseImage.setDrawable(new TextureRegionDrawable(startPauseRegions[1]));
		}
		else
		{
			//PLAY
			startPauseImage.setDrawable(new TextureRegionDrawable(startPauseRegions[0]));
			startPauseImage.addAction(sequence( delay( 1f ), fadeOut( 0.75f )));
		}
	}
	

}
