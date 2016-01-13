/**
 * 
 */
package npe.sim.event;

import npe.sim.light.TrafficLightController;

/**
 * @author adam
 *
 */
public class StateChangeEvent extends Event
{

	/** The traffic light controller. USed for changing states of lights */
	private TrafficLightController tlc;
	/**
	 * The Constructor
	 * @param time The time of the state change event
	 * @param tlc The traffic light controller, needed for changing states of all traffic lights
	 */
	public StateChangeEvent( long time , TrafficLightController tlc )
	{
		super(time);
		this.tlc = tlc;
		
	}

	/**
	 * Process the event
	 * Change the state of all traffic lights in the traffic light controller
	 */
	public void processEvent()
	{
		long statetime = tlc.changeState();
		setTime(getTime() + statetime * 30); //change the time that the evevnt will occur (ie time of next change of light);
	}

	/**
	 * Compartor - used for prioirity queue. Comparing events based on time of event
	 * @return -1 if the time is less than the time at e. 0 if the times are equal, 1 otherwise
	 */
	public int compareTo(Event o) 
	{
		return super.compare(o);
	}

}
