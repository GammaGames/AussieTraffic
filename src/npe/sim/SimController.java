package npe.sim;
import java.awt.Graphics2D;
import java.util.PriorityQueue;
import npe.sim.event.AddPedestrianEvent;
import npe.sim.event.Event;
import npe.sim.event.StateChangeEvent;
import npe.sim.light.TrafficLightController;
import npe.sim.model.Model;
import npe.sim.road.Intersection;
import npe.sim.road.PedestrianLane;
import npe.sim.road.Road;
import npe.sim.results.StatsCollector;

/**
 * Main controller for the simulation.
 */

public class SimController {
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**The number of ticks which have elapsed since the simulation began.*/
	private long t = 0;
	//SIMULATION COMPONENTS
	/**The intersection being simulated.*/
	private Intersection intersection;
	/**The controller for the traffic lights being simulated.*/
	private TrafficLightController trafficLightController;
	/**The queue which stores upcoming simulation events.*/
	PriorityQueue<Event> eventQ;
	//SIMULATION VARIABLES
	/**The time of day at which the simulation will be run.*/
	private int timeOfDay;
	/**The StatsCollector that will hold values for the results graph */
	private StatsCollector statsCollector;
	
	//////////////////
	//---CREATION---//
	//////////////////
	/**Initialises the simulation.*/
	public SimController(SimProperties sp, StatsCollector st)
	{
		//---STORE THE SIMULATION VARIABLES---//
		timeOfDay = sp.timeOfDay;
		trafficLightController = sp.trafficLightController;
		
		//---INITIALIZE THE STATSCOLLECTOR---//
		statsCollector = st;
		
		//---INITIALISE EVENT QUEUE---///
		eventQ = new PriorityQueue<Event>(14);
		eventQ.add(new StateChangeEvent(0,trafficLightController));
		Event[] pedE = trafficLightController.getPedestrianLightEvents();
		for ( int i = 0 ; i < pedE.length ; i++ ){
			eventQ.add(pedE[i]);
		}
		
		//---CREATE THE INTERSECTION---//
		intersection = new Intersection(sp, statsCollector);
		
		//Get the data model for controlling arrival times
		Model.getModel(timeOfDay,intersection,eventQ);
		
		//Initialist pedestrian events
		Road[] roads = { intersection.getNorthTce() , intersection.getFromeRd() } ;
		long time = 0;
		for ( Road r : roads ) {
			for ( PedestrianLane pl : r.getPedestrianLanes() ) {
				Event e = new AddPedestrianEvent(time,pl);
				eventQ.add(e);
				time ++;
			}
		}
	}

	//////////////
	//---LOOP---//
	//////////////
	/**Performs all processing which must be done on each tick of the simulation.*/
	public void tick()
	{
		//Process the intersection (includes all roads / lanes / vehicles / pedestrians)
		intersection.tick();
		//Process events
		Event e;
		while ( eventQ.peek().isTime(t) <= 0 ) {
			e = eventQ.remove();
			e.processEvent();
			//If event has changed its time, add it back to the priority queue
			//State change events will change their time to that of the next state
			if ( e.isTime(t) > 0 ) { 
				eventQ.add(e);
			}
		}
		
		
		//Process traffic lights
		trafficLightController.tick();
		
		//Pass tick information to stats collector
		statsCollector.tick();
		
		//Increment simulation time
		t++;
	}
	/**Performs all drawing which must be done at the end of each tick of the simulation.
	 * @param g Graphics context to draw on.
	 */
	public void draw(Graphics2D g)
	{
		//Draw the intersection (includes all roads / lanes / vehicles / pedestrians)
		intersection.draw(g);
		//Draw the traffic lights
		trafficLightController.draw(g);
	}
	
	////////////////
	//---ACCESS---//
	////////////////
	/**Provides access to the simulation time.
	 * @return The number of ticks which have elapsed since the simulation began.
	 */
	public long t()
	{
		return t;
	}
	
}