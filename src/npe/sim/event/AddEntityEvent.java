package npe.sim.event;

import npe.sim.SimPanel;
import npe.sim.Utils;
import npe.sim.entity.Entity;
import npe.sim.entity.Pedestrian;
import npe.sim.road.Lane;
import npe.sim.road.VehicleLane;

public abstract class AddEntityEvent extends Event {

	private Entity entity;
	private Lane lane;
	
	/**
	 * Construtor for adding an entity event
	 * @param time The time of the event
	 * @param lane The lane that the event will add an entity to
	 */
	public AddEntityEvent(long time, Lane lane) {
		super(time);
		this.lane = lane;
//		this.entity = createEntity();
	}
	
	/**
	 * This is a method called to create the entity of a lane. The entity created will be based on the concrete class
	 * This is protected becuase it does not need to be seen from the outside, and cant make a private abstact method.
	 * @return Returns a created entity in the lane
	 */
	protected abstract Entity createEntity();

	/**
	 * Process the event
	 * When this event is processed, it will add an entity to the lane that it is storing
	 * If the lane is full, the event will be delayed by 1 second
	 * If the entity is a pedestrian, it will modifiy the time of the event, so another pedestrian will be created later
	 */
	public void processEvent() {
		boolean lanefull = false;
		if( lane instanceof VehicleLane ) {
			lanefull = ((VehicleLane) lane).laneFull();
		}
		if( !lanefull ) {
			this.entity = createEntity();
			lane.addEntity(entity);
		} else {
			//if the lane is full, we will delay the event by 1 second
			setTime(getTime() + (long)Utils.convertTime(1));
		}
		
		
		if( entity instanceof Pedestrian ) {
			setTime(getTime() + ( 10 + ( Utils.random(35)) ) * SimPanel.TPS );
			this.entity = createEntity();
		}
	}
	
	/**
	 * Comparator method, required by prioirtiy queue of event
	 * @return -1 if the time of this event is less than the time of e. 0 if the times are equals and 1 otherwise
	 * 
	 */
	public int compareTo(Event e) {
		return compare(e);
	}
	
	/**
	 * Getter method for a lane
	 * @return The lane that the entity will be added to
	 */
	public Lane getLane(){
		return lane;
	}


}
