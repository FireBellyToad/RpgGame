package com.me.rpggame.entities;

/**
 * CLASSE DELLE ENTITA' DI GIOCO
 * 
 * @author Jacopo "Faust" Buttiglieri
 * @author Steve Register [arns@arns.freeservers.com] (per linea di vista)
 */

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.me.rpggame.items.Weapon;
import com.me.rpggame.tools.Pathfinder;
import com.me.rpggame.world.Console;
import com.me.rpggame.world.WorldManager;

public abstract class Entity extends Image {	

	//---------------- COSTANTI ----------------//

	private static final int MAX_DICES = 10;	//Dadi massimi	

	//---------------- CAMPI DELLA CLASSE ----------------//
	
	protected ArrayList<Vector2> currentPath;	//Percorso attuale
	protected Vector2 mapPosition;				//Posizione sulla matrice
	protected WorldManager wm;					//WorldManager
    protected Random dice=new Random();			//Oggetto per il tiro di dadi
    protected int actualPathStep;				//Passo attuale nel percorso    
    protected boolean isMoving;    				//Flag che indica se l'entità si sta muovendo
    public static int gameTicks;				//Tick del gioco
    protected int remainingTicks;				//Ticks rimanenti nel round attuale
    protected int spentTicks;					//Ticks spesi per l'azione attuale
    protected EAction currentAction;			//Azione attualmente in uso
    protected int tickAtStart;
	protected Entity target;					//Bersaglio attacco
	protected Weapon wieldedWeapon;				//Arma
	protected ArrayList<Ailment> statusAilments;
    
	//---------------- CARATTERISTICHE ENTITA' ----------------//

	protected int maxHp=0;				//Punti ferita massimi	
	protected int actualHp=0;			//Punti ferita attuale. Muore se a 0
	protected int defense=10;			//Difesa
	protected int level=0;				//Livello dell'Entità
	protected int totalATK=0;			//Bonus attacco totale;
	public int sightRadius;				//Raggio di visuale
	protected boolean isDead;			//Flag che indica la morte dell'entità
	protected int numberOfAttacks;		//Attacchi in un round
	protected int magicResistance=0;		//Resistenza alla magia
	protected int damageResistance=0;		//Resistenza al danno
	protected int savingThrow;			//Tiro salvezza
	protected String name;

	
	/**Costruttore con coordinate**/
	public Entity(Texture texture, String name,int mx, int my, WorldManager wm){
		super(texture);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		//Inizializza i campi della classe
		mapPosition=new Vector2(mx,my); 
		wm.getTileAtPosition(mapPosition).setTemporaryPassability(false);
		actualPathStep=0;
		setPosition((mx+1)*getWidth(),800-(my+2)*getHeight());
		sightRadius=4;
		this.setVisible(false);
		this.wm=wm;
		this.name=name;
		remainingTicks=6;
		isMoving=false;
		currentAction=new EAction(EAction.STEP,0);
		tickAtStart=0;
		numberOfAttacks=1;
		statusAilments=new ArrayList<Ailment>();
	}		

    
    /**Funzione per calcolo della distanza
     * 
     * @param point1 primo punto
     * @param point2 secondo punto
     * @return
     */
    public int distance(Vector2 point1, Vector2 point2){
        double result=Math.pow((point1.x-point2.x),2)+Math.pow((point1.y-point2.y),2);
        result = Math.sqrt(result);
        return (int) result;
    }
    
    /**Funzione per calcolo del dado    
     * 
     * @param Numero di dadi tirati
     * @param valore del dado (20 per il d20, 12 per il d12...)
     * @param bonus da sommare al tiro totale     * 
     * @param showResult flag da mettere a vero se si vuole stampare il tiro
     * @param ignoreUnderOne flag da mettere a vero se i risultati minori di uno vanno arrotondati ad 1
     * @return il risultato
     */
    public int diceRoll(int number,int diceThrown, int bonus,boolean showResult)
    {
        int results[]=new int[MAX_DICES];
        int totalResult=0;
        int dicesThrown=0;
        
        //Tira i dadi
        for(int i=0;i<number;i++){
            results[i]+=dice.nextInt(diceThrown-1)+1;
            dicesThrown=i+1;
        }
        
        //Somma i risultati di tiri multipli
        for(int i=0;i<dicesThrown;i++){
        	totalResult+=results[i];
        }
        
        totalResult+=bonus;
        
        if(showResult)
        	Console.getInstance().addDiceRoll(diceThrown, number, bonus, totalResult);
                
        return totalResult;
    }
    
