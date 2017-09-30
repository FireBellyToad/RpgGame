package com.me.rpggame.world;

/**
 * CLASSE PER LA GESTIONE DEI LIVELLI DI GIOCO
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.me.rpggame.inventory.Inventory;
import com.me.rpggame.screens.GameScreen;

public class WorldManager{
	
	private Texture tileSheet;							//Foglio dei tiles
	private ArrayList<TextureRegion> tilesRegions;		//Texture Regions
	private ArrayList<Tile> tilesImage;					//Tiles
	private ArrayList<Tile> itemsImage;					//Tiles	
	
	private Inventory invent;
	private GameScreen targetScreen;					//Schermo di gioco
	
	private boolean mustRedrawLevel=false;					//Flag per controllare se il livello è stato cambiato

	private int levelWidth=11;							//Larghezza attuale del livello
	private int levelHeight=11;							//Altezza attuale del livello
	//private final int MAX_WIDTH = 100;					//Larghezza massima
	//private final int MAX_HEIGHT = 100;					//Altezza massima
	
	//--------------TILES--------------//
	
	public final int TILE_FLOOR = 0;
	public final int TILE_WALL = 1;
	public final int TILE_DOOR_CLOSED= 2;
	public final int TILE_DOOR_OPENED= 3;
	public final int TILE_LIQUID = 4;
	public final int TILE_SECRET_DOOR = 5;
	public final int TILE_SECRET_FLOOR= 6;
	
	//private int currentWorld[][]=new int[MAX_WIDTH][MAX_HEIGHT];			//Mondo attuale
	private int currentWorld[][]={
			 {0,0,0,0,0,0,0,0,0,0,0},
			 {0,0,0,0,0,0,0,0,0,0,0},
			 {0,0,0,0,0,0,0,0,0,0,0},
			 {0,0,0,0,0,0,0,0,0,0,0},
			 {0,1,0,0,1,2,1,0,0,1,0},
			 {0,6,0,0,1,0,1,0,0,5,0},
			 {0,1,0,0,1,2,1,0,0,1,0},
			 {0,0,0,0,0,0,0,0,0,0,0},
			 {0,0,0,0,0,0,0,0,0,0,0},
			 {0,0,0,0,0,0,0,0,0,0,0},
			 {0,0,0,0,0,0,0,0,0,0,0}
			};
	
	private int currentItems[][]={
			 {0,0,3,0,0,0,0,0,0,0,0},
			 {0,0,0,0,0,0,0,0,0,0,0},
			 {0,0,0,0,0,0,0,0,1,0,0},
			 {0,0,0,0,0,0,0,0,0,0,0},
			 {0,0,0,0,0,0,0,0,0,0,0},
			 {0,0,0,0,0,1,0,0,0,0,0},
			 {0,0,0,0,0,0,0,0,0,0,0},
			 {0,0,0,0,0,0,0,0,0,0,0},
			 {0,0,0,0,0,0,0,0,0,0,0},
			 {0,0,0,0,0,0,0,0,0,0,0},
			 {2,0,0,0,0,0,0,0,0,0,4}
			};
	
	/**Costruttore
	 * 
	 * @param loadFile il file da caricare. Se è null genera un nuovo livello
	 * @param targetScreen lo schermo dove sarà disegnato lo stage
	 */
	public WorldManager(String loadFile,GameScreen targetScreen,Inventory invent){
		
		tilesRegions=new ArrayList<TextureRegion>();
		tilesImage=new ArrayList<Tile>();		
		itemsImage=new ArrayList<Tile>();		
				
		//Carica foglio dei tiles
		tileSheet =new Texture(Gdx.files.internal("data/tiles.png"));
		tileSheet.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				
		//Crea le texture regions
		//TODO tilesset multipli
		TextureRegion tmp[][] = TextureRegion.split(tileSheet,tileSheet.getWidth()/4,  tileSheet.getHeight());              
		
		tilesRegions.add(tmp[0][0]);
		tilesRegions.add(tmp[0][1]);	
		tilesRegions.add(tmp[0][2]);	
		tilesRegions.add(tmp[0][3]);		
		
		//Setta lo schermo e crea i tile per il livello
		this. targetScreen= targetScreen;
		this.invent= invent;
		changeWorld(loadFile);
				
	}
	
	//Cambia i tile mostrati a schermo
	public void changeDrawnWorld(){
		
		tilesImage.clear();
	
		for(int i=0;i<levelWidth;i++){			
			for(int j=0;j<levelHeight;j++){
				
				//Creazione del tile cliccabile
								
				final Tile tileTemp= buildTileImage(currentWorld[i][j],i,j);					

				//Se non è un tile di muro o di segreto permette di selezionarlo come destinazione di un path
				tileTemp.setOrigin(tilesRegions.get(0).getRegionWidth()/2, tilesRegions.get(0).getRegionHeight()/2);
				tileTemp.addListener(new InputListener() {		
			        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			        	if(isTileWall(currentWorld[tileTemp.getMapY()][tileTemp.getMapX()])){
				        	tileTemp.addAction(Actions.sequence( Actions.scaleTo(0.95f, 0.95f),Actions.delay( 0.0125f ),Actions.scaleTo(1f, 1f), Actions.delay( 0.15f )));
				        	targetScreen.getPlayer().setNewPath(tileTemp.getMapPosition());
				        	targetScreen.getPlayer().setTarget(null);
			        	}
		                return true;
			        }			        
				});
					

				//Creazione dell'oggetto cliccabile				
				if(currentItems[i][j]!=0){
					final Tile itemTemp=  new Tile(invent.getItemRegion(currentItems[i][j]-1),j,i);				

					//Se non è un tile di muro o di segreto permette di selezionarlo come destinazione di un path
					itemTemp.setOrigin(invent.getItemRegion(0).getRegionWidth()/2,invent.getItemRegion(0).getRegionHeight()/2);
					itemTemp.addListener(new InputListener() {		
				        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					        targetScreen.getPlayer().setNewPath(itemTemp.getMapPosition());
					        targetScreen.getPlayer().setTarget(null);
			                return true;
				        }			        
					});
					itemsImage.add(itemTemp); 
					tileTemp.setOnTop(itemTemp);	
				}			
				
				tilesImage.add(tileTemp); 
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
		
		if((type!= TILE_SECRET_DOOR)&&(type!= TILE_SECRET_FLOOR))
			return new Tile(tilesRegions.get(type),yy,xx);
			
		//Se è un passaggio o porta segreta, visualizzerà un tile di muro		
		return new Tile(tilesRegions.get(TILE_WALL),yy,xx);
	}

	//Carica il livello o lo genera random
	public void changeWorld(String loadFile){
		
		if(loadFile==null){
			//TODO algoritmo di generazione casuale
		}
		else{
			//TODO algoritmo per caricarlo dal file
		}
		
		changeDrawnWorld();	
		
	}
	
	/** Assegna allo stage i tile. Viene eseguita solo se il livello cambia
	 * 
	 * @param stage stage in cui vengono assegnati i tile
	 * @param player entità del giocatore
	 */
	public void reassignTiles(Stage stage){	
					
		stage.clear();
		//Tiles
		for(int i=0;i<tilesImage.size();i++){
			tilesImage.get(i).setX((tilesImage.get(i).getMapX()+1)*tilesImage.get(i).getWidth());
			tilesImage.get(i).setY(800-((tilesImage.get(i).getMapY()+2)*tilesImage.get(i).getHeight()));
			stage.addActor(tilesImage.get(i));			
		}	
		//Oggetti
		for(int i=0;i<itemsImage.size();i++){
			itemsImage.get(i).setX((itemsImage.get(i).getMapX()+1)*itemsImage.get(i).getWidth());
			itemsImage.get(i).setY(800-((itemsImage.get(i).getMapY()+2)*itemsImage.get(i).getHeight()));
			stage.addActor(itemsImage.get(i));			
		}	
		
		mustRedrawLevel=false;	
		
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
					
					switch(getMapTileAtPosition(temp)){
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
		
		tilesImage.get((int) (mapPosition.x+(levelHeight*mapPosition.y))).setRegion(tilesRegions.get(newType));
	}

	/**Ritorna il Tile esatto mostrato a schermo in quella posizione
	 * 
	 * @param mapPosition posizione del tile sullo schermo
	 * @return il tile
	 */
	public Tile getTileAtPosition(Vector2 mapPosition) {		
	
		if((mapPosition.x>=0)&&(mapPosition.x<levelWidth)&&(mapPosition.y>=0)&&(mapPosition.y<levelHeight))
			return(tilesImage.get((int) (mapPosition.x+(levelHeight*mapPosition.y))));
		else
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

			if((x>=0)&&(x<levelWidth)&&(y>=0)&&(y<levelHeight))
				return(tilesImage.get((int) (x+(levelHeight*y))));
			else
				return null;
	}
	
	/**Ritorna il tile sulla matrice in quella posizione
	 * 
	 * @param x coordinata x sullo schermo del tile
	 * @param y coordinata y sullo schermo del tile
	 * @return il tile
	 */
	public int getMapTileAtPosition(int x,int y) {		

		if((x>=0)&&(x<levelWidth)&&(y>=0)&&(y<levelHeight))
			return currentWorld[y][x];
		else
			return -1;
	}

	/**Ritorna il tile sulla matrice in quella posizione
	 * 
	 * @param mapPosition posizione sulla matrice
	 * @return il tile
	 */
	public int getMapTileAtPosition(Vector2 mapPosition) {
		
		if(( mapPosition.x>=0)&&(mapPosition.x<levelWidth)&&(mapPosition.y>=0)&&(mapPosition.y<levelHeight))
			return currentWorld[(int) mapPosition.y][(int) mapPosition.x];
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
	public int getLevelWidth() {
		return levelWidth;
	}
	
	/**Ottiene l'altezza del livello
	 * .
	 * @return l'altezza del livello
	 */
	public int getLevelHeight() {
		return levelHeight;
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
		
		Tile temp=getTileAtPosition(x, y);
		
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
	private boolean isTileWall(int type) {
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
			itemsImage.remove(tilesImage.get((int) (mapPosition.x+(levelHeight*mapPosition.y))).getOnTop());
			tilesImage.get((int) (mapPosition.x+(levelHeight*mapPosition.y))).getOnTop().remove();
			tilesImage.get((int) (mapPosition.x+(levelHeight*mapPosition.y))).setOnTop(null);
		}
		
		return memo;
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


}
