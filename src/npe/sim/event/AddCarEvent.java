package npe.sim.event;

import npe.sim.Utils;
import npe.sim.entity.Car;
import npe.sim.entity.Entity;
import npe.sim.entity.Taxi;
import npe.sim.entity.Vehicle;
import npe.sim.entity.Vehicle.Intention;
import npe.sim.road.Lane;
import npe.sim.road.VehicleLane;

public class AddCarEvent extends AddEntityEvent 
{
	/** The probability that a car will be a taxi */
	public final double TAXI_PROB;
	/** The probability that a car will turn left */
	private double leftProb;
	
	/**
	 * Cosntructor for a car and a taxi event
	 * @param time The time the event will occur
	 * @param lane The lane that the car/taxi will be added to
	 * @param leftProb The probability that the car will turn left
	 */
	public AddCarEvent(long time, Lane lane, double leftProb)
	{
		super(time, lane);
		this.leftProb = leftProb;
		if( lane instanceof VehicleLane ) {
			VehicleLane vLane = (VehicleLane ) lane;
			if( vLane.dirDeg() > 90 ) {
				TAXI_PROB = 0;
			} else {
				switch ( vLane.getType() ) {
				case LEFT:
					//$FALL_THROUGH
				case LEFT_STRAIGHT :
					switch( vLane.road().getROAD() ) {
					case FROME :
						TAXI_PROB = 0.2;
						this.leftProb = 1;
						break;
					default :
						TAXI_PROB = 0;
					}
					break;
				default :
					TAXI_PROB = 0;
				}
			}
		} else {
			System.err.println("ERROR: Creating a car in a non vehicle lane");
			TAXI_PROB = 0;
		}
	}
	
	/** Create an entity of the type car or type taxi */
	protected Entity createEntity()
	{
		Vehicle newCar = null;
		Lane lane = getLane();
		//------SANITY CHECK----//
		if( lane instanceof VehicleLane ){
			VehicleLane vLane = (VehicleLane) lane;

			//set the intention of the car
			Intention intention = Intention.STRAIGHT;
			switch ( vLane.getType()){
			case LEFT :
				intention = Intention.LEFT;
				break;
			case RIGHT :
				intention = Intention.RIGHT;
				break;
			case STRAIGHT :
				intention = Intention.STRAIGHT;
				break;
			case LEFT_STRAIGHT :
				if (Utils.random() < leftProb) {
					intention = Intention.LEFT;
				} else {
					intention = Intention.STRAIGHT;
				}
				break;
				
			}
			//check to see wheter to crate a taxi or a car
			if( Utils.random() < TAXI_PROB ) {
				newCar = new Taxi(vLane.getEntryX(), vLane.getEntryY(), vLane.dirDeg(), vLane, intention);
			} else {
				newCar = new Car(vLane.getEntryX(), vLane.getEntryY(), vLane.dirDeg(), vLane, intention);
			}

			//add the box to the new car
			//only set the box to the new car if it is going straight
			if( newCar.getIntent() == Intention.STRAIGHT) {
				newCar.setBox(vLane.getBox());
			}
		} else {
			System.err.println("NOT INSTANCE OF VLANE");
		}
		return newCar;
	}
}
