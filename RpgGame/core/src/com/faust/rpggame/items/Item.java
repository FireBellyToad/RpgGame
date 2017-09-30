package com.faust.rpggame.items;

import com.faust.rpggame.entities.Player;

/**
 * 
 * CLASSE ASTRATTA DEGLI OGGETTI
 *  
 * @author Jacopo "Faust" Buttiglieri
 *
*/

public abstract class Item {
	
	protected int itemID;
	protected Player player;
	protected String description;
	
	public Item(int itemID,Player player){
		this.player=player;
		this.itemID = itemID;	
		
		setDescription();
	}
	
	public int getItemID(){
		return itemID;
	}
	
	protected abstract void setDescription();
	
	protected String getDescription(){
		return description;
	}
	
	public abstract void useItem();

}
