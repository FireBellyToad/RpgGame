package com.faust.rpggame.skills;

import com.faust.rpggame.CommonUtils;
import com.faust.rpggame.conditions.Ailment;
import com.faust.rpggame.conditions.Effect;
import com.faust.rpggame.entities.EAction;
import com.faust.rpggame.entities.Entity;
import com.faust.rpggame.entities.Player;
import com.faust.rpggame.world.Console;

public class ClericSkill extends Skill {
	
	//---------------------- SKILL ---------------------- //
	
	public static final int SKILL_PROTECTION = 0;
	public static final int SKILL_HEALING = 1;
	public static final int SKILL_BLESSWEAPON= 2;
	public static final int SKILL_CUREPOISON = 3;
	
	
	public ClericSkill(int type,int skillPoints,int skillLevel,Player user){
		this.type=type;
		this.skillPoints=skillPoints;	
		this.skillLevel=skillLevel;
		this.user=user;
		energyCost=0;			
		
		//Tempo di casting
		castingTime=(int) (7+(skillLevel/2)-user.getMind());
		castingTime=Math.min(6,castingTime);
		castingTime=Math.max(2,castingTime);		
		
		switch(type){
			case SKILL_PROTECTION:{
				energyCost=2*skillPoints;
				duration=10*skillPoints;
				break;
			}
			case SKILL_HEALING:{
				energyCost=2*skillPoints;
				break;
			}			
			case SKILL_BLESSWEAPON:{
				energyCost=5+(2*skillPoints);
				duration=15*skillPoints;
				break;
			}
			case SKILL_CUREPOISON:{
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
			case SKILL_PROTECTION:{
				user.addEffect(new Effect(Effect.EFFECT_PROTECTED,duration,skillPoints));
				Console.getInstance().addString("Protezione: +"+skillPoints+" a TS e RD");
				Console.getInstance().printLine();
				break;
			}
			case SKILL_HEALING:{

				Console.getInstance().addString("Guarigione: ");
				user.cureDamage(CommonUtils.diceRoll(skillPoints, 8, skillPoints, true));
				Console.getInstance().addString(" pf curati");
				Console.getInstance().printLine();
				
				break;
			}			
			case SKILL_BLESSWEAPON:{
				user.addEffect(new Effect(Effect.EFFECT_BLESSED,duration,skillPoints));
				Console.getInstance().addString("Arma Benedetta: +"+skillPoints+" a ATK e Danno");
				Console.getInstance().printLine();
				break;
			}
			case SKILL_CUREPOISON:{
				user.removeStatusType(Ailment.STATUS_POISONED);
				Console.getInstance().addString("Veleno rimosso");
				Console.getInstance().printLine();
				break;
			}
		}
	}

}