    /**Funzione per calcolo del tiro di dado con un'arma che arrotonda a 1 i risultato 0 o negativi    
     * 
     * @param Numero di dadi tirati
     * @param valore del dado (20 per il d20, 12 per il d12...)
     * @param bonus da sommare al tiro totale     * 
     * @param showResult flag da mettere a vero se si vuole stampare il tiro
     * @return il risultato
     */
    public int weaponDiceRoll(Weapon weapon, int bonus,boolean showResult)
    {
        int results[]=new int[MAX_DICES];
        int totalResult=0;
        int dicesThrown=0;
        
        //Tira i dadi
        for(int i=0;i<weapon.getWeaponDiceNumber();i++){
        	//Se non è un d1, tira il dado, altrimenti infligge 1 danno
        	if(weapon.getWeaponDice()!=1)
        		results[i]+=dice.nextInt(weapon.getWeaponDice()-1)+1;
        	else
        		results[i]+=1;
        	
            dicesThrown=i+1;
        }
        
        //Somma i risultati di tiri multipli
        for(int i=0;i<dicesThrown;i++){
        	totalResult+=results[i];
        }
        
        totalResult+=bonus;
        totalResult+=weapon.getWeaponMagicBonus();
        totalResult=Math.max(1,totalResult);
        
        if(showResult)
        	Console.getInstance().addDiceRoll(weapon.getWeaponDice(),weapon.getWeaponDiceNumber(), bonus, totalResult);
        
        return totalResult;
    }
		
	/**Cambia la posizione di partenza**/
	public void setStartPosition(Vector2 startPos){
		mapPosition.x=startPos.x;
		mapPosition.y=startPos.y;
		setPosition((2+startPos.x)*getWidth(),800-(1-startPos.y)*getHeight());
	}
	
	/**Gestisce la logica dell'oggetto**/
	public void logic(){
		
		//Se il tile su cui è posizionata è visibile
		if(wm.getTileAtPosition(mapPosition).isTileInSight())
			//L'entità è visibile
			this.setVisible(true);
		else
			this.setVisible(false);
				
		//Se ha meno di 0 hp
		if(actualHp<=0){
			//E' morto. Rende passabile la casella che occupava
			isDead=true;
			wm.getTileAtPosition(mapPosition).setTemporaryPassability(true);
		}
		
		//Se l'entità bersaglio è morta, si elimina il target
		if((target!=null)&&(target.isDead())){
			target=null;
		}
	}
	
	/**Gestisce le condizioni dello status dell'entità**/
	protected void handleStatus() {

		//Cicla tutti le condizioni di stato dell'entità
		for(int i=0;i<statusAilments.size();i++){
			
			//Gestisce i singoli stati
			switch(statusAilments.get(i).getStatusType()){
				case Ailment.STATUS_POISONED:{
					//Avvelenato: -1 pf a round
					actualHp--;
					
					Console.getInstance().addString(name+": subisce 1 danno da veleno");
					Console.getInstance().printLine();
					break;
				}
			}
			
			//Decrementa la durata dello status
			statusAilments.get(i).decrementDuration();			
		}
		
		//Se la durata di uno status è terminata, toglie lo status
		for(int j=0;j<statusAilments.size();j++){
			if(statusAilments.get(j).getDuration()<=0)
				statusAilments.remove(j);
		}
			
		
	}


