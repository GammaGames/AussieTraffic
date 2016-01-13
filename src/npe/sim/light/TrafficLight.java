/**
 * 
 */
package npe.sim.light;

import npe.sim.entity.Vehicle.Intention;

import java.util.ArrayList;

import npe.sim.road.Box;
import java.awt.*;
import java.io.*;

import npe.sim.SimPanel;
import npe.sim.Sprite;
import npe.sim.Utils;

import npe.sim.road.*;



public class TrafficLight
{

	public enum State 
	{
		GREEN,
		YELLOW,
		RED,
		OFF;
	}
	/**
	 * An enum stating the direction of the light
	 * @author adam
	 *
	 */
	public enum Type
	{
		LEFT,
		STRAIGHT,
		RIGHT;
	}
	
	/** The width of the light, used for drawing */
	private final int LIGHT_WIDTH = 20;
	/** The height of the light, used for drawing */
	private final int LIGHT_HEIGHT = 20;
	
	/** The x position of the light, used for drawing */
	private int x;
	/** The y position of the light, used for drawing */
	private int y;
	/** The direction of the light, used ofr drawing */
	private double direction;
	/** The gif that will be drawn */
	private String gif;
	
	/** The traffic light id */
	private int id;
	
	/**The length of time the green light is on */
	private int greenDuration;
	/** The length of time the yellow light is on*/
	private int yellowDuration;
	/** The length of time the red light is on */
	private int redDuration;
	
	/** The length of time the light is set to off */
	private int offDuration;
	/** The time of that a light has been on yellow for */
	private int yellowT;
	
	/** The state that the traffic light is currently in */
	private State state;
	/** The direction of the traffic light */
	public final Type TYPE;
	/** An arraylist of boxes that will correspond to whether it is safe for a vehicle to turn */
	private ArrayList<Box> boxes = new ArrayList<Box>(2);
	
	/** Arraylist of traffic lights */
	private ArrayList<VehicleLane> lanes;
	
	/**
	 * Create a traffic light
	 * @param id The id of the traffic light
	 */
	public TrafficLight(int id) 
	{
		this.id = id;
		state = State.RED;
		lanes = new ArrayList<VehicleLane>(2);
		switch( id / 3 ) {
		case 0 :
			direction = 90;
			break;
		case 1 : direction = -90;
			break;
		case 2 : direction = 180;
			break;
		case 3 : direction = 0;
		}
		
		switch ( id % 3 ) {
		case 0 :
			gif = "Left.gif";
			TYPE = Type.LEFT;
			break;
		case 1 :
			gif = "Straight.gif";
			TYPE = Type.STRAIGHT;
			break;
		case 2 :
			gif = "Right.gif";
			TYPE = Type.RIGHT;
			break;
			default:
				System.err.println("ERROR: Invalid light id");
				TYPE = Type.LEFT;
				break;
		}
	}
	
	/**
	 * Add time to green duration
	 * @param duration Time to add
	 */
	public void addGreenDuration(int duration)
	{
		greenDuration += duration;
	}

	/**
	 * Add time to yellow duration
	 * @param duration Time to add
	 */
	public void addYellowDuration(int duration)
	{
		yellowDuration += duration;
	}

	/**
	 * Add time to red duration
	 * @param duration Time to add
	 */
	public void addRedDuration(int duration)
	{
		redDuration += duration;
	}

	/**
	 * Get the green duration of the traffic light in one full sequence
	 * @return the greenDuration
	 */
	public int getGreenDuration() 
	{
		return greenDuration;
	}

	/**
	 * Get the time that the light yellow for in one full sequence
	 * @return the yellowDuration
	 */
	public int getYellowDuration()
	{
		return yellowDuration;
	}

	/**
	 * Get the time the light is red for in one full sequence
	 * @return the redDuration
	 */
	public int getRedDuration()
	{
		return redDuration;
	}

	/**
	 * Get the off duration of the traffic light
	 * @return the offDuration
	 */
	public int getOffDuration()
	{
		return offDuration;
	}
	
	/** 
	 * Get the id of the traffic light
	 * @return id
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * @param greenDuration the greenDuration to set
	 */
	public void setGreenDuration(int greenDuration)
	{
		this.greenDuration = greenDuration;
	}

	/**
	 * @param yellowDuration the yellowDuration to set
	 */
	public void setYellowDuration(int yellowDuration)
	{
		this.yellowDuration = yellowDuration;
	}

	/**
	 * @param redDuration the redDuration to set
	 */
	public void setRedDuration(int redDuration)
	{
		this.redDuration = redDuration;
	}

	/**
	 * @param offDuration the offDuration to set
	 */
	public void setOffDuration(int offDuration)
	{
		this.offDuration = offDuration;
	}
	
	/**
	 * Method used to change the state of a traffic light
	 * @param state The new state of the traffic light
	 */
	public void changeState(State state)
	{
		this.state = state;
		yellowT = 0;
	}

	/**
	 * Get the State of the traffic light
	 * @return the state of the traffic light
	 */
	public TrafficLight.State getState()
	{
		return state;
	}
	
	/**
	 * To string method: 
	 * @return The ide of the traffic light and the current state it is in
	 */
	public String toString()
	{
		String retString = "";
		retString += "ID: " + id + " Colour: " + state.toString();
		return retString;
	}

