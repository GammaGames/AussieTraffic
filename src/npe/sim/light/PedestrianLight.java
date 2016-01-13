/**
 * 
 */
package npe.sim.light;

import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import npe.sim.Sprite;
import npe.sim.road.PedestrianLane;
/**
 * @author adam
 *
 */
public class PedestrianLight {
	/**
	 * Enum for the states of a traffic light
	 * @author adam
	 */
	
	/** The width of the light sprite */
	public static final int LIGHT_WIDTH = 20;
	/** The height of the light sprite */
	public static final int LIGHT_HEIGHT = 20;
	
	public enum State {
		GREEN,
		FLASHING,
		RED;
		
		public String toString(){
			switch( this ){
			case GREEN :
				return "walk.gif";
			case FLASHING:
				return "flashing.gif";
			case RED :
				return "stop.gif";
			default :
				return "Unknown";
			}
		}
	}

	/** The id of the pedestrian light */
	private int id;
	/** The current state of a pedestrian light */
	private State state;
	
	/**An arraylist of lanes that a pedestrain light will control */
	private ArrayList<PedestrianLane> lanes = new ArrayList<PedestrianLane>(2);
	
	//-------DRAWING VARIABLES---------//
	/** The x coordinate for the ped light to be drawn at. Each ped light is drawn twice */
	private int[] x = new int[2];
	/** The y coordinate for the ped light to be drawn at. Each ped light is drawn twice */
	private int[] y = new int[2];
	/** THe direction of the ped light to be rotated at when drawn */
	private int direction;
	
	
	public PedestrianLight(int aId){
		this.id = aId;
		this.state = State.RED;
		switch ( id ){
		case 0 :
		case 1 :
			direction = 90;
			break;
		case 2 : 
		case 3 :
			direction = 180;
			break;
		}
	}
	
	/** 
	 * Tick method for a pedestrian light.
	 * This method will tell all corresponding lanes to stop/start
	 */
	public void tick(){
		switch ( state ) {
		case GREEN:
			for( PedestrianLane lane : lanes ){
				lane.start();
			}
			break;
		case FLASHING:
		case RED:
			for( PedestrianLane lane : lanes ) {
				lane.stop();
			}
			break;
		}
	}

	/**
	 * Setter method for state
	 * @param state The new state of the traffic light
	 */
	public void setState(State state){
		this.state = state;
	}

	public void changeState() {
		switch(state){
		case RED :
			state = State.GREEN;
			break;
		case GREEN :
			state = State.FLASHING;
			break;
		case FLASHING :
			state = State.RED;
		}
	}
	
	public String toString(){
		return state.toString();
	}

	public void setX(int[] x) {
		this.x = x;
	}
	
	public void setY(int[] y) {
		this.y = y;
	}
	
	/** Draw the pedestiran light */
	public void draw(Graphics2D g){
		try{
			for( int i = 0 ; i < x.length ; i++ ) {
				new Sprite("pedestrian_lights/"+state.toString()).draw(g,x[i] - LIGHT_WIDTH/2, y[i] - LIGHT_HEIGHT/2 ,Math.toRadians(i * 180 + direction), x[i] ,y[i] );
			}
		} catch (FileNotFoundException e){
			System.out.println(e.getMessage());
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void addPedestrianLane(PedestrianLane lane){
		lanes.add(lane);
	}
	
}
