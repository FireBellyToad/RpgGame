package com.faust.rpggame.world;
import java.util.*; 

import com.badlogic.gdx.math.Vector2;
import com.faust.rpggame.entities.Entity;


/**
 * 
 * CLASSE PER LA GENERAZIONE DI DUNGEON CASUALI
 *  
 * @author Solarnus
 * @author Jacopo "Faust" Buttiglieri
 *
*/

 
public class DungeonGenerator{
	
	private WorldManager wm;
	private ArrayList<Vector2> enemyPlaceholders = new ArrayList<Vector2>();
	
	//max size of the map
	private int xmax = 500; //500 columns
	private int ymax = 500; //500 rows
 
	//size of the map
	private int xsize = 0;
	private int ysize = 0;
 
	//number of "objects" to generate on the map
	private int objects = 0;
 
	//define the %chance to generate either a room or a corridor on the map
	//BTW, rooms are 1st priority so actually it's enough to just define the chance
	//of generating a room
	private int chanceRoom = 75; 
 
	//our map
	private int[][] dungeon_map = { };
 
	//the old seed from the RNG is saved in this one
	private long oldseed = 0;
 
	public DungeonGenerator(WorldManager worldManager) {
		wm = worldManager;
	}

	//setting a tile's type
	private void setCell(int x, int y, int celltype){
		dungeon_map[y][x] = celltype;
	}
 
	//returns the type of a tile
	private int getCell(int x, int y){
		return dungeon_map[y][x];
	}
 
	//The RNG. the seed is based on seconds from the "java epoch" ( I think..)
	//perhaps it's the same date as the unix epoch
	private int getRand(int min, int max){
		//the seed is based on current date and the old, already used seed
		Date now = new Date();
		long seed = now.getTime() + oldseed;
		oldseed = seed;
 
		Random randomizer = new Random(seed);
		int n = max - min + 1;
		int i = randomizer.nextInt(n);
		if (i < 0)
			i = -i;

		return min + i;
	}
 
//	private boolean makeCorridor(int x, int y, int lenght, int direction){
//		//define the dimensions of the corridor (er.. only the width and height..)
//		int len = getRand(2, lenght);
//		int floor = wm.TILE_FLOOR;
//		int dir = 0;
//		if (direction > 0 && direction < 4) dir = direction;
// 
//		int xtemp = 0;
//		int ytemp = 0;
// 
//		switch(dir){
//		case 0:
//		//north
//			//check if there's enough space for the corridor
//			//start with checking it's not out of the boundaries
//			if (x < 0 || x > xsize) return false;
//			else xtemp = x;
// 
//			//same thing here, to make sure it's not out of the boundaries
//			for (ytemp = y; ytemp > (y-len); ytemp--){
//				if (ytemp < 0 || ytemp > ysize) return false; //oh boho, it was!
//				if (getCell(xtemp, ytemp) != wm.TILE_UNUSED) return false;
//			}
// 
//			//if we're still here, let's start building
//			for (ytemp = y; ytemp > (y-len); ytemp--){
//				setCell(xtemp, ytemp, floor);
//			}
//			break;
//		case 1:
//		//east
//				if (y < 0 || y > ysize) return false;
//				else ytemp = y;
// 
//				for (xtemp = x; xtemp < (x+len); xtemp++){
//					if (xtemp < 0 || xtemp > xsize) return false;
//					if (getCell(xtemp, ytemp) != wm.TILE_UNUSED) return false;
//				}
// 
//				for (xtemp = x; xtemp < (x+len); xtemp++){
//					setCell(xtemp, ytemp, floor);
//				}
//			break;
//		case 2:
//		//south
//			if (x < 0 || x > xsize) return false;
//			else xtemp = x;
// 
//			for (ytemp = y; ytemp < (y+len); ytemp++){
//				if (ytemp < 0 || ytemp > ysize) return false;
//				if (getCell(xtemp, ytemp) != wm.TILE_UNUSED) return false;
//			}
// 
//			for (ytemp = y; ytemp < (y+len); ytemp++){
//				setCell(xtemp, ytemp, floor);
//			}
//			break;
//		case 3:
//		//west
//			if (ytemp < 0 || ytemp > ysize) return false;
//			else ytemp = y;
// 
//			for (xtemp = x; xtemp > (x-len); xtemp--){
//				if (xtemp < 0 || xtemp > xsize) return false;
//				if (getCell(xtemp, ytemp) != wm.TILE_UNUSED) return false; 
//			}
// 
//			for (xtemp = x; xtemp > (x-len); xtemp--){
//				setCell(xtemp, ytemp, floor);
//			}
//			break;
//		}
// 
//		//woot, we're still here! let's tell the other guys we're done!!
//		return true;
//	}
 
