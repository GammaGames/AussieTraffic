package npe.sim.entity;
import java.awt.MediaTracker;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import npe.sim.Sprite;
import npe.sim.entity.Vehicle.Intention;
import npe.sim.entity.Vehicle.State;
import npe.sim.road.VehicleLane;
import npe.sim.Utils;

public class Taxi extends Vehicle {

	///////////////////
	//---VARIABLES---//
	///////////////////
	/**The width of the Taxi.*/
	public static final int WIDTH = 40;
	/**The height of the Taxi.*/
	public static final int HEIGHT = 25;
	/**The acceleration of the taxi when taking off.*/
	private static final double ACCEL = Utils.convertAcceleration(5);
	/**A list of available taxi sprites.*/
	private static ArrayList<Sprite> sprites;
	/**Variable describing whether a taxi has already stopped at the taxi rank.*/
	private boolean hasStopped = false;
	
	
	//////////////////
	//---CREATION---//
	//////////////////
	/**Creates a car at the given position.*/
	public Taxi(double x, double y, double direction, VehicleLane lane, Intention dir)
	{
		super(x, y, WIDTH, HEIGHT, direction, ACCEL, lane, dir);
		
		if (debugging) {
			setSprite("taxis/testDummy.jpg");
		} else {
			chooseSprite();
		}
	}
	
	//////////////
	//---LOOP---//
	//////////////
	public void tick()
	{
		super.tick();
		
		switch(getState()) {
		case STOPPING:
			//$FALL_THROUGH$
		case STOPPED:
			//If we are stopping or stopped and passed intersection, tell vehicles behind us to change lanes
			if (passedIntersection()) {	
				//Get the vehicle behind this one
				Vehicle nextVehicle = getNextVehicle();
				if (nextVehicle != null) {
					//If the next Vehicle is not a taxi, tell it to change lanes
					if (!(nextVehicle instanceof Taxi)) {
						double x = nextVehicle.x() - w()*cosDir();
						double y = nextVehicle.y() - w()*sinDir();
						
						//Calculate the distance the car needs to stop in
						double distance = Math.abs((x() - x)*cosDir()) + Math.abs((y() - y)*sinDir());
						
						//Tell vehicle behind to change lanes if it is less than 20m behind
						if (distance < Utils.convertDistance(40)) {
							nextVehicle.forceChangeLanes();
						}
					}
				}
			}
			break;
		}
	}

	public void leaveRank()
	{
		super.leaveRank();
		
		//We have already stopped at the taxi rank
		hasStopped = true;
	}
	
	////////////////////////
	//---SPRITE CONTROL---//
	////////////////////////
	/**Selects a random taxi sprite to use for this taxi.*/
	private void chooseSprite()
	{
		//Ensure taxi sprites exist
		if (sprites.isEmpty()) {
			System.err.println("ERROR: No taxi sprites have been loaded");
			return;
		}
		
		//Select a random sprite
		int index = (int) Math.floor( Math.random()*sprites.size() );
		setSprite( sprites.get(index) );
	}
	
	/**Loads all the taxi sprites into memory.*/
	public static void loadSprites(MediaTracker m)
	{
		sprites = new ArrayList<Sprite>();
		
		//Get list of files in taxi sprite directory
		File folder = new File("res/taxis");
		String[] files = folder.list();
		
		//Load all .gif files from the directory
		for(String fileName : files){
			if (fileName.endsWith(".gif")) {
				try {
					sprites.add( new Sprite("taxis/"+fileName, m) );
				} catch (FileNotFoundException e) {
					System.err.println("ERROR: Unable to load taxi sprite "+fileName);
				}
			}
		}
		
		//Ensure at least one sprite was loaded
		if (sprites.isEmpty()) {
			System.err.println("ERROR: Unable to load any taxi sprites");
		}
	}

	public boolean hasStopped() 
	{
		return hasStopped;
	}

	public void setHasStopped(boolean hasStopped) {
		this.hasStopped = hasStopped;
	}
	
}
