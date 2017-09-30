package com.faust.rpggame.entities;

/**
 * CLASSE DELLE AZIONI DI GIOCO
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

public class EAction {
	
	//-------------- TIPI DI AZIONI-------------//
	
	public static final int STEP = 0;
	public static final int ATTACK = 1;
	public static final int SPELL = 2;
	public static final int SEARCH= 3;
	public static final int PICK= 4;
	public static final int DRINK= 5;
	
	//-------------- CAMPI -------------//
	
	private int costInTicks;	//Costo dell'azione in ticks
	private int actionType;		//Tipo dell'azione
	
	/**Costruttore**/
	public EAction(int type,int cost){
		
		setCostInTicks(cost);	
		setActionType(type);				
	}

	/**Ottiene il tipo di azione**/
	public int getActionType() {
		return actionType;
	}

	/**Setta il tipo di azione**/
	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

	/**Ottiene il costo in ticks**/
	public int getCostInTicks() {
		return costInTicks;
	}

	/**Setta il costo in ticks
	 * 
	 * @param costInTicks costo in ticks
	 */
	public void setCostInTicks(int costInTicks) {
		this.costInTicks = costInTicks;
	}

	/**Decrementa il costo dell'azione**/
	public void decreaseCost() {
		costInTicks--;		
	}

}
