package npe.sim.event;


import npe.sim.Utils;
import npe.sim.entity.Bus;
import npe.sim.entity.Entity;
import npe.sim.entity.Vehicle;
import npe.sim.entity.Vehicle.Intention;
import npe.sim.road.Lane;
import npe.sim.road.VehicleLane;

public class AddBusEvent extends AddEntityEvent {
	private double leftProp;

	/**
	 * Constructor for the bus event
	 * @param time
	 * @param lane
	 */
	public AddBusEvent(long time, Lane lane, double leftProp) {
		super(time, lane);
		this.leftProp = leftProp;
	}


	@Override
	/**
	 * Create an entity of type car
	 */
	protected Entity createEntity() {
		Lane lane = getLane();
		Vehicle newBus = null;
		//sanity check
		if( lane instanceof VehicleLane ){
			VehicleLane vLane = (VehicleLane) lane;
			Intention intention = Intention.STRAIGHT;
			switch ( ((VehicleLane) lane).getType()){
			case LEFT :
				intention = Intention.LEFT;
				break;
			case RIGHT :
				intention = Intention.RIGHT;
				break;
			case STRAIGHT :
				intention = Intention.STRAIGHT;
			case LEFT_STRAIGHT :
				if (Utils.random()< leftProp) {
					intention = Intention.LEFT;
				} else {
					intention = Intention.STRAIGHT;
				}
				
			}
			newBus = new Bus(vLane.getEntryX(), vLane.getEntryY(), vLane.dirDeg(), vLane, intention);

			//add the box to the new car
			//only set the box to the new car if it is going straight
			if( newBus.getIntent() == Intention.STRAIGHT) {
				newBus.setBox(vLane.getBox());
			}
		} else {
			System.err.println("ERROR: TRYING TO CREATE VEHICLE IN NON VEHICLE LANE");
		}

		return newBus;
	}
}