	private boolean makeRoom(int x, int y, int xlength, int ylength, int direction){
		//define the dimensions of the room, it should be at least 4x4 tiles (2x2 for walking on, the rest is walls)
		int xlen = getRand(4, xlength);
		int ylen = getRand(4, ylength);
		//the tile type it's going to be filled with
		int floor = wm.TILE_FLOOR; //jordgolv..
		int wall = wm.TILE_WALL; //jordv????gg
		//choose the way it's pointing at
		int dir = 0;
		if (direction > 0 && direction < 4) dir = direction;
 
		switch(dir){
		case 0:
		//north
			//Check if there's enough space left for it
			for (int ytemp = y; ytemp > (y-ylen); ytemp--){
				if (ytemp < 0 || ytemp > ysize) return false;
				for (int xtemp = (x-xlen/2); xtemp < (x+(xlen+1)/2); xtemp++){
					if (xtemp < 0 || xtemp > xsize) return false;
					if (getCell(xtemp, ytemp) != wm.TILE_UNUSED) return false; //no space left...
				}
			}
 
			//we're still here, build
			for (int ytemp = y; ytemp > (y-ylen); ytemp--){
				for (int xtemp = (x-xlen/2); xtemp < (x+(xlen+1)/2); xtemp++){
					//start with the walls
					if (xtemp == (x-xlen/2)) setCell(xtemp, ytemp, wall);
					else if (xtemp == (x+(xlen-1)/2)) setCell(xtemp, ytemp, wall);
					else if (ytemp == y) setCell(xtemp, ytemp, wall);
					else if (ytemp == (y-ylen+1)) setCell(xtemp, ytemp, wall);
					//and then fill with the floor
					else setCell(xtemp, ytemp, floor);
				}
			}
			break;
		case 1:
		//east
			for (int ytemp = (y-ylen/2); ytemp < (y+(ylen+1)/2); ytemp++){
				if (ytemp < 0 || ytemp > ysize) return false;
				for (int xtemp = x; xtemp < (x+xlen); xtemp++){
					if (xtemp < 0 || xtemp > xsize) return false;
					if (getCell(xtemp, ytemp) != wm.TILE_UNUSED) return false;
				}
			}
 
			for (int ytemp = (y-ylen/2); ytemp < (y+(ylen+1)/2); ytemp++){
				for (int xtemp = x; xtemp < (x+xlen); xtemp++){
 
					if (xtemp == x) setCell(xtemp, ytemp, wall);
					else if (xtemp == (x+xlen-1)) setCell(xtemp, ytemp, wall);
					else if (ytemp == (y-ylen/2)) setCell(xtemp, ytemp, wall);
					else if (ytemp == (y+(ylen-1)/2)) setCell(xtemp, ytemp, wall);
 
					else setCell(xtemp, ytemp, floor);
				}
			}
			break;
		case 2:
		//south
			for (int ytemp = y; ytemp < (y+ylen); ytemp++){
				if (ytemp < 0 || ytemp > ysize) return false;
				for (int xtemp = (x-xlen/2); xtemp < (x+(xlen+1)/2); xtemp++){
					if (xtemp < 0 || xtemp > xsize) return false;
					if (getCell(xtemp, ytemp) != wm.TILE_UNUSED) return false;
				}
			}
 
			for (int ytemp = y; ytemp < (y+ylen); ytemp++){
				for (int xtemp = (x-xlen/2); xtemp < (x+(xlen+1)/2); xtemp++){
 
					if (xtemp == (x-xlen/2)) setCell(xtemp, ytemp, wall);
					else if (xtemp == (x+(xlen-1)/2)) setCell(xtemp, ytemp, wall);
					else if (ytemp == y) setCell(xtemp, ytemp, wall);
					else if (ytemp == (y+ylen-1)) setCell(xtemp, ytemp, wall);
 
					else setCell(xtemp, ytemp, floor);
				}
			}
			break;
		case 3:
		//west
			for (int ytemp = (y-ylen/2); ytemp < (y+(ylen+1)/2); ytemp++){
				if (ytemp < 0 || ytemp > ysize) return false;
				for (int xtemp = x; xtemp > (x-xlen); xtemp--){
					if (xtemp < 0 || xtemp > xsize) return false;
					if (getCell(xtemp, ytemp) != wm.TILE_UNUSED) return false; 
				}
			}
 
			for (int ytemp = (y-ylen/2); ytemp < (y+(ylen+1)/2); ytemp++){
				for (int xtemp = x; xtemp > (x-xlen); xtemp--){
 
					if (xtemp == x) setCell(xtemp, ytemp, wall);
					else if (xtemp == (x-xlen+1)) setCell(xtemp, ytemp, wall);
					else if (ytemp == (y-ylen/2)) setCell(xtemp, ytemp, wall);
					else if (ytemp == (y+(ylen-1)/2)) setCell(xtemp, ytemp, wall);
 
					else setCell(xtemp, ytemp, floor);
				}
			}
			break;
		}
 
		//yay, all done
		return true;
	}
 
