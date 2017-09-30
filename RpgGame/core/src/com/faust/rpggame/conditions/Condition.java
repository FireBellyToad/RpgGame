package com.faust.rpggame.conditions;

/**
 * CLASSE DELLE CONDIZIONI DELLE ENTITA'
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

public abstract class Condition {	
	
	protected int type;
	protected int duration;
	protected int value;
	
	public Condition(int type,int duration){
		this.type=type;
		this.duration=duration;
	}	
	
	public Condition(int type,int duration,int value){
		this.type=type;
		this.duration=duration;
		this.value=value;
	}		

	public int getStatusType() {
		return type;
	}

	public int getDuration() {
		return duration;
	}
	
	public int getValue() {
		return value;
	}	
	
	public void decrementDuration(){
		duration--;
	}

	public void end() {
		duration=0;		
	}

}
