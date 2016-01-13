package npe.sim.road;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import npe.sim.Utils;
import npe.sim.entity.Entity;
import npe.sim.entity.Pedestrian;

/** An instance of a Lane which handles Pedestrians.
 */
public class PedestrianLane extends Lane {
	///////////////////
	//---CONSTANTS---//
	///////////////////
	public static final double LANE_WIDTH = 50;
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**An arraylist of all the pedestrians in this lane.*/
	private ArrayList<Pedestrian> pedestrians;
	/**A rectangle which defines the area in which pedestrians stop and wait in.*/
	private Rectangle2D stoppingArea;
	/**The cosine of the direction, used to reduce number of calls to Math.cos.*/
	private double cosDir = 0;
	/**The sine of the direction, used to reduce number of calls to Math.sin.*/
	private double sinDir = 0;
	
	//////////////////
	//---CREATION---//
	//////////////////
	public PedestrianLane(Road road, double x, double y, double direction)
	{
		super(road, x, y, direction);
		
		cosDir = Math.cos(Math.toRadians(direction));
		sinDir = Math.sin(Math.toRadians(direction));
		
		pedestrians = new ArrayList<Pedestrian>(50);
	
		double stopX = x() + length()*cosDir - LANE_WIDTH*(int)((cosDir - 1)/-2) - LANE_WIDTH*(int)((sinDir + 1)/2);
		double stopY = y() + length()*sinDir - LANE_WIDTH*(int)((cosDir - 1)/-2) - LANE_WIDTH*(int)((sinDir - 1)/-2);
		
		stoppingArea = new Rectangle2D.Double(stopX, stopY, LANE_WIDTH, LANE_WIDTH);
		
	}
	
	//////////////
	//---LOOP---//
	//////////////
	public void tick()
	{
		for (int i = 0; i < pedestrians.size(); i++) {
			Pedestrian p = pedestrians.get(i);
			
			if (p != null) {
				p.tick();
			}
		}
	}
	
	/** Draws the lane
	 * @param g
	 */
	public void draw(Graphics2D g)
	{ 
		super.draw(g);
		
		double xRec = x() - length()*(int)((cosDir - 1)/-2) - LANE_WIDTH*(int)((sinDir + 1)/2);
		double yRec = y() - LANE_WIDTH*(int)((cosDir - 1)/-2) - length()*(int)((sinDir - 1)/-2);
		
		Rectangle2D lane = new Rectangle2D.Double(xRec, yRec, Math.abs(length()*cosDir + LANE_WIDTH*sinDir), Math.abs(length()*sinDir + LANE_WIDTH*cosDir));
		
		g.setColor(Color.LIGHT_GRAY);
		g.fill(lane);
	}
	
	/** Draws all the pedestrians in the lane
	 * @param g
	 */
	public void drawPedestrians(Graphics2D g) 
	{
		for (int i = 0; i < pedestrians.size(); i++) {
			Pedestrian p = pedestrians.get(i);
			
			if (p != null) {
				p.draw(g);
			}
		}
	}
	
	/** Sends message to all the pedestrians to start
	 * 
	 */
	public void start() 
	{
		for (int i = 0; i < pedestrians.size(); i++) {
			Pedestrian p = pedestrians.get(i);
			
			if (p != null) {
				p.start();
			}
		}
	}
	
	/** Sends message to all the pedestrians to stop
	 * THIS SHOULDNT GET CALLED -> Adam: why shouldnt this get called, we want to tell the pedestrian to stop when they don't have a green light
	 */
	public void stop() 
	{
		for (int i = 0; i < pedestrians.size(); i++) {
			Pedestrian p = pedestrians.get(i);
			
			if (p != null) {
				if(p.isInArea(stoppingArea)){
					p.stop();
				}
			}
		}
	}
		
	//////////////////////
	//---MODIFICATION---//
	/////////////////////
	/** Adds a pedestrian to a randomized location at the start of the lane
	 * 
	 */
	public void addPedestrian()
	{
		//Randomize where pedestrians are spawned at the start of the lane
		double x = x() - Utils.random()*LANE_WIDTH*sinDir;
		double y = y() + Utils.random()*LANE_WIDTH*cosDir;
		
		if (x == x()) {
			x -= 7.5*sinDir;
		} else if (x == x() -LANE_WIDTH*sinDir) {
			x += 7.5*sinDir;
		}
		if (y == y()) {
			y += 7.5*cosDir;
		} else if (y == y() +LANE_WIDTH*cosDir) {
			y -= 7.5*cosDir;
		}
		
		Pedestrian p = new Pedestrian(x, y, dirDeg(), this);
		
		if (!pedestrians.add(p)) {
			System.out.println("Failed to add Pedestrian");
		}
	}
	
	/** A method for removing a pedestrian from this lanes list of pedestrians
	 *TODO as pedestrians ar an arraylist, do we realise that this is really inefficient to remove them like this.
	 * @param Pedestrian, the Pedestrian to remove
	 */
	public void removePedestrian(Pedestrian p) 
	{
		int index = 0;
		
		index = pedestrians.indexOf(p);
		
		if (index != -1) {
			pedestrians.remove(index);
		} else {
			System.out.println("ERROR: Pedestrian does not exist in the array list");
		}
	}
	
	public Rectangle2D getStoppingArea(){
		return stoppingArea;
	}
	
	public void addEntity(Entity e){
		if(e instanceof Pedestrian) {
			Pedestrian p = (Pedestrian) e;
			if (!pedestrians.add(p)) {
				System.out.println("Failed to add Pedestrian");
			}
		} else {
			System.err.println("Failed to add entity to lane, Entity was not an instance of pedestrian");
		}
	}
	


}
