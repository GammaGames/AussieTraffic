package npe.sim.event;

import npe.sim.light.PedestrianLight;


public class PedestrianStateChangeEvent extends Event {

	/** Pedestrian light that this will change the state for*/
	PedestrianLight pl;
	/** The total state duration so the next state of this nature can be inserted into event queue*/
	int totalDuration;
	
	/**
	 * Constructor for a pedestrian event
	 * @param time The time that the pedestrian state change event will occur
	 * @param pedestrianLight The pedestrian light that this state change will affect
	 * @param duration The duration of the entire sequence, this will be used for creating an event for the next light cycle
	 */
	public PedestrianStateChangeEvent(long time,PedestrianLight pedestrianLight, int duration) 
	{
		super(time * 30);
		
		pl = pedestrianLight;
		totalDuration = duration;
	}

	/**
	 * Process the event
	 * Simply change the state, and set the time of the event to 1 entire cycle later
	 */
	public void processEvent() {
		pl.changeState();
		super.setTime(super.getTime() + totalDuration * 30);
	}

	/**
	 *  Compare two events based on time
	 * @return -1 if the time is less than the time at e. 0 if the times are equal, 1 otherwise
	 */
	public int compareTo(Event e) {
		return super.compare(e);
	}

}
