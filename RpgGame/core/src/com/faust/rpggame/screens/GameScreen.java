package com.faust.rpggame.screens;

/**
 * CLASSE DELLA SCHERMATA DI GIOCO
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.faust.rpggame.CommonUtils;
import com.faust.rpggame.RpgGame;
import com.faust.rpggame.entities.Entity;
import com.faust.rpggame.entities.Player;
import com.faust.rpggame.tools.FOVManager;
import com.faust.rpggame.tools.Pathfinder;
import com.faust.rpggame.world.Console;
import com.faust.rpggame.world.Room;
import com.faust.rpggame.world.WorldManager;

public class GameScreen extends BaseScreen {
	
	//---------------- CAMPI DELLA CLASSE ----------------//
	
	private WorldManager wm;							//Gestore del mondo
	private OrthographicCamera camera;					//Telecamera
	private Room room;									//Livello
	private FPSLogger fpsLogger=new FPSLogger();	
	
	private int timeToTick;		//Tempo tra un tick e l'altro
	private boolean doAction;	//Flag che indica se i personaggi possono agire
	private boolean paused;
	
	private FOVManager fov;		//Calcolo Field of view
	
	//---------------- FUNZIONI ----------------//
		
	/**Costruttore**/
	public GameScreen(RpgGame game){
		super(game);		
        Gdx.input.setCatchBackKey(true);  
        timeToTick=CommonUtils.TIME_TO_TICK;
        doAction=true;  
        paused=false;
        resize(0,0);
	}
	
	@Override
	public void show() {
		
		room= new Room();
		
		wm= new WorldManager(null,room);
		
		fov =new FOVManager(wm);

		Pathfinder.getInstance().setWorldManager(wm);

		room.createPlayer(Player.WIZARD);
		room.createMonsters(5);
		room.getHud().setScreen(this);
		room.getHud().initHud(stage);
		room.setFov(fov);
		
		stage.addActor(room);
		
        Gdx.input.setInputProcessor( stage );        
		
		camera = (OrthographicCamera) stage.getCamera();		
		
		
	}


	@Override
	public void resize(int width, int height) {
		//Funzione di resize
		super.resize(width, height);
		Console.getInstance().setWidth(stage.getWidth());
	}

	@Override
	public void render(float delta){

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0f, 0, 0f, 1.0f);
				
		//Permette la pressione dei tasti
		stage.act();
				
		//Riassegna gli attori al cambio del livello
		room.reassignActorsToScreen();
		
		//Se il tasto back è premuto torna al menù dello schermo
		if (Gdx.input.isKeyPressed(Keys.BACK)){
			Console.getInstance().clearConsole();
			game.setScreen(new MenuScreen(game));	
		}


		//Se si può agire e il gioco non è in pausa, i personaggi agiscono
		if((doAction)&&(!paused) ){			
			
//			Console.getInstance().addString(String.valueOf(Entity.gameTicks));
//			Console.getInstance().printLine();	
			
			room.doTimedLogic();
			
			//Cambia il flag che permette di agire
			doAction=false;		
			
		}
		
		if(!paused)
			room.doUntimedLogic();
		
		
		//Muove la telecamera insieme al giocatore
		handleCameraMovement();
		
		//Aggiorna l'HUD
		room.getHud().updateHUD();		

//		fpsLogger.log();

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
				timeToTick=CommonUtils.TIME_TO_TICK;
	        }
	        else
	        	//Diminuisce il tempo da aspettare tra i tick
	        	timeToTick--;
		}
			
			
	}
	
	//Gestisce il movimento della telecamera
	private void handleCameraMovement() {
		//Muove la telecamera seguendo il giocatore
		camera.position.set(room.getPlayer().getX(),room.getPlayer().getY(),0);
		
		room.updateCamera(camera,stage);
		
		camera.update();
		stage.getBatch().setProjectionMatrix(camera.combined);	
		
	}
	
	/**Cambia lo stato di pausa del gioco**/
	public void togglePause(){
		paused=!paused;
	}

	public boolean isPaused() {
		return paused;
	}
}
