package npe.sim.light;

/**
 * 
 * @author donald
 */

public class State {

	/**
	 * Enum used to represent the different types of scenarios of a state.
	 * <br>GREEN -> lights are green and red only
	 * <br>YELLOW -> lights are yellow and red only
	 * <br>RED -> lights are all red
	 * <br>GREEN_OFF -> lights are green, off and red. 
	 * @author adam
	 */
	public enum Type {
		GREEN,
		YELLOW,
		RED,
		GREEN_OFF,
		GREEN_RIGHT;
	}
	
	/**
	 * Array of all the traffic light states, for this state
	 */
	private TrafficLight.State[] states;
	
	/**
	 * Duration of the state
	 */
	private int stateDuration;
	
	/**
	 * Id of the state
	 */
	private int id;
	
	private Type type;
	
	/**
	 * Default consturctor, does not add any values to the light
	 */
	public State (int id, TrafficLight.State[] states)
	{
		this.id = id;
		this.states = states;
		
		//set state type, by default red
		type = Type.RED;

		//Loop through all TrafficLight States
		//if state is yellow, set type to yellow.
		//if state is green, and if type has not been set yet, set type to green
		//if state is off, set type to green_off
		//if state is green, i %3 == 2 (ie a right turn, make it a green right
		for( int i = 0 ; i < states.length ; i++ ) {
			switch ( states[i] ) {
			case GREEN : 
				switch (type) {
				case RED : 
					type = Type.GREEN;
					break;
				} 
				if ( (i % 3) == 2 ) {
					type = Type.GREEN_RIGHT;
				}
				break;
			case YELLOW :
				type = Type.YELLOW;
				//as soon  as its yellow, we dont care about the rest
				return;
				
			case OFF : 
				type = Type.GREEN_OFF;
				break;
			}
		}
	}
	
	/**
	 * Set a states type to red
	 */
	public void setAsRed()
	{
		type = Type.RED;
	}
		
	/**
	 * @return the stateDuration
	 */
	public int getStateDuration() {
		return stateDuration;
	}

	/**
	 * @param stateDuration the stateDuration to set
	 */
	public void setStateDuration(int stateDuration) {
		this.stateDuration = stateDuration;
	}
	
	/**
	 * Method to get the state of a traffic light in this state
	 * @param tlId The traffic light id to find the state of
	 * @return The state of the traffic light.
	 */
	public TrafficLight.State getTrafficLightState(int tlId){
		return states[tlId];
	}
	
	/**
	 * Method used to get the state id
	 * @return The state id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Getter method for StateType
	 * @return type of state
	 */
	public Type getType(){
		return type;
	}
}
