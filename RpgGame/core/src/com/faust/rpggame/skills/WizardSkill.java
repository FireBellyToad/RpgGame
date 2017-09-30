package com.faust.rpggame.skills;

import com.faust.rpggame.CommonUtils;
import com.faust.rpggame.conditions.Effect;
import com.faust.rpggame.entities.EAction;
import com.faust.rpggame.entities.Entity;
import com.faust.rpggame.entities.Player;
import com.faust.rpggame.projectiles.ProjectileFactory;
import com.faust.rpggame.world.Console;


public class WizardSkill extends Skill {
	
	//---------------------- SKILL ---------------------- //
	
	public static final int SKILL_SHIELD = 0;
	public static final int SKILL_FIREBALL = 1;
	public static final int SKILL_ICERAY= 2;
	public static final int SKILL_BLUR = 3;	


	public WizardSkill(int type,int skillPoints,int skillLevel,Player user){
		this.type=type;
		this.skillPoints=skillPoints;	
		this.skillLevel=skillLevel;
		this.user=user;
		energyCost=0;			
		
//		Tempo di casting = 7
		castingTime=(int) (7+(skillLevel/2)-user.getMind());
		castingTime=Math.min(CommonUtils.MAX_TIME_TO_CAST-2,castingTime);
		castingTime=Math.max(CommonUtils.MIN_TIME_TO_CAST-2,castingTime);	
		
		if(skillLevel == 1)
			castingTime=Math.max(CommonUtils.MIN_TIME_TO_CAST-2,castingTime-2);	
		
		
		switch(type){
			case SKILL_SHIELD:{
				energyCost=4*skillPoints;
				duration=15*skillPoints;
				break;
			}
			case SKILL_FIREBALL:{
				energyCost=2*skillPoints;
				break;
			}			
			case SKILL_ICERAY:{
				energyCost=5+(2*skillPoints);
				duration=15*skillPoints;
				break;
			}
			case SKILL_BLUR:{
				energyCost=4;
				break;
			}
		}
		
	}
	
	@Override
	public void useSkill() {

		if(user.getActualEp()>=energyCost){
			
			user.spendEnergy(energyCost);
			user.addGameAction(EAction.SPELL,castingTime);
			user.setSpellToCast(type);			
		}
	}
	
	@Override
	public void castSpell(Entity target) {
		switch(type){
			case SKILL_SHIELD:{
				user.addEffect(new Effect(Effect.EFFECT_SHIELDED,skillPoints*10 ,skillPoints*10));
				Console.getInstance().addString("Scudo: +"+(10*skillPoints)+" PF temporanei");
				Console.getInstance().printLine();			
				break;
			}
			case SKILL_FIREBALL:{
				
				if(target!= null){
					user.createProjectile(ProjectileFactory.getInstance().createProjectile(ProjectileFactory.PRO_FIREBALL,skillPoints,user,target));
				}
				
				break;
			}			
			case SKILL_ICERAY:{

				//TODO Crea raggio di gelo
				break;
			}
			case SKILL_BLUR:{
				user.addEffect(new Effect(Effect.EFFECT_BLURRED,duration,skillPoints));
				Console.getInstance().addString("Sfocatura: "+(25+(5*skillPoints))+"% di essere mancato");
				Console.getInstance().printLine();		
				break;
			}
						
		}
		
	}

}
