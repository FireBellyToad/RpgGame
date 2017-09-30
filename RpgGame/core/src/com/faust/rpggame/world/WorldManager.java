package com.faust.rpggame.world;

/**
 * CLASSE PER LA GESTIONE DEI LIVELLI DI GIOCO
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

import com.badlogic.gdx.math.Vector2;
import com.faust.rpggame.CommonUtils;
import com.faust.rpggame.entities.Entity;

public class WorldManager{
	
	public final static int LEVELSTYLE_DUNGEON = 0;

	private int levelSize=20;							//Larghezza attuale del livello
	private Room room;
	//private final int MAX_WIDTH = 100;					//Larghezza massima
	//private final int MAX_HEIGHT = 100;					//Altezza massima
	
	//--------------TILES--------------//

	public final int TILE_UNUSED = -1;	
	public final int TILE_FLOOR = 0;
	public final int TILE_WALL = 1;
	public final int TILE_DOOR_CLOSED= 2;
	public final int TILE_DOOR_OPENED= 3;
	public final int TILE_LIQUID = 4;
	public final int TILE_SECRET_DOOR = 5;
	public final int TILE_SECRET_FLOOR= 6;
	
	//private int currentWorld[][]=new int[MAX_WIDTH][MAX_HEIGHT];			//Mondo attuale
	private int currentWorld[][];
	
	private int currentItems[][];

	private DungeonGenerator dg;
	
	
	/**Costruttore
	 * 
	 * @param loadFile il file da caricare. Se è null genera un nuovo livello
	 * @param targetScreen lo schermo dove sarà disegnato lo stage
	 */
	public WorldManager(String loadFile,Room room){

		this.room=room;
		dg = new DungeonGenerator(this);
		room.setWorldManager(this);
		changeWorld(loadFile,0);
	}
	
	/**Carica il livello o lo genera random
	 * 
	 * @param loadFile nome del file da caricare
	 */
	public void changeWorld(String loadFile,int levelStyle){
		
		if(loadFile==null){
			levelSize=CommonUtils.diceRoll(50, 2, 0, false);
			currentWorld = dg.createDungeon(levelSize, levelSize, 30);
			
		}
		else{
			//TODO algoritmo per caricarlo dal file
		}

		room.changeDrawnWorld();	
		for(Vector2 holder : dg.getEnemyPlaceholders()){
			room.addMonster(CommonUtils.diceRoll(1,3,0,false), (int) holder.x,(int)  holder.y);
		}
		
	}
	
	/**Funzione chiamata quando il giocatore effettura una ricerca
	 * 
	 * @param searchResult esito della ricerca (true se è riuscita)
	 * @param mapPosition posizione del giocatore sulla matrice
	 */
	public void playerSearch(boolean searchResult, Vector2 mapPosition) {
		//Se la ricerca ha avuto successo
		if(searchResult){
			
			Vector2 temp= new Vector2();
			
			//Cerca nelle 8 caselle adiacenti porte o passaggi segreti, aprendoli
			for(int yy=-1;yy<2;yy++){
				
				temp.y= mapPosition.y+yy;
				
				
				for(int xx=-1;xx<2;xx++){

					temp.x= mapPosition.x+xx;
					
					switch(getTileAtMapPosition(temp)){
					//Porta segreta					
					case TILE_SECRET_DOOR:{
							changeTileType(temp,TILE_DOOR_CLOSED);
							break;
						}
					//Passaggio segreta					
					case TILE_SECRET_FLOOR:{
							changeTileType(temp,TILE_FLOOR);
							break;
						}
					}
				}				
			}
		}
		
	}
	
	/**Cambia il tipo di tile in quella posizione 
	 * 
	 * @param mapPosition posizione del tile sulla matrice
	 * @param newType nuovo tipo del tile
	 */
	public void changeTileType(Vector2 mapPosition,int newType){
		
		currentWorld[(int) mapPosition.y][(int) mapPosition.x]= newType;
		
		room.changeTile(mapPosition,newType);
	}

	
	/**Ritorna il tile sulla matrice in quella posizione
	 * 
	 * @param x coordinata x sullo schermo del tile
	 * @param y coordinata y sullo schermo del tile
	 * @return il tile
	 */
	public int getTileAtMapPosition(int x,int y) {		

		if((x>=0)&&(x<levelSize)&&(y>=0)&&(y<levelSize))
			return currentWorld[y][x];
		else
			return -1;
	}

	/**Ritorna il tile sulla matrice in quella posizione
	 * 
	 * @param mapPosition posizione sulla matrice
	 * @return il tile
	 */
	public int getTileAtMapPosition(Vector2 mapPosition) {
		
		if(( mapPosition.x>=0)&&(mapPosition.x<levelSize)&&(mapPosition.y>=0)&&(mapPosition.y<levelSize))
			return currentWorld[(int) mapPosition.y][(int) mapPosition.x];
		else
			return -1;
	}	
	
	/**Ritorna l'oggetto in quella posizione
	 * 
	 * @param x coordinata x sullo schermo del tile
	 * @param y coordinata y sullo schermo del tile
	 * @return il tile
	 */
	public int getItemAtMapPosition(int x,int y) {		

		if((x>=0)&&(x<levelSize)&&(y>=0)&&(y<levelSize))
			return currentItems[y][x];
		else
			return -1;
	}
	/**Ritorna l'oggetto sulla matrice in quella posizione
	 * 
	 * @param mapPosition posizione sulla matrice
	 * @return il tile
	 */
	public int getItemAtMapPosition(Vector2 mapPosition) {
		
		if(( mapPosition.x>=0)&&(mapPosition.x<levelSize)&&(mapPosition.y>=0)&&(mapPosition.y<levelSize))
			return currentItems[(int) mapPosition.y][(int) mapPosition.x];
		else
			return -1;
	}	
	
	/**Ottiene la matrice del livello (mappa)
	 * 
	 * @return la matrice del livello
	 */
	public int[][] getWorld() {
		return  currentWorld;
	}

	/**Ottiene la larghezza del livello
	 * 
	 * @return la larghezza del livello
	 */
	public int getLevelSize() {
		return levelSize;
	}

	/**Ritorna se il tile è un ostacolo alla visuale
	 * 
	 * @param type tipo del tile
	 * @return vero se è d'ostacolo alla visuale (muri, porte chiuse, limiti del livello e segreti non trovati)
	 */
	public boolean isTileViewObstacle(int type) {
		
		boolean isObstacle=((type==TILE_WALL)||(type==TILE_DOOR_CLOSED)||(type==-1));
		
		isObstacle=(isObstacle || (type==TILE_SECRET_DOOR) || (type==TILE_SECRET_FLOOR));		
		
		return isObstacle;		
	}
	
	/**Ritorna se il tile può essere attraversato o meno
	 * 
	 * @param type tipo del tile
	 * @param x coordinata x sulla matrice
	 * @param y coordinata y sulla matrice
	 * @return vero se è attraversabile
	 */
	public boolean isTilePassable(int type, int x,int y) {
		//TODO gestione passabilità con levitazione
		boolean isPassable=!isTileViewObstacle(type);
		
		isPassable= isPassable && (type!=TILE_LIQUID);
		
		Tile temp=room.getTileAtPosition(x, y);
		
		//Se il tile è temporaneamente non attraversabile, restituisce false
		if(temp!=null)
			isPassable= isPassable && temp.getTemporaryPassability();
		
		return isPassable;		
	}
			
	/**Ritorna se il tile è un muro o una porta o passaggio nascosto
	 * 
	 * @param type il tipo del tile
	 * @return veri se è un muro o segreti non trovati
	 */
	boolean isTileWall(int type) {
		//TODO gestione passabilità con levitazione
		boolean isWall=((type!=TILE_WALL)&&(type!=TILE_SECRET_DOOR)&&(type!=TILE_SECRET_FLOOR));
		
		return isWall;		
	}
	
	/** Raccoglie l'oggetto in quella posizione
	 * 
	 * @param mapPosition posizione del tile sulla matrice
	 * @param newType nuovo tipo del tile
	 * @return il tipo di oggetto raccolto
	 */
	public int pickItem(Vector2 mapPosition){
		
		int memo =currentItems[(int) mapPosition.y][(int) mapPosition.x];
		
		if(memo!=0){			
			//Toglie l'oggetto raccolto dalla mappa
			currentItems[(int) mapPosition.y][(int) mapPosition.x]= 0;
			
			//Toglie l'oggetto raccolto dallo schermo
			room.pickItem(mapPosition);
		}
		
		return memo;
	}

	/**Getter della room
	 * 
	 * @return room attuale
	 */
	public Room getRoom() {
		return room;
	}

	public Vector2 getFirstTile() {
		for(int i=0;i<levelSize;i++)
			for(int j=0;j<levelSize;j++){
				if(getTileAtMapPosition(j, i)==TILE_FLOOR){
					return new Vector2(j,i);
				}
			}
		return null;
	}

}
