package npe.sim.entity;
import java.awt.MediaTracker;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import npe.sim.Sprite;
import npe.sim.Utils;
import npe.sim.road.VehicleLane;
import static npe.sim.Utils.*;

/**
 * Represents a single car in the simulation.
 * A concrete cubclass of vehicle, with behaviour specific to cars.
 */

public class Car extends Vehicle {
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**The width of the car.*/
	private static final int WIDTH = 40;
	/**The height of the car.*/
	private static final int HEIGHT = 25;
	/**The acceleration of a car when taking off.*/
	private static final double ACCEL = convertAcceleration(5);
	/**A list of available car sprites.*/
	private static ArrayList<Sprite> sprites;	

	private final double LANE_CHANGE_PROB = 0.2;
	//////////////////
	//---CREATION---//
	//////////////////
	/**Creates a car at the given position.*/
	public Car(double x, double y, double direction, VehicleLane lane, Intention intent)
	{
		super(x, y, WIDTH, HEIGHT,direction, ACCEL, lane, intent);
		
		if(debugging){
			setSprite("cars/testDummy.jpg");
		}else{
			chooseSprite();
		}
		//if the cars intention is straight give it a probability of being a lane changing car
		if( intent == Intention.STRAIGHT ) {
			if( Utils.random() < LANE_CHANGE_PROB) {
				//if the left lane is the leftmost lane, we dont want to change into it
				//if the right lane is the rightmost lane, we dont want to change into it
				// if its the left most lane it must change lanes to the right
				if( leftMostLane(lane)){
					setLaneChange(Vehicle.LaneChange.RIGHT);
				} else if (rightMostLane(lane)){
					setLaneChange(Vehicle.LaneChange.LEFT);
				}else if( leftMostLane( lane.getLeftLane() ) && rightMostLane( lane.getRightLane() ) ) {
					setLaneChange(Vehicle.LaneChange.STRAIGHT);
				} else if (leftMostLane( lane.getLeftLane() )) {
					setLaneChange(Vehicle.LaneChange.RIGHT);
				} else if ( rightMostLane( lane.getRightLane() ) ) {
					setLaneChange(Vehicle.LaneChange.LEFT);
					//if it doesnt matter which way it changes lanes, do a 50 / 50
				} else {
					if( Utils.random() <= 0.5) {
						setLaneChange(Vehicle.LaneChange.RIGHT);
					} else {
						setLaneChange(Vehicle.LaneChange.LEFT);
					}
				}
			}
		//if not going straight, dont change its lanes, so set lane change to straight
		} else {
			setLaneChange(Vehicle.LaneChange.STRAIGHT);
		}
	}
	/**Selects a random car sprite to use for this car.*/
	private void chooseSprite()
	{
		//Ensure car sprites exist
		if (sprites.isEmpty()) {
			System.err.println("ERROR: No car sprites have been loaded");
			return;
		}
		
		//Select a random sprite
		int index = (int) Math.floor( Math.random()*sprites.size() );
		setSprite( sprites.get(index) );
	}
	/**Loads all the car sprites into memory.*/
	public static void loadSprites(MediaTracker m)
	{
		sprites = new ArrayList<Sprite>();
		
		//Get list of files in car sprite directory
		File folder = new File("res/cars");
		String[] files = folder.list();
		
		//Load all car*.gif files from the directory
		for(String fileName : files){
			if (fileName.endsWith(".gif")) {
				try {
					sprites.add( new Sprite("cars/"+fileName, m) );
				} catch (FileNotFoundException e) {
					System.err.println("ERROR: Unable to load car sprite "+fileName);
				}
			}
		}
		
		//Ensure at least one sprite was loaded
		if (sprites.isEmpty()) {
			System.err.println("ERROR: Unable to load any car sprites");
		}
	}
	
	/**
	 * Determine whether a lane is the rightmost lane of a road
	 * @param lane The lane to check
	 * @return True if lane is the rightmost lane of a road
	 */
	private boolean rightMostLane(VehicleLane lane)
	{
		return lane.getRightLane() == null;
	}
	
	/**
	 * Determine whether a lane is the leftmost lane of a road
	 * @param lane The lane to check
	 * @return True if lane is the leftmost lane of a road
	 */
	private boolean leftMostLane(VehicleLane lane)
	{
		return lane.getLeftLane() == null;
	}

}