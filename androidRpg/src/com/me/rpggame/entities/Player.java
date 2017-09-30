package com.me.rpggame.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.me.rpggame.inventory.Inventory;
import com.me.rpggame.items.Item;
import com.me.rpggame.items.ItemFactory;
import com.me.rpggame.items.Weapon;
import com.me.rpggame.world.Console;
import com.me.rpggame.world.WorldManager;

public class Player extends Entity {
	
	public static final int MAX_ITEMS=20;				//Numero massimo di oggetti		
		
	private ArrayList<Item> itemsCarried;				//Oggetti trasportati	
	private Inventory inv;								//Inventario
	
	private static final int SKILLS_NUMBER = 12;			//Numero delle abilità selezionabili
	
	//---------------------- ABILITA' ---------------------- //
		
	public static final int WEAPON_EXPERT_SLASHING = 0;		//Esperto nelle Armi taglienti
	public static final int WEAPON_EXPERT_BLUNT = 1;		//Esperto nelle Armi contundenti
	public static final int SEARCHER = 2;					//Cercatore
	public static final int TRADER = 3;						//Commerciante
	public static final int AGILE = 4;						//Agile
	public static final int TOUGH = 5;						//Robusto
	public static final int SAPIENT = 6;					//Sapiente
	public static final int SHARP_EYE = 7;					//Occhio acuto
	public static final int RESISTANT = 8;					//Resistente
	public static final int STOIC = 9;						//Stoico
	
	//---------------------- CLASSI ---------------------- //
	
	public static final int CLERIC = 0;					//Chierico
	public static final int FIGHTER = 1;				//Guerriero
	public static final int THIEF = 2;					//Ladro
	public static final int WIZARD = 3;					//Mago
	
	//---------------------- ATTRIBUTI ---------------------- //
	
	private int charClass;									//Classe
	
	private int toughness=0;								//Robustezza
	private int agility=0;									//Agilità
	private int mind=0;										//Mente

	private int toughnessDamage=0;							//Danni a Robustezza
	private int agilityDamage=0;							//Danni a Agilità
	private int mindDamage=0;								//Danni a Mente

	protected int maxEp=0;									//Punti energia massimi	
	protected int actualEp=0;								//Punti energia attuale.
		
	private int classAttackBonus=0;							//Bonus Attacco di classe
	
	private boolean skills[]= new boolean [SKILLS_NUMBER];	//Skill selezionate



	public Player(Texture texture, String name, int mx, int my, WorldManager wm,Inventory inv,int charClass) {
		super(texture,name, mx, my, wm);
		initPlayer(charClass);
		this.inv=inv;
		itemsCarried=new ArrayList<Item>();

		// TODO Auto-generated constructor stub
	}
	
	/** Inizializza il giocatore
	 * 
	 * @param charClass classe del giocatore
	 */
	private void initPlayer(int charClass) {
		switch(charClass){
    	
    	case WIZARD:{/*
        		spellToCast[0]=1;
        		spellToCast[0]+=getBonusSpells(1);*/
	    		toughness=-1;
	    		agility=1;
	    		mind=2;	//1d4+3  
	    		for(int i=0;i<36;i++){/*
	    			for(int j=0;j<spellsMemo.size();j++){
	    				if(!spellsMemo.get(j).isClerical())
	    					classSpells.add(spellsMemo.get(j));
	    				}*/
	    			}
	    		maxHp=actualHp=6+toughness-toughnessDamage;
	    		maxEp=actualEp=6;
        		break;
        	}     	
    	
    	case CLERIC:{
        		//spellToCast[0]=0;
    			toughness=1;
	    		mind=1;
	    		maxHp=actualHp=8+toughness-toughnessDamage;
	    		maxEp=actualEp=4;
	    		wieldedWeapon=new Weapon("Light Mace",1,4,0);
	    		for(int i=0;i<20;i++){/*
	    			for(int j=0;j<spellsMemo.size();j++){
	    				if(spellsMemo.get(j).isClerical())
	    					classSpells.add(spellsMemo.get(j));
	    			}*/
	    				
	    		}
        		break;
        	}     	
    	
    	case FIGHTER:{    		
    			toughness=2;
    			agility=1;
        		mind=-1; 
	        	classAttackBonus=1;
	    		maxHp=actualHp=10+toughness-toughnessDamage;
	    		wieldedWeapon=new Weapon("Short Sword",1,6,0);
        		break;
        	}     
    	
	    case THIEF:{    		
				toughness=1;	
				agility=1;		
	    		maxHp=actualHp=8+toughness-toughnessDamage;
        		break;
        	}           
		}		
		
		level=1;
		defense+=agility-agilityDamage;
		totalATK+=agility-agilityDamage+classAttackBonus;
		savingThrow= toughness+agility+mind;
		this.charClass=charClass;
		
		//Inizializza le skill
		for(int i=0;i<SKILLS_NUMBER;i++)
			skills[i]=false;
		
	}
	
