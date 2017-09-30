package com.faust.rpggame.skills;

import com.faust.rpggame.entities.Entity;
import com.faust.rpggame.entities.Player;

/**
 * CLASSE DELLE ABILITA' DELLE CLASSI DEI PERSONAGGI
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

public abstract class Skill {
	
	protected int skillPoints;		//Skill points
	protected int duration;			//Durata
	protected int type;				//Tipo skill
	protected int skillLevel;		//Livello Skill
	protected Player user;			//Utilizzatore
	protected int energyCost;		//Costo in punti energia
	protected int castingTime;		//Tempo di lancio
	protected boolean autoPause;	//Flag di autopausa
	
	public Skill(){
		type=0;
		duration=0;
		skillPoints=0;	
		skillLevel=0;	
		user=null;
	}
		
	public abstract void useSkill();

	public abstract void castSpell(Entity target);
}