	//and here's the one generating the whole map
	public int[][] createDungeon(int inx, int iny, int inobj){
		
		if (inobj < 1) objects = 20;
		else objects = inobj;
 
		//adjust the size of the map, if it's smaller or bigger than the limits
		if (inx < 3) xsize = 3;
		else if (inx > xmax) xsize = xmax;
		else xsize = inx;
 
		if (iny < 3) ysize = 3;
		else if (iny > ymax) ysize = ymax;
		else ysize = iny;
 
		//redefine the map var, so it's adjusted to our new map size
		dungeon_map = new int[ysize][xsize];
 
		//start with making the "standard stuff" on the map
		for (int y = 0; y < ysize; y++){
			for (int x = 0; x < xsize; x++){
				//ie, making the borders of unwalkable walls
				if (y == 0) setCell(x, y, wm.TILE_WALL);
				else if (y == ysize-1) setCell(x, y, wm.TILE_WALL);
				else if (x == 0) setCell(x, y, wm.TILE_WALL);
				else if (x == xsize-1) setCell(x, y, wm.TILE_WALL);
 
				//and fill the rest with dirt
				else setCell(x, y, wm.TILE_UNUSED);
			}
		}
 
		/*******************************************************************************
		And now the code of the random-map-generation-algorithm begins!
		*******************************************************************************/
 
		//start with making a room in the middle, which we can start building upon
		makeRoom(xsize/2, ysize/2, 12, 10, getRand(0,3)); //getrand saken f????r att slumpa fram riktning p?? rummet
 
		//keep count of the number of "objects" we've made
		int currentFeatures = 1; //+1 for the first room we just made
 
		//then we sart the main loop
		for (int countingTries = 0; countingTries < 1000; countingTries++){
			//check if we've reached our quota
			if (currentFeatures == objects){
				break;
			}
 
			//start with a random wall
			int newx = 0;
			int xmod = 0;
			int newy = 0;
			int ymod = 0;
			int validTile = -1;
			//1000 chances to find a suitable object (room or corridor)..
			//(yea, i know it's kinda ugly with a for-loop... -_-')
			for (int testing = 0; testing < 1000; testing++){
				newx = getRand(1, xsize-2);
				newy = getRand(1, ysize-2);
				validTile = -1;

				if (getCell(newx, newy) == wm.TILE_WALL || getCell(newx, newy) == wm.TILE_FLOOR){
					//check if we can reach the place
					if (getCell(newx, newy+1) == wm.TILE_FLOOR){
						validTile = 0; //
						xmod = 0;
						ymod = -1;
					}
					else if (getCell(newx-1, newy) == wm.TILE_FLOOR){
						validTile = 1; //
						xmod = +1;
						ymod = 0;
					}
					else if (getCell(newx, newy-1) == wm.TILE_FLOOR){
						validTile = 2; //
						xmod = 0;
						ymod = +1;
					}
					else if (getCell(newx+1, newy) == wm.TILE_FLOOR){
						validTile = 3; //
						xmod = -1;
						ymod = 0;
					}
 
					//check that we haven't got another door nearby, so we won't get alot of openings besides
					//each other
					if (validTile > -1){
						if (getCell(newx, newy+1) == wm.TILE_DOOR_CLOSED) //north
							validTile = -1;
						else if (getCell(newx-1, newy) == wm.TILE_DOOR_CLOSED)//east
							validTile = -1;
						else if (getCell(newx, newy-1) == wm.TILE_DOOR_CLOSED)//south
							validTile = -1;
						else if (getCell(newx+1, newy) == wm.TILE_DOOR_CLOSED)//west
							validTile = -1;
					}
 
					//if we can, jump out of the loop and continue with the rest
					if (validTile > -1) break;
				}
			}
			if (validTile > -1){
				//choose what to build now at our  newly found place, and at what direction
				int feature = getRand(0, 100);
				if (feature <= chanceRoom){ //a new room
					if (makeRoom((newx+xmod), (newy+ymod), 8, 6, validTile)){
						currentFeatures++; //add to our quota
 
						//then we mark the wall opening with a door
						if(getRand(0, 6)!=1){
							if(getRand(0, 3)!=1)
								setCell(newx, newy, wm.TILE_DOOR_CLOSED);
							else
								setCell(newx, newy, wm.TILE_FLOOR);
						}else{
							setCell(newx, newy, wm.TILE_SECRET_FLOOR);
						}
 
						//clean up infront of the door so we can reach it
						setCell((newx+xmod), (newy+ymod), wm.TILE_FLOOR);
					}
				}
			}
		}
 
 
		/*******************************************************************************
		All done with the building, let's finish this one off
		*******************************************************************************/
 
		//sprinkle out the bonusstuff (stairs, chests etc.) over the map
		int newx = 0;
		int newy = 0;
		int ways = 0; //from how many directions we can reach the random spot from
		int state = 0; //the state the loop is in, start with the stairs
		int endState = getRand(10,20); //the end of the state 
		while (state != endState){
			for (int testing = 0; testing < 1000; testing++){
				newx = getRand(1, xsize-2);
				newy = getRand(1, ysize-2); //cheap bugfix, pulls down newy to 0<y<24, from 0<y<25

				ways = 4; //the lower the better
 
				//check if we can reach the spot
				if (getCell(newx, newy+1) == wm.TILE_FLOOR){
				//north
					if (getCell(newx, newy+1) != wm.TILE_DOOR_CLOSED)
					ways--;
				}
				if (getCell(newx-1, newy) == wm.TILE_FLOOR){
				//east
					if (getCell(newx-1, newy) != wm.TILE_DOOR_CLOSED)
					ways--;
				}
				if (getCell(newx, newy-1) == wm.TILE_FLOOR){
				//south
					if (getCell(newx, newy-1) != wm.TILE_DOOR_CLOSED)
					ways--;
				}
				if (getCell(newx+1, newy) == wm.TILE_FLOOR){
				//west
					if (getCell(newx+1, newy) != wm.TILE_DOOR_CLOSED)
					ways--;
				}
 
				if (state == 0){
					if (ways == 0){
					//we're in state 0, let's place a "upstairs" thing
						setCell(newx, newy, wm.TILE_WALL);
						state = 1;
						break;
					}
				}				
				else if (state >0 && state <(endState-1)/2){
					//middle state, place a "river"
					//check if we can reach the spot

					if (ways > 0 && ways < 4){
						
						makeRiver(newx, newy,getDirection(newx,newy));
						state++;
						break;
					}
				}
				else if (state >=(endState-1)/2 && state <(endState-1)){

					if (ways == 0){
						enemyPlaceholders.add(new Vector2(newx,newy));
						state++;
						break;
					}
				}
				else if (state == (endState-1)){
					if (ways == 0){
					//state 2, place a "downstairs"
						setCell(newx, newy,  wm.TILE_WALL);
						state = endState;
						break;
					}
				}
			}
		}
 
		return dungeon_map;
	}

