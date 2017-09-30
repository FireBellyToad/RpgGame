package com.faust.rpggame.world;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.faust.rpggame.CommonUtils;
import com.faust.rpggame.entities.Entity;
import com.faust.rpggame.entities.Monster;
import com.faust.rpggame.entities.MonsterFactory;
import com.faust.rpggame.entities.Player;
import com.faust.rpggame.inventory.Inventory;
import com.faust.rpggame.items.ItemFactory;
import com.faust.rpggame.projectiles.Projectile;
import com.faust.rpggame.tools.FOVManager;


public class Room extends Group {
	
	private Texture tileSheet;							//Foglio dei tiles
	private ArrayList<TextureRegion> tilesRegions;		//Texture Regions
	private ArrayList<Tile> tilesImage;					//Tiles
	private ArrayList<Tile> itemsImage;					//Tiles	

	private ArrayList<Monster> monsters;				//Mostri;
	private Player player;								//Personaggio	

	private ArrayList<Projectile> projectiles;			//Mostri;
	
	private Hud hud;									//Hud
	private Inventory inv;								//Inventario
	private static WorldManager wm;							//World Manager

	private boolean mustRedrawLevel=false;					//Flag per controllare se il livello è stato cambiato

	private FOVManager fov;
	
	private final static ArrayList<Entity> entities= new ArrayList<Entity>();	//Riferimenti a tutte le entità;
		
	public Room(){
		
		inv= new Inventory();
		
		monsters = new ArrayList<Monster>();
		projectiles = new ArrayList<Projectile>();
		
		tilesRegions=new ArrayList<TextureRegion>();
		tilesImage=new ArrayList<Tile>();		
		itemsImage=new ArrayList<Tile>();		
		
		//Carica foglio dei tiles
		tileSheet =new Texture(Gdx.files.internal("data/tiles.png"));
		tileSheet.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				
		//Crea le texture regions
		//TODO tilesset multipli
		TextureRegion tmp[][] = TextureRegion.split(tileSheet,tileSheet.getWidth()/5,  tileSheet.getHeight());              
		
		tilesRegions.add(tmp[0][0]);
		tilesRegions.add(tmp[0][1]);	
		tilesRegions.add(tmp[0][2]);	
		tilesRegions.add(tmp[0][3]);
		tilesRegions.add(tmp[0][4]);			

        hud= new Hud(inv);    
		
	}
	
	/**Riassegna gli attori allo stage se lo schermo se viene cambiato**/
	public void reassignActorsToScreen() {
		
		if(mustRedraw()){		
			//Riassegna i tiles il livello
			reassignTiles();
						
			for(int i=0;i<entities.size();i++)
				addActor(entities.get(i));
			
			//Riassegna l'hud e la console
			addActor(hud);
			addActor(Console.getInstance());
			addActor(inv);
		}	
		
	}
	
	/**Ritorna l'array dei mostri
	 * 
	 * @return array dei mostri
	 */
	public ArrayList<Monster> getMonsters(){
		return monsters;
	}
	
	/**Crea il personaggio giocante
	 * 
	 * @param characterClass classe
	 */
	public void createPlayer(int characterClass){
		
		String classes[] = {"cleric","fighter","thief","wizard"};
		
		Vector2 firstTile = wm.getFirstTile();
		
		player=new Player(new Texture(Gdx.files.internal("data/"+classes[characterClass]+".png")),"Giocatore",(int) firstTile.x,(int) firstTile.y,wm,inv,this,characterClass);		
		
		ItemFactory.getInstance().setPlayer(player);
		inv.setPlayer(player);
		hud.setPlayer(player);

		entities.add(player);
	}
	
	/**Aggiunge un proiettile
	 * 
	 * @param proj il proiettile
	 */
	public void addProjectile(Projectile proj){
		projectiles.add(proj);
		addActor(proj);
	}
		
	/**Getter del personaggio giocante
	 * 
	 * @return il personaggio
	 */
	public Player getPlayer() {
		return player;
	}

	/**Setter del personaggio giocante
	 * 
	 * @param player personaggio
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**Setter del World Manager
	 * 
	 * @param wm World Manager
	 */
	public void setWorldManager(WorldManager wm) {
		Room.wm = wm;
	}
	
