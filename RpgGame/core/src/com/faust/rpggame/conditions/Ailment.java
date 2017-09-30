package com.faust.rpggame.conditions;

/**
 * CLASSE DELLE CONDIZIONI NEGATIVE DELLE ENTITA'
 * 
 * @author Jacopo "Faust" Buttiglieri
 * 
 * La differenza tra Effetti e Condizioni Negative è che gli effetti vengono controllati uno alla
 * volta quando vengono applicati all'entità, le condizioni negative invece si controllano alla 
 * fine di ogni round. Inoltre, le Condizioni Negative, se duplicate, vengono estese della durata
 * della nuova Condizione, mentre gli Effetti ripartono semplicemente dall'inizio
 */

public class Ailment extends Condition {

	//---------------------- STATUS ---------------------- //
	
	public static final int STATUS_NONE = 0;
	public static final int STATUS_POISONED = 1;
	public static final int STATUS_STUNNED = 2;
	public static final int STATUS_PARALIZED = 3;
	public static final int STATUS_DISEASED = 4;
	public static final int STATUS_WEAKENED = 5;
	
	public Ailment(int type, int duration) {
		super(type, duration);
	}
	
	public Ailment(int type, int duration,int value) {
		super(type, duration, value);
	}

	public Ailment(Ailment ail) {
		super(ail.getStatusType(),ail.getDuration(),ail.value);
	
	}

	public void extendDuration(int value){
		duration+=value;
	}

	
}
