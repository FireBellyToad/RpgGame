package com.faust.rpggame.world;

/**CLASSE DELLA CONSOLE DI GIOCO (SINGLETON)
 * 
 * @author Jacopo "Faust" Buttiglieri *
 * 
 */

import java.awt.Graphics;
import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.faust.rpggame.CommonUtils;

public class Console extends Group{
	
	//-------------- TIPI DI AZIONI-------------//
	
	public static final int MAX_TEXT_SHOWN = 5;
	
	final static Console INSTANCE= new Console();	

	private Texture consoleTexture;			//Texture dello sfondo della console
	private Image consoleImage;  			//Immagine dello sfondo della console	
	
	private String tempText;				//Variabile usata per costruire il testo mostrato
	private LinkedList<Label> textShown;	//Testo mostrato sulla console (5 frasi)
	private LinkedList<String> textLog;		//Testo nel log

	private BitmapFont textFont;

	private LabelStyle style;

//	private Skin skin;
	
	/**Costruttore**/
	private Console(){
		super();

		textShown=new LinkedList<Label>();
		textLog= new LinkedList<String>();

		// Carica le texture
		consoleTexture =new Texture(Gdx.files.internal("data/consoleBack.png"));
		consoleTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		consoleImage = new Image(consoleTexture);

	    FileHandle fontFile = Gdx.files.internal("data/main_font.ttf");
	    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
	    
	    FreeTypeFontParameter fontParameters = new FreeTypeFontParameter();
	    fontParameters.size = CommonUtils.textSize;
	    
	    textFont = generator.generateFont(fontParameters);
	    generator.dispose();
				
		style = new LabelStyle(textFont, Color.WHITE);

		setHeight( consoleImage.getHeight());

		addActor(consoleImage);
	}
	
	/**Mostra a schermo il dado tirato ed il risultato
	 * 
	 * @param diceValue dado tirato (20 per il d20)
	 * @param numberOfDices numero di dadi tirati
	 * @param bonus	bonus aggiunto al tiro
	 * @param result risultato
	 */
	public void addDiceRoll(int diceValue, int numberOfDices, int bonus, int result){
		
		// numero dei dadi "d" dado "+" bonus (es 2d6+2 )
		if(tempText==null)
			tempText=String.valueOf(numberOfDices)+"d"+String.valueOf(diceValue);
		else
			tempText+=String.valueOf(numberOfDices)+"d"+String.valueOf(diceValue);
				
		//Se il bonus è negativo, mostra un - invece di un +. Se è 0 non mostra nulla
		if(bonus!= 0)
			if(bonus<0)
				tempText+="-"+String.valueOf(Math.abs(bonus));
			else
				tempText+="+"+String.valueOf(bonus);
						
		//Risultato
		tempText +=" = "+String.valueOf(result);		
					}
	
	
	/**Mostra a schermo il dado tirato ed il risultato
	 * 
	 * @param diceValue dado tirato (20 per il d20) nel primo tiro
	 * @param numberOfDices numero di dadi tirati nel primo tiro
	 * @param diceValue2 dado tirato (20 per il d20) nel secondo tiro
	 * @param numberOfDice2s numero di dadi tirati nel secondo tiro
	 * @param bonus	bonus aggiunto al tiro
	 * @param result risultato
	 */
	public void addDiceRoll(int diceValue, int numberOfDices,int diceValue2, int numberOfDices2, int bonus, int result) {
		
		// numero dei dadi "d" dado "+" bonus (es 2d6+2 ) del primo tiro
		if(tempText==null)
			tempText=String.valueOf(numberOfDices)+"d"+String.valueOf(diceValue);
		else
			tempText+=String.valueOf(numberOfDices)+"d"+String.valueOf(diceValue);
		
		// numero dei dadi "d" dado "+" bonus (es 2d6+2 ) del secondo tiro
		tempText+=" + "+String.valueOf(numberOfDices2)+"d"+String.valueOf(diceValue2);
				
		//Se il bonus è negativo, mostra un - invece di un +
		if(bonus<0)
			tempText+="-"+String.valueOf(Math.abs(bonus));
		else
			tempText+="+"+String.valueOf(bonus);
					
		//Risultato
		tempText +=" = "+String.valueOf(result);		
				
		//Stampa il tiro
		printLine();
		
	}
	/**Inizia a costruire il testo. NON mostra a schermo nulla, per farlo bisogna utilizzare printLine dopo questo metodo
	 * 
	 * @param string stringa da inserire
	 */
	public void addString(String string){
		
		// Testo
		if(tempText==null)
			tempText=string;
		else
			tempText+=string;

	}	
	
	/**Stampa a schermo la stringa memorizzata**/
	public void printLine(){		
		
		//Se la coda è piena
		if(textShown.size()>=MAX_TEXT_SHOWN){
			//Elimina l'elemento più vecchio inserito
			this.getChildren().removeIndex(1);
			textShown.removeFirst();
		}
		
		Label label = new Label(tempText, style);
		label.setFontScale(0.25f);
		
		//Aggiunge una Label contenente il testo da mostrare
		textShown.add(label);
		
		//Shifta la posizione del vecchio testo verso l'alto
		for(int i=0;i<textShown.size();i++)
			textShown.get(i).setPosition(getWidth()/50, ((textShown.size()-i)*label.getHeight()*label.getFontScaleY())-(getHeight()/3));		
		
		//Mostra a schermo l'ultimo testo
		this.addActor(textShown.getLast());		

		//Aggiunge il testo nel log
		textLog.add(tempText);
		
		tempText=null;
	}

	/**Pulisce il testo mostrato a schermo e il log**/
	public void clearConsole(){
		clear();
		textShown.clear();
		textLog.clear();
		addActor(consoleImage);
	}
	
	public static Console getInstance() {
		return INSTANCE;
	}

}