	private boolean[] getDirection(int newx, int newy) {

		boolean dirVector[]= {false,false,false,false};
		

		if ((getCell(newx, newy+1) == wm.TILE_FLOOR)&&(getCell(newx, newy+1) != wm.TILE_DOOR_CLOSED)){
		//north
			dirVector[Entity.DIRECTION_UP] = true;
		}
		
		if ((getCell(newx-1, newy) == wm.TILE_FLOOR)&&(getCell(newx-1, newy) != wm.TILE_DOOR_CLOSED)){
		//east
			dirVector[Entity.DIRECTION_RIGHT] = true;
		}
		
		if ((getCell(newx, newy-1) == wm.TILE_FLOOR)&&(getCell(newx, newy-1) != wm.TILE_DOOR_CLOSED)){
		//south
			dirVector[Entity.DIRECTION_DOWN] = true;
		}
		
		if ((getCell(newx+1, newy) == wm.TILE_FLOOR)&&(getCell(newx+1, newy) != wm.TILE_DOOR_CLOSED)){
		//west 
			dirVector[Entity.DIRECTION_LEFT] = true;
		}
		
		return dirVector;
	}

	private void makeRiver (int x, int y,boolean direction[]) {
		
		int directionArray[] = new int[5];
		
		for(int i =0 ;i<direction.length;i++){
			if(direction[i]){
				switch(i){
					case Entity.DIRECTION_UP:{
						directionArray[0]=Entity.DIRECTION_UP;
						directionArray[1]=Entity.DIRECTION_UP;
						directionArray[2]=Entity.DIRECTION_UP;
						directionArray[3]=Entity.DIRECTION_LEFT;
						directionArray[4]=Entity.DIRECTION_RIGHT;				
						break;
					}
					case Entity.DIRECTION_RIGHT:{
						directionArray[0]=Entity.DIRECTION_RIGHT;
						directionArray[1]=Entity.DIRECTION_RIGHT;
						directionArray[2]=Entity.DIRECTION_RIGHT;
						directionArray[3]=Entity.DIRECTION_UP;
						directionArray[4]=Entity.DIRECTION_DOWN;				
						break;
					}
					case Entity.DIRECTION_DOWN:{
						directionArray[0]=Entity.DIRECTION_DOWN;
						directionArray[1]=Entity.DIRECTION_DOWN;
						directionArray[2]=Entity.DIRECTION_DOWN;
						directionArray[3]=Entity.DIRECTION_LEFT;
						directionArray[4]=Entity.DIRECTION_RIGHT;				
						break;
					}
					case Entity.DIRECTION_LEFT:{
						directionArray[0]=Entity.DIRECTION_LEFT;
						directionArray[1]=Entity.DIRECTION_LEFT;
						directionArray[2]=Entity.DIRECTION_LEFT;
						directionArray[3]=Entity.DIRECTION_DOWN;
						directionArray[4]=Entity.DIRECTION_UP;				
						break;
					}
				}
				if(getRand(0,1) ==0)
					break;
			}
		}
		
		int xTemp=x;
		int yTemp=y;
		boolean end = false;
		
		while(!end){
			
			int choice = getRand(0,4);
			
			switch(directionArray[choice]){
				case Entity.DIRECTION_UP:{
					yTemp++;			
					break;
				}
				case Entity.DIRECTION_RIGHT:{
					xTemp++;				
					break;
				}
				case Entity.DIRECTION_DOWN:{
					yTemp--;						
					break;
				}
				case Entity.DIRECTION_LEFT:{
					xTemp--;					
					break;
				}
			}
			
			if (getCell(xTemp, yTemp) == wm.TILE_WALL || getCell(xTemp, yTemp) == wm.TILE_UNUSED )
				end = true;
			else
				setCell(xTemp,yTemp,wm.TILE_LIQUID);	
			
		}
		
		
	}

	public ArrayList<Vector2> getEnemyPlaceholders() {
		return enemyPlaceholders;
	}

	public void setEnemyPlaceholders(ArrayList<Vector2> enemyPlaceholders) {
		this.enemyPlaceholders = enemyPlaceholders;
	}
}