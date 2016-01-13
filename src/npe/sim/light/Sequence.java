/**
 * 
 */
package npe.sim.light;


import npe.sim.road.Road;

/**
 * @author adam
 *
 */
public class Sequence {
	
	private static final int NUM_LIGHTS = 12;

	/**A 2D array of states, which full represents the traffic light sequence. */
	private TrafficLight.State[][] states;
	
	/** The current sequence of north terrace */
	private int northSequence;
	
	/** The current sequence of frome road */
	private int fromeSequence;
	
	/**
	 * Constructor
	 * Set initial sequences to 0
	 */
	public Sequence()
	{
		northSequence = 0;
		fromeSequence = 0;
		setStates();
	}
	
	/** 
	 * Set a sequence. After setting the sequence value
	 * @param raodTpye the type of the raod that will have its sequence updated
	 * @param sequence The new sequence of the road
	 */
	public void setSequence(Road.Type roadType, int sequence){
		switch (roadType) {
		case NORTH: 
			northSequence = sequence;
			break;
		case FROME:
			fromeSequence = sequence;
			break;
		}
		//set the states based on the new sequence
		setStates();
	}
	
	/**
	 * Get all the states of this sequence
	 * @return A 2d array of the traffic light states across the whole sequence
	 */
	public TrafficLight.State[][] getStates(){
		return states;
	}
	
	/** Set the states according to the current valuein northSequence and fromeSequence.*/
	private void setStates(){
		int length = getSequenceLength(northSequence) + getSequenceLength(fromeSequence);
		states = new TrafficLight.State[length][NUM_LIGHTS];
		TrafficLight.State[][] northStates = getSequence(northSequence,Road.Type.NORTH);
		TrafficLight.State[][] fromeStates = getSequence(fromeSequence,Road.Type.FROME);

		//append north tce and frome road states all into one 2D array
		for(int i = 0 ; i < northStates.length ; i++) {
			for( int j = 0 ; j < northStates[i].length ; j++ ){
				states[i][j] = northStates[i][j];
			}
		}
		for(int i = 0 ; i < fromeStates.length ; i++) {
			for( int j = 0 ; j < fromeStates[i].length ; j++ ){
				states[i + northStates.length][j] = fromeStates[i][j];
			}
		}

	}
	
	/**
	 * Private method used to get the lenght of sequence
	 * @param sequence The sequence to check the length of
	 * @return The lenght of the sequence
	 */
	public int getSequenceLength(int sequence){
		switch(sequence){
		case 0:
			return 7;
		case 1:
			return 9;
		case 2 : 
			return 9;
		case 3 : 
			return 4;
		}
		//return -1 if sequence does not correspond correctly
		return -1;
	}
	
	/**
	 * Get the sequence
	 * @param sequence The sequence value to get 
	 * @param roadType The type of road
	 * @return This will return a 2D array of traffic light states which will represent the entire state of all the traffic lights based on the sequence.
	 */
	private TrafficLight.State[][] getSequence(int sequence, Road.Type roadType){
		TrafficLight.State[][] states = new TrafficLight.State[getSequenceLength(sequence)][NUM_LIGHTS];
		
		//Road number is used for offsetting the traffic lights due to which road they are on
		int roadNum = roadType.getRoadNum();
		//Set all traffic lights to red, then change the states that need to be changed
		for( int i = 0 ; i < states.length; i++ ) {
			for (int j = 0 ; j < NUM_LIGHTS ; j++){
				states[i][j] = TrafficLight.State.RED;
			}
		}
		//massively hard coded statement, there was no point soft coding this.
		//soft coding would have taken as much time, and these are the values we wanted
		switch(sequence){
		case 0:
			states[0][(2 + roadNum)%NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[0][(5 + roadNum)%NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[0][(6 + roadNum)%NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[0][(9 + roadNum)%NUM_LIGHTS] = TrafficLight.State.GREEN;

			states[1][(2 + roadNum)%NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[1][(5 + roadNum)%NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[1][(6 + roadNum)%NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[1][(9 + roadNum)%NUM_LIGHTS] = TrafficLight.State.YELLOW;

			states[3][(1 + roadNum)%NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[3][(4 + roadNum)%NUM_LIGHTS] = TrafficLight.State.GREEN;

			states[4][(0 + roadNum)%NUM_LIGHTS] = TrafficLight.State.OFF;
			states[4][(1 + roadNum)%NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[4][(2 + roadNum)%NUM_LIGHTS] = TrafficLight.State.OFF;
			states[4][(3 + roadNum)%NUM_LIGHTS] = TrafficLight.State.OFF;
			states[4][(4 + roadNum)%NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[4][(5 + roadNum)%NUM_LIGHTS] = TrafficLight.State.OFF;

			states[5][(0 + roadNum)%NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[5][(1 + roadNum)%NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[5][(2 + roadNum)%NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[5][(3 + roadNum)%NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[5][(4 + roadNum)%NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[5][(5 + roadNum)%NUM_LIGHTS] = TrafficLight.State.YELLOW;
			break;
		case 1:
			states[0][(0 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[0][(1 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[0][(2 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[0][(9 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;

			states[1][(0 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[1][(1 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[1][(2 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[1][(9 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;

			states[2][(0 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[2][(1 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			
			states[3][(0 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[3][(1 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[3][(2 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[3][(3 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[3][(4 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[3][(5 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;

			states[4][(0 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[4][(1 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[4][(2 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[4][(3 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[4][(4 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[4][(5 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;

			states[5][(3 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[5][(4 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[5][(5 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;

			states[6][(3 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[6][(4 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[6][(5 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[6][(6 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;

			states[7][(3 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[7][(4 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[7][(5 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[7][(6 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			break;
		
		case 2:
			states[0][(3 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[0][(4 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[0][(5 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[0][(6 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;

			states[1][(3 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[1][(4 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[1][(5 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[1][(6 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;

			states[2][(3 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[2][(4 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			
			states[3][(0 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[3][(1 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[3][(2 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[3][(3 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[3][(4 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[3][(5 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;

			states[4][(0 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[4][(1 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[4][(2 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[4][(3 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[4][(4 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[4][(5 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;

			states[5][(0 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[5][(1 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[5][(2 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;

			states[6][(0 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[6][(1 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[6][(2 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[6][(9 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;

			states[7][(0 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[7][(1 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[7][(2 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[7][(9 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			break;
			
		case 3:
			states[0][(1 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[0][(4 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;

			states[1][(0 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[1][(1 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[1][(2 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[1][(3 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;
			states[1][(4 + roadNum) % NUM_LIGHTS] = TrafficLight.State.GREEN;
			states[1][(5 + roadNum) % NUM_LIGHTS] = TrafficLight.State.OFF;

			states[2][(0 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[2][(1 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[2][(2 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[2][(3 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[2][(4 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
			states[2][(5 + roadNum) % NUM_LIGHTS] = TrafficLight.State.YELLOW;
		}
		return states;
	}

	/** Get the sequence number that north sequence is set to */
	public int getNorth() {
		return northSequence;
	}
	/** Get the sequence number the frome sequence is set to */
	public int getFrome() 
	{
		return fromeSequence;
	}
	/** Set the sequence number of north terrace, this will be called by tlc
	 * @param north The new sequence number of north terrace
	 */
	public void setNorth(int north)
	{
		//ensure it is a valid sequence
		if( north < 0 || north > 3 ) {
			System.err.println("ERROR: Failed to set north tce sequence number");
		} else {
			northSequence = north;
		}
	}
	
	/** Set the sequence number of frome road, this will be called by tlc
	 * @param frome The new sequence number of frome road
	 */
	public void setFrome( int frome ) 
	{
		//ensure it is a valid sequence
		if( frome < 0 || frome > 3 ) {
			System.err.println("ERROR: Failed to set frome road sequence number");
		}else {
			fromeSequence = frome;
		}
	}
	
}
