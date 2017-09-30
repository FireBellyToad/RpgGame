package com.me.rpggame.screens;

/**
 * CLASSE DELLA SCHERMATA DI GIOCO
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.me.rpggame.RpgGame;
import com.me.rpggame.entities.Entity;
import com.me.rpggame.entities.Monster;
import com.me.rpggame.entities.MonsterFactory;
import com.me.rpggame.entities.Player;
import com.me.rpggame.inventory.Inventory;
import com.me.rpggame.items.ItemFactory;
import com.me.rpggame.tools.FOVManager;
import com.me.rpggame.tools.Pathfinder;
import com.me.rpggame.world.Console;
import com.me.rpggame.world.Hud;
import com.me.rpggame.world.WorldManager;

public class GameScreen extends BaseScreen {
	
	//---------------- CAMPI DELLA CLASSE ----------------//
	
	private WorldManager wm;						//Gestore del mondo
	private ArrayList<Monster> monsters;				//Mostri;
	private Player player;							//Personaggio
	private Camera camera;							//Telecamera
	private FPSLogger fpsLogger=new FPSLogger();
	
	private Hud hud;			//Hud
	private FOVManager fov;			//Hud
	private Inventory inv;
	private int timeToTick;
	private boolean doAction;
	private boolean paused;
	
	//---------------- FUNZIONI ----------------//
		
	/**Costruttore**/
	public GameScreen(RpgGame game){
		super(game);		
        Gdx.input.setCatchBackKey(true);  
        timeToTick=16;
        doAction=true;  
        paused=false;
        resize(0,0);
	}
	
	@Override
	public void show() {


		inv= new Inventory();
		wm= new WorldManager(null,this,inv);
		Pathfinder.getInstance().setWorldManager(wm);
		fov =new FOVManager(wm);
		monsters = new ArrayList<Monster>();

		player=new Player(new Texture(Gdx.files.internal("data/cleric.png")),"Giocatore",0,0,wm,inv,Player.FIGHTER);
		
		createMonsters(2);			

		ItemFactory.getInstance().setPlayer(player);
		inv.setPlayer(player);
		
        hud= new Hud(this,stage,inv,player);             

        Gdx.input.setInputProcessor( stage );        
		
		camera = stage.getCamera();		
		
		
	}
	
	/**Crea i mostri nel livello**/
	private void createMonsters(int monstersNumber) {

		//TODO creare mostri diversi in base al livello
		for(int i=0;i<monstersNumber-1;i++){
				
			//Crea il mostro
			final Monster tempMon = MonsterFactory.getInstance().createMonster(MonsterFactory.MON_GOBLIN,wm,5,8);
					
			//Rende il mostro cliccabile
			tempMon.addListener(new InputListener() {		
		        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		        	player.setNewPath(tempMon.getMapPosition());
		        	player.setTarget(tempMon);
	                return true;
		        }			        
			});
			
			//Lo aggiunge ai mostri del livello
			monsters.add(tempMon);	
		}
		
		//TODO temporaneo
		
		final Monster tempMon = MonsterFactory.getInstance().createMonster(MonsterFactory.MON_SCORPION,wm,9,9);
		
		//Rende il mostro cliccabile
		tempMon.addListener(new InputListener() {		
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	        	player.setNewPath(tempMon.getMapPosition());
	        	player.setTarget(tempMon);
                return true;
	        }			        
		});		
		
		//Lo aggiunge ai mostri del livello
		monsters.add(tempMon);		
	}

	@Override
	public void resize(int width, int height) {
		//Funzione di resize
		stage.setViewport(SCREEN_WIDTH, SCREEN_WIDTH, true);
		Console.getInstance().setWidth(stage.getWidth());
	}

	@Override
	public void render(float delta){

		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0f, 0, 0f, 1.0f);
				
		//Permette la pressione dei tasti
		stage.act();
				
		//Riassegna gli attori al cambio del livello
		reassignActorsToScreen();
		
		//Se il tasto back è premuto torna al menù dello schermo
		if (Gdx.input.isKeyPressed(Keys.BACK)){
			Console.getInstance().clearConsole();
			game.setScreen(new MenuScreen(game));	
		}


		//Se si può agire e il gioco non è in pausa, i personaggi agiscono
		if((doAction)&&(!paused)){			
			
			//Console.getInstance().addString(String.valueOf(Entity.gameTicks));
			//Console.getInstance().printLine();	
			
			//Logica del giocatore
			player.logic(monsters,wm);		
		
			//Aggiorna il campo di visale
			fov.refreshFOV(player.getMapPosition(), player.sightRadius);
			
			
			//Logica di mostri
			//Se il mostro esiste e non è morto
			for(int i=0;i<monsters.size();i++)
				if((!monsters.get(i).isDead()))
					//Esegue la logica
					monsters.get(i).logic(player);
				else{
					//Se il mostro è morto
					//Lo toglie dal livello e scrive sulla console un messaggio
					stage.getActors().removeValue(monsters.get(i), true);
					monsters.get(i).printDeath();
					monsters.remove(i);
			}					
			
			//Cambia il flag che permette di agire
			doAction=false;		

		}

		//Muove la telecamera insieme al giocatore
		handleCameraMovement();
		
		//Aggiorna l'HUD
		hud.updateHUD();		

		fpsLogger.log();

		//Disegna a schermo tutti gli oggetti della scena
		//TODO disegno con profondità isometrica
		super.render(delta);
		Table.drawDebug(stage);
				
		//Se non è in pausa
		if(!paused){
			//Temporizza i ticks  
	        if(timeToTick==0){
				if(Entity.gameTicks>=5){
					//Resetta i ticks, facendo ricominciare il round      
					Entity.gameTicks=0;
				}
				else{			
						//Altrimenti avanza nel tempo del turno
						Entity.gameTicks++;	
				}
				//Può agire
				doAction=true;
				timeToTick=16;
	        }
	        else
	        	//Diminuisce il tempo da aspettare tra i tick
	        	timeToTick--;
		}
			
			
	}
	
	//Gestisce il movimento della telecamera
	private void handleCameraMovement() {
		//Muove la telecamera seguendo il giocatore
		camera.position.set(player.getX(),player.getY(),0);
		
		//Sposta l'hud e la console di conseguenza
		hud.setPosition(camera.position.x-stage.getWidth()/2, camera.position.y-stage.getHeight()/2);
		Console.getInstance().setPosition(camera.position.x-stage.getWidth()/2,camera.position.y+( stage.getHeight()/2-Console.getInstance().getHeight()));
		inv.setPosition(camera.position.x-inv.getWidth()/2, camera.position.y-inv.getHeight()/2.5f);
		
		camera.update();
		stage.getSpriteBatch().setProjectionMatrix(camera.combined);	
		
	}

	/**Riassegna gli attori allo stage se lo schermo se viene cambiato**/
	private void reassignActorsToScreen() {
		
		if(wm.mustRedraw()){		
			//Riassegna i tiles il livello
			wm.reassignTiles(stage);
			
			//Riassegna i mostri e il giocatore
			stage.addActor(player);
			
			for(int i=0;i<monsters.size();i++)
				stage.addActor(monsters.get(i));
			
			//Riassegna l'hud e la console
			stage.addActor(hud);
			stage.addActor(Console.getInstance());
			stage.addActor(inv);
		}	
		
	}

	/**Ritorna l'istanza del giocatore**/
	public Player getPlayer() {
		return player;
	}
	
	/**Cambia lo stato di pausa del gioco**/
	public void togglePause(){
		paused=!paused;
	}

	public boolean isPaused() {
		return paused;
	}

}
