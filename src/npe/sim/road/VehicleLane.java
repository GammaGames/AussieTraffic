package npe.sim.road;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import npe.sim.entity.Entity;
import npe.sim.entity.Vehicle;
import npe.sim.entity.Vehicle.Intention;
import npe.sim.entity.Vehicle.State;
import npe.sim.results.StatsCollector;
import npe.sim.entity.Bus;
import npe.sim.Sprite;
import java.io.FileNotFoundException;

/** An instance of a Lane which handles Vehicles.

 */

public class VehicleLane extends Lane {

	public enum Type 
	{
		LEFT( new int[] {0} ) ,
		LEFT_STRAIGHT( new int[] {0,1} ),
		LEFT_RIGHT( new int[]{0,2} ),
		RIGHT(new int[]{2}),
		RIGHT_STRAIGHT(new int[]{1,2}),
		STRAIGHT(new int[]{1}),
		//lanes on the other side of the road (a road only has 2 sides)
		LEFT_(new int[]{3}),	//redundant
		LEFT_STRAIGHT_(new int[]{3,4}),
		LEFT_RIGHT_(new int[]{3,5}),
		RIGHT_(new int[]{5}),	
		RIGHT_STRAIGHT_(new int[]{4,5}),
		STRAIGHT_(new int[]{4});
		
		int[] lights;
		private Type(int[] dir) 
		{
			lights = dir;
		}
		public int[] getLights()
		{
			return lights;
		}
	}
	
	///////////////////
	//---CONSTANTS---//
	///////////////////
	public static final double LANE_WIDTH = 35;
	//private static final double TAKE_OFF_ACCEL = Intersection.convertAcceleration(5);
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	private double speedLimit;
	/**The type of this lane (indicates Vehicle intentions and whether it is an entry lane or exit lane.*/
	private final Type laneType;
	/**The first vehicle in this lane.*/
	private Vehicle firstVehicle;
	/** The last vehicile in the lane */
	private Vehicle lastVehicle;
	/**A list of lanes that cars in this lane will turn into when leaving the intersection.*/
	private ArrayList<VehicleLane> exitLanes;
	//---Lane Entry and Exit points---//
	/**The entry point is the point at which Vehicles move into the lane.*/
	private double entryX;
	private double entryY;
	/**The exit point is the point at which Vehicles move out of the lane.*/
	private double exitX;
	private double exitY;
	/**A boolean that represents when the lane is full or not.*/
	private boolean laneFull = false;
	/**The cosine of the direction, used to reduce number of calls to Math.cos.*/
	private double cosDir = 0;
	/**The sine of the direction, used to reduce number of calls to Math.sin.*/
	private double sinDir = 0;
	
	/**Drawing.*/
	private Stroke dotStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{9}, 0);
	private Line2D laneLine;
	

	/** The lane left of this lane. If this is the leftmost lane on a road it will be null */
	private VehicleLane leftLane;
	/** The lane right of this lane. If this is the rightmost lane on a road it will be null */
	private VehicleLane rightLane;
	
	private StatsCollector statsCollector;
 
	//////////////////
	//---CREATION---//
	//////////////////
	public VehicleLane(Type laneType, Road road, double x, double y, double direction, StatsCollector st) 
	{
		super(road, x, y, direction);
		statsCollector = st;
		
		this.laneType = laneType;
		speedLimit = road.getSpeedLimit();
		
		//Set the entry and exit points of entry lanes
		sinDir = Math.sin(dirRad());
		cosDir = Math.cos(dirRad());
		
		laneLine = new Line2D.Double(x(), y(), x() + length()*Math.cos(dirRad()), y() + length()*Math.sin(dirRad()));
		
		entryX = x() - (LANE_WIDTH/2)*sinDir;
		entryY = y() + (LANE_WIDTH/2)*cosDir;
		exitX = x() + road.getLength()*cosDir - (LANE_WIDTH/2)*sinDir;
		exitY = y() + road.getLength()*sinDir + (LANE_WIDTH/2)*cosDir;
		
		switch(laneType) { 
			case LEFT :
			case LEFT_STRAIGHT :
			case STRAIGHT :
			case RIGHT_STRAIGHT : 
			case RIGHT : 
				break;
			//If the lane is an exit lane, we swap entry and exit points
			case RIGHT_ :
			case RIGHT_STRAIGHT_ :
			case STRAIGHT_ :
			case LEFT_STRAIGHT_ :
			case LEFT_ :
				//Swap x
				double temp = entryX;
				entryX = exitX;
				exitX = temp;
				//Swap y
				temp = entryY;
				entryY = exitY;
				exitY = temp;
				break;
		}
		exitLanes = new ArrayList<VehicleLane>();
	}
	
	//////////////
	//---LOOP---//
	//////////////
	public void tick()
	{
		if (firstVehicle != null) {
			//Check if lane is full
			if (((Math.abs(lastVehicle.x() - x()) < Bus.WIDTH + Vehicle.MAX_BUFFER) && (Math.abs(lastVehicle.y() - y()) < Bus.WIDTH ))) {
				laneFull = true;
			} else {
				laneFull = false;
			}
			
			//Check for boundary crossing
			if( leftIntersection() ) {
				firstVehicle.removeFromCollisionList();
				firstVehicle.deletingVehicle();
				removeVehicle(firstVehicle);
				
			}
			
			if (firstVehicle != null) {
				firstVehicle.tick();
			}
		} else {
			laneFull = false;
		}
		
	}
	
	/** 
	 * Check to see if the first vehicle in the lane has left the intersection
	 * @return True if the first vehicle is out of the bounds of the intersection
	 */
	private boolean leftIntersection()
	{
		return (firstVehicle.x() < road().getIntersection().getMinX() || firstVehicle.x() > road().getIntersection().getMaxX()) || (firstVehicle.y() < road().getIntersection().getMinY() || firstVehicle.y() > road().getIntersection().getMaxY());
		
	}

	/** Draws the lane
	 * @author David
	 * @param g Graphics
	 */
	public void draw(Graphics2D g)
	{ 
		super.draw(g);
		
		g.setColor(Color.white);
		g.setStroke(dotStroke);
		g.draw(laneLine);
		
		String arrow = laneType.toString().toLowerCase();
		if(!arrow.endsWith("_")){
			
			try{
				/*
				int xpos = (int)Math.cos(dirRad())*150 + (int)Math.sin(dirRad())*15;
				int ypos = (int)Math.cos(dirRad())*15 + (int)Math.sin(dirRad())*150 ;
				int rotationXPos = (int)Math.cos(dirRad())*100;
				int rotationYPos = (int)Math.sin(dirRad())*100;
				System.out.printf("type : %s\n",arrow);
				System.out.printf("xpos : %d\n",xpos);
				System.out.printf("ypos : %d\n",ypos);
				System.out.printf("rxpos : %d\n",rotationXPos);
				System.out.printf("rtpos : %d\n",rotationYPos);
				System.out.printf("dir : %.0f\n",dirDeg());
				System.out.println();
				//System.out.printf("type : %s, dir : %.0f, pos : %d, rotationpos : %d cos : %.0f, sin : %.0f\n",arrow,dirDeg(),xpos,rotationXPos,Math.cos(dirRad()),Math.sin(dirRad()));
				//new Sprite(String.format("road_arrows/%s.gif",arrow)).draw(g,exitX-xpos,exitY-ypos,dirDeg(),exitX-rotationXPos,exitY-rotationYPos);
				//new Sprite(String.format("road_arrows/%s.gif",arrow)).draw(g,exitX-150,exitY-15,dirDeg(),exitX-100,exitY);
				if(Math.sin(dirRad()) == 0){
					//we are on north tce
					new Sprite(String.format("road_arrows/%s.gif",arrow)).draw(g,exitX-150*Math.cos(dirRad()),exitY-15*Math.cos(dirRad()));
					g.setColor(Color.RED);
					g.setStroke(new BasicStroke(1));
					g.draw(new Line2D.Double(exitX,exitY,exitX-150*Math.cos(dirRad()),exitY-15*Math.cos(dirRad())));
				} else {
					new Sprite(String.format("road_arrows/%s.gif",arrow)).draw(g,exitX-15*Math.sin(dirRad()),exitY-150*Math.sin(dirRad()));
				}*/
				//David : replace this with no switch statement :/
				Sprite sp = new Sprite(String.format("road_arrows/%s.gif",arrow));
				switch((int)dirDeg()){
					case 0 : 
						sp.draw(g,exitX-150,exitY-15);
						break;
					case 90 : 
						sp.draw(g,exitX+15,exitY-150,dirRad(),exitX+15,exitY-150);
						break;
					case 180 : 
						sp.draw(g,exitX+150,exitY-15,-dirRad(),exitX+150,exitY);
						break;
					case 270 : 
						sp.draw(g,exitX-15,exitY+150,dirRad(),exitX-15,exitY+150);
						break;
				}
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	/** Draws all the cars in the lane
	 * @param g
	 */
	public void drawVehicles(Graphics2D g) 
	{
		if (firstVehicle != null) {
			firstVehicle.draw(g);
		}
	}

	/** Sends message to first vehicle in lane to start
	 * @param Intention, the start message will apply to cars with Intention dir
	 */
	public void start(Intention dir) 
	{
		if (firstVehicle != null) {
			firstVehicle.start(dir);
		} 
	}
	
	/** Sends message to first vehicle in lane to stop
	 * @param Intention, the stop message will apply to cars with Intention dir
	 */
	public void stop(Intention dir) 
	{
		if (firstVehicle != null) {
			firstVehicle.stop(exitX, exitY, dir);
		}		
	}
	
	////////////////
	//---ACCESS---//
	////////////////
	/**Get the first vehicle in the lane
	 * @return the firstVehicle
	 */
	public Vehicle getFirstVehicle() 
	{
		return firstVehicle;
	}
	
	/**Get the last vehicle in the lane
	 * @return lastVehicle
	 */
	public Vehicle getLastVehicle() 
	{
		return lastVehicle;
	}

	/**Get the Type of the VehicleLane
	 * @return the laneType
	 */
	public Type getType() 
	{
		return laneType;
	}

	/**Get the x position of the exit point of this lane
	 * @return the x coordinate of the exit point
	 */
	public double getExitX()
	{
		return exitX;
	}

	/**Get the y position of the exit point of this lane
	 * @return the y coordinate of the exit point
	 */
	public double getExitY() 
	{
		return exitY;
	}

	/**Get the x position of the entry point of this lane
	 * @return the x coordinate of the entry point
	 */
	public double getEntryX() 
	{
		return entryX;
	}
	
	/**Get the y position of the entry point of this lane
	 * @return the y coordinate of the entry point
	 */
	public double getEntryY() 
	{
		return entryY;
	}

	/**Get the lanes that Vehicles of this lane exit into
	 * @return An arraylist of exit lanes
	 */
	public ArrayList<VehicleLane> getExitLanes() 
	{
		return exitLanes;
	}
	
	public double getSpeedLimit()
	{
		return speedLimit;
	}
	
	//////////////////////
	//---MODIFICATION---//
	/////////////////////
	/** Set the first vehicle in this lane
	 * This will set the first vehicle prev vehicle to null, as the first vehicle must always have null as its first vehicel
	 * @param firstVehicle the firstVehicle to set
	 */
	private void setFirstVehicle(Vehicle firstVehicle) 
	{
		this.firstVehicle = firstVehicle;
		if( firstVehicle != null ) {
			firstVehicle.setPrevVehicle(null);
		}
	}
	
	/**
	 * Set the last vehicle of the lane
	 * Also set that vehicles next vehicle to null
	 * @param lastVehicle The vehicle that is the new last vehicle
	 */
	private void setLastVehicle(Vehicle lastVehicle) 
	{
		this.lastVehicle = lastVehicle;
		if( lastVehicle != null ) {
			lastVehicle.setNextVehicle(null);
		}
	}
	
	/** A method for removing vehicle from this lanes list of vehicles
	 * @param Vehicle, the vehicle to remove
	 */
	public void removeVehicle(Vehicle vehicle) 
	{
		Vehicle prev = vehicle.getPrevVehicle();
		Vehicle next = vehicle.getNextVehicle();
		if( prev == null ) {
			setFirstVehicle(next);
		} else {
			prev.setNextVehicle(next);
		}
		if( next == null ) {
			setLastVehicle(prev);
		} else {
			next.setPrevVehicle(prev);
		}
	}
	
	/** A method for inserting vehicles to the end of the vehicle list
	 * This method is part of the functionality of moving cars from entry lanes to exit lanes
	 * @param newVehicle, the vehicle to insert
	 */
	public void insertVehicle(Vehicle newVehicle)
	{
		if (newVehicle == null) {
			System.err.println("Attempt to insert a null vehicle into lane");
			return;
		}
		//While inserting a vehicle into the end of the list, temporarily set lane full to true
		laneFull = true;
		
		newVehicle.setCurrentLane(this);
		//Check if list is empty
		if (lastVehicle == null) {
			//Set vehicle to the first vehicle in the list
			setFirstVehicle( newVehicle );
			setLastVehicle ( newVehicle );
		} else {
				lastVehicle.setNextVehicle(newVehicle);
				newVehicle.setPrevVehicle(lastVehicle);
				newVehicle.setNextVehicle(null);
				setLastVehicle( newVehicle );
		}
	}
	
	/** A method for inserting vehicles behind another vehicle
	 * This method is part of the functionality of moving cars from one lane to another (changing lanes)
	 * @param newVehicle, the vehicle to insert
	 * @param prevVehicle, the vehicle that will reference the new vehicle
	 */
	public void insertVehicle(Vehicle newVehicle, Vehicle nextVehicle)
	{
		if (newVehicle == null) {
			System.out.println("Attempt to insert a null vehicle into lane");
			return;
		}
		newVehicle.setCurrentLane(this);
		//if prev vehicle is null, we are at the start of this list, therefore new Vehicle = firstVehicle
		if( nextVehicle.getPrevVehicle() == null ) {
			setFirstVehicle(newVehicle);
			newVehicle.setNextVehicle(nextVehicle);
			nextVehicle.setPrevVehicle(newVehicle);
		} else {
			Vehicle prevVehicle = nextVehicle.getPrevVehicle();
			newVehicle.setNextVehicle(nextVehicle);
			newVehicle.setPrevVehicle(prevVehicle);
			nextVehicle.setPrevVehicle(newVehicle);
			prevVehicle.setNextVehicle(newVehicle);
		}
	}
	
	/**Add an exit lane to the current lane.
	 * @param VehicleLane, a lane in which cars in this lane will exit onto
	 */
	public void addExitLane(VehicleLane exitLane)
	{
		exitLanes.add(exitLane);
	}

	@Override
	public void addEntity(Entity e) {
		if( e instanceof Vehicle ) {
			insertVehicle((Vehicle)e);
		}
		//No vehicles in lane, make the car the first vehicle

	}

	public boolean laneFull() {
		return laneFull;
	}
	
	/**
	 * Set the lane left of this lane
	 * @param lane The left lane to set
	 */
	public void setLeftLane(VehicleLane lane){
		leftLane = lane;
	}

	/**
	 * Get lane left of this lane
	 * @return The lane left of this lane
	 */
	public VehicleLane getLeftLane()
	{
		return leftLane;
	}
	
	/**
	 * Set the lane right of this lane
	 * @param lane The lane right of this lane
	 */
	public void setRightLane(VehicleLane lane)
	{
		rightLane = lane;
	}
	
	/**
	 * Get the lane right of this lane
	 * @return The lane right of this lane, If this is the rightmost lane, return null
	 */
	public VehicleLane getRightLane()
	{
		return rightLane;
	}

	/**
	 * Get the stats collector for this lane
	 */
	public StatsCollector getStatsCollector()
	{
		return statsCollector;
	}
	/**Method to get the cosine of the lane's direction without an extra call to Math.cos
	 * @return cosDir The cosine of the lane's direction
	 */
	public double cosDir() 
	{
		return cosDir;
	}
	/**Method to get the sine of the lane's direction without an extra call to Math.sin
	 * @return sinDir The sine of the lane's direction
	 */
	public double sinDir() 
	{
		return sinDir;
	}
	
	/** Check to see if the lane is empty 
	 * @return True if there are no vehicles in the lane
	 */
	public boolean isEmpty() 
	{
		return firstVehicle == null;
	}
	
	
	
}
