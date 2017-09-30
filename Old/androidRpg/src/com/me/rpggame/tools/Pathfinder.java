package com.me.rpggame.tools;

/**CLASSE PER IL PATHFINDING (SINGLETON)
 * 
 * @author Jacopo "Faust" Buttiglieri *
 * 
 * Il pathfinding è una variante dell'algoritmo di Djikstra. Dato un array:
 * 
 *   1- si inserisce il nodo di partenza (ovvero quello in cui ci si deve muovere) nell'array
 *   
 *   2- si considera un nodo preso dall'array
 *   3- si trovano tutti i suoi vicini validi (attraversabili ed entro i bordi dello schermo)
 *   4- si aggiungono alla lista, ma solo se non sono già stati aggiunti in precedenza
 *   5- si controllano i nodi in lista: se uno di loro è il nodo destinazione (ovvero quello in cui è il personaggio) il ciclo termina
 *   6- se il ciclo non è terminato, si ricomincia dal passo 2
 *   
 *   7- se il ciclo è terminato, si prende il nodo destinazione e si risale ai suoi genitori, aggiungendo ogni volta i nodi trovati al percorso
 *   8- si converte il percorso trovato in coordinate leggibili dall'entità (al contrario, perchè si parte dalla fine del cammino da compiere)
 */

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.me.rpggame.world.WorldManager;

public class Pathfinder{
	
	WorldManager wm;
	
	private final static Pathfinder INSTANCE = new Pathfinder();
	
	/**Costruttore**/
	private Pathfinder(){
	}
	
	/**Costruttore**/
	public void setWorldManager(WorldManager wm){
		this.wm=wm;
	}
	
	/**Algoritmo pathfinder
	 * 
	 * @param start x e y della partenza
	 * @param end x e y della destinazione
	 * @param excludeLast vero se ci si deve fermare un passo prima
	 * @return	il percorso costruito dai nodi trovati
	 */
	public synchronized ArrayList<Vector2> buildNewPath(Vector2 start,Vector2 end){
		
		//Numero di passi
		int step=0;
		//Memorizza l'indice del tile finale del percorso
		int endIndex=0;		
		//Flag che indica se la fine è stata trovata
		boolean endFound=false;
		
		//Array per contenere i nodi da controllare ed i nodi vicini a quello controllato
		ArrayList<Node> openNodes=new ArrayList<Node>();
		ArrayList<Node> neigborsNodes=new ArrayList<Node>();
		
		//Aggiunge il nodo di partenza per iniziare la ricerca
		openNodes.add(new Node(end));
		
		//Fino a che la fine non è stata trovata
		while(!endFound){
			
			//Incrementa il passo attuale
			step++;
			
			//Pulisce la lista dei nodi vicini
			neigborsNodes.clear();
			
			//Trova i vicini
			for(int i=0;i<openNodes.size();i++){
				neigborsNodes.addAll(getNeighborsToSearch(openNodes.get(i)));
				
				//Setta il nodo genitore per ogni nodo vicino trovato (solo se non ha un genitore)
				for(int p=0;p<neigborsNodes.size();p++){
					if(neigborsNodes.get(p).getParent()==null)
						neigborsNodes.get(p).setParent(openNodes.get(i));
				}
			}			
			
			//Controlla i vicini trovati
			for(int j=0;j<neigborsNodes.size();j++){
				//Non inserisce nodi già presenti nella lista	
				if(!isDuplicate(neigborsNodes.get(j), openNodes)){
					openNodes.add(neigborsNodes.get(j));
				}				
			}
						
			//Se si è arrivati a destinazione, si termina il ciclo
			for(int z=0;(z<openNodes.size())&&(!endFound);z++){
				if(openNodes.get(z).getVector().equals(start)){
					endFound=true;
					endIndex = z;
				}
			}
			
			//Se l'obiettivo è troppo distante, termina il ciclo e ritorna null
			if(step>9)
				return null;
			
		}
			
		//Ritorna il path creato
		return buildPath(openNodes.get(endIndex),new ArrayList<Vector2>(),step);		
	}
	
