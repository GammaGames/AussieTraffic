package npe.sim.event;

import java.util.PriorityQueue;

import npe.sim.model.Interval;

public class StartIntervalEvent extends Event 
{
	/** The interval of the event */
	private Interval interval;
	/** The priority queue, all the events in the interval event will be added to this eventQ */
	private PriorityQueue<Event> eventQ;
	/**
	 * Constructor
	 * @param time The time of the event
	 * @param i The interval that will be used to create vehicles
	 * @param events The event queue that all events will be added to
	 */
	public StartIntervalEvent(long time, Interval i,PriorityQueue<Event> events)
	{
		super(time);
		interval = i;
		eventQ = events;
	}

	/**
	 * Compartor - used for prioirity queue. Comparing events based on time of event
	 * @return -1 if the time is less than the time at e. 0 if the times are equal, 1 otherwise
	 */
	public int compareTo(Event e) 
	{
		return compare(e);
	}

	/**
	 * Process event.
	 * In this event, get all the events from an interval, and add them to the eventQ
	 */
	public void processEvent() 
	{
		Event[] events = interval.getEvents(getTime());
		for( int i = 0 ; i < events.length ; i++ ) {
			eventQ.add(events[i]);
		}
		
		setTime(getTime() + interval.getTotalDuration());
	}
}
