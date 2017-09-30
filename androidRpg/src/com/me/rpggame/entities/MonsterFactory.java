package com.me.rpggame.entities;

/**
 * CLASSE FACTORY PER I MOSTRI (SINGLETON)
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.me.rpggame.items.Weapon;
import com.me.rpggame.world.WorldManager;

public class MonsterFactory {
	
	//--------------MOSTRI--------------//
	
	public static final int MON_GOBLIN = 1;	
	public static final int MON_SCORPION = 2;
	
	private static MonsterFactory INSTANCE =new MonsterFactory();
	
	/**Crea un mostro dato il nome
	 * 
	 * @param monsterType tipo del mostro da creare
	 * @param wm istanza del WorldManager attuale
	 * @return il mostro creato
	 */
	public Monster createMonster(int monsterType,WorldManager wm, int x,int y){
		
		
		//TODO recupera mostro da file (?)
		switch(monsterType){
			case MON_GOBLIN:{
				return new Monster(new Texture(Gdx.files.internal("data/goblin.png")),x,y,wm,"Goblin"
						,5,1,new Weapon(1, 3),0,0,0);
			}			
			case MON_SCORPION:{
				return new Monster(new Texture(Gdx.files.internal("data/scorpion.png")),x,y,wm,"Scorpione"
						,6,-1,new Weapon(1, 1, new Ailment(Ailment.STATUS_POISONED,2),15,0),0,2,0);
			}
		}
		
		
		return null;	
		
	}
	
	public static MonsterFactory getInstance(){
		return INSTANCE;
	}

}
