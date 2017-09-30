package com.faust.rpggame.items;

/**
 * CLASSE FACTORY PER GLI OGGETTI (SINGLETON)
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

import com.faust.rpggame.entities.Player;

public class ItemFactory {

	private static ItemFactory INSTANCE = new ItemFactory();
	
	private Player player;
	
	//--------------ITEMS--------------//
	
	public static final int ITEM_SMALLHEALTH = 1;
	public static final int ITEM_MEDIUMHEALTH= 2;
	public static final int ITEM_BIGHEALTH= 3;
	public static final int ITEM_SMALLENERGY = 4;
	public static final int ITEM_MEDIUMENERGY= 5;
	public static final int ITEM_BIGENERGY= 6;
	
	private ItemFactory(){}
	
	public void setPlayer(Player player){
		this.player=player;
	}
	
	public Item createItem(int index){
		
		if((index>=ITEM_SMALLHEALTH)&&(index<=ITEM_BIGENERGY)){
			return new RecoverPotion(index,player);
		}
		
		return null;
	}
	
	public static ItemFactory getInstance(){
		return INSTANCE;
	}
}
