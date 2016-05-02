package npe.sim.entity;
import java.awt.MediaTracker;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import npe.sim.Sprite;
import npe.sim.road.VehicleLane;
import static npe.sim.Utils.*;

public class Bus extends Vehicle {

	///////////////////
	//---VARIABLES---//
	///////////////////
	/**The width of the bus.*/
	public static final int WIDTH = 100;
	/**The height of the bus.*/
	public static final int HEIGHT = 30;
	/**The acceleration of the bus when taking off.*/
	public static final double ACCEL = convertAcceleration(3);
	/**A list of available bus sprites.*/
	private static ArrayList<Sprite> sprites;
	
	//////////////////
	//---CREATION---//
	//////////////////
	/**Creates a car at the given position.*/
	public Bus(double x, double y, double direction, VehicleLane lane, Intention dir)
	{
		super(x, y, WIDTH, HEIGHT, direction, ACCEL, lane, dir);
		
		if (debugging) {
			setSprite("buses/testDummy.jpg");
		} else {
			chooseSprite();
		}
	}
	/**Selects a random bus sprite to use for this bus.*/
	private void chooseSprite()
	{
		//Ensure bus sprites exist
		if (sprites.isEmpty()) {
			System.err.println("ERROR: No bus sprites have been loaded");
			return;
		}
		
		//Select a random sprite
		int index = (int) Math.floor( Math.random()*sprites.size() );
		setSprite( sprites.get(index) );
	}
	/**Loads all the bus sprites into memory.*/
	public static void loadSprites(MediaTracker m)
	{
		sprites = new ArrayList<Sprite>();
		
		//Get list of files in bus sprite directory
		File folder = new File("res/buses");
		String[] files = folder.list();
		
		//Load all .gif files from the directory
		for(String fileName : files){
			if (fileName.endsWith(".gif")) {
				try {
					sprites.add( new Sprite("buses/"+fileName, m) );
				} catch (FileNotFoundException e) {
					System.err.println("ERROR: Unable to load bus sprite "+fileName);
				}
			}
		}
		
		//Ensure at least one sprite was loaded
		if (sprites.isEmpty()) {
			System.err.println("ERROR: Unable to load any bus sprites");
		}
	}
	
}
