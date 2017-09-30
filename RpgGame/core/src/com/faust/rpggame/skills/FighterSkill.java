package com.faust.rpggame.skills;

import java.util.ArrayList;

import com.faust.rpggame.conditions.Ailment;
import com.faust.rpggame.conditions.Effect;
import com.faust.rpggame.entities.EAction;
import com.faust.rpggame.entities.Entity;
import com.faust.rpggame.entities.Player;
import com.faust.rpggame.world.Console;
import com.faust.rpggame.world.Room;

/**
 * CLASSE DELLE ABILITA' DA GUERRIERO
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

public class FighterSkill extends Skill {
	
	//---------------------- SKILL ---------------------- //
	
	public static final int SKILL_POWERATTACK = 0;
	public static final int SKILL_DEFENSIVEPOSTURE = 1;
	public static final int SKILL_SHOUT = 2;
	public static final int SKILL_BULLRUSH= 3;
	
	private boolean toggleSkill=false;	//Flag per le skill che richiedono attivazione
	
	public FighterSkill(int type,int skillPoints,int skillLevel,Player user){
		this.type=type;
		this.skillPoints=skillPoints;	
		this.skillLevel=skillLevel;
		this.user=user;
		
	}
	
	@Override
	public void useSkill() {
		
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

				user.addGameAction(EAction.SPELL,2);
				user.setSpellToCast(type);
				
				break;
			}
		}
	}

	@Override
	public void castSpell(Entity target) {

		switch(type){		
			case SKILL_BULLRUSH:{
				//user.addStatus(new Condition(Condition.STATUS_DEFENSIVE, duration,skillPoints));		
				break;
			}
			case SKILL_SHOUT:{
				
				//Urlo: Entro 4 metri tutti i nemici che falliscono un TS hanno -1 a ATK, Difesa e Danni per skillPoint
				
				Console.getInstance().addString("Urlo: Nemici entro "+skillPoints+" metri indeboliti");
				Console.getInstance().printLine();		
				
				ArrayList<Entity> targets=Room.getEntitesInRange(user.getMapPosition(),4,true);
				
				for(int i=0;i<targets.size();i++)
					if(targets.get(i).makeSavingThrow(13, 0))
						targets.get(i).addStatus(new Ailment(Ailment.STATUS_WEAKENED, 5*skillPoints, skillPoints));
				
				break;
			}
		}
		
	}

}
