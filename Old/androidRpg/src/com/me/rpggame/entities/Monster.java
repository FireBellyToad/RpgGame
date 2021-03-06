package com.me.rpggame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.me.rpggame.items.Weapon;
import com.me.rpggame.world.Console;
import com.me.rpggame.world.WorldManager;

public class Monster extends Entity {	
	
	//-------------- COMPORTAMENTO MOSTRO -------------//
	
	public static final int PEACEFUL = 0;
	public static final int HOSTILE = 1;

	private Vector2 destination;
	private int damageBonus;
	private boolean stillSearching;
	private boolean startAttack;
	

	/**Costruttore**/
	public Monster(Texture texture, int mx, int my, WorldManager wm, String name, int hp,int savingThrow,Weapon weapon,int damageBonus,
			int damageResistance,int magicResistance) {
		super(texture,name, mx, my, wm);
		maxHp=actualHp=hp;
		this.savingThrow=savingThrow;
		this.wieldedWeapon=weapon;	
		this.damageResistance=damageResistance;
		this.magicResistance=magicResistance;
		stillSearching=false;
		startAttack=false;
		// TODO Costruttore del mostro
	}
	
	/**logica del mostro**/
	public void logic(Player player){		
		
		super.logic();
		
		startAttack=searchPlayer(player);
		
		//Inizializza il bersaglio (i nemici attaccano solo il giocatore)
		if(target==null)
			setTarget(player);
		
		prepareAttack(player);
		
		//In base all'azione attuale
		switch(currentAction.getActionType()){
			case EAction.ATTACK:{	
				//Attacca il bersaglio (giocatore)
					attackTarget();
				break;
			}
			case EAction.STEP:{
				//Muove il personaggio
				moveToTarget();		
				
				//Posiziona lo sprite a schermo
				setPosition((1+mapPosition.x)*getWidth(),800-(2+mapPosition.y)*getHeight());
				break;
			}
		}

		//Se il tile su cui � posizionata � visibile
		if(wm.getTileAtPosition(mapPosition).isTileInSight())
			//L'entit� � visibile
			this.setVisible(true);
		else
			this.setVisible(false);

		//Se il round � ricominciato
		if(gameTicks==0){

			//Resetta i ticks disponibili a inizio round
			remainingTicks=6;
			
			//Se il giocatore � in vista o � appena uscito dalla vista
			if(!startAttack&&!stillSearching){
				//Passeggia
				wander();					
			}
			else{					
					//Se non � adiacente al giocatore
					if(distance(player.getMapPosition(),mapPosition)>1)
					{
						//Lo raggiunge
						setNewPath(player.getMapPosition());						
					}
					else
						//Si ferma
						addGameAction(EAction.STEP,0);					

					//Se il giocatore � fuori vista ed � passato un round, termina di cercarlo
					if(!searchPlayer(player))
						stillSearching=false;
			}			
		}
	}	
	
	/**Funzione di ricerca del giocatore**/
	private boolean searchPlayer(Player player) {
		
		//Flag che indica se � stato trovato
		boolean founded=false;
		
		//Se � entro la visuale del mostro e non � dietro ad un muro
		if((distance(player.getMapPosition(),mapPosition)<=sightRadius))
			founded=los(player.getMapPosition());
		else
			return false;
		
		//Se � stato trovato, si metter� a cercarlo una volta uscito dal suo campo di visuale
		if(founded)
			stillSearching=true;
						
		return (founded);
	}

	/**Procedura per preparare l'azione di attacco**/
	public void prepareAttack(Player target){
        
		//Se il mostro � adiacente al giocatore
        if(distance(mapPosition,target.getMapPosition())<=1){
      		//Se l'azione non � un'azione d'attacco, la cambia in un'azione d'attacco
        	if((currentAction.getCostInTicks()==0)&&(currentAction.getActionType()==EAction.STEP)){                		
        		//Lo seleziona come nuovo bersaglio se non c'� bersaglio
            	if(target!=null)
            		setTarget(target);   		
        		addGameAction(EAction.ATTACK,6/numberOfAttacks);
        	}        	
        }
        else{
        	//Se il mostro non � adiacente al giocatore e l'azione � un'azione d'attacco, la cambia in una di movimento
        	if(currentAction.getActionType()==EAction.ATTACK)
        		addGameAction(EAction.STEP,0);
        }
	}
	
	/**Procedura per l'azione di attacco**/
	public void attackTarget(){
        
		//Se l'azione pu� essere eseguita in questo round
		if(currentAction.getCostInTicks()<=remainingTicks){  
			//Se si � raggiunto il momento di eseguire l'attacco
			if((gameTicks==3)||((gameTicks==0)&&(numberOfAttacks==2))){
			
				//Riduce i Ticks disponibili del costo dell'attaco
    			remainingTicks-=currentAction.getCostInTicks();
    			
    			//Se il risultato del tiro supera la difesa del mostro
	            if(attackRoll()>=target.getDefense()){
	            	
	                Console.getInstance().addString(name+": colpisce, ");
	                
	                int result=weaponDiceRoll(wieldedWeapon,damageBonus,false);
	                
	                Console.getInstance().addString(String.valueOf(result)+" danni subiti");
	                Console.getInstance().printLine();
	                
	                //Infligge i danni
	                target.inflictDamage(result);
	                
	                wieldedWeapon.secondaryEffect(target);
	            }
	            else{
	                Console.getInstance().addString(name+": manca");
	                Console.getInstance().printLine();
	            }			            	
	        }
		}
	}
	
	/**Tiro per colpire**/
    public int attackRoll(){
        
        int totalThrow;
        
        totalThrow=diceRoll(1,20,totalATK,false); 
        
        return totalThrow;
    }  
	
	/**Procedura per la camminata casuale**/
	private void wander() {
		
		//Se non � gi� in movimento
			if(currentPath==null)
			//Con una possibilit� del 30%
			if(diceRoll(1,100,0,false)>=30){
				//Si sposta in un quadretto casuale
				destination=new Vector2(mapPosition);
				
				destination.x+=diceRoll(1,3,-2,false)*diceRoll(1,6,0,false);
				destination.y+=diceRoll(1,3,-2,false)*diceRoll(1,6,0,false);				
	
				if(wm.isTilePassable(wm.getMapTileAtPosition(destination),(int) destination.x,(int) destination.y)){
					
					if(destination.x<0)
						destination.x=mapPosition.x+diceRoll(1,3,0,false);
					
					if(destination.x>=wm.getLevelWidth())
						destination.x=mapPosition.x+diceRoll(1,3,-3,false);		
		
					if(destination.y<0)
						destination.y=mapPosition.y+diceRoll(1,3,0,false);
					
					if(destination.y>=wm.getLevelHeight())
						destination.y=mapPosition.y+diceRoll(1,3,-3,false);		
					
					setNewPath(destination);
		
				}
			}

	}
	
	/**Stampa sulla console il messaggio della morte del mostro**/
	public void printDeath(){
		if(isDead()){			                
            Console.getInstance().addString(name+": morte");
            Console.getInstance().printLine();			
		}
	}
	
	/**Effettua tiro salvezza**/
	@Override
	public boolean makeSavingThrow(int difficulty,int malus) {
		
		boolean savingResult=(diceRoll(1,20,savingThrow-malus,false)>=difficulty);
		
		Console.getInstance().addString(name+":");
		
		if(savingResult)
			Console.getInstance().addString(" Supera Tiro Salvezza");
		else
			Console.getInstance().addString(" Fallisce Tiro Salvezza");

		Console.getInstance().printLine();
		
		return savingResult;
	}
}
