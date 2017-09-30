package com.faust.rpggame.skills;

import com.faust.rpggame.entities.Entity;
import com.faust.rpggame.entities.Player;


public class ThiefSkill extends Skill {
	
	private boolean toggleSkill=false;	//Flag per le skill che richiedono attivazione
	
	public ThiefSkill(int type,int skillPoints,int skillLevel,Player user){
		this.type=type;
		this.skillPoints=skillPoints;	
		this.skillLevel=skillLevel;
		this.user=user;
		
	}
	
	@Override
	public void useSkill() {
		/*
		switch(type){		
			case SKILL_POWERATTACK:{
				
				//Attacco Poderoso: bonus di 3+skillPoints ai danni, malus di (3+skillPoints)/2 all'ATK
				
				if(!toggleSkill){
					user.addEffect(new Effect(Effect.EFFECT_POWERATTACK, -1,3+skillPoints));
					
					Console.getInstance().addString("Attacco Poderoso Attivato");
					toggleSkill=true;
				}
				else{
					user.addEffect(new Effect(Effect.EFFECT_POWERATTACK));

					Console.getInstance().addString("Attacco Poderoso Disattivato");
					toggleSkill=false;
				}
				
				Console.getInstance().printLine();
				
				break;
			}
			case SKILL_DEFENSIVEPOSTURE:{
				
				//Postura difensiva: bonus di skillPoints alla Difesa, malus di skillPoints all'ATK
				
				if(!toggleSkill){
					user.addEffect(new Effect(Effect.EFFECT_DEFENSIVE, -1,skillPoints));
					Console.getInstance().addString("Postura Difensiva Attivata");
					toggleSkill=true;
				}
				else{
					user.addEffect(new Effect(Effect.EFFECT_DEFENSIVE));

					Console.getInstance().addString("Postura Difensiva Disattivata");
					toggleSkill=false;
				}
				
				Console.getInstance().printLine();		
				break;
			}
			case SKILL_BULLRUSH:{
				//user.addStatus(new Condition(Condition.STATUS_DEFENSIVE, duration,skillPoints));		
				break;
			}
			case SKILL_SHOUT:{
				//user.addStatus(new Condition(Condition.STATUS_DEFENSIVE, duration,skillPoints));		
				break;
			}
		}*/
	}

	@Override
	public void castSpell(Entity target) {}

}
