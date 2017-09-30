package com.faust.rpggame.conditions;

/**
 * CLASSE DEGLI EFFETTI DELLE ENTITA'
 * 
 * @author Jacopo "Faust" Buttiglieri
 * 
 * La differenza tra Effetti e Condizioni Negative è che gli effetti vengono controllati uno alla
 * volta quando vengono applicati all'entità, le condizioni negative invece si controllano alla 
 * fine di ogni round. Inoltre, le Condizioni Negative, se duplicate, vengono estese della durata
 * della nuova Condizione, mentre gli Effetti ripartono semplicemente dall'inizio
 */

public class Effect extends Condition {

	//---------------------- STATUS ---------------------- //
	
	public static final int EFFECT_DEFENSIVE = 1;
	public static final int EFFECT_SHIELDED = 2;
	public static final int EFFECT_BLURRED = 3;
	public static final int EFFECT_PROTECTED = 4;
	public static final int EFFECT_BLESSED = 5;
	public static final int EFFECT_POWERATTACK = 6;
	

	public Effect(int type, int duration) {
		super(type, duration);
	}
	
	public Effect(int type, int duration,int value) {
		super(type, duration, value);
	}

	public Effect(int type) {
		super(type,0);
	}

	public void setDuration(int duration) {
		this.duration=duration;		
	}

}