	/**Costruisce il percorso più breve
	 * 
	 * @param nodo di partenza
	 * @param percorso
	 * @param step attuale per fermare la ricorsione
	 * @return il percorso da compiere
	 */
	private synchronized ArrayList<Vector2> buildPath(Node start,ArrayList<Vector2> path,int step){
		
			//Array per memorizzare i nodi e i passi da compiere
			ArrayList<Vector2> finalPath= new ArrayList<Vector2>();
			ArrayList<Node> finalPathInNodes = new ArrayList<Node>();
			
			//Inizia costruzione ricorsiva
			finalPathInNodes.addAll(buildPathRecursion(start.getParent(),new ArrayList<Node>(),step-1));
						
			//Crea il percorso in passi (Vector2) leggibili dall'entità. Al contrario perchè il percorso
			//è trovato dalla fine all'inizio.
				for(int i=finalPathInNodes.size()-1;i>=0;i--){
					if(finalPathInNodes.get(i)!=null){
						finalPath.add(finalPathInNodes.get(i).getVector());
					}
				}
				
			//Ritorna il percorso completato
			return finalPath;
			
	}	
	
	/**Passo ricorsivo per costruire il percorso seguendo i genitori dal nodo di partenza
	 * 
	 * @param nodo di partenza
	 * @param percorso da costruire
	 * @param step decrescente per fermare la ricorsione
	 * @return il percorso costruito
	 */
	private ArrayList<Node> buildPathRecursion(Node start, ArrayList<Node> path,int step){
		
		if(step==0){	
			//Caso base
			path.add(start);
			return path;
		}
			//Passo ricorsivo
			path=buildPathRecursion(start.getParent(),path,step-1);
			path.add(start);
			return path;
			
	}
	
	/**Controlla se il nodo neighbor è già contenuto in openNodes	
	 * 
	 * @param neighbor nodo da controllare l'unicità
	 * @param openNodes lista dei nodi da controllare
	 * @return vero se è già conteunto, falso altrimenti
	 */
	private boolean isDuplicate(Node neighbor, ArrayList<Node> openNodes) {
		
		for(int i=0;i<openNodes.size();i++)
			if(neighbor.getVector().equals(openNodes.get(i).getVector())){
				return true;
			}
		
		return false;
	}

	/**trova tutti i vicini di un nodo
	 * 
	 * @param node nodo di cui saranno ispezionati i vicini
	 * @return i nodi adiacenti che siano passabili
	 */
	private ArrayList<Node> getNeighborsToSearch(Node node){
		
		ArrayList<Node> tempNodes= new ArrayList<Node>();
		
		int tempWorld[][]=wm.getWorld();
		
		//8 direzioni dei possibili vicini. Le precedenza alle direzioni non diagonali per
		//dare priorità ai movimenti dritti
		int directions [][]= {
								{(int) (node.getVector().x+1),(int) (node.getVector().y+0)},
								{(int) (node.getVector().x-1),(int) (node.getVector().y+0)},
								{(int) (node.getVector().x+0),(int) (node.getVector().y+1)},
								{(int) (node.getVector().x+0),(int) (node.getVector().y-1)},
								{(int) (node.getVector().x+1),(int) (node.getVector().y+1)},
								{(int) (node.getVector().x-1),(int) (node.getVector().y+1)},
								{(int) (node.getVector().x-1),(int) (node.getVector().y-1)},
								{(int) (node.getVector().x+1),(int) (node.getVector().y-1)},
								};
		
		
		//Per ogni vicino valido (entro i bordi del livello e attraversabile)
		for(int i=0;i<8;i++){
			if((directions [i][0]>=0)&&(directions [i][1]>=0))
				if(directions [i][0]<wm.getLevelWidth())
					if(directions [i][1]<wm.getLevelHeight())
						if(wm.isTilePassable(tempWorld[directions [i][1]][directions [i][0]],directions [i][0],directions [i][1])){
							
							//Crea un nodo temporaneo con x e y modificati a seconda della posizione.
							tempNodes.add(new Node(directions [i][0],directions [i][1]));								
						}
		}	
				
		//Ritorna i vicini validi trovati
		return tempNodes;
		
	}

	public static Pathfinder getInstance() {
		return INSTANCE;
	}

	

}