	/**Muove il personaggio**/
	protected void moveToTarget(){
		
		//Se il path è stato costruito e ci sono azioni in coda
		if((currentPath!=null)&&(currentAction!=null)){			
			//Se il path non è colpletamente esplorato e l'azione è di movimento
			if(actualPathStep < currentPath.size()){
				if((currentAction.getActionType()==EAction.STEP)&&(currentAction.getCostInTicks()<=remainingTicks)){
				
					//Se lo step attuale non è su una porta o se non c'è il target nelle vicinanze
					if(!checkSolids()&&!checkTarget()){
						
						wm.getTileAtPosition(mapPosition).setTemporaryPassability(true);
						
						//Esegue il passo successivo dello step
						mapPosition=currentPath.get(actualPathStep);
						actualPathStep++;		
	
						wm.getTileAtPosition(mapPosition).setTemporaryPassability(false);
	
						remainingTicks--;
						currentAction.decreaseCost();
					}
				}
			}
			else{	

				currentPath.clear();
				currentPath=null;
				actualPathStep=0;
				isMoving=false;
			}
		}
		
		if(currentPath==null)
			//Se il path è completamente esplorato, elimina i passi
			addGameAction(EAction.STEP,0);
	}
	
		
	/**Controlla che il prossimo nodo sia attraversabile
	 * Non può comunque aprire una porta passandoci attraverso, si deve cliccare sulla porta per aprirla
	 * Una volta raggiunta la porta, la apre ed interrompe il movimento
	 * 
	 * @return vero se il prossimo nodo non è attraversabile
	 */
	protected boolean checkSolids() {
		
			int xx=(int) currentPath.get(actualPathStep).x;
			int yy=(int) currentPath.get(actualPathStep).y;	

			//Se il prossimo nodo non è attraversabile
			if(!wm.isTilePassable(wm.getMapTileAtPosition(currentPath.get(actualPathStep)),xx,yy)){
		
				//Se è una porta, la apre
				if(wm.getMapTileAtPosition(currentPath.get(actualPathStep))== wm.TILE_DOOR_CLOSED)
					wm.changeTileType(currentPath.get(actualPathStep), wm.TILE_DOOR_OPENED);
					
				isMoving=false;
				
				//Svuota il path (termina di muoversi)
				currentPath.clear();
				currentPath=null;
				actualPathStep=0;
				return true;
			}				
			
			return false;
	}

	/**Controllo della vicinanza al bersaglio
	 * 
	 * @return vero se il bersaglio esiste ed è adiacente all'entità 
	 */
	protected boolean checkTarget(){
		
		if(target!=null)
			return distance(mapPosition,target.getMapPosition())==1;
		else 
			return false;
	}
	
	/**Ottiene la posizione rispetto allo schermo
	 * 
	 * @return posizione a schermo
	 */
	public Vector2 getPosition() {
		return new Vector2(this.getX(),this.getY());
	}
	
	/**Ottiene la posizione rispetto alla mappa
	 * 
	 * @return posizione sulla matrice di mappa
	 */
	public Vector2 getMapPosition(){
		return mapPosition;
	}	

	/**Setta il percorso da seguire
	 * 
	 * @param mapEndPosition posizione di destinazione
	 */
	public void setNewPath(Vector2 mapEndPosition) {		

		//Rende attraversabile la casella attuale
		wm.getTileAtPosition(mapPosition).setTemporaryPassability(true);		
		
		//Se un path è già stato creato, lo cancella
		if(currentPath!=null){		
			
			currentPath.clear();
			currentPath=null;
			actualPathStep=0;
		}
		
		isMoving=true;
		
		//Crea il nuovo path
		currentPath=Pathfinder.getInstance().buildNewPath(mapPosition, mapEndPosition);		
		
		if(currentPath!=null)
			addGameAction(EAction.STEP,currentPath.size());		
		else
			addGameAction(EAction.STEP,0);		
		
	}
	
	/**Cambia l'azione attuale
	 * 
	 * @param type il tipo di azione
	 * @param cost il costo dell'azione
	 */
	public void addGameAction(int type,int cost){
					
		currentAction=new EAction(type, cost);
	}
	
