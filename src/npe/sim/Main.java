package npe.sim;
import java.awt.MediaTracker;
import java.io.FileNotFoundException;
import javax.swing.JPanel;
import npe.sim.entity.Bus;
import npe.sim.entity.Car;
import npe.sim.entity.Pedestrian;
import npe.sim.entity.Taxi;
import npe.sim.entity.Vehicle;

import java.io.PrintStream;
import java.io.FileOutputStream;

/*TODO list:
 * Add "are you sure" quit dialog
 * Update sequence tab with new graphics
 * Tick boxes for adding lanes
 */

/**
 * Driver program for simtersection.
 */

public class Main {
	
	/**Main method for driver program.
	 * @param args Command line arguments.
	 */
	public static void main(String[] args)
	{
		loadSprites();
		SimWindow sw = new SimWindow();
		configure(sw);
	}
	
	/**Loads all the sprites needed by the simulation, and blocks until finished.*/
	private static void loadSprites()
	{
		//Load all sprites
		try {
			MediaTracker m = new MediaTracker(new JPanel()); //Tracks the loading of images
			//Load entity sprites
			Car.loadSprites(m);
			Bus.loadSprites(m);
			Taxi.loadSprites(m);
			Pedestrian.loadSprites(m);
			//Load road signs
			new Sprite("frome_rd/roadSign.gif", m);
			new Sprite("north_tce/roadSign.gif", m);
			//Load road arrows
			new Sprite("road_arrows/left.gif", m);
			new Sprite("road_arrows/left_straight.gif", m);
			new Sprite("road_arrows/right_straight.gif", m);
			new Sprite("road_arrows/straight.gif", m);
			new Sprite("road_arrows/right.gif", m);
			//Load pedestrian light sprites
			new Sprite("pedestrian_lights/flashing.gif", m);
			new Sprite("pedestrian_lights/stop.gif", m);
			new Sprite("pedestrian_lights/walk.gif", m);
			//Load traffic light sprites
			new Sprite("traffic_lights/greenLeft.gif", m);
			new Sprite("traffic_lights/greenRight.gif", m);
			new Sprite("traffic_lights/greenStraight.gif", m);
			new Sprite("traffic_lights/yellowLeft.gif", m);
			new Sprite("traffic_lights/yellowRight.gif", m);
			new Sprite("traffic_lights/yellowStraight.gif", m);
			new Sprite("traffic_lights/redLeft.gif", m);
			new Sprite("traffic_lights/redRight.gif", m);
			new Sprite("traffic_lights/redStraight.gif", m);
			new Sprite("traffic_lights/offLeft.gif", m);
			new Sprite("traffic_lights/offRight.gif", m);
			new Sprite("traffic_lights/offStraight.gif", m);
			//Wait for loading to finsh
			m.waitForAll();
		} catch (FileNotFoundException e) {
			System.err.println("ERROR: One or more sprites could not be loaded");
		} catch (InterruptedException e) {
			System.err.println("ERROR: Loading process interrupted");
		}
	}
	
	/**Configures the program with debugging options.*/
	private static void configure(SimWindow sw)
	{
		
		//---Logging---//
		if(Utils.logging){
			try{
				System.setOut(new PrintStream(new FileOutputStream("stdLog")));
				System.setErr(new PrintStream(new FileOutputStream("errorLog")));
			} catch(FileNotFoundException e){}
		}
		
		//Utils.setSeed(10);
		
		//---Vehicle Debugging---//
		if(Utils.vehicleDebugging){
			System.out.println("Vehicle debugging enabled");
			System.out.printf("%8s | %s\n","State","Colour");
			System.out.println("------------------");
			System.out.printf("%8s | %s\n","DRIVING","Green");
			System.out.printf("%8s | %s\n","STOPPING","Yellow");
			System.out.printf("%8s | %s\n","STOPPED","Red");
			System.out.printf("%8s | %s\n","TURNING","Grey");
			System.out.printf("%8s | %s\n","CHANGING","Pink");
			System.out.printf("%8s | %s\n","CREEPING","Cyan");
			System.out.println();
		}	
		
		//---Vehicle Collision Detection---//
		if (Utils.collisionDetection) {
			System.out.println("Collision dection enabled");
			System.out.println();
			
			Vehicle.useCollisionDetection(sw);
		}
	}
	
}