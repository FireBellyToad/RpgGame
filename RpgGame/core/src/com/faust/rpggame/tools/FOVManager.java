package com.faust.rpggame.tools;

import com.badlogic.gdx.math.Vector2;
import com.faust.rpggame.world.WorldManager;

/**
 * 
 * CLASSE PER LA GESTIONE DEI LIVELLI DI GIOCO (SINGLETON)
 *  
 * @author Christoffer Niska <ChristofferNiska@gmail.com>
 * @author Jacopo "Faust" Buttiglieri
 *
*/

public class FOVManager {
		
		private WorldManager wm;
				
	    // Moltiplicatori per convertire coordinate in sezioni
	    protected int[][] multipliers = {
	            {1, 0, 0, -1, -1, 0, 0, 1},
	            {0, 1, -1, 0, 0, -1, 1, 0},
	            {0, 1, 1, 0, 0, -1, -1, 0},
	            {1, 0, 0, 1, -1, 0, 0, -1}
	    };
	
	    //Costruttori
	    public FOVManager(WorldManager wm){

	    	this.wm=wm;
	    }
	    
	    /**
	     * Disegna l'ombra proiettata
	     * 
	     * @param startPos le coordinate x e y di partenza
	     * @param row la colonna della matrice esaminata
	     * @param startSlope Lo slope di partenza
	     * @param endSlope Lo slope di fine
	     * @param radius raggio del campo di visuale
	     * @param xx The xx multiplier.
	     * @param xy The xy multiplier.
	     * @param yx The yx multiplier.
	     * @param yy The yy multiplier.
	     * @param depth profondità attuale della ricorsione
	     */	    
	    private void castShadow(Vector2 startPos, int row, double startSlope, double endSlope, int radius,int xx, int xy, int yx, int yy, int depth) {
	        
	    	if (startSlope >= endSlope)
	        {
	            double radiusSquared = radius * radius;

	            for (int i = row; i <= radius; i++)
	            {
	                int dx = -i - 1;
	                int dy = -i;
	                boolean solid = false;
	                double newStartSlope = 0.0;

	                while (dx <= 0)
	                {
	                    dx++;            	                    

	                    // Translate the dx and dy coordinates.
	                    int x = (int) (startPos.x + dx * xx + dy * xy);
	                    int y = (int) (startPos.y + dx * yx + dy * yy);
	                    
	                    double leftSlope = (dx - 0.5) / (dy + 0.5);
	                    double rightSlope = (dx + 0.5) / (dy - 0.5);

	                    if (startSlope < rightSlope)
	                    {
	                        continue;
	                    }
	                    else if (endSlope > leftSlope)
	                    {
	                        break;
	                    }
	                    else
	                    {
	                        // Mark visible squares.
	                        if (((dx * dx) + (dy * dy)) < radiusSquared)
	                        {
	                            // Make sure that we do not exceed the grid's boundaries.
	                            if ((x >= 0 && x < wm.getLevelSize()) && (y >= 0 && y < wm.getLevelSize()))
	                            {
	                                int tile = wm.getTileAtMapPosition(x, y);

	                                if (tile!= -1)
	                                {
		                                int topTile = wm.getTileAtMapPosition(x, y-1);

                                        if (!wm.isTileViewObstacle(tile) && ((topTile != -1)&&(wm.isTileViewObstacle(topTile))))
                                        {
                                            wm.getRoom().getTileAtPosition(x, y-1).setExplored(true);
                                        }                                    
	                                }

	                                if(wm.getRoom().getTileAtPosition(x, y) != null)
	                                	wm.getRoom().getTileAtPosition(x, y).setExplored(true);
	                            }
	                        }

	                        int tile = wm.getTileAtMapPosition(x, y);
	                        // Previous spot was solid.
	                        if (solid)
	                        {
	                            // We are scanning solid squares.
	                            if (wm.isTileViewObstacle(tile))
	                            {
	                                newStartSlope = rightSlope;
	                                continue;
	                            }
	                            // We are scanning non-solid squares.
	                            else
	                            {
	                                solid = false;
	                                startSlope = newStartSlope;
	                            }
	                        }
	                        // Previous spot was non-solid.
	                        else
	                        {
	                            if (wm.isTileViewObstacle(tile) && i < radius)
	                            {
	                                solid = true;
	                                castShadow(startPos, i + 1, startSlope, leftSlope, radius, xx, xy, yx, yy, depth + 1);

	                                newStartSlope = rightSlope;
	                            }
	                        }
	                    }
	                }

	                if (solid)
	                {
	                    break;
	                }
	            }
	        }
	    }

	    /**
	     * Updates the field of view.
	     * @param cx The current x-coordinate.
	     * @param cy The current y-coordinate.
	     */
	    public void refreshFOV(Vector2 currentPos,int radius)
	    {
	        int section = 0;
	        wm.getRoom().createFog();
	        
	        if (currentPos.x >= 0 && currentPos.x <  wm.getLevelSize() && currentPos.y >= 0 && currentPos.y < wm.getLevelSize())
	        {

	        	wm.getRoom().getTileAtPosition(currentPos).setExplored(true);
	        	
	            while (section < 8) // there are always eight sections
	            {
	                this.castShadow(currentPos, 1, 1.0, 0.0, radius,
	                        multipliers[0][section], multipliers[1][section],
	                        multipliers[2][section], multipliers[3][section], 0);

	                section++; // move to the next section
	            }
	        }
	    }

	}
