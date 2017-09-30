package com.me.rpggame.world;

/**
 * CLASSE DEI TILE
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Tile extends Image{
		
	private Tile onTop=null;			//Tile in cima (oggetti)
	private Vector2 mapPosition;		//Posizione sulla mappa (matrice)
	private boolean tempPassability;	//attraversabilità temporanea
	private boolean isWithinSight;		//Se è visibile dal personaggio
	
	/**Costruttore con x e y
	 * 
	 * @param region texture region del tile
	 * @param x coordinata x del tile sulla matrice di mappa
	 * @param y coordinata y del tile sulla matrice di mappa
	 */
	public Tile(TextureRegion region, int x,int y){
		super(region);
		setExplored(false);
		mapPosition=new Vector2(x,y);
		tempPassability=true;
	}
		
	/**Costruttore con Vector2
	 * 
	 * @param region texture region del tile
	 * @param mapPosition posizione del tile sulla matrice di mappa
	 */
	public Tile(TextureRegion region, Vector2 mapPosition) {
		super(region);
		setExplored(false);
		this.mapPosition=mapPosition;
	}

	/**Funziona che ritorna le coordinate sulla matrice**/
	public Vector2 getMapPosition(){
		return mapPosition;
	}
	
	/**Procedura per il cambio dell'immagine visualizzata
	 * 
	 * @param region nuova texture region 
	 */
	public void setRegion(TextureRegion region){
		
		super.setDrawable(new TextureRegionDrawable(region));
	}

	/**Funzione che ritorna la X sulla matrice
	 * 
	 * @return coordinata x sulla mappa
	 */
	public int getMapX() {
		return (int) mapPosition.x;
	}

	/**Funziona che ritorna la Y sulla matrice
	 * 
	 * @return coordinata y sulla mappa
	 */
	public int getMapY() {
		return (int) mapPosition.y;
	}
	
	/**Funzione che ritorna se il tile è visibile
	 * 
	 * @return se è visibile o esplorato
	 */
	public boolean isTileInSight(){
		return (isWithinSight&&isVisible());
	}
	
	/** setta il flag del tile che indica se è stato esplorato
	 * 
	 * @param value nuovo valore del flag
	 */
	public void setExplored(boolean value){
		setWithinSight(value);
		setVisible(value);
		
		if(onTop!=null)
			onTop.setExplored(value);
	}

	/**Setta l'attraversabilità temporanea
	 * 
	 * @param newValue nuovo valore dell'attraversabilità temporanea
	 */
	public void setTemporaryPassability(boolean newValue) {
		tempPassability= newValue;		
	}

	/**Ottiene il valore dell'attraversabilità temporanea
	 * 
	 * @return attraversabilità temporanea
	 */
	public boolean getTemporaryPassability(){
		return tempPassability;
	}
	
	/**Setta il tile in cima al tile attuale
	 * 
	 * @param onTop il tile sopra quello attuale
	 */
	public void setOnTop(Tile onTop){
		this.onTop=onTop;
	}	
	
	/**Ottiene il tile in cima al tile attuale
	 * @return 
	 * 
	 * @return il tile sopra quello attuale
	 */
	public Tile getOnTop(){
		return onTop;
	}
	
	/**Procedura del disegno del tile. Override per gestire la fog of war**/
	@Override
	public void draw (SpriteBatch batch, float parentAlpha) {
		//Se non è in vista lo disegna semitrasparente (Fog of war)
		if(!isWithinSight)
			super.draw(batch,0.5f);
		else
			super.draw(batch,1f);
	}

	/**Setta se il tile è in vista o no
	 * 
	 * @param value vero se è in vista
	 */
	public void setWithinSight(boolean value) {
		isWithinSight=value;	
		
		if(onTop!=null)
			onTop.setWithinSight(value);
	}
}