	/**
	 * Get the durations of the lights in string form, this is used for debugging
	 * @return A string containing red,yellow and green durations
	 */
	public String getDurations() 
	{
		String retString = "";
		retString += "Green Duration: " + greenDuration + "\n";
		retString += "Yellow Duration: " + yellowDuration + "\n";
		retString += "Red Duration: " + redDuration + "\n";
		
		return retString;
	}
	
	/**
	 * Add a lane that this traffic light will tell to stop
	 * @param lane The lane that will be added to the traffic light
	 */
	public void addLane(VehicleLane lane)
	{
		lanes.add(lane);
	}

	/**
	 * Tick
	 * If the light is red, call stop
	 * If the light is off, call start if it is safe, call stop otherwise
	 * If the light is green, call start
	 * If the light is yellow, call start for the first second, stop otherwise.
	 * If the light is yellow and the traffic light is right, call start at last second
	 */
	public void tick()
	{
		switch ( state ) {
		case OFF :
			if( !isSafe()){
				 callStop();
				 break;
			} else {
				callStart();
				break;
			}
		case GREEN :

			callStart();
			break;
		case YELLOW :
			//tell a car to start for the first 2 seconds of a yellow light
			//it is a right hand turn, tell it to start in the last second
			if( id % 3 == 2) {
				if( yellowT  < Utils.convertTime(yellowDuration) - SimPanel.TPS ) {
					//System.out.println(yellowDuration);
					if( !isSafe()){
						 callStop();
						 break;
					} else {
						callStart();
						break;
					}
				} else { 
					callStart();
				}
			//if its a left traffic light
			//only go if it is safe to go
			} else if ( id % 3 == 0 ) {
				if( !isSafe()){
					 callStop();
					 break;
				} else {
					callStart();
					break;
				}
			//if it is a straight light, tell it to start for the first second, stop afterwards
			} else {
				if( yellowT < SimPanel.TPS ) {
					callStart();
				} else {
					callStop();
				}
				break;
			}
		case RED :
			callStop();
			break;
		}
		yellowT++;
	}
	
	/**
	 * Draws the traffic lights
	 * @params Graphs2D g
	 */
	public void draw(Graphics2D g)
	{
		try{
			new Sprite("traffic_lights/"+state.toString().toLowerCase()+gif).draw(g,x - LIGHT_WIDTH/2, y - LIGHT_HEIGHT/2 ,Math.toRadians(direction), x ,y );
		} catch (FileNotFoundException e){
			System.out.println(e.getMessage());
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/** Reset all the durations of the traffic light  */
	public void clearDurations() 
	{
		greenDuration = 0;
		yellowDuration = 0;
		redDuration = 0;
		offDuration = 0;
	}
	
	/**
	 * Setter method for the x coordinate of the traffic light
	 * @param x The new x coordinate of the traffic light
	 */
	public void setX(int x)
	{
		this.x = x;
	}
	
	/**
	 *  Setter method for the y coordinate of the traffic light
	 * @param y The new y coordinate of the traffic light
	 */
	public void setY(int y)
	{
		this.y = y;
	}

	/** 
	 * Add a box the the traffic light, this box will be checked in the isSafe method
	 * @param box The box to add to the traffic lights box array
	 */
	public void addBox(Box box)
	{
		boxes.add(box);
	}
	
	/**
	 * Method to determine if it is safe for a car to turn right/left with care
	 * @return True if it is good to go
	 */
	public boolean isSafe()
	{
		for( Box box : boxes){
			if(!box.isEmpty()){
				return false;
			}
		}
		return true;
	}

	/** Tell the lanes that the traffic light is responsible for to stop */
	private void callStop()
	{
		//If we have more than one lane, this traffic light handles straight cars
		if (lanes.size() > 1) {
			for (VehicleLane lane : lanes) {	
				lane.stop(Intention.STRAIGHT);
			}
		} else if (lanes.size() == 1) {
			//Single lane, pass message with intention based on lane type
			VehicleLane lane = lanes.get(0);
			switch (lane.getType()) {
			case LEFT :
				lane.stop(Intention.LEFT);
				break;
			case LEFT_STRAIGHT :
				lane.stop(Intention.LEFT);
				break;
			case STRAIGHT :
				lane.stop(Intention.STRAIGHT);
				break;
			case RIGHT_STRAIGHT : 
				lane.stop(Intention.RIGHT);
				break;
			case RIGHT :
				lane.stop(Intention.RIGHT);
				break;
			}
		}
	}
	/** Tell lanes that the traffic light is responsible for to start */
	private void callStart()
	{
		if (lanes.size() > 1) {
			for (VehicleLane lane : lanes) {	
				lane.start(Intention.STRAIGHT);
			}
		} else if (lanes.size() == 1) {
			//Single lane, pass message with intention based on lane type
			VehicleLane lane = lanes.get(0);
			switch (lane.getType()) {
			case LEFT :
				lane.start(Intention.LEFT);
				break;
			case LEFT_STRAIGHT :
				lane.start(Intention.LEFT);
				break;
			case STRAIGHT :
				lane.start(Intention.STRAIGHT);
				break;
			case RIGHT_STRAIGHT : 
				lane.start(Intention.RIGHT);
				break;
			case RIGHT :
				lane.start(Intention.RIGHT);
				break;
			}
		}
	}
}
