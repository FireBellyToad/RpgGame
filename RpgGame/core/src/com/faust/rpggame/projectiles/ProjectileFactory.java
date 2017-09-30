package com.faust.rpggame.projectiles;

/**
 * CLASSE FACTORY PER I MOSTRI (SINGLETON)
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

import com.badlogic.gdx.math.Vector2;
import com.faust.rpggame.entities.Entity;
import com.faust.rpggame.entities.Player;

public class ProjectileFactory {
	
	//--------------MOSTRI--------------//
	
	public static final int PRO_FIREBALL = 1;	
	public static final int PRO_ICEDART = 2;
	
	private static ProjectileFactory INSTANCE =new ProjectileFactory();
	
	/**Crea un mostro dato il nome
	 * @param skillPoints 
	 * 
	 * @param monsterType tipo del mostro da creare
	 * @param wm istanza del WorldManager attuale
	 * @return il mostro creato
	 */
	public Projectile createProjectile(int projectileType, int skillPoints, Entity user, Vector2 destination){
				
		//TODO recupera mostro da file (?)
		switch(projectileType){
			case PRO_FIREBALL:{
				return new Dart(skillPoints,user,destination);
			}
		}
		
		
		return null;	
		
	}
	
	public static ProjectileFactory getInstance(){
		return INSTANCE;
	}

	public Projectile createExplosion(Entity user, Vector2 position) {
		return new Explosion(1,user, position);
	}

	public Projectile createProjectile(int projectileType, int skillPoints,
			Player user, Entity target) {
		
		//TODO recupera mostro da file (?)
		switch(projectileType){
			case PRO_FIREBALL:{
				return new Dart(skillPoints,user,target);
			}
		}
		return null;
	}

}
