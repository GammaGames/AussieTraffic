/**
 * 
 */
package npe.sim.road;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Queue;

import npe.sim.entity.Entity;
/**
 
 *
 */
public class Box {
	
	/** then x coordinate for the start of the box */
	private int x1;
	/** the x coordinate for the end of the box */
	private int x2;
	/** The y coordinate for the start of the box */
	private int y1;
	/** The y coordinate for the end of the box */
	private int y2;

	/** An arraylist of entities in the box */
	Queue<Entity> entities = new LinkedList<Entity>();
	
	public Box(){
	}
	
	public void setCoords(int x1, int x2, int y1, int y2){
		//set the coordinates of the box, if make sure x1 < x2 and y1 < y2
		
		if ( x1 < x2) {
			this.x1 = x1;
			this.x2 = x2;			
		} else {
			this.x2 = x1;
			this.x1 = x2;			
		}
		if ( y1 < y2 ) {
			this.y1 = y1;
			this.y2 = y2;
		} else {
			this.y1 = y2;
			this.y2 = y1;
		}
	}
	
	/**
	 * Method for adding an entity to a box
	 * @param entity The entity to be added to the box
	 */
	public void addEntity(Entity entity){
		entities.offer(entity);
	}
	
	/**
	 * Remove the head of the entity list
	 */
	private void removeEntity(){
		entities.remove();
	}
	
	/**
	 * Method to determine whether a car is inside of abox
	 * @param entity The enitity that we will be checking
	 * @return True if the enity is insed of the box
	 */
	public boolean insideBox(Entity entity){
		//get x and y pos
		double x = entity.x();
		double y = entity.y();
		return ( x > x1 && x < x2 && y > y1 && y < y2);
	}
	
	/**
	 * Garbage collect the box, remove all cars that are no longer inside of the box. This may get moved to a tick phase
	 */
	private void cleanUp() {
		while( !entities.isEmpty() ){
			if( ! (insideBox(entities.peek())) ){
				removeEntity();
			} else {
				break;
			}
		}
	}
	
	/**
	 * Check to see if the box is empty, if it is it's safe to turn.
	 * @return True if nthe box is empty
	 */
	public boolean isEmpty() {
		cleanUp();
		return entities.isEmpty();
	}
	
	public String toString() {
		return "x1: " + x1 + " x2: " + x2 + " y1: " + y1 + " y2: " + y2;
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		Rectangle2D rect = new Rectangle2D.Double(x1,0, x2-x1, 4000);
		g.draw(rect);
	}

	
	
}
