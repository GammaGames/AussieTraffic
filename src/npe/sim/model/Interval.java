package npe.sim.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import npe.sim.Utils;
import npe.sim.event.AddBusEvent;
import npe.sim.event.AddCarEvent;
import npe.sim.event.Event;
import npe.sim.road.Lane;
import npe.sim.road.VehicleLane;

public class Interval {
	/** The minimum a mount of entities for the interval */
	private int minEntities;
	/** The maximum amount of entities for the itnerval */
	private int maxEntities;
	/** The duration of the interval in ticks*/
	private long minTime;
	/** The max time between entitites in ticks */
	private long maxTime;
	/** The chance that it will be a bus */
	private double busChance;
	/** The chance that an entity will be a bus */
	private double carChance;
	/** The chance that a car will be turning left */
	private double carLeftChance;
	/** The chance that a car will be going straight */
	private double carStraightChance;
	/** The chance that a car will be turning right */
	private double carRightChance;
	/** The chance that a bus will be turning left */
	private double busLeftChance;
	/** The chance that a bus will be going straight */
	private double busStraightChance;
	/** The chance that a bus will be turning right */
	private double busRightChance;
	/** The Straight lanes that the interval affects */
	private ArrayList<Lane> straightLanes = new ArrayList<Lane>(3);
	/** The left lane that the interval affects */
	private Lane leftLane;
	/** The right lane that the interval affects */
	private Lane rightLane;
	/** The duration of the interval in ticks */
	private long intervalDuration;
	/** The duration of the interval gap in ticks */
	private long totalDuration;
	
	/**
	 * Create an interval from a csv file
	 * @param filename The name of the csv file that will be used to get all the data
	 */
	public Interval(String filename)
	{
		try{
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			readFile(br);
			fr.close();
		} catch (IOException e) {
			System.err.println("ERROR: Failed to read data for the interval: " + filename);
		}
	}
	
	/**
	 * Parse the csv file 
	 * @param br The buffered reader 
	 */
	private void readFile(BufferedReader br) throws IOException
	{
		String line = br.readLine();
		//set string tokenizer for ,
		StringTokenizer st = new StringTokenizer(line,",");
		getDurations(st);
		line = br.readLine();
		st = new StringTokenizer(line,",");
		getPercentages(st);
		line = br.readLine();
		st = new StringTokenizer(line,",");
		getLeftPercentages(st);
		line = br.readLine();
		st = new StringTokenizer(line,",");
		getStraightPercentages(st);
		line = br.readLine();
		st = new StringTokenizer(line,",");
		getRightPercentages(st);
	}
	
	private void getDurations(StringTokenizer st)
	{
		intervalDuration = Integer.parseInt(st.nextToken());
		intervalDuration = (long)Utils.convertTime(intervalDuration);
		totalDuration = Integer.parseInt(st.nextToken());
		totalDuration = (long) Utils.convertTime(totalDuration);
		totalDuration += intervalDuration;
	}
	
	private void getPercentages(StringTokenizer st)
	{
		minEntities = Integer.parseInt(st.nextToken());
		maxEntities = Integer.parseInt(st.nextToken());
		//get min and max chance of a car				TAXI_PROB = 1;

		double minChance = Double.parseDouble(st.nextToken());
		double maxChance = Double.parseDouble(st.nextToken());
		//get average chance of a car
		carChance = ( ( minChance + maxChance ) /2);
		//get min and max chance of a bus
		minChance = Double.parseDouble(st.nextToken());
		maxChance = Double.parseDouble(st.nextToken());
		busChance = ( ( minChance + maxChance ) /2 );
		minTime = intervalDuration / (maxEntities + 1);
		maxTime = intervalDuration / (minEntities + 1);
	}
	
	private void getLeftPercentages(StringTokenizer st)
	{
		//get min and max chance of a car turning left
		double minChance = Double.parseDouble(st.nextToken());
		double maxChance = Double.parseDouble(st.nextToken());
		//get average
		carLeftChance = ( ( minChance + maxChance) / 2);
		
		//get min and max chance of a bus turning left
		minChance = Double.parseDouble(st.nextToken());
		maxChance = Double.parseDouble(st.nextToken());
		busLeftChance = ( ( minChance + maxChance ) / 2);
	}

