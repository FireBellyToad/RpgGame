package com.faust.rpggame.projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.faust.rpggame.entities.Entity;

public class Explosion extends Projectile{
	
	
	public Explosion(int life, Entity user, Vector2 position){
		super(new Texture(Gdx.files.internal("data/fireball.png")), user, position);
		this.life=life;
		this.position = position.cpy();
		horizontalSpeed=0;
		verticalSpeed=0;
		setPosition((position.x+1)*getWidth(),800-(position.y+2)*getHeight());
	}

	@Override
	public void onDeath() {
		// TODO Auto-generated method stub
		
	}

}