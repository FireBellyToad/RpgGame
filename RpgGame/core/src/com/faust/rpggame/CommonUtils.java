package com.faust.rpggame;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.faust.rpggame.items.Weapon;
import com.faust.rpggame.world.Console;

public class CommonUtils {

    private final static Random dice=new Random();		//Oggetto per il tiro di dadi
	public static final float FONT_STANDARD_SIZE = 48;
	public static final int textSize = (int) (CommonUtils.FONT_STANDARD_SIZE*Gdx.graphics.getDensity());
	public static final int TIME_TO_TICK = 32;
	public static final int MAX_TIME_TO_CAST = 6;
	public static final int MIN_TIME_TO_CAST = 2;
	
	 /**Funzione per calcolo della distanza
     * 
     * @param point1 primo punto
     * @param point2 secondo punto
     */
    public static int distance(Vector2 point1, Vector2 point2){
        double result=Math.pow((point1.x-point2.x),2)+Math.pow((point1.y-point2.y),2);
        result = Math.sqrt(result);
        return (int) result;
    }
    
	 /**Funzione per calcolo della distanza tra quattro coordinate
     * 
     */
	public static int distance(float x1, float y1, float x2, float y2) {
		
		return distance(new Vector2(x1,y1),new Vector2(x2,y2));
	}
    
    /**Funzione che ritorna l'oggetto per i numeri casuali
     * 
     * @return l'oggetto Random
     */
    public static Random getDice() {
		return dice;
	}

	/**Funzione per calcolo del dado    
     * 
     * @param Numero di dadi tirati
     * @param valore del dado (20 per il d20, 12 per il d12...)
     * @param bonus da sommare al tiro totale     * 
     * @param showResult flag da mettere a vero se si vuole stampare il tiro
     * @return il risultato
     */
    public static int diceRoll(int number,int diceThrown, int bonus,boolean showResult)
    {
    		
        int results[]=new int[number];
        int totalResult=0;
        int dicesThrown=0;
        
        //Tira i dadi
        for(int i=0;i<number;i++){
            results[i]+=dice.nextInt(diceThrown)+1;
            dicesThrown=i+1;
        }
        
        //Somma i risultati di tiri multipli
        for(int i=0;i<dicesThrown;i++){
        	totalResult+=results[i];
        }
        
        totalResult+=bonus;
        
        if(showResult)
        	Console.getInstance().addDiceRoll(diceThrown, number, bonus, totalResult);
                
        return totalResult;
    }
    
    /**Funzione per calcolo del tiro di dado con un'arma che arrotonda a 1 i risultato 0 o negativi    
     * 
     * @param Numero di dadi tirati
     * @param valore del dado (20 per il d20, 12 per il d12...)
     * @param bonus da sommare al tiro totale     * 
     * @param showResult flag da mettere a vero se si vuole stampare il tiro
     * @param isCritical flag da mettere a vero se il colpo è critico
     * @return il risultato
     */
    public static int weaponDiceRoll(Weapon weapon, int bonus,boolean showResult,boolean isCritical)
    {
        int results[]=new int[weapon.getWeaponDiceNumber(isCritical)];
        int totalResult=0;
        int dicesThrown=0;
        
        //Tira i dadi
        for(int i=0;i<weapon.getWeaponDiceNumber(isCritical);i++){
        	//Se non è un d1, tira il dado, altrimenti infligge 1 danno
        	if(weapon.getWeaponDice()!=1)
        		results[i]+=dice.nextInt(weapon.getWeaponDice())+1;
        	else
        		results[i]+=1;
        	
            dicesThrown=i+1;
        }
        
        //Somma i risultati di tiri multipli
        for(int i=0;i<dicesThrown;i++){
        	totalResult+=results[i];
        }
        
        totalResult+=bonus;
        totalResult+=weapon.getWeaponMagicBonus();
        totalResult=Math.max(1,totalResult);
        
        if(showResult)
        	Console.getInstance().addDiceRoll(weapon.getWeaponDice(),weapon.getWeaponDiceNumber(isCritical), bonus, totalResult);
        
        return totalResult;
    }

}
