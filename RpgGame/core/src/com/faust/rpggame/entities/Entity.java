package com.faust.rpggame.entities;

/**
 * CLASSE DELLE ENTITA' DI GIOCO
 * 
 * @author Jacopo "Faust" Buttiglieri
 * @author Steve Register [arns@arns.freeservers.com] (per linea di vista)
 */

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.faust.rpggame.CommonUtils;
import com.faust.rpggame.conditions.Ailment;
import com.faust.rpggame.conditions.Effect;
import com.faust.rpggame.items.Weapon;
import com.faust.rpggame.projectiles.Projectile;
import com.faust.rpggame.tools.Pathfinder;
import com.faust.rpggame.world.Console;
import com.faust.rpggame.world.Room;
import com.faust.rpggame.world.WorldManager;

public abstract class Entity extends Image {	

	//---------------- COSTANTI ----------------//
//
//	private static final int MAX_DICES = 10;	//Dadi massimi	
	public static final int DIRECTION_UP = 0;
	public static final int DIRECTION_RIGHT = 1;
	public static final int DIRECTION_DOWN = 2;
	public static final int DIRECTION_LEFT = 3;

	//---------------- CAMPI DELLA CLASSE ----------------//
	
	protected ArrayList<Vector2> currentPath;		//Percorso attuale
	protected Vector2 mapPosition;					//Posizione sulla matrice
	protected WorldManager wm;						//WorldManager
	protected Room room;							//Room
    protected int actualPathStep;					//Passo attuale nel percorso    
    protected boolean isMoving;    					//Flag che indica se l'entità si sta muovendo
    public static int gameTicks;					//Tick del gioco
    protected int remainingTicks;					//Ticks rimanenti nel round attuale
    protected int spentTicks;						//Ticks spesi per l'azione attuale
    protected EAction currentAction;				//Azione attualmente in uso
    protected int tickAtStart;						//
	protected Entity target;						//Bersaglio attacco
	protected Weapon wieldedWeapon;					//Arma
	protected ArrayList<Ailment> statusAilments;	//Condizioni sull'entità
	protected ArrayList<Effect> activeEffects;		//Effettu attivi sull'entità

    
	//---------------- CARATTERISTICHE ENTITA' ----------------//

	protected int maxHp=0;				//Punti ferita massimi	
	protected int actualHp=0;			//Punti ferita attuali. Muore se a 0
	protected int tempHp=0;			//Punti ferita temporanei
	protected int defense=10;			//Difesa
	protected int defensePenalty=0;		//Penalità alla Difesa
	protected int level=0;				//Livello dell'Entità
	protected int ATK=0;				//Bonus attacco totale;
	protected int ATKpenalty=0;			//Malus all' attacco totale
	protected int damageBonus=0;		//Bonus al danno
	protected int damagePenalty=0;		//Malus al danno
	public int sightRadius;				//Raggio di visuale
	protected boolean isDead;			//Flag che indica la morte dell'entità
	protected int numberOfAttacks;		//Attacchi in un round
	protected int magicResistance=0;	//Resistenza alla magia
	protected int damageResistance=0;	//Resistenza al danno
	protected int savingThrow;			//Tiro salvezza
	protected String name;				//Nome dell'entità
	protected int spellToCast;			//Incantesimo che si stà lanciando
	protected boolean startedCastingFlag;
	
	/**Costruttore con coordinate**/
	public Entity(Texture texture, String name,int mx, int my, Room room, WorldManager wm){
		super(texture);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		this.room=room;
				
		//Inizializza i campi della classe
		mapPosition=new Vector2(mx,my); 
		this.room.getTileAtPosition(mapPosition).setTemporaryPassability(false);
		actualPathStep=0;
		setPosition((mx+1)*getWidth(),800-(my+2)*getHeight());
		sightRadius=4;
		this.setVisible(false);
		this.name=name;
		this.wm =wm;
		remainingTicks=6;
		isMoving=false;
		currentAction=new EAction(EAction.STEP,0);
		tickAtStart=0;
		numberOfAttacks=1;
		statusAilments=new ArrayList<Ailment>();
		activeEffects=new ArrayList<Effect>();
		setSpellToCast(-1);
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
		if(room.getTileAtPosition(mapPosition).isTileInSight())
			//L'entità è visibile
			this.setVisible(true);
		else
			this.setVisible(false);
				
		//Se ha meno di 0 hp
		if(actualHp<=0){
			//E' morto. Rende passabile la casella che occupava
			isDead=true;
			
			//TODO debug
			if(!(this instanceof Player))
				room.getTileAtPosition(mapPosition).setTemporaryPassability(true);
		}
		
		//Se l'entità bersaglio è morta, si elimina il target
		if((target!=null)&&(target.isDead())){
			target=null;
		}
	}
	
