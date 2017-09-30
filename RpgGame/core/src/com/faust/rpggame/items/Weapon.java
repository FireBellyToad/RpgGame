package com.faust.rpggame.items;

import com.faust.rpggame.conditions.Ailment;
import com.faust.rpggame.entities.Entity;

/**
 * CLASSE DELLE ARMI
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

public class Weapon {
	
	private String name;			//Nome dell'arma
	private int diceNumber;			//Numero dei dadi da tirare
	private int dice;				//Dado tirato
	private int magicBonus=0;			//Bonus magico dell'arma
	private int savingDifficulty=0;
	private int savingMalus=0;
	
	private Ailment statusInflicted=null;
	
	/**Costruttore breve per attacchi dei mostri senza effetti negativi
	 * 
	 * @param diceNumber numero dei dadi tirati
	 * @param dice dado da tirare
	 */
	public Weapon(int diceNumber, int dice){
		
		this.name=null;
		this.diceNumber=diceNumber;
		this.dice=dice;
	}
	
	/**Costruttore breve per attacchi dei mostri con effetti negativi
	 * 
	 * @param diceNumber numero dei dadi tirati
	 * @param dice dado da tirare
	 * @param status effetto negativo inflitto
	 * @param difficulty difficoltà tiro salvezza
	 * @param malus malus al tiro salvezza 
	 */
	public Weapon(int diceNumber, int dice, Ailment status, int difficulty,int malus){
		
		this.name=null;
		this.diceNumber=diceNumber;
		this.dice=dice;
		this.statusInflicted=status;	
		this.savingDifficulty=difficulty;
		this.savingMalus=malus;
	}
	
	/**Costruttore esteso, per armi del giocatore con effetti speciali
	 * 
	 * @param name nome dell'arma
	 * @param diceNumber numero dei dadi tirati
	 * @param dice dado da tirare
	 * @param magicBonus bonus magico
	 */
	public Weapon(String name,int diceNumber, int dice, int magicBonus,Ailment statusInflicted){
		
		this.name=name;
		this.diceNumber=diceNumber;
		this.dice=dice;
		this.magicBonus=magicBonus;
		this.statusInflicted=statusInflicted;
		savingDifficulty=10+(magicBonus*2);
		this.savingMalus=0;
	}

	/**Costruttore esteso, per armi del giocatore**/
	public Weapon(String name,int diceNumber, int dice, int magicBonus) {
		this.name=name;
		this.diceNumber=diceNumber;
		this.dice=dice;
		this.magicBonus=magicBonus;
		savingDifficulty=10+(magicBonus*2);
		this.savingMalus=0;
	}

	/**Ottiene il dado dell'arma**/
	public int getWeaponDice(){
		return dice;		
	}
	
	/**Ottiene il numero di dadi tirati**/
	public int getWeaponDiceNumber(boolean isCritical){
		if(isCritical)
			return diceNumber*2;
			
		return diceNumber;
	}
	
	/**Ottiene il bonus magico dell'arma**/
	public int getWeaponMagicBonus(){
		return magicBonus;
	}

	/**Ottiene il nome dell'arma**/
	public String getName() {
		return name;
	}

	/**Se ha un effetto secondario, lo infligge**/
	public void secondaryEffect(Entity target) {

		if(statusInflicted!= null){	
			//Se il bersaglio fallisce il tiro salvezza
			if(!target.makeSavingThrow(savingDifficulty, savingMalus))
				target.addStatus(new Ailment(statusInflicted));
		}
		
	}

}