	/**Crea i mostri nel livello**/
	public void createMonsters(int monstersNumber) {

		//TODO creare mostri diversi in base al livello
		for(int i=0;i<monstersNumber;i++){
//			addMonster(2,5,5);
		}	
	}
	
	public void addMonster(int type,int x,int y){
		
		//Crea il mostro
		final Monster tempMon = MonsterFactory.getInstance().createMonster(type,wm,x,y,this);
				
		//Rende il mostro cliccabile
		tempMon.addListener(new InputListener() {		
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	        	
	        	if(!player.startedCasting())
	        		player.setNewPath(tempMon.getMapPosition());
	        	
	        	player.setTarget(tempMon);
	            return true;
	        }			        
		});
		
		monsters.add(tempMon);	
		entities.add(tempMon);
	}
		
	//Cambia i tile mostrati a schermo
	public void changeDrawnWorld(){
		
		tilesImage.clear();
	
		for(int i=0;i<wm.getLevelSize();i++){			
			for(int j=0;j<wm.getLevelSize();j++){
				
				//Creazione del tile cliccabile
								
				if(wm.getTileAtMapPosition(j, i) != -1){
					final Tile tileTemp= buildTileImage(wm.getTileAtMapPosition(j, i),i,j);					
	
					//Se non è un tile di muro o di segreto permette di selezionarlo come destinazione di un path
					tileTemp.setOrigin(tilesRegions.get(0).getRegionWidth()/2, tilesRegions.get(0).getRegionHeight()/2);
					tileTemp.addListener(new InputListener() {		
				        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				        	if(wm.isTileWall(wm.getTileAtMapPosition(tileTemp.getMapX(),tileTemp.getMapY()))){
					        	tileTemp.addAction(Actions.sequence( Actions.scaleTo(0.95f, 0.95f),Actions.delay( 0.0125f ),Actions.scaleTo(1f, 1f), Actions.delay( 0.15f )));
					        	player.setNewPath(tileTemp.getMapPosition());
					        	player.setTarget(null);
				        	}
			                return true;
				        }			        
					});
						
	/*
					//Creazione dell'oggetto cliccabile				
					if(wm.getItemAtMapPosition(j, i)!=0){
						final Tile itemTemp=  new Tile(inv.getItemRegion(wm.getItemAtMapPosition(j, i)-1),j,i);				
	
						//Se non è un tile di muro o di segreto permette di selezionarlo come destinazione di un path
						itemTemp.setOrigin(inv.getItemRegion(0).getRegionWidth()/2,inv.getItemRegion(0).getRegionHeight()/2);
						itemTemp.addListener(new InputListener() {		
					        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					        	player.setNewPath(itemTemp.getMapPosition());
						        player.setTarget(null);
				                return true;
					        }			        
						});
						itemsImage.add(itemTemp); 
						tileTemp.setOnTop(itemTemp);	
					}			
					*/
					tilesImage.add(tileTemp); 
				}
			}
		}		
				
		mustRedrawLevel=true;
	}
	
	/**Assegna l'immagine corretta a seconda del tipo di tile sulla matrice (mappa)
	 * 
	 * @param type tipo di Tile
	 * @param xx coordinate x del tile sulla matrice
	 * @param yy coordinate y del tile sulla matrice
	 * @return il Tile costruito
	 */
	private Tile buildTileImage(int type, int xx, int yy) {
		
		if((type!= wm.TILE_SECRET_DOOR)&&(type!= wm.TILE_SECRET_FLOOR))
			return new Tile(tilesRegions.get(type),yy,xx);
			
		//Se è un passaggio o porta segreta, visualizzerà un tile di muro		
		return new Tile(tilesRegions.get(wm.TILE_WALL),yy,xx);
	}

	/** Assegna allo stage i tile. Viene eseguita solo se il livello cambia
	 * 
	 * @param stage stage in cui vengono assegnati i tile
	 * @param player entità del giocatore
	 */
	public void reassignTiles(){	
					
		this.clear();
		//Tiles
		for(int i=0;i<tilesImage.size();i++){
			tilesImage.get(i).setX((tilesImage.get(i).getMapX()+1)*tilesImage.get(i).getWidth());
			tilesImage.get(i).setY(800-((tilesImage.get(i).getMapY()+2)*tilesImage.get(i).getHeight()));
			this.addActor(tilesImage.get(i));			
		}	
		//Oggetti
		for(int i=0;i<itemsImage.size();i++){
			itemsImage.get(i).setX((itemsImage.get(i).getMapX()+1)*itemsImage.get(i).getWidth());
			itemsImage.get(i).setY(800-((itemsImage.get(i).getMapY()+2)*itemsImage.get(i).getHeight()));
			this.addActor(itemsImage.get(i));			
		}	
		
		mustRedrawLevel=false;	
		
	}		

	/**Ritorna il Tile esatto mostrato a schermo in quella posizione
	 * 
	 * @param mapPosition posizione del tile sullo schermo
	 * @return il tile
	 */
	public Tile getTileAtPosition(Vector2 mapPosition) {		
	
		if((mapPosition.x>=0)&&(mapPosition.x<wm.getLevelSize())&&(mapPosition.y>=0)&&(mapPosition.y<wm.getLevelSize())){
			
			for(Tile tile: tilesImage)
				if(tile.getMapPosition().equals(mapPosition))
					return tile;
		}
		
		return null;
	}	

	/**Ritorna il Tile o L'oggetto esatto mostrato a schermo in quella posizione
	 * 
	 * @param x coordinata x sullo schermo del tile o dell'oggetto
	 * @param y coordinata y sullo schermo del tile o dell'oggetto
	 * @param isItem flag che è vero se si deve ritornare un oggetto
	 * @return il tile o l'oggetto
	 */
	public Tile getTileAtPosition(int x, int y) {		

			if((x>=0)&&(x<wm.getLevelSize())&&(y>=0)&&(y<wm.getLevelSize())){
				
				for(Tile tile: tilesImage){
					Vector2 pos = tile.getMapPosition();
					if(pos.x == x && pos.y == y)
						return tile;
				}
			}
			
			return null;
	}
	
	/**Cambia il tile visualizzato
	 * 
	 * @param mapPosition indice del tile
	 * @param newType nuovo tipo del tile
	 */
	public void changeTile(Vector2 mapPosition, int newType) {
		
		getTileAtPosition(mapPosition).setRegion(tilesRegions.get(newType));
	}
	
	/**Ritorna i tiles
	 * 
	 * @return i tiles
	 */
	public ArrayList<Tile> getTiles(){
		return tilesImage;
	}

	/**Ritorna se si deve ridisegnare il livello
	 * 
	 * @return vero se si deve ridisegnare il livello
	 */
	public boolean mustRedraw() {
		return mustRedrawLevel;
	}
	
	/**Crea la Fog of War**/
	public void createFog() {

		for(int i=0;i<tilesImage.size();i++)
			tilesImage.get(i).setWithinSight(false);
	}

	/**Raccoglie un oggetto e lo rimuove dalla matrice**/
	public void pickItem(Vector2 mapPosition) {
		
		itemsImage.remove(tilesImage.get((int) (mapPosition.x+(wm.getLevelSize()*mapPosition.y))).getOnTop());
		tilesImage.get((int) (mapPosition.x+(wm.getLevelSize()*mapPosition.y))).getOnTop().remove();
		tilesImage.get((int) (mapPosition.x+(wm.getLevelSize()*mapPosition.y))).setOnTop(null);
		
	}
	
	/**Esegue la logica di gioco temporizzata**/
	public void doTimedLogic() {
		
		//Logica dei proiettili
		//Se il mostro esiste e non è morto
		for(int i=0;i<projectiles.size();i++)
			if((!projectiles.get(i).isDead()))
				//Esegue la logica
				projectiles.get(i).decreaseLife();
			else{
				//Se il mostro è morto
				//Lo toglie dal livello e scrive sulla console un messaggio
				removeActor(projectiles.get(i));
				projectiles.get(i).onDeath();
				projectiles.remove(i);
				
		}	
		
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
				removeActor(monsters.get(i));
				monsters.get(i).printDeath();
				entities.remove(monsters.get(i));
				monsters.remove(i);
				
		}	
		
	}
	
	/**Esegue la logica di gioco temporizzata**/
	public void doUntimedLogic() {		
		
		for(int i=0;i<projectiles.size();i++)
			if((!projectiles.get(i).isDead()))
				//Esegue la logica
				projectiles.get(i).move();
		
	}
	
	/**Ritorna l'array di tutte le entità entro un range da una data posizione
	 * 
	 * @param position posizione centrale
	 * @param range raggio della ricerca
	 * @param excludeCenter flag, true se si deve escludere il punto di partenza
	 * @return array di entità entro il limite
	 */
	public static ArrayList<Entity> getEntitesInRange(Vector2 position, int range, boolean excludeCenter){

		ArrayList<Entity> entitiesFound=new ArrayList<Entity>();
		
		for(int i=0;i<range;i++){
			
			for(int j=0;j<entities.size();j++){
				if(CommonUtils.distance(position,entities.get(j).getMapPosition())==i)
					if(!entitiesFound.contains(entities.get(j)))
						entitiesFound.add(entities.get(j));
			}
		}		

		if(excludeCenter)
			entitiesFound.remove(0);
		
		return entitiesFound;
	}
	
	/**Getter dell'HUD
	 * 
	 * @return l'hud
	 */
	public Hud getHud() {
		return hud;
	}
	
	/**Aggiorna la posizione della camera
	 * 
	 * @param camera camera da aggiornare
	 */
	public void updateCamera(Camera camera,Stage stage) {
		//Sposta l'hud e la console di conseguenza
		hud.setPosition(camera.position.x-stage.getWidth()/2, camera.position.y-stage.getHeight()/2);
		Console.getInstance().setPosition(camera.position.x-stage.getWidth()/2,camera.position.y+( stage.getHeight()/2-Console.getInstance().getHeight()));
		inv.setPosition(camera.position.x-inv.getWidth()/2, camera.position.y-inv.getHeight()/2.5f);		
		
	}

	/**Setter del manager del Field of View
	 * 
	 * @param fov l'oggetto
	 */
	public void setFov(FOVManager fov) {
		this.fov=fov;
	}
	
	/**Linea di vista
	 * 
	 * @param targetposition posizione rispetto alla quale si deve controllare la linea di vista
	 * @return vero se l'entità riesce a vederla
	 */
	public static boolean los(Vector2 startposition,Vector2 targetposition)
	{
		float t, x, y, ax, ay, sx, sy, dx, dy;
		
		//Se per caso la casella di destinazione e partenza sono le stesse, si restituisce automaticamente vero
		if(startposition.equals(targetposition))
			return true;
	 
	   //Si ottiene la differenza (delta) tra le coordinate
	   dx = startposition.x - targetposition.x;
	   dy = startposition.y - targetposition.y;
	   	 
	   //Valori assoluti molticati per 2
	   ax = Math.abs((int) dx)*2;
	   ay = Math.abs((int) dy)*2;
	 
	   //Segni di x e y
	   sx = Math.signum(dx);
	   sy = Math.signum(dy);
	 
	   //Coordinate x e y della destinazione
	   x = targetposition.x;
	   y = targetposition.y;
	 
	   //Controlla se la linea è più orizzontale che verticlae
	   if(ax > ay)
	   {
	      // t = assoluto di y - il doppio dell'assoluto di x
	      t = ay - (ax/2);
	      do
	      {
	         if(t >= 0)
	         {
	            /* se t è maggiore o uguale a zero, aggiunge il segno di
	             *  DeltaY a y e sottrae l'assoluto di X da t      */
	            y += sy;
	            t -= ax;
	         }
	 
	         //aggiunge il segno di dx a x e l'assoluto di Y a t
	         x += sx;
	         t += ay;
	 
	         //controlla se non abbiamo raggiunto la destinazione
	         if( x == startposition.x && y == startposition.y)
	         {
	            //si può vedere la destinazione
	            return true;
	         }
	      //continua a cercare fino a che la visuale è bloccata da un ostacolo
	      }
	      while(wm.isTileViewObstacle(wm.getTileAtMapPosition((int)x,(int) y)) == false);
	 
	      //Non si può vedere la destinazione
	      return false;
	   }
	   else
	   {
	      //caso in cui la linea sia più verticale. Uguale al caso superiore solo invertito
	      t = ax - (ay/2);
	      do
	      {
	         if(t >= 0)
	         {
	            x += sx;
	            t -= ay;
	         }
	         y += sy;
	         t += ax;
	         if(x == startposition.x && y == startposition.y)
	         {
	            return true;
	         }
	      }
	      while(wm.isTileViewObstacle(wm.getTileAtMapPosition((int)x,(int) y)) == false);
	      return false;
	   }
	}
	
}
