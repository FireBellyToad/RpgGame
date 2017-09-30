package com.faust.rpggame.projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.faust.rpggame.CommonUtils;
import com.faust.rpggame.entities.Entity;
import com.faust.rpggame.entities.Player;
import com.faust.rpggame.world.Console;

public class Dart extends Projectile {
	
	private int firePower;

	public Dart(int skillPoints, Entity user, Vector2 destination) {
		super(new Texture(Gdx.files.internal("data/fireball.png")),user,destination);
		firePower= skillPoints;
	}
		
	public Dart(int skillPoints, Player user, Entity target) {
		super(new Texture(Gdx.files.internal("data/fireball.png")),user,target);
		firePower= skillPoints;
	}

	@Override
	public void onDeath() {
		
//		int fireballRange = (firePower/3)+1;

		int totalDamage = CommonUtils.diceRoll(firePower, 8, 0, false);			
		
		
//		ArrayList<Entity> targets=Room.getEntitesInRange(destination,fireballRange,false);
//		
//		for(Entity target : targets){
//			//Se è entro l'esplosione
//			if(Room.los(destination, target.getMapPosition())){
//				user.createProjectile(ProjectileFactory.getInstance().createExplosion(user,target.getMapPosition()));
//				//Se supera la resistenza alla magia
				if(CommonUtils.diceRoll(1, 100, 0, false)>= target.getMagicResistance()){
					target.inflictDamage(totalDamage);
					Console.getInstance().addString(target.getName()+": subisce "+totalDamage+" danni");				
					Console.getInstance().printLine();
				}else{

					Console.getInstance().addString(target.getName()+": Resistenza alla magia ");				
					Console.getInstance().printLine();
				}
//				
//			}
//		}
//		
		

	}
}
