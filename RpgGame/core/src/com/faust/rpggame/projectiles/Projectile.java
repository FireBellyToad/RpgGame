package com.faust.rpggame.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.faust.rpggame.CommonUtils;
import com.faust.rpggame.entities.Entity;
import com.faust.rpggame.entities.Player;

public abstract class Projectile extends Image{
	
	protected int life;
	protected Entity user;
	protected double verticalSpeed;
	protected double horizontalSpeed;
	protected int explosionRange;
	protected Vector2 position;
	protected Vector2 destination;
	protected Entity target;
	
	public Projectile(Texture texture, Entity user, Vector2 destination){
		super(texture);
		this.user = user;
		this.position = user.getMapPosition().cpy();
		this.destination = destination;
		life= 1;
		verticalSpeed=0;
		horizontalSpeed=0;
		calculateSpeed();
	}
	
	
	
	public Projectile(Texture texture, Player user2, Entity target) {
		this(texture, user2, target.getMapPosition());
		life= 100;
		this.target = target;
	}



	private void calculateSpeed() {
								
		//Ottiene le coordinate del protagonista e calcola le distanze orrizzontali e verticali
		//(Valore assoluto, dopo richiede correzione per valori negativi)
		horizontalSpeed=Math.abs(destination.x-position.x)/16;
		verticalSpeed=Math.abs(destination.y-position.y)/16;
		
		//Si sistemano i valori nel caso fossero negativi
		if(destination.x<position.x)
			horizontalSpeed = -horizontalSpeed;
		if(destination.y<position.y)
			verticalSpeed= -verticalSpeed;
	
	}

	public void move(){			
		
		position.x+=horizontalSpeed;
		position.y+=verticalSpeed;
		
		setPosition((position.x+1)*getWidth(),800-(position.y+2)*getHeight());
		
		if(target != null)
			if(CommonUtils.distance(getX(),getY(),(target.getMapPosition().x+1)*getWidth(),800-(target.getMapPosition().y+2)*getHeight()) < 5)
				life=0;
	}
	
	public void decreaseLife(){
		life--;
	}
	
	public boolean isDead(){
		return life<=0;
	}
	
	public abstract void onDeath();

}