	/**Ricerca di segreti**/
	private boolean searchRoll(){
		int result=0;
		
    	//Se l'azione da eseguire è un'azione di attacco
    	if((currentAction!=null)&&(currentAction.getActionType()==EAction.SEARCH)){
    		//Se l'azione può essere eseguita in questo round
    		if(currentAction.getCostInTicks()<=remainingTicks){      			
    			
				//Riduce i Ticks disponibili del costo dell'azione
    			remainingTicks-=currentAction.getCostInTicks();
	    		
				//Aggiunge Mente
				result=mind-mindDamage;
				
    			Console.getInstance().addString("Ricerca: ");
				
				//Se ha l'abilità "Cercatore" ha un +2
				if(skills[SEARCHER])
					result+=2;
				
				//Tira un d20
				result= diceRoll(1,20,result,true);	//1d20+0				
				Console.getInstance().printLine();
			}
	    }
	    						
		//La prova riesce (restituisce vero) se il tiro totale è 15 o più
		return (result>=15);
	}
	

	/**Gestisce la logica dell'oggetto**/
	public void logic(ArrayList<Monster> monsters, WorldManager wm){		

		super.logic();
		
		
		if(currentAction.getActionType()== EAction.STEP){
			//Muove il personaggio
			moveToTarget();		
			
			//Posiziona lo sprite a schermo
			setPosition((1+mapPosition.x)*getWidth(),800-(2+mapPosition.y)*getHeight());
			spentTicks=0;
		}
		else
			spentTicks++;

		//Controlla se può attaccare i mostri nelle vicinanze		
		searchNearestEnemy(monsters);

		//Se i tick spesi sono uguali al costo dell'azione attuale
		if(spentTicks==(currentAction.getCostInTicks()-1)){
			//In base all'azione attuale, se non di movimento
			switch(currentAction.getActionType()){
				case EAction.ATTACK:{	
					//Attacca il mostro bersaglio
					if(target!=null)
						attackTarget();
					break;
				}
				case EAction.SEARCH:{
					//Cerca nelle caselle adiacenti
					wm.playerSearch(searchRoll(), mapPosition);
					addGameAction(EAction.STEP,0);	
					break;
				}			
				case EAction.PICK:{
					//Cerca nelle caselle adiacenti
					int itemAtPosition=wm.pickItem(mapPosition);
					if(itemAtPosition!=0)
						addItemToInventory(ItemFactory.getInstance().createItem(itemAtPosition));
					addGameAction(EAction.STEP,0);
					break;
				}
			}
		}
		
		//Resetta i ticks disponibili a fine round
		if(gameTicks==0){
			handleStatus();
			remainingTicks=6;
			spentTicks=0;        	
		}
	}
	

	/**Cerca il nemico più vicino e lo sceglie
	 * 
	 * @param monsters array dei mostri
	 */
	private void searchNearestEnemy(ArrayList<Monster> monsters) {

		boolean found=false;
		
		//Cerca il primo nemico adiacente
		for(int i=0;(i<monsters.size()&&(!found));i++){

			//Quando trova il primo nemico adiacente nella lista, esce dal ciclo
			found=prepareAttack(monsters.get(i));
		}	
		
	}

	/**Procedura per preparare l'azione di attacco
	 * 
	 * @param target bersaglio dell'attacco
	 * @return vero se ha trovato un bersaglio adiacente, altrimenti falso
	 */
	public boolean prepareAttack(Monster target){
        
		//Se il mostro è adiacente al giocatore
        if(distance(mapPosition,target.getMapPosition())<=1){

      		//Lo seleziona come nuovo bersaglio se non c'è bersaglio
           if(target!=null)
        	   setTarget(target);
            	
           //Inizia l'azione di attacco
           if(currentAction.getActionType()!=EAction.ATTACK)
        	   addGameAction(EAction.ATTACK,6/numberOfAttacks);
        		
        	return true;            
        }
        
        return false;

	}

	/**Procedura per l'azione di attacco verso il bersaglio**/
	private void attackTarget(){

		//Se l'azione può essere eseguita in questo round
		if(currentAction.getCostInTicks()<=remainingTicks){  
			//Se si è raggiunto il momento di eseguire l'attacco
			if((gameTicks==5)||((gameTicks==2)&&(numberOfAttacks==2))){
			
				//Riduce i Ticks disponibili del costo dell'attaco
    			remainingTicks-=currentAction.getCostInTicks();
    			
    			//Se il risultato del tiro supera la difesa del mostro
	            if(attackRoll(target.getDefense())){
	            	
	            	calculateDamage();
	            }
	            
	            //Si termina l'azione di attacco
	        	if(gameTicks==5);
	        		addGameAction(EAction.STEP,0);	        	
			}
		}	        
	}
	
