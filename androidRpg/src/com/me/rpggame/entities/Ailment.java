package com.me.rpggame.entities;

public class Ailment {	

	//---------------------- STATUS ---------------------- //
	
	public static final int STATUS_NONE = 0;
	public static final int STATUS_POISONED = 1;

	private int type;
	private int duration;
	
	public Ailment(int type,int duration){
		this.type=type;
		this.duration=duration;
	}	
	
	public Ailment(Ailment ail){
		this.type=ail.getStatusType();
		this.duration=ail.getDuration();
	}

	public int getStatusType() {
		return type;
	}

	public int getDuration() {
		return duration;
	}
	
	public void decrementDuration(){
		duration--;
	}


}