	/**Gestisce gli effetti attivi sull'entità**/
	protected  void handleEffect(Effect effect) {
		//Gestisce i singoli stati
		switch(effect.getStatusType()){
			case Effect.EFFECT_BLESSED:{
				//Benedetto: bonus ad ATK e Danno pari al valore
				ATK+=effect.getValue();
				damageBonus+=(effect.getValue()/2)+1;
				
				break;
			}			
			case Effect.EFFECT_PROTECTED:{
				//Protetto: bonus a TS e Resistenza al danno pari al valore
				damageResistance+=effect.getValue();
				savingThrow+=effect.getValue();
				
				break;
			}			
			case Effect.EFFECT_POWERATTACK:{
				//Attacco Poderoso: bonus ai danni, malus all'ATK
				
				ATK-=effect.getValue()/2;
				damageBonus+=effect.getValue();
				
				break;
			}				
			case Effect.EFFECT_DEFENSIVE:{
				//Postura Difensiva: bonus alla Difesa, malus all'ATK
				
				ATK-=effect.getValue();
				defense+=effect.getValue();
				
				break;
			}						
			case Effect.EFFECT_SHIELDED:{
				//Scudo Magico: bonus ai punti ferita temporanei	
				
				addTemporaryHp(effect.getValue());		
				
				break;
			}			
		}	
		
		clearEffects();
	}
	
	/**Decrementa la durata degli effetti attivi**/
	protected void decrementEffectsDuration() {
		
		//Cicla tutti le condizioni di stato dell'entità
		for(int i=0;i<activeEffects.size();i++)
		{
			if(activeEffects.get(i).getDuration()>0)
				activeEffects.get(i).decrementDuration();
			
			clearEffects();
		}
	}

	/**Rimuove gli effetti la cui durata è terminata**/
	private synchronized void clearEffects() {
		//Cicla tutti le condizioni di stato dell'entità
		for(int i=0;i<activeEffects.size();i++)
		{					
			if(activeEffects.get(i).getDuration()==0){				
				//Gestisce i singoli stati
				switch(activeEffects.get(i).getStatusType()){
					case Effect.EFFECT_BLESSED:{
						//Benedetto: bonus ad ATK e Danno pari al valore
						ATK-=activeEffects.get(i).getValue();
						damageBonus-=activeEffects.get(i).getValue();
						
						break;
					}					
					case Effect.EFFECT_PROTECTED:{
						//Protetto: bonus a TS e Resistenza al danno pari al valore
						damageResistance-=activeEffects.get(i).getValue();
						savingThrow-=activeEffects.get(i).getValue();

						Console.getInstance().addString("Protezione "+name+" : Terminata ");
						Console.getInstance().printLine();
						
						break;
					}		
					case Effect.EFFECT_POWERATTACK:{
						//: bonus ad ATK e Danno pari al valore
						ATK+=activeEffects.get(i).getValue()/2;
						damageBonus-=activeEffects.get(i).getValue();
						
						break;
					}
					case Effect.EFFECT_DEFENSIVE:{
						//Postura Difensiva: bonus alla Difesa, malus all'ATK
						
						ATK+=activeEffects.get(i).getValue();
						defense-=activeEffects.get(i).getValue();
						
						break;
					}						
					case Effect.EFFECT_SHIELDED:{
						//Scudo: bonus alla Difesa		
						
						removeTemporaryHp();		
						
						break;
					}			
				}	
				
				activeEffects.remove(i);
			}
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
				case Ailment.STATUS_STUNNED:{
					//Stordito: non attacca, ha movimento ridotto e ha -2 a difesa
					defensePenalty+=2;
					remainingTicks=2;
					
					Console.getInstance().addString(name+": Stordito");
					Console.getInstance().printLine();					
					break;
				}		
				case Ailment.STATUS_PARALIZED:{
					//Paralizzato: non agisce e ha -4 a difesa
					defensePenalty+=4;
					remainingTicks=0;
					
					Console.getInstance().addString(name+": Paralizzato");
					Console.getInstance().printLine();					
					break;
				}		
				case Ailment.STATUS_DISEASED:{
					//Malattia: si muore allo scadere dei round
										
					if(statusAilments.get(i).getDuration()>=1){
						Console.getInstance().addString(name+": "+(statusAilments.get(i).getDuration())+" round alla morte");
						Console.getInstance().printLine();			
					}
					else
						inflictDamage(actualHp+damageResistance+tempHp);
						
					break;
				}				
				case Ailment.STATUS_WEAKENED:{
					//Indebolito: si ottiene un malus ad ATK, Danni e Difesa pari al valore
					ATKpenalty+=statusAilments.get(i).getValue();
					damagePenalty+=statusAilments.get(i).getValue();
					defensePenalty+=statusAilments.get(i).getValue();
						
					break;
				}		
			}			
			//Decrementa la durata dello status
			if(statusAilments.get(i).getDuration()>=0)
				statusAilments.get(i).decrementDuration();
		}
		