	/**Ottiene la Difesa**/
	public int getDefense(){
		return defense;
	}
	
	/**Ottiene la Resistenza al danno**/
	public int getDamageResistance(){
		return damageResistance;
		
	}	
	
	/**Ottiene la Resistenza alla Magia**/
	public int getMagicResistance(){
		return magicResistance;
	}
	/**Funzione per infliggere danni all'entità**/
	public void inflictDamage(int damage){
		   actualHp=Math.max(actualHp-(damage-damageResistance),0);
	} 	
	
	/**Funzione per infliggere danni all'entità**/
	public void cureDamage(int cure){
		   actualHp=Math.min(actualHp+cure,maxHp);
	} 
	
	/**Funzione che ritorna se la creatura è viva o morta**/
	public boolean isDead(){
		return isDead;
	}
	
	/**Funzione per settare il bersaglio dell'attacco**/
	public void setTarget(Entity entity){
		target=entity;
	}
	
	/**Funzione per il tiro salvezza**/
	public abstract boolean makeSavingThrow(int difficulty,int malus);
	
	/**Linea di vista
	 * 
	 * @param targetMapPosition posizione rispetto alla quale si deve controllare la linea di vista
	 * @return vero se l'entità riesce a vederla
	 */
	protected boolean los(Vector2 targetMapPosition)
	{
		float t, x, y, ax, ay, sx, sy, dx, dy;
		
		//Se per caso la casella di destinazione e partenza sono le stesse, si restituisce automaticamente vero
		if(mapPosition.equals(targetMapPosition))
			return true;
	 
	   //Si ottiene la differenza (delta) tra le coordinate
	   dx = mapPosition.x - targetMapPosition.x;
	   dy = mapPosition.y - targetMapPosition.y;
	   	 
	   //Valori assoluti molticati per 2
	   ax = Math.abs((int) dx)*2;
	   ay = Math.abs((int) dy)*2;
	 
	   //Segni di x e y
	   sx = Math.signum(dx);
	   sy = Math.signum(dy);
	 
	   //Coordinate x e y della destinazione
	   x = targetMapPosition.x;
	   y = targetMapPosition.y;
	 
	   //Controlla se la linea è più orizzontale che verticlae
	   if(ax > ay)
	   {
	      // t = assoluto di y - il doppio dell'assoluto di x
	      t = ay - (ax/2);
	      do
	      {
	         if(t >= 0)
	         {
	            /* se t è maggiore o uguale a zero, aggiunge il segno di
	             *  DeltaY a y e sottrae l'assoluto di X da t      */
	            y += sy;
	            t -= ax;
	         }
	 
	         //aggiunge il segno di dx a x e l'assoluto di Y a t
	         x += sx;
	         t += ay;
	 
	         //controlla se non abbiamo raggiunto la destinazione
	         if( x == mapPosition.x && y == mapPosition.y)
	         {
	            //si può vedere la destinazione
	            return true;
	         }
	      //continua a cercare fino a che la visuale è bloccata da un ostacolo
	      }
	      while(wm.isTileViewObstacle(wm.getMapTileAtPosition((int)x,(int) y)) == false);
	 
	      //Non si può vedere la destinazione
	      return false;
	   }
	   else
	   {
	      //caso in cui la linea sia più verticale. Uguale al caso superiore solo invertito
	      t = ax - (ay/2);
	      do
	      {
	         if(t >= 0)
	         {
	            x += sx;
	            t -= ay;
	         }
	         y += sy;
	         t += ax;
	         if(x == mapPosition.x && y == mapPosition.y)
	         {
	            return true;
	         }
	      }
	      while(wm.isTileViewObstacle(wm.getMapTileAtPosition((int)x,(int) y)) == false);
	      return false;
	   }
	}

	/**Aggiunge uno status**/
	public void addStatus(Ailment newStatus) {
		
		statusAilments.add(newStatus);	
	}
	
	
}
