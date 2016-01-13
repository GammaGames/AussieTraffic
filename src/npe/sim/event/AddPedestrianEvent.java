package npe.sim.event;

import npe.sim.Utils;
import npe.sim.entity.Entity;
import npe.sim.entity.Pedestrian;
import npe.sim.road.Lane;
import npe.sim.road.PedestrianLane;

public class AddPedestrianEvent extends AddEntityEvent {

	//a pedestrianEvent will create 
	public AddPedestrianEvent(long time, Lane lane) {
		super(time, lane);
	}
	
	/** Create an entity of the type pedestrian */
	protected Entity createEntity()
	{
		Lane lane = getLane();
		//sanity check
		if( lane instanceof PedestrianLane){
			PedestrianLane pLane = (PedestrianLane) lane;
			
			//create the pedestrian
			//Range of values within the lane a Pedestrian can be spawned at
			double rangeMin = Pedestrian.PEDESTRIAN_WIDTH/2;
			double rangeMax = PedestrianLane.LANE_WIDTH - Pedestrian.PEDESTRIAN_WIDTH/2;
			
			double randRange = rangeMin + (rangeMax - rangeMin)*Utils.random();
			
			double x = pLane.x() - randRange*Math.sin(pLane.dirRad());
			double y = pLane.y() + randRange*Math.cos(pLane.dirRad());
			
			if (x == pLane.x()) {
				x -= 7.5*Math.sin(pLane.dirRad());
			} else if (x == pLane.x() -PedestrianLane.LANE_WIDTH*Math.sin(pLane.dirRad())) {
				x += 7.5*Math.sin(pLane.dirRad());
			}
			if (y == pLane.y()) {
				y += 7.5*Math.cos(pLane.dirRad());
			} else if (y == pLane.y() +PedestrianLane.LANE_WIDTH*Math.cos(pLane.dirRad())) {
				y -= 7.5*Math.cos(pLane.dirRad());
			}
			
			Pedestrian p = new Pedestrian(x, y, pLane.dirDeg(), pLane);
			return p;
			} else {
				System.err.println("ERROR: Can't add a pedestrian to a non pedestrian lane");
				return null;
			}

	}

}
