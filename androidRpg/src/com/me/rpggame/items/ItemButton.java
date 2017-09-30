package com.me.rpggame.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ItemButton extends Image {
	
	private int itemIndex=0;
	
	public ItemButton(TextureRegion region,int itemIndex){
		super(region);
		this.itemIndex = itemIndex;
	}
	
	public int getItemIndex(){
		return itemIndex;
	}

}
