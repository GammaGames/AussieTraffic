package npe.sim.model;

import java.util.ArrayList;
import java.util.PriorityQueue;

import npe.sim.event.Event;
import npe.sim.event.StartIntervalEvent;
import npe.sim.road.Intersection;
import npe.sim.road.Road;
import npe.sim.road.VehicleLane;

public class Model {

	
	private static final int MORNING = 0;
	private static final int AFTERNOON = 1;
	private static final int NIGHT = 2;

	public static void getModel(int modelNum, Intersection intersection, PriorityQueue<Event> pq ) {
		switch( modelNum ) {
		case MORNING:
			setUpModel("Morning",intersection,pq);
			break;
		case AFTERNOON : 
			setUpModel("Afternoon",intersection,pq);
			break;
		case NIGHT : 
			setUpModel("Night",intersection,pq);
			break;
			default :
				System.err.println("ERROR: Failed to get model from gui, using morning as default");
				setUpModel("Morning",intersection,pq);
		}
		//set the lanes up for reach interval
	}

	private static void setUpModel(String time,Intersection intersection, PriorityQueue<Event> eventQ) {
		Interval ntw = new Interval("res/model/" + time + "NorthWest.csv");
		Interval nte = new Interval("res/model/" + time + "NorthEast.csv");
		Interval frn = new Interval("res/model/" + time + "FromeNorth.csv");
		Interval frs = new Interval("res/model/" + time + "FromeSouth.csv");
		
		//get the roads from intersection
		Road northTce = intersection.getNorthTce();
		Road fromeRd = intersection.getFromeRd();
		//add the lanes to the cycles, this is need for initialisation of the model
		
		//---------------ADD ALL THE ENTRY LANES TO THE APPOPRIATE CYCLES-----------------//
		ArrayList<VehicleLane> lanes = fromeRd.getVehicleLanes();
		ArrayList<VehicleLane> entryLanes;
		entryLanes = new ArrayList<VehicleLane>();
		//All frome road north lane are the first n/2 entry lanes
		for( int i = 0 ; i < lanes.size() /2 ; i +=2 ) {
			entryLanes.add(lanes.get(i));
		}
		frn.addLanes(entryLanes);
		//all frome road south lanes are the second n/2 entry lanes
		entryLanes = new ArrayList<VehicleLane>();
		for( int i = lanes.size() -1 ; i >= lanes.size() /2 ; i-= 2) {
			entryLanes.add(lanes.get(i));
		}
		frs.addLanes(entryLanes);
		
		//all northTce west lanes are the first n/2 entry lanes
		entryLanes = new ArrayList<VehicleLane>();
		lanes = northTce.getVehicleLanes();
		for( int i = 0 ; i < lanes.size() /2 ; i +=2 ) {
			entryLanes.add(lanes.get(i));
		}
		ntw.addLanes(entryLanes);
		entryLanes = new ArrayList<VehicleLane>();
		//all north tce east lanes are the second n/2 entry lanes
		for( int i = lanes.size() -1 ; i >= lanes.size() /2 ; i-= 2) {
			entryLanes.add(lanes.get(i));
		}
		nte.addLanes(entryLanes);
		
		//add start cycle events for all the cycles
		eventQ.add(new StartIntervalEvent(0,frn,eventQ));
		eventQ.add(new StartIntervalEvent(0,frs,eventQ));
		eventQ.add(new StartIntervalEvent(0,ntw,eventQ));
		eventQ.add(new StartIntervalEvent(0,nte,eventQ));
	}
}
