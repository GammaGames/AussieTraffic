/**
 * 
 */
package npe.sim.event;

/**
 * @author Adam Leibhardt
 *
 */
public abstract class Event implements Comparable<Event> {
	
	/** The time the event occurs in ticks */
	private long time;
	
	/**
	 * Event constructor
	 * @param time The time that the event will occur in ticks
	 * @param type The type of event
	 */
	public Event( long time ){
		this.time = time;
	}
	
	/**
	 * Check to see whether the event occurs in this tick
	 * @param tick The current tick of the system
	 */
	public int isTime( long tick ) {
		if( time < tick ) {
			return -1 ;
		} else if ( time == tick ) {
			return 0;
		} else {
			return 1;
		}
	}
	
	/**
	 * Compare method - needed for priority Queue
	 * @param e The event to compare
	 * @return -1 if the time of the 
	 */
	public int compare( Event e ) {
		long eTime = e.getTime();
		if ( time < eTime ) {
			return -1;
		} else if ( time == eTime ) {
			return 0;
		} else {
			return 1;
		}
	}
	
	/** 
	 * Get the time of the event
	 * @return The time of the event in ticks
	 */
	public long getTime(){
		return time;
	}
	
	/**
	 * Set the time of the event
	 * @param time The time of the event in ticks
	 */
	protected void setTime(long time) {
		this.time = time;
	}
	
	/** Process the event */
	public abstract void processEvent();
	
	
	
}
