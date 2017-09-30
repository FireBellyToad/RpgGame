package com.faust.rpggame.tools;

/**
 * CLASSE DEI NODI PER IL PATHFINDER
 * 
 * @author Jacopo "Faust" Buttiglieri
 */

import com.badlogic.gdx.math.Vector2;

public class Node {
	
	private Vector2 vector;
	private Node parent;
	
	public Node(Vector2 node){
		this.vector=node;
	}		
	
	public Node(int x, int y){
		this.vector=new Vector2(x,y);
	}	
	
	
	public void setParent(Node parent){
		this.parent=parent;
	}
	
	public Node getParent(){
		return parent;
	}
	
	
	public Vector2 getVector(){
	return vector;
	}
		
}
