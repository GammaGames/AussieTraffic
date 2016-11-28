package npe.sim;
import npe.sim.light.Sequence;
import npe.sim.light.TrafficLightController;
import npe.sim.road.VehicleLane;

/**
 * Stores the simulation properties selected by the GUI so they can be transferred to the simulation.
 */

public class SimProperties {
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**The speed limit for vehicles, in km/h.*/
	public int speedLimit = 50;
	/**The time of day for the simulation to run (traffic model is selected based on this).*/
	public int timeOfDay = 0;
	/**The duration of the simulation (use zero to run until stopped by the user).*/
	public double duration = 0;
	/**The selected traffic light sequence.*/
	public Sequence sequence;
	/**The traffic light controller which contains the selected traffic light durations.*/
	public TrafficLightController trafficLightController = new TrafficLightController(12);	
	/**The number of lanes on North Tce.*/
	public int numLanesNorth  = 4;
	/**The number of extra lanes on North Tce.*/
	public int numLanesNorthExtra = 0;
	/**The number of lanes on Frome Rd.*/
	public int numLanesFrome  = 3;
	/**The number of extra lanes on North Tce.*/
	public int numLanesFromeExtra = 0;
	/**The variable to enable the taxi rank.*/
	public boolean taxiRank = false;
	/**The variable .*/
	public VehicleLane.Type leftMostLaneType = VehicleLane.Type.LEFT_STRAIGHT;
	/**The variable .*/
	public VehicleLane.Type leftMostLaneTypeOpposite = VehicleLane.Type.LEFT_STRAIGHT_;
	

}
