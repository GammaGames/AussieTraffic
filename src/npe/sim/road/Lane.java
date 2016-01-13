package npe.sim.road;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import npe.sim.entity.Entity;

/**
 * An abstraction for any lane which contains and handles entities.
 */

public abstract class Lane {
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**What road the lane belongs to.*/
	private Road road;
	/** x and y position of the lane.*/
	private double x;
	private double y;
	/**The length of the lane.*/
	private double length;
	/**The direction of traffic in the lane in degrees.*/
	private double direction;
	/**The direction of traffic in the lane in radians.*/
	private double directionRad;
	/** The box that all entities in the lane will enter, used for turn right with care */
	private Box box;

	
	/**Constructor.*/
	public Lane(Road road, double x, double y, double direction) 
	{
		this.road = road;
		this.x = x;
		this.y = y;
		length = road.getLength();
		this.direction = direction;
		directionRad = Math.toRadians(direction);
	}

	/**
	 * Draws the lane

	 * @param g Graphics
	 */
	public void draw(Graphics2D g)
	{ 
		//Any General drawing for a lane goes here
	}
	
	////////////////
	//---ACCESS---//
	////////////////
	/**Provides access to the road the lane belongs to.
	 * @return The lane's road.
	 */
	public Road road() 
	{
		return road;
	}
	/**Provides access to the lane's x-coordinate.
	 * @return The lane's x-coordinate.
	 */
	public double x() 
	{
		return x;
	}
	/**Provides access to the lane's y-coordinate.
	 * @return The lane's y-coordinate.
	 */
	public double y() 
	{
		return y;
	}
	/**Provides access to the lane's length.
	 * @return The lane's length.
	 */
	public double length() 
	{
		return length;
	}
	/**Provides access to the lane's direction (in radians).
	 * @return The lane's direction in radians.
	 */
	public double dirRad()
	{
		return directionRad;
	}
	/**Provides access to the lane's direction (in degrees).
	 * @return The lane's direction in degrees.
	 */
	public double dirDeg()
	{
		return direction;
	}
	
	public void setBox(Box box) {
		this.box = box;
	}
	/**
	 * Getter method for box, this is put in lane, becuase both pedestrians and vehicles use it
	 * @return The box
	 */
	public Box getBox(){
		return box;
	}

	public abstract void addEntity(Entity e) ;
	

	
}