	private void getStraightPercentages(StringTokenizer st)
	{
		//get min and max chance of a car turning Straight
		double minChance = Double.parseDouble(st.nextToken());
		double maxChance = Double.parseDouble(st.nextToken());
		//get average
		carStraightChance = ( ( minChance + maxChance) / 2);
		
		//get min and max chance of a bus turning Straight
		minChance = Double.parseDouble(st.nextToken());
		maxChance = Double.parseDouble(st.nextToken());
		busStraightChance = ( ( minChance + maxChance ) / 2);
	}

	private void getRightPercentages(StringTokenizer st)
	{
		//get min and max chance of a car turning right
		double minChance = Double.parseDouble(st.nextToken());
		double maxChance = Double.parseDouble(st.nextToken());
		//get average
		carRightChance = ( ( minChance + maxChance) / 2);
		
		//get min and max chance of a bus turning right
		minChance = Double.parseDouble(st.nextToken());
		maxChance = Double.parseDouble(st.nextToken());
		busRightChance = ( ( minChance + maxChance ) / 2);
	}
	
	/** Get the car and bus events from an interval 
	 * @param startTime The time in ticks at which vehicles will be created
	 * @return An array of Events that will be used in the event q.
	 */
	public Event[] getEvents(long startTime) {
		//get the number of entities in this interval
		//create the events array
		ArrayList<Event> events = new ArrayList<Event>(maxEntities);
		//the index of the current lane that we will add the event to
		long time = startTime;
		while( time < startTime + intervalDuration ) {
			//if maxTime and minTime is the same we dont want to get random val
			if( maxTime == minTime ) {
				time = time + minTime;
			} else {
				time = time + minTime + Utils.random( (int) ( maxTime - minTime ) );
			}
			if( Utils.random() < busChance ) {
				//work out what lane the bus will be in
				double lane = Utils.random();
				
				if( lane < busLeftChance ) {
					events.add(new AddBusEvent( time, leftLane, 1) );
				} else if ( lane <= busLeftChance + busStraightChance ) {
					//decide whether to add the straight bus 
					//work out what lane to add the bus to
					//a straight bus can be added to a left lane as well
					int numLanes = straightLanes.size();
					//the chance that the bus will be in the left lane
					double chance = 1/((double)numLanes + 3);
					if( Utils.random() <= chance ) {
						events.add( new AddBusEvent( time, leftLane , 1 ) );
					} else {
						int laneNum = Utils.random(numLanes);
						events.add( new AddBusEvent( time, straightLanes.get(laneNum) ,0) );
					}
				} else {
					events.add( new AddBusEvent( time, rightLane , 0 ) );
				}
			} else {
				//work out what lane the Car will be in
				double lane = Utils.random();
				if( lane <= carLeftChance ) {
					events.add( new AddCarEvent( time, leftLane , 1 ) );
				} else if ( lane <= carLeftChance + carStraightChance ) {
					//decide whether to add the straight car 
					//work out what lane to add the car to
					//a straight car can be added to a left lane as well
					int numLanes = straightLanes.size();
					//the chance that the car will be in the left lane
					double chance = 1/((double)numLanes+1);
					if( Utils.random() <= chance ) {
						events.add( new AddCarEvent( time, leftLane , 0 ) );
					} else {
						int laneNum = Utils.random(numLanes);
						events.add( new AddCarEvent( time, straightLanes.get(laneNum) ,1) );
					}
				} else {
					events.add( new AddCarEvent( time, rightLane , 1 ) );
				}
			}
		}
		Event[] ret = new Event[events.size()];
		for( int i = 0 ; i < ret.length ; i++ ) {
			ret[i] = events.get(i);
		}
		return ret;
	}

	/**
	 * Add all the lanes of a road to an interval
	 * @param lanes The lanes in the road section to add the the interval. Left lanes will be added to interval 0, straihgt lanes to 1 and right lanes to 2
	 */
	public void addLanes(ArrayList<VehicleLane> lanes)
	{
		if( lanes.size() == 0 ) {
			System.err.println("ERROR: Trying to add 0 lanes to Interval");
		}
		//left lanes are added to the first interval
		leftLane = lanes.get(0);
		//right lanes are added to the last interval
		rightLane = lanes.get(lanes.size()-1);
		//all other lanes are added to the middle interval
		for( int i = 1 ; i < lanes.size()-1; i++ ) {
			straightLanes.add(lanes.get(i));
		}
	}
	
	public long getTotalDuration()
	{
		return totalDuration;
	}
	
}
