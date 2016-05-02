package npe.sim.event;
import npe.sim.entity.Entity;
import npe.sim.entity.Taxi;
import npe.sim.entity.Vehicle;
import npe.sim.entity.Vehicle.Intention;
import npe.sim.road.Lane;
import npe.sim.road.VehicleLane;

public class AddTaxiEvent extends AddEntityEvent {
	
	
	public AddTaxiEvent(long time, Lane lane) 
	{
		super(time, lane);
	}
	
	/** Create an entity of the type car */
	protected Entity createEntity(){
		Vehicle newTaxi = null;
		Lane lane = getLane();
		if( lane instanceof VehicleLane ){
			VehicleLane vLane = (VehicleLane) lane;
			//set the intention of the taxi
			Intention intention = Intention.STRAIGHT;
			switch ( vLane.getType()){
			case LEFT_STRAIGHT :
				//If we are on Frome road, we have a left intention
				if (Math.cos(vLane.dirRad()) != 0) {
					intention = Intention.LEFT;
				}
				break;
			}
			newTaxi = new Taxi(vLane.getEntryX(), vLane.getEntryY(), vLane.dirDeg(), vLane, intention);

			//add the box to the new car
			//only set the box to the new car if it is going straight
			if( newTaxi.getIntent() == Intention.STRAIGHT) {
				newTaxi.setBox(vLane.getBox());
			}
		} else {
			System.err.println("ERROR: Trying to add a taxi to a non vehicle lane");
		}
		return newTaxi;
	}
}