	/**Calcola il danno inflitto**/
	private void calculateDamage() {
		
		int damageMemo=0;
		
		//Se il giocatore è un Guerriero, ha una percentuale di fare colpo critico
		//TODO colpo critico del ladro
    	if((charClass==FIGHTER)&&(diceRoll(1, 100, 0, false)<=level*2)){
    		Console.getInstance().addString("COLPO CRITICO: ");
            
            //Infligge i danni
            damageMemo=weaponDiceRoll(wieldedWeapon,toughness-toughnessDamage*2,true);
            target.inflictDamage( damageMemo );
            
            //Se il bersaglio resiste ai danni, si stampano a schermo i danni resistiti
            if(target.getDamageResistance()>0)
            	Console.getInstance().addString(" ("+Math.min(target.getDamageResistance(),damageMemo)+" resistiti)");
    	}else{		
    		//Se non è un guerriero o non fa colpo critico, infligge danni normalmente
            Console.getInstance().addString("Danni: ");
            
            //Infligge i danni
            damageMemo=weaponDiceRoll(wieldedWeapon,toughness-toughnessDamage,true);
            target.inflictDamage( damageMemo );
            
            //Se il bersaglio resiste ai danni, si stampano a schermo i danni resistiti
            if(target.getDamageResistance()>0)
            	Console.getInstance().addString(" ("+Math.min(target.getDamageResistance(),damageMemo)+" resistiti)");
                       
    	}	
    	
    	Console.getInstance().printLine();
	}

	/**Tiro per colpire
	 * 
	 * @param targetDefense difesa del bersaglio dell'attacco
	 * @return vero se il tiro totale è maggiore o uguale alla difesa del bersaglio
	 */
    public boolean attackRoll(int targetDefense){
        
        int totalThrow;
        
        Console.getInstance().addString("Attacco: ");
        
        totalThrow=diceRoll(1,20,totalATK,true);  
        
        if(totalThrow>=targetDefense)
            Console.getInstance().addString("(colpito)");
        else
            Console.getInstance().addString("(mancato)");
           
        Console.getInstance().printLine();
        
        return totalThrow>=targetDefense;
    }

    /**Ritorna gli HP massimi
     * 
     * @return gli Hp massimi
     */
	public int getMaxHp() {
		return maxHp;
	};   
	
	/**Ritorna gli HP attuali
	 * 
	 * @return gli Hp attuali
	 */
	public int getActualHp() {
		return actualHp;
	}

	/**Ritorna gli Ep attuali
	 * 
	 * @return gli Ep attuali
	 */
	public int getActualEp() {
		return actualEp;
	};   
	
	/**Ritorna gli Ep attuali
	 * 
	 * @return gli Ep attuali
	 */
	public int getMaxEp() {
		return maxEp;
	}

	/**Ritorna la Robustezza
	 * 
	 * @return Robustezza
	 */
	public int getToughness() {
		return toughness-toughnessDamage;
	};   
	
	/**Ritorna l'Agilità
	 * 
	 * @return Agilità
	 */
	public int getAgility() {
		return agility-agilityDamage;
	};   
	
	/** Ritorna la Mente
	 * 
	 * @return Mente
	 */
	public int getMind() {
		return mind-mindDamage;
	};   
	
	/**Funzione per spendere punti Energia**/
	public void spendEnergy(int amount){
		   actualEp=Math.max(actualEp-amount,0);
	};	
	
	/**Funzione per riguadagnare punti Energia**/
	public void regainEnergy(int amount){
		   actualEp=Math.min(actualEp+amount,0);
	};

	/**Aggiunge un oggetto all'inventario
	 * 
	 * @param item oggetto da aggiungere
	 */
	public void addItemToInventory(Item item){
		
		//Aggiunge agli oggetti che si trasportano
		itemsCarried.add(item);
		inv.createInventoryScreen();
	}

	/**Ottiene la lista degli oggetti raccolti**/
	public ArrayList<Item> getItemsCarried() {
		return itemsCarried;
	}

	/**Toglie un oggetto dagli oggetti raccolti dato l'indice
	 * 
	 * @param itemIndex l'indice dell'oggetto da togliere
	 */
	public void removeItem(int itemIndex) {
		itemsCarried.remove(itemIndex);
	}
	
	/**Effettua un tiro salvezza**/
	@Override
	public boolean makeSavingThrow(int difficulty,int malus) {

		Console.getInstance().addString("Tiro Salvezza:");
		
		//Tiro salvezza
		boolean savingResult=(diceRoll(1,20,savingThrow-malus,true)>=difficulty);
		
		if(savingResult)
			Console.getInstance().addString("(Superato)");
		else
			Console.getInstance().addString("(Fallito)");

		Console.getInstance().printLine();
				
		return savingResult;
	}
}
