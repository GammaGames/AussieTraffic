package npe.sim.road;
import java.awt.Graphics2D;
import java.util.LinkedList;
import npe.sim.Sprite;
import npe.sim.Utils;
import npe.sim.entity.Taxi;
import npe.sim.entity.Vehicle;
import npe.sim.entity.Vehicle.State;
import npe.sim.entity.Vehicle.Intention;

/**
 * An object representing the Taxi Rank on the eastern side of North Terrace
 */

public class TaxiRank {

	///////////////////
	//---VARIABLES---//
	///////////////////
	/**Sprite for the Taxi rank.*/
	protected Sprite sprite = null;
	//Dimensions
	/**The x coordinate of the top left of the rank.*/
	private double x;
	/**The y coordinate of the top left of the rank.*/
	private double y;
	/**The width of the sprite.*/
	public static final int WIDTH = 40;
	/**The height of the sprite.*/
	public static final int HEIGHT = 42;
	/**The lane representing the Taxi rank where Taxis will stop.*/
	private VehicleLane lane;
	/**The current taxi stopping at the Taxi rank.*/
	private Vehicle currentTaxi = null;
	/**The coordinates of where the first taxi should stop.*/
	private double stopPointX;
	private double stopPointY;
	/**Variables used to keep taxis stopped at the Taxi rank.*/
	private int t = 0;
	private boolean timer = false;
	private static final int STOP_TIME = (int) Utils.convertTime(2);
	
	/**Queue containing all the taxis entering the taxi rank lane.*/
	LinkedList<Vehicle> taxiQueue;
	
	//////////////////
	//---CREATION---//
	//////////////////
	public TaxiRank(double x, double y, VehicleLane lane)
	{
		this.x = x;
		this.y = y;
		this.lane = lane;
		
		//Set the sprite
		setSprite("north_tce/taxiRankSign.gif");
		
		//Initialise taxi Queue
		taxiQueue = new LinkedList<Vehicle>();
		
		//Set the stopping points
		stopPointX = x + Taxi.WIDTH;
		stopPointY = y + HEIGHT + PedestrianLane.LANE_WIDTH + VehicleLane.LANE_WIDTH/2;
	}
	
	//////////////
	//---LOOP---//
	//////////////
	public void tick()
	{
		//Get last vehicle in the lane
		Vehicle lastVehicle = lane.getLastVehicle();
		Vehicle lastTaxi = null;
		
		if (!taxiQueue.isEmpty()) {
			//Get the last taxi in the queue
			lastTaxi = taxiQueue.getLast();
		}

		//If the last vehicle in the lane is a taxi and hasn't already been added
		//To the taxi queue, add it to the end
		if (lastVehicle != lastTaxi && lastVehicle instanceof Taxi) {
			//Check if the taxi has already stopped at the taxi rank
			if (!((Taxi)lastVehicle).hasStopped()) {
				taxiQueue.add(lastVehicle);
			}
		}
		
		//Get the first taxi in the queue
		currentTaxi = taxiQueue.peek();
	
		//If the vehicle is a taxi and not already passed the rank, tell it to stop
		if (currentTaxi != null) {
			if (!timer) {
				currentTaxi.stop(stopPointX, stopPointY, currentTaxi.getIntent());	
				if (currentTaxi.getState() == State.STOPPED) {
					timer = true;
				}
			} else if (t < STOP_TIME) {
				currentTaxi.stop(stopPointX, stopPointY, currentTaxi.getIntent());
			} else {
				((Taxi)currentTaxi).leaveRank();
				taxiQueue.remove();
				currentTaxi = null;
				timer = false;
				t = 0;
			}
		}
		
		//If timer is on, increment time
		if (timer) {
			t++;
		}		
	}

	public void draw(Graphics2D g)
	{
		//g.setColor(Color.WHITE);
		sprite.draw(g, x, y);
		//g.drawRect((int) x, (int) y, (int) WIDTH, (int) HEIGHT);
		//g.drawOval((int) stopPointX, (int) stopPointY, 2, 2);
	}
	
	/**Sets the sprite for the entity.
	 * @param spriteName Filename of sprite to use.
	 */
	public void setSprite(String spriteName)
	{
		try {
			sprite = new Sprite(spriteName);
		} catch (Exception e) {
			System.err.println("Sprite '"+spriteName+"' does not exist.");
		}
	}
	
}
