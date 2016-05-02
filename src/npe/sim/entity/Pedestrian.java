package npe.sim.entity;
import java.awt.Graphics2D;
import java.awt.MediaTracker;
import java.awt.geom.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import npe.sim.Sprite;
import npe.sim.Utils;
import npe.sim.road.Box;
import npe.sim.road.PedestrianLane;
import static npe.sim.Utils.*;

/**
 * Represents a pedestrian in the simulation.
 */

public class Pedestrian extends Entity {
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**Contains all the possible states a pedestrian can be in.*/
	public enum State
	{
		ENTERING,
		STOPPED,
		EXITING;
	}
	
	public static final double PEDESTRIAN_WIDTH = 15;
	public static final double PEDESTRIAN_HEIGHT = 15;
	/** The maxiumum speed of a pedestrian */
	public static final double MAX_SPEED = 9;
	/** The minimum speed of a pedestrian */
	public static final double MIN_SPEED = 6;
	
	private static ArrayList<String> sprites;
	/** The speed of the pedestrian */
	private double speed ;
	private PedestrianLane lane;
	private State state;
	
	private String spriteName;
	/** Boolean to dertermine if the car is in a box */
	private boolean inBox = false;
	
	public double stoppingPoint = x();
		
	//////////////////
	//---CREATION---//
	//////////////////
	public Pedestrian(double x, double y, double direction, PedestrianLane lane)
	{
		super(x,y,PEDESTRIAN_WIDTH,PEDESTRIAN_HEIGHT);
		this.setDirection(direction);
		this.lane = lane;
		state = State.ENTERING;
		//the number of intervals of different speeds a pedestrian has
		double intervals = 30;
		//the different of the speeds, used for calculate random speed
		double speedDif = MAX_SPEED - MIN_SPEED;
		speed = convertSpeed((double) ( Utils.random((int) ( speedDif * intervals)) ) * 1/intervals + MIN_SPEED);
		if(debugging){
			setSprite("pedestrians/testDummy.jpg");
		}else{
			chooseSprite();
		}
		
		setBox(lane.getBox());
		
	}
	
	/////////////////
	//---SPRITES---//
	/////////////////
	/**Selects a random pedestrian sprite to use for this pedestrian.*/
	private void chooseSprite()
	{
		//Ensure pedestrian sprites exist
		if (sprites.isEmpty()) {
			System.err.println("ERROR: No pedestrian sprites have been loaded");
			return;
		}

		//Select a random sprite
		int index = (int) Math.floor( Math.random()*sprites.size() );
		spriteName = sprites.get(index);
		setSprite("pedestrians/"+spriteName);
	}
	/**Loads all the pedestrian sprites into memory.*/
	public static void loadSprites(MediaTracker m)
	{
		sprites = new ArrayList<String>();
		
		//Get list of files in pedestrian sprite directory
		File folder = new File("res/pedestrians");
		String[] files = folder.list();
		
		//Load all .gif files from the directory
		for(String fileName : files){
			if (fileName.endsWith(".gif")) {
				try {
					if (fileName.endsWith("Walk1.gif")) {
						sprites.add(fileName);
					}
					new Sprite("pedestrians/"+fileName, m);
				} catch (FileNotFoundException e) {
					System.err.println("ERROR: Unable to load pedestrian sprite "+fileName);
				}
			}
		}
		
		//Ensure at least one sprite was loaded
		if (sprites.isEmpty()) {
			System.err.println("ERROR: Unable to load any pedestrian sprites");
		}
	}
	
	//////////////
	//---LOOP---//
	//////////////
	/**Controls pedestrian movement.*/
	public void tick()
	{
		super.tick();
		
		switch(state) {
		case ENTERING:
			if (speed() == 0) {
				setSpeed(speed);
			}
			break;
		case STOPPED:
			if (speed() > 0) {
				setSpeed(0);
			}
			//Remain in same state
			break;
		case EXITING:
			if (speed() == 0) {
				setSpeed(speed);
			}
			//turn();
			//Remain in same state
			break;
		}
		
		// Check to see if enity has entered a box
		Box box = getBox();
		if( box != null){
		//check to see if the box contains the vehicle, and check ot see if the pedsetrian is already in the box
		//if so add it to the box
			if( box.insideBox(this) && !inBox && state == State.EXITING){ 
				box.addEntity(this);
				inBox = true;
			}
		}

		
		//Check for boundary crossing
		if ((x() < lane.road().getIntersection().getMinX() || x() > lane.road().getIntersection().getMaxX()) || (y() < lane.road().getIntersection().getMinY() || y() > lane.road().getIntersection().getMaxY())) {
			lane.removePedestrian(this);
		}
		
		
	}
	/**Draws the Pedestrian on the screen.
	 * @param g Graphics context to draw on.
	 */
	public void draw(Graphics2D g)
	{
		//Draw this Pedestrian
		super.draw(g);
	}
	
	/////////////////
	//---CONTROL---//
	/////////////////
	/**Starts the pedestrian walking in a straight line.*/	
	public void start()
	{
		if (state == State.STOPPED) {
			state = State.EXITING;
			setSprite("pedestrians/"+spriteName);
		}
		//if the pedestrian is in the entering state, and never stops, it won't go into the exiting phase.
		else if ( state == State.ENTERING) {
			if(isInArea(lane.getStoppingArea())){
				state = State.EXITING;
			}
		}
		setSpeed(speed);
	}
	/**Stops the pedestrian instantly.*/
	public void stop()
	{
		if (state == State.ENTERING) {
			setSpeed(0);
			state = State.STOPPED;
			setSprite("pedestrians/"+spriteName.substring(0,spriteName.indexOf("Walk1.gif")) + "Stop1.gif");
		}
	}
	/**Checks to see if the pedestrian is stopped.
	 * @return True if the pedestrian is stopped.
	 */
	public boolean isStopped()
	{
		return speed()==0;
	}
	
	////////////////
	//---ACCESS---//
	////////////////
	public State getState()
	{
		return state;
	}

	public void changeState(State state)
	{
		this.state = state;
	}
	/**
	 * Checks if the pedestrian is in the stopping area
	 * @Parm : Area the area where pedestraisn are told to stop
	 */
	public boolean isInArea(Rectangle2D Area)
	{
		Point2D.Double point = new Point2D.Double( x(), y() );
		return Area.contains(point);
	}

}