		clearStatus();		
	}	

	/**Rimuove gli status esauriti**/
	private void clearStatus() {
		
		//Se la durata di uno status è terminata e non era uno status di malattia
		for(int i=0;i<statusAilments.size();i++){
			
			if(statusAilments.get(i).getDuration()==0){	
				
				//Guarisce le penalità dai singoli stati
				switch(statusAilments.get(i).getStatusType()){
					case Ailment.STATUS_STUNNED:{
						//Stordito: non attacca, ha movimento ridotto e ha -2 a difesa
						defensePenalty-=2;				
						break;
					}		
					case Ailment.STATUS_PARALIZED:{
						//Paralizzato: non agisce e ha -4 a difesa
						defensePenalty-=4;				
						break;
					}						
					case Ailment.STATUS_WEAKENED:{
						//Indebolito: si ottiene un malus ad ATK, Danni e Difesa pari al valore
						ATKpenalty-=statusAilments.get(i).getValue();
						damagePenalty-=statusAilments.get(i).getValue();
						defensePenalty-=statusAilments.get(i).getValue();
							
						break;
					}					
				}
				
				//Toglie lo status
				statusAilments.remove(i);	
			}
					
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
					if(!checkSolids()/*&&!checkTarget()*/){
						
						room.getTileAtPosition(mapPosition).setTemporaryPassability(true);
						
						//Esegue il passo successivo dello step
						mapPosition=currentPath.get(actualPathStep);
						actualPathStep++;		
	
						room.getTileAtPosition(mapPosition).setTemporaryPassability(false);
	
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
			if(!wm.isTilePassable(wm.getTileAtMapPosition(currentPath.get(actualPathStep)),xx,yy)){
		
				//Se è una porta, la apre
				if(wm.getTileAtMapPosition(currentPath.get(actualPathStep))== wm.TILE_DOOR_CLOSED)
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
			return CommonUtils.distance(mapPosition,target.getMapPosition())==1;
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
		room.getTileAtPosition(mapPosition).setTemporaryPassability(true);		
		
		//Se un path è già stato creato, lo cancella
		resetPath();
		
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

		spentTicks=0;	
		//Cancella l'eventuale spell che si stava lanciando
		setSpellToCast(-1);
		
		if(type != EAction.STEP)
			resetPath();	
			
	}

	/**Resetta il path**/
	private void resetPath(){
		
		if(currentPath!=null){	
			
			currentPath.clear();
			currentPath=null;
			actualPathStep=0;
		}		
	}


	/**Setta l'incantesimo da lanciare
	 * 
	 * @param spell incantesimo da lanciare
	 */
	public void setSpellToCast(int spell) {
		spellToCast=spell;		
		
		if(spellToCast!=-1)
			startedCastingFlag=true;
	};  
	
	/**Aggiunge uno status
	 * 
	 * @param newStatus status da aggiungere
	 */
	public void addStatus(Ailment newStatus) {
		
		if(!conditionIsDuplicate(newStatus))
			statusAilments.add(newStatus);	
	}
	
	/**Rimuove uno status dato il tipo
	 * 
	 * @param status Tipo dello stato da rimuovere
	 */
	public void removeStatusType(int statusType) {
		
		for(int i=0;i<statusAilments.size();i++)
			if(statusAilments.get(i).getStatusType()==statusType)
				statusAilments.get(i).end();
		
		clearStatus();
	}
	

	/**Aggiunge un effetto
	 * 
	 * @param newEffect effetto da aggiungere
	 */
	public void addEffect(Effect newEffect) {

		if(!conditionIsDuplicate(newEffect)){
			activeEffects.add(newEffect);
			
			//Gli effetti vengono gestiti solo se non sono duplicati,
			//perchè gli effetti non si cumulano
			handleEffect(newEffect);
		}
		
	}
	
	/**Ottiene la Difesa**/
	public int getDefense(){
		return (defense-defensePenalty);
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
		
		if(tempHp > 0){
			
			//Se ha punti ferita temporanei, vengono diminuiti per primi
			tempHp-= (damage-damageResistance);
			
			//Se subisce più danni dei punti ferita temporanei, viene sottratta la differenze dai punti ferita
			if(tempHp < 0)
				actualHp=Math.max(actualHp-tempHp,0);
		}
		else{					
			actualHp=Math.max(actualHp-(damage-damageResistance),0);
		}
	} 	
	
	/**Funzione per infliggere danni all'entità**/
	public void cureDamage(int cure){
		   actualHp=Math.min(actualHp+cure,maxHp);
	} 
	
	/**Funzione per aggiungere punti ferita temporanei**/
	public void addTemporaryHp(int amount){
		   tempHp+=amount;
	}
	
	/**Funzione per aggiungere punti ferita temporanei**/
	public void removeTemporaryHp(){
		   tempHp = 0;
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
	
	/**Controlla i duplicati. Se ne trova uno, aumenta la durata dello stato base
	 * 
	 * @param newCondition nuova Condizione
	 * @return false se non ci sono duplicati, vero se ne ha trovato uno
	 */
	private boolean conditionIsDuplicate(Ailment newCondition) {

		//Controllo tra le condizioni
		for(int i=0;i<statusAilments.size();i++)
			if(newCondition.getStatusType()==statusAilments.get(i).getStatusType()){
				statusAilments.get(i).extendDuration(newCondition.getDuration());
				return true;
			}
		
		return false;
	}
	
	/**Controlla i duplicati. Se ne trova uno, resetta la durata dello stato base
	 * 
	 * @param newCondition nuovo Effetto
	 * @return false se non ci sono duplicati, vero se ne ha trovato uno
	 */
	private boolean conditionIsDuplicate(Effect newCondition) {
		
		//Controllo tra gli effetti
		for(int i=0;i<activeEffects.size();i++)
			if(newCondition.getStatusType()==activeEffects.get(i).getStatusType()){
				activeEffects.get(i).setDuration(newCondition.getDuration());
				return true;
			}
	
		return false;
	}
	
	/**Fa creare un proiettile all'entità
	 * 
	 * @param proj proiettile creato
	 */
	public void createProjectile(Projectile proj){
		room.addProjectile(proj);		
	}
	
	/**Ritorna il nome dell'Entità
	 * 
	 * @return nome dell'entità
	 */
	public String getName(){
		return name;
	}

	/**Controlla che abbia iniziato a lanciare un incantesimo
	 * 
	 * @return vero se ha iniziato
	 */
	public boolean startedCasting() {
		
		if(startedCastingFlag){
			startedCastingFlag=false;
			return true;
		}
		
		return false;
	}

}
