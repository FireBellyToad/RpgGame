package com.faust.rpggame.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.faust.rpggame.RpgGame;

public class CharacterScreen extends BaseScreen {
	
	private Table rootTable;
	private Table charChoiceTable;
	private Table charDetailTable;

	private Texture charImageSheet;		//Texture dei pulsanti		
	private TextureRegion[] charImageRegion=new TextureRegion[3];	//Region dei pulsanti

	public CharacterScreen(RpgGame game) {
		super(game);
		rootTable = new Table();
		charChoiceTable = new Table();
		charDetailTable = new Table();
	}

}
