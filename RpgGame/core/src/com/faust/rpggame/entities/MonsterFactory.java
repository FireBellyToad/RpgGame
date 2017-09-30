package com.faust.rpggame.entities;

/**
 * CLASSE FACTORY PER I MOSTRI (SINGLETON)
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.faust.rpggame.conditions.Ailment;
import com.faust.rpggame.items.Weapon;
import com.faust.rpggame.world.Room;
import com.faust.rpggame.world.WorldManager;

public class MonsterFactory {
	
	//--------------MOSTRI--------------//
	
	public static final int MON_INFECT_DOG = 1;	
	public static final int MON_ZOMBIE = 2;
	public static final int MON_MUTANT = 3;
	
	private static MonsterFactory INSTANCE =new MonsterFactory();
	
	/**Crea un mostro dato il nome
	 * 
	 * @param monsterType tipo del mostro da creare
	 * @param wm istanza del WorldManager attuale
	 * @return il mostro creato
	 */
	public Monster createMonster(int monsterType,WorldManager wm, int x,int y,Room room){
		
		
		//TODO recupera mostro da file (?)
		switch(monsterType){
			case MON_INFECT_DOG:{
				return new Monster(new Texture(Gdx.files.internal("data/dog.png")),x,y,wm,"Cane Inf."
						,4,-1,-2,6,new Weapon(1, 2),0,1,0,0, room);
			}			
			case MON_ZOMBIE:{
				return new Monster(new Texture(Gdx.files.internal("data/zombie.png")),x,y,wm,"Zombie"
						,7,-1,-1,6,new Weapon(1, 3,new Ailment(Ailment.STATUS_POISONED,3),5 ,0),0,1,1,0, room);
			}
			case MON_MUTANT:{
				return new Monster(new Texture(Gdx.files.internal("data/mutant.png")),x,y,wm,"Mutante"
						,6,0,0,8,new Weapon(1, 1),0,2,0,0, room);
			}
		}
		
		
		return null;	
		
	}
	
	public static MonsterFactory getInstance(){
		return INSTANCE;
	}

}
