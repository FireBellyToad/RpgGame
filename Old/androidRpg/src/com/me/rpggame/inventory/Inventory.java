package com.me.rpggame.inventory;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.me.rpggame.entities.Player;
import com.me.rpggame.items.Item;
import com.me.rpggame.items.ItemButton;

/**
 * CLASSE DELL'INVENTARIO
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

public class Inventory extends Group {	
	
	private Texture itemSheet;			//Foglio dei tile
	private Texture invTexture;			//Texture dello sfondo dell'hud		
	
	private Image invImage;  			//Immagine dello sfondo dell'hud
	
	
	private ArrayList<TextureRegion> itemsRegions;		//Texture Regions

	private Table rootTable;
	private Player player;
	
	/**Costruttore**/
	public Inventory(){
		
		rootTable=new Table();
		rootTable.left().top();
		itemsRegions=new ArrayList<TextureRegion>();
		
		// Carica le texture
		invTexture =new Texture(Gdx.files.internal("data/inventoryBack.png"));
		invTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		//Carica foglio dei tiles
		itemSheet =new Texture(Gdx.files.internal("data/items.png"));
		itemSheet.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		//Crea le texture regions
		//TODO tilesset multipli
		TextureRegion tmp[][] = TextureRegion.split(itemSheet,itemSheet.getWidth()/4, itemSheet.getHeight()); 
		 
		itemsRegions.add(tmp[0][0]);
		itemsRegions.add(tmp[0][1]);	
		itemsRegions.add(tmp[0][2]);	
		itemsRegions.add(tmp[0][3]);
		
		//Crea le immagini per i pulsanti dell'hud
		invImage =new Image(invTexture);
		this.addActor(invImage);
		
		
		setSize(invImage.getWidth(),invImage.getHeight());
		this.addActor(rootTable);		
		rootTable.setFillParent(true);
		
		setVisible(false);

	}
	
	/**Associa il giocatore
	 * 
	 * @param player giocatore
	 */
	public void setPlayer(Player player){

		this.player=player;
		createInventoryScreen();
	}
	
	/**Crea la schermata dell'inventario**/
	public void createInventoryScreen(){
		
		rootTable.clear();
		ArrayList<Item> itemsTemp=player.getItemsCarried();
		
		for(int i=0;i<itemsTemp.size();i++){
			addItemToInventory(itemsTemp.get(i),i);
		}
	}
	
	/**Aggiunge un oggetto all'inventario
	 * 
	 * @param item oggetto da aggiungere
	 * @param index indice
	 */
	public void addItemToInventory(Item item,int index){

		if((index%(Player.MAX_ITEMS/5)==0)){
			//Se non è il primo elemento, aggiunge una riga
			if(index!=0)
				rootTable.row();
			//Il primo di ogni riga è spostato più a destra rispetto agli altri (padLeft(25))
			rootTable.add(addButton(item,index)).width(70).height(70).pad(15).padLeft(20);
		}
		else
			rootTable.add(addButton(item,index)).width(70).height(70).pad(15);
	}	
			
	/**Aggiunge un pulsante che utilizza un oggetto se cliccato
	 * 
	 * @param item oggetto che verrà usato sul clic;
	 * @param index l'indice dell'oggetto
	 * @return 
	 */
	private ItemButton addButton(Item item, int index){
		
		//Creazione del pulsante cliccabile per usare l'oggetto
		//TODO immagine del pulsante
		final ItemButton itemTemp= new ItemButton(itemsRegions.get(item.getItemID()-1),index);	

		//Se si clicca sul pulsante si utilizza l'oggetto consumandolo
		itemTemp.setOrigin(itemsRegions.get(0).getRegionWidth()/2, itemsRegions.get(0).getRegionHeight()/2);
		itemTemp.addListener(new InputListener() {		
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		        player.getItemsCarried().get(itemTemp.getItemIndex()).useItem();
		        player.removeItem(itemTemp.getItemIndex());
		        createInventoryScreen();
                return true;
	        }			        
		});
		
		//itemTemp.setPosition(125+(currentRow), 125+(currentColumn));
		return itemTemp;
			
	}	
	
	/**Ottiene la region dell'oggetto
	 * 
	 * @param region region da ritornare
	 * @return la region ritornata
	 */
	public TextureRegion getItemRegion(int region){
		return itemsRegions.get(region);
	}

}
