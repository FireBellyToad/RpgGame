package com.me.rpggame.items;

import com.me.rpggame.entities.Player;
import com.me.rpggame.world.Console;

/**
 * 
 * CLASSE DELLE POZIONI DI VITA E DI ENERGIA
 *  
 * @author Jacopo "Faust" Buttiglieri
 *
*/
public class RecoverPotion extends Item {

	public RecoverPotion(int itemID,Player player) {				
		super(itemID,player);	
	}
	
	@Override
	public void useItem() {
		
		int result=0;
		
		//In base al tipo, cura i pf o i pe con il tiro corretto
		switch(itemID){
		case ItemFactory.ITEM_SMALLHEALTH:{
				result = player.diceRoll(1, 8, 1, false);
				break;
			}		
		case ItemFactory.ITEM_MEDIUMHEALTH:{
				result = player.diceRoll(2, 8, 2, false);
				break;
			}	
		case ItemFactory.ITEM_BIGHEALTH:{
				result = player.diceRoll(4, 8, 4, false);
				break;
			}
		case ItemFactory.ITEM_SMALLENERGY:{
			result = player.diceRoll(1, 4, 1, false);
			break;
			}		
		case ItemFactory.ITEM_MEDIUMENERGY:{
				result = player.diceRoll(2, 4, 2, false);
				break;
			}	
		case ItemFactory.ITEM_BIGENERGY:{
				result = player.diceRoll(4, 4, 4, false);
				break;
			}
		}
		
		//Se era una pozione di vita, stampa i pf guariti
		if((itemID>=ItemFactory.ITEM_SMALLHEALTH)&&(itemID<=ItemFactory.ITEM_BIGHEALTH)){
			Console.getInstance().addString("PF guariti: "+result);
			player.cureDamage(result);		
		}
		
		//Se era una pozione di energia, stampa i pe recuperati
		if((itemID>=ItemFactory.ITEM_SMALLENERGY)&&(itemID<=ItemFactory.ITEM_BIGENERGY)){
			Console.getInstance().addString("PE recuperati: "+result);
			player.regainEnergy(result);		
		}
		
		Console.getInstance().printLine();				
	}

	@Override
	protected void setDescription() {	
		
		int numberOfDices=0, dice=0, bonus=0;
		
		switch(itemID){
			case ItemFactory.ITEM_SMALLHEALTH:{
					numberOfDices=bonus=1;
					dice=8;
					break;
			}		
			case ItemFactory.ITEM_MEDIUMHEALTH:{
					numberOfDices=bonus=2;
					dice=8;
					break;
			}	
			case ItemFactory.ITEM_BIGHEALTH:{
					numberOfDices=bonus=4;
					dice=8;
					break;
			}
			case ItemFactory.ITEM_SMALLENERGY:{
					numberOfDices=bonus=1;
					dice=4;
					break;
			}		
			case ItemFactory.ITEM_MEDIUMENERGY:{
					numberOfDices=bonus=2;
					dice=4;
					break;
			}	
			case ItemFactory.ITEM_BIGENERGY:{
					numberOfDices=bonus=4;
					dice=4;
					break;
			}
		}
		
		//Crea la descrizione		
		description="Questa pozione una volta bevuta permette di recupeare"+numberOfDices+"d"+dice+"+"+bonus+" punti ";
		
		//Se era una pozione di vita
		if((itemID>=ItemFactory.ITEM_SMALLHEALTH)&&(itemID<=ItemFactory.ITEM_BIGHEALTH)){
			description+="ferita";	
		}
		
		//Se era una pozione di energia
		if((itemID>=ItemFactory.ITEM_SMALLENERGY)&&(itemID<=ItemFactory.ITEM_BIGENERGY)){
			description+="energia";		
		}
		
		
	}

}
