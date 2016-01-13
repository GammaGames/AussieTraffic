package npe.sim.light;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import npe.sim.event.PedestrianStateChangeEvent;
import npe.sim.event.Event;
import npe.sim.road.Box;
import npe.sim.road.PedestrianLane;
import npe.sim.road.Road;
import npe.sim.road.VehicleLane;

public class TrafficLightController
{
	public final static int DEFAULT_GREEN_DURATION = 30;
	public final static int DEFAULT_RIGHT_GREEN_DURATION = 8;
	public final static int DEFAULT_YELLOW_DURATION = 5;
	public final static int DEFAULT_RED_DELAY = 5; 
	public final static int FLASHING_DELAY = 20;
	public final static int NUM_PED_LIGHTS = 4;
	
	public final static int MIN_RIGHT_DURATION = 6;
	public final static int MAX_RIGHT_DURATION = 20;
	public final static int MIN_STRAIGHT_DURATION = 20;
	public final static int MAX_STRAIGHT_DURATION = 45;
	public final int MIN_YELLOW_DURATION = 4;
	public final int MAX_YELLOW_DURATION = 6;
	public final int MIN_STATE_DURATION = 4;

	

	public final static int NTW_DEFAULT_RIGHT = 7;
	public final static int NTE_DEFAULT_RIGHT = 7;
	public final static int NTW_DEFAULT_STRAIGHT = 23;
	public final static int NTE_DEFAULT_STRAIGHT = 23;
	public final static int NT_DEFAULT_YELLOW = 5;
	
	public final static int FRN_DEFAULT_RIGHT = 7;
	public final static int FRS_DEFAULT_RIGHT = 7;
	public final static int FRN_DEFAULT_STRAIGHT = 25;
	public final static int FRS_DEFAULT_STRAIGHT = 25;
	public final static int FR_DEFAULT_YELLOW = 5;

	
	
	/** The duration of the north tce west right hand turn */
	private int ntw_right = 10;
	/** The duration of the norht tce east right hand turn */
	private int nte_right = 5;
	/** The duration of the straight light on north tce west*/
	private int ntw_straight = 23;
	/** The duration of the straight light on the north tce east */
	private int nte_straight = 23;
	/** The duration of the nt yellow lights */
	private int nt_yellow = 5;
	/** The duration of the right hand turn on frome road north */
	private int frn_right = 7;
	/** The duration of the right hand turn on frome road south */
	private int frs_right = 7;
	/** The duration of the straight light on from road north */
	private int frn_straight = 25;
	/** The duration of the straight light on from road south */
	private int frs_straight = 25;
	
	/** Traffic lights that the controller has controller over */
	private TrafficLight[] trafficLight;
	
	/**Pedestrian lights that the controller has control over */
	private PedestrianLight[] pedestrianLight;
	
	/** States of the traffic light sequence */
	private State[] state;
	
	/** The traffic light sequence */
	private Sequence sequence;
	
	

	/** Integer to represent the current state the intersection is in */
	private int currentState;
	
	
	/**
	 * Construct Traffic Light Controller with a set number of lights
	 * @param numTrafficLights Number of traffic lights the controller will controll
	 */
	public TrafficLightController( int numTrafficLights )
	{
		//read the csv

		//-------------INTIALISE---------------//
		trafficLight = new TrafficLight[numTrafficLights];
		pedestrianLight = new PedestrianLight[NUM_PED_LIGHTS];
		sequence = new Sequence();
		readCsv();
		
		//Initialise all traffic lights
		for( int i = 0 ; i < trafficLight.length ; i++ ) {
			trafficLight[i] = new TrafficLight(i);
		}
		
		//Initialise all pedestrian lights
		for ( int i = 0 ; i < pedestrianLight.length ; i++ ) {
			pedestrianLight[i] = new PedestrianLight(i);
		}

		//Get states from sequence
		TrafficLight.State[][] states = sequence.getStates();

		//Initialise all states of the intersection
		state = new State[states.length];
		for ( int i = 0 ; i < states.length ; i++ ) {
			state[i] = new State(i,states[i]);
		}
		currentState = state.length - 1; //first state should be a red light state
		
		//Set default durations of the system
		setDefaultStateDuration();

		//Set green/yellow/red durations for sequence
		for( int i = 0 ; i < state.length ; i++ ) {
			for ( int j = 0 ; j < trafficLight.length ; j++ ) {
				increaseDuration( trafficLight[j] , state[i].getTrafficLightState(j) , state[i].getStateDuration() );
			}
		}
	}
	
	
	/**
	 * Loads a config file
	 */
	private void readCsv()
	{
		FileReader fr = null;
		try {
			fr = new FileReader("config.csv");
			BufferedReader br = new BufferedReader(fr);
			String s = br.readLine();
			String[] tokens = s.split(",");
			int northSeq = Integer.parseInt(tokens[0]);
			int fromeSeq = Integer.parseInt(tokens[1]);
			s = br.readLine();
			tokens = s.split(",");
			ntw_right = Integer.parseInt(tokens[0]);
			nte_right = Integer.parseInt(tokens[1]);
			ntw_straight = Integer.parseInt(tokens[2]);
			nte_straight = Integer.parseInt(tokens[3]);
			nt_yellow = Integer.parseInt(tokens[4]);
			s = br.readLine();
			tokens = s.split(",");
			frn_right = Integer.parseInt(tokens[0]);
			frs_right = Integer.parseInt(tokens[1]);
			frn_straight = Integer.parseInt(tokens[2]);
			frs_straight = Integer.parseInt(tokens[3]);
			//set the sequence numbers
			sequence.setSequence(Road.Type.FROME, fromeSeq);
			sequence.setSequence(Road.Type.NORTH, northSeq);

		} catch (IOException e){
			System.err.println("WARNING: Failed to read from config file: Loading defaults");
			//set the defaults up
			setDefaults();
		} catch (NumberFormatException e) {
			System.err.println("Warning: CSV File is corrupt, using default configs");
			//If values in the csv are not valid, set as defaults
			setDefaults();
		}
		
		finally {
			try {
				if( fr != null ) {
					fr.close();
				}
			} catch (IOException e) {
				System.err.println("WARNING: Failed to close CSV file");
			}
		}
	}

	private void setDefaults() 
	{
		ntw_right = NTW_DEFAULT_RIGHT;
		nte_right = NTE_DEFAULT_RIGHT;
		ntw_straight = NTW_DEFAULT_STRAIGHT;
		nte_straight = NTE_DEFAULT_STRAIGHT;
		nt_yellow = NT_DEFAULT_YELLOW;
		frn_right = FRN_DEFAULT_RIGHT;
		frs_right = FRS_DEFAULT_RIGHT;
		frn_straight = FRN_DEFAULT_STRAIGHT;
		frs_straight = FRS_DEFAULT_STRAIGHT;
	}

	/**
	 * Change the duration of a traffic light. This method will also update the duration of the state changed
	 * @param tid traffic light number that has been changed
	 * @param state State of the traffic light whose duration is being changed
	 * @param duration the new duration of the traffic light state
	 * @return The new duration of the traffic light. On the case when the new duration is invalid it will return the orignal duration
	 */
	public int changeLight(int tid, TrafficLight.State state, int duration )
	{
		//find all the states that will be changed.
		ArrayList<State> statesChanged = findTrafficLightInState(tid,state);
		//if no states were changed, then we want to return 0
		if( statesChanged.isEmpty() ) {
			return 0;
		}
		if( state == TrafficLight.State.YELLOW ) {
			//loop through all the states that have been changed, and update the time
			for( int i = 0 ; i < statesChanged.size() ; i++ ) {
				//get the state id that is being changed
				int stateId = statesChanged.get(i).getId();
				//find out what the duration previously was
				int oldDuration = this.state[stateId].getStateDuration();
				//set the state duration to duration.
				this.state[stateId].setStateDuration(duration);
				//get the difference of durations. This will be used to increase/decrease the time of of the light in that sequence.
				int durationDifference = duration - oldDuration;
				//loop through all traffic lights, if the state is yellow, increase the duration by duration.
				//for all other traffic lights increase by the duration difference
				for ( int j = 0 ; j < this.trafficLight.length ; j++ ) {
					if( this.state[stateId].getTrafficLightState(j) == TrafficLight.State.YELLOW ) {
						increaseDuration(this.trafficLight[j],this.state[stateId].getTrafficLightState(j), duration);
					} else {
						increaseDuration(this.trafficLight[j],this.state[stateId].getTrafficLightState(j), durationDifference);
					}
				}
			}
			//successful -> return duration
			saveDurations();
			return duration ;
		}
		int oldDuration = 0; 
		State finalState = null;

		
		///find out what kind of light it is
		switch ( trafficLight[tid].TYPE ){
		//left and right lanes simply change only 1 state, the state at which they occur in
		case LEFT:
			//$FALL_THROUGH$
		case RIGHT:
			finalState = statesChanged.get(0);
			break;
		case STRAIGHT:
			//the state that will be changed will be the different depending on what sequence is set.
			int sequenceNum;
			//the traffic light is on north tce if its id is from 0-5 and frome rd from 6-11
			if( tid / 6 == 0 ) {
				sequenceNum = sequence.getNorth();
			} else {
				sequenceNum = sequence.getFrome();
			}
			//depending on what the sequence is depends on what states will being changed
			switch (sequenceNum) {
			case 0 :
			//$FALL_THROUGH$
			case 3 :
				//sequence 1 and 4 the 2nd state changed is the state that we want
				finalState = statesChanged.get(1);
				break;
			case 1 :
				//if its the 2nd sequence the state changed will be the 
				//need to wrok out if the traffic light is on the first side of the road or the opposite side
				if( ( (tid / 3 ) % 2 ) == 0 ) {
					finalState = statesChanged.get(3);
				} else {
					finalState = statesChanged.get(2);
				}
				break;
			case 2: 
				//if its the 3rd sequence the state changed will be the 
				//need to wrok out if the traffic light is on the first side of the road or the opposite side
				if( ( (tid / 3 ) % 2 ) == 0 ) {
					finalState = statesChanged.get(2);
				} else {
					finalState = statesChanged.get(3);
				}
				break;
			}
			
			break;
		}

		//the duration that is being changed is actually the duration passed in minus all the durations of the state changed events except the final state
		for( State s : statesChanged ) {
			if( s!= finalState) {
				duration = duration - s.getStateDuration();
			}
		}
		int minDuration = getMinDuration(finalState);
		int maxDuration = getMaxDuration(finalState);
		//ensure that duration is greater than 5
		//if it is not 5 seconds in length, then we want dont want to update the state
		if( duration < minDuration || duration > maxDuration) {
			//unsuccesful -> return the minimum number of the state
			return trafficLight[tid].getGreenDuration();
		}
		
		oldDuration = finalState.getStateDuration();
		finalState.setStateDuration(duration);

		//Get the duration difference. This will be used to the new state duration
		int durationDifference = duration - oldDuration;
		//Go through all traffic lights in final state, and update the duration of the light in that state.
		//ie all red lights in the final state add the red light duration
		updateTrafficLightDurations(finalState,durationDifference);
		//successful -> return true
		return trafficLight[tid].getGreenDuration();
	}
	
	
	/**
	 * Find Which states the traffic light is a certain state/colour in. Ie If tlState was green, find all states that the traffic light is green in.
	 * @param lightIndex The index of the Traiffic light that is being checked
	 * @param tlState The states that is being searched for
	 * @return An arrayList of states. That the traffic light at light index = tlState
	 */
	private ArrayList<State> findTrafficLightInState(int lightIndex, TrafficLight.State tlState) 
	{
		ArrayList<State> statesFound = new ArrayList<State>();
		
		for(int i = 0 ; i<state.length ; i++) {
			TrafficLight.State tempState = state[i].getTrafficLightState(lightIndex);
			if( tempState == tlState ) {
				statesFound.add(state[i]);
			}
		}
		return statesFound;
	}
	
	/** Set all states to the default durations */
	private void setDefaultStateDuration() 
	{
		int stateSize = 0;
		switch ( sequence.getNorth() ){
		case 0 :
			stateSize = setDefaultSeqOne(stateSize);
			break;
		case 1:
			stateSize = setDefaultSeqTwo(stateSize);
			break;
		case 2 :
			stateSize = setDefaultSeqThree(stateSize);
			break;
		case 3 : 
			stateSize = setDefaultSeqFour(stateSize);
			break;
			default:
				//TODO HANDLE SOME SORT OF CRASH
				System.err.println("ERROR: Invalid sequence intered in traffic light controller");
				break;
		}
		
		switch ( sequence.getFrome() ){
		case 0 :
			stateSize = setDefaultSeqOne(stateSize);
			break;
		case 1:
			stateSize = setDefaultSeqTwo(stateSize);
			break;
		case 2 :
			stateSize = setDefaultSeqThree(stateSize);
			break;
		case 3 : 
			stateSize = setDefaultSeqFour(stateSize);
			break;
			default:
				//TODO HANDLE SOME SORT OF CRASH
				System.err.println("ERROR: Invalid sequence intered in traffic light controller");
				break;
		}
		//ensure that firstState + 5 is at least 4
		for( State s : state){
			if( s.getStateDuration() < MIN_STATE_DURATION) {
				s.setStateDuration(MIN_STATE_DURATION);
			}
		}
		//ensure that all traffic lights are within there maximum values
	}
	
	/**
	 * Get a jtable of the traffic light sequence
	 * @return JTable of traffic light sequence
	 */
	public JTable getJTable(int width, int height)
	{
		//make sure that the sequence is updateed
		updateSequence();
		//Create a tabel model
		TrafficLightModel model = new TrafficLightModel();
		JTable trafficLightT;
		trafficLightT = new JTable(model);
		//trafficLightT.setEnabled(false);
		//set background colour to match background of gui
		trafficLightT.setBackground(new Color(238,238,238));
		//table has no rows to start with
		model.setRowCount(0);
		//set number of columns to number of traffic lights + 1, the +1 is to account for the column for state durations
		model.setColumnCount(trafficLight.length + 1);
		//set the cell rederer to the one i created, see CellRender below.
		trafficLightT.setDefaultRenderer(Object.class, new CellRender());
		//set the size of the table to the parameters passed in
		trafficLightT.setSize(width,height); 
		
		//row length varia
		int rowLength = trafficLight.length + 1;
		Vector<String> row1 = new Vector<String>(trafficLight.length + 1);
		
		row1.add("<html>T<br>i<br>m<br>e<br>(s)<br></html>");
		for( int i = 0 ; i < trafficLight.length ; i++ ) {
			row1.add( "<html>L<br>i<br>g<br>h<br>t<br> " + (i + 1) );
		}

		model.addRow(row1);
		
		//add all states to the table
		for ( int i = 0 ; i < state.length ; i++ ) {
			Vector<Object> row = new Vector<Object>(rowLength);
			//add the state duration to the first column of the row
			row.add(Integer.toString(state[i].getStateDuration()));
			for ( int j = 0 ; j < trafficLight.length ; j++ ) {
				row.add(state[i].getTrafficLightState(j));
			}
			//set the cell as editable if it is yellow, green_off or green_right
			switch( state[i].getType()){
			case YELLOW:
			case GREEN_OFF:
			case GREEN_RIGHT:
				model.addEditableState(i);
				break;
			}

			model.addRow(row);
		}
		
		//--------SCALE ROWS BY STATE DURATION--------//
		int totalDuration = totalDuration();
		trafficLightT.setRowHeight(0,60);
		//get the total height to play with for the cells, I will scale the height of each row based on its state duration
		int stateHeight = height - trafficLightT.getRowHeight(0); 
		for ( int i = 0 ; i < state.length ; i++ ) {
			int stateDuration = state[i].getStateDuration();
			double rowHeight = ((double) stateDuration / (double)totalDuration) * stateHeight;
			trafficLightT.setRowHeight(i+1, (int) rowHeight);
		}
		

		trafficLightT.setShowGrid(true);
		return trafficLightT;
	}
	
	/** Get the number of states in the currenly set sequence */
	public int getNumStates()
	{
		return state.length;
	}
	
	/** To string method used for debugging, to get all the traffic light durations */
	public String getTrafficLightDurations()
	{
		String retString = "";
		for ( int i = 0 ; i < trafficLight.length ; i ++ ) {
			retString += "TrafficLight: " +  i + "\n" + trafficLight[i].getDurations();
		}
		return retString;
	}
	
	/**
	 * Cell renderer, used to paint the colours of the cell the appropriate colours.
	 */
	private class CellRender extends DefaultTableCellRenderer
	{
		/** overiding getTable cell rederer
		 * This rendered will paint all cells with a light the appropriate colour
		 * It overides the value of traffic light state cells so they dont display there string
		 */
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			Font font = new Font("Serif",Font.PLAIN,8);
			if( value instanceof TrafficLight.State ){
				switch( (TrafficLight.State) value ) {
				case RED : 
					cell.setBackground(Color.RED);
					break;
				case GREEN : 
					cell.setBackground(Color.GREEN);
					break;
				case YELLOW :
					cell.setBackground(Color.YELLOW);
					break;
				case OFF :
					cell.setBackground(Color.WHITE);
				}
				super.setValue("");	
			} else if (value instanceof Integer){
				cell.setBackground(Color.WHITE);
				cell.setFont(font);
			} else if (value instanceof String) {
				cell.setBackground(Color.WHITE);
				cell.setFont(font);
			}
				
			return cell;
		}


	}
	
	/**
	 * Table light model. Used by the traffic light table
	 * The table model is needed for enabling certain cells to be editable
	 */
	private class TrafficLightModel extends DefaultTableModel
	{
		/** An array list of integers corresponding to the the editable cells in the table */
		private ArrayList<Integer> editableStates = new ArrayList<Integer>();

		/**
		 * Method to determine whether a cell is editable
		 * @param row The row of the table the cell is at
		 * @param col The column of the table the cell is at
		 * @return false for all cells other than the leftmost cells. For the leftmost cells it will return true for the editable states
		 */
		public boolean isCellEditable(int row , int col )
		{
			//the top row contains titles and hence is not editable
			if( row == 0 ) {
				return false;
			}
			//only the left most column will be editable, as that contains the state times
			if( col != 0 ) {
				return false;
			}
			//we want to return true if the row number appears in editable states
			//the row number will have to be incremented by 1 to offest the title row.
			for( int i = 0 ; i < editableStates.size() ; i++ ) {
				if( row -1 == editableStates.get(i) ){
					return true;
				}
			}
			//if the column was not in editable states at all return false;
			return false;
		}
		/**
		 * Add an editable state to the table model
		 * @param state The state number that will now be editable
		 */
		public void addEditableState(int state)
		{
			editableStates.add(state);
		}
	}
	
	/** Get the total duration of one loop of the entire sequence  */
	private int totalDuration()
	{
		int duration = 0;
		for( int i = 0 ; i < state.length ; i++ ){
			duration += state[i].getStateDuration();
		}
		return duration;
	}
	
	/**
	 * Private function used to increment the duration of a colour of a traffic light.
	 * @param light The Traffic light to change
	 * @param duration The time added onto the duration of that colour
	 */
	private void increaseDuration(TrafficLight light, TrafficLight.State state, int duration) 
	{
			switch(state){
			case RED :
				light.addRedDuration(duration);
				break;
			case YELLOW :
				light.setYellowDuration(duration);
				break;
			case GREEN :
				light.addGreenDuration(duration);
				break;
			}

	}

	/**
	 *  Get the duration total red duration of a traffic light
	 * @param selected The traffic light id
	 * @return The red duration of the traffic light
	 */
	public int getTrafficLightRedDuration(int selected) 
	{
		return trafficLight[selected].getRedDuration();
	}
	
	/**
	 * Get the yellow duration of a selected traffic light
	 * @param selected The index of the traffic light
	 * @return The yellow duration of the selected traffic light
	 */
	public int getTrafficLightYellowDuration(int selected)
	{
		return trafficLight[selected].getYellowDuration();
	}

	/**
	 * Get the total green duration of the selected traffic light
	 * @param selected The index of the traffic light
	 * @return The total green duration of the selected traffic light
	 */
	public int getTrafficLightGreenDuration(int selected) 
	{
		return trafficLight[selected].getGreenDuration();
	}
	
	/**
	 * Get the current sequence of the traffic light system
	 * @return The sequence that the traffic light controller has set
	 */
	public Sequence getSequence()
	{
		return sequence;
	}
	
	/** Update the traffic light sequence, based on the new sequence that has been set. */
	public void updateSequence()
	{
		//Get states from sequence
		TrafficLight.State[][] states = sequence.getStates();

		//check to see if current sequence is the same as updated sequence will be, if so don't want to create a new one
		if (checkSequence(states)){
			return;
		}

		//Initialise all states of the intersection
		state = new State[states.length];
		for ( int i = 0 ; i < states.length ; i++ ) {
			state[i] = new State(i,states[i]);
		}
		
		//update current state to last state, ie starts at a red light
		currentState = state.length -1;
		
		//Set default durations
		setDefaultStateDuration();
		
		//reset all traffic light durations to 0
		for( TrafficLight tl : trafficLight ) {
			tl.clearDurations();
		}
		//Set green/yellow/red durations for sequence
		for( int i = 0 ; i < state.length ; i++ ) {
			for ( int j = 0 ; j < trafficLight.length ; j++ ) {
				increaseDuration( trafficLight[j] , state[i].getTrafficLightState(j) , state[i].getStateDuration() );
			}
		}
		
		//ensure that all new traffic light durations are within there maximum bounds
		for( TrafficLight tl : trafficLight ) {
			switch ( tl.TYPE ) {
			case LEFT :
			case RIGHT :
				//if the green duration > max that traffic lights duration to 
				if( tl.getGreenDuration() > MAX_RIGHT_DURATION ) {
					changeLight(tl.getId(),TrafficLight.State.GREEN,MAX_RIGHT_DURATION);
				}
				break;
			case STRAIGHT : 
				if( tl.getGreenDuration() > MAX_STRAIGHT_DURATION ) {
					System.out.println("MAX");
					changeLight(tl.getId(),TrafficLight.State.GREEN,MAX_STRAIGHT_DURATION);
				}
				break;
				
			}
		}

		
		
	}
	
	/**
	 *  Adds the appropriate lane to a traffic light(s). 
	 *  1 lane may have multiple traffic lights that affect it 
	 * One traffic light might affect multiple lanes 
	 * */
	public void addLane(VehicleLane lane)
	{
		VehicleLane.Type dir = lane.getType();
		Road.Type road = lane.road().getROAD();
		int[] lightOffset = dir.getLights();
		for ( int i = 0 ; i < lightOffset.length ; i++ ) {
				int light = lightOffset[i]; // the number of the light that the lane will be added to
				// if the direction of the lane is 180 or 270, it is on the opposite side of the road, so 3 needs to be added to the light
				if ( lane.dirDeg() == 180 || lane.dirDeg() == 270 ) {
					light +=3;
				}
				trafficLight[light + road.getRoadNum()].addLane(lane); 
		}
	}
	
	/**
	 * Change all the states of the traffic lights. THis will be called by traffic light controller when a state changed event arrises
	 * @return The time of the new state.
	 */
	public int changeState()
	{
		currentState = (currentState + 1) % state.length;
		for( int i = 0 ; i < trafficLight.length ; i++ ) {
			trafficLight[i].changeState(state[currentState].getTrafficLightState(i));
		}
		return state[currentState].getStateDuration();
	}
	
	/** The tick method, this will be called during simulation at each step. */
	public void tick() 
	{
		for ( TrafficLight tl : trafficLight ) {
			tl.tick();
		}
		for (PedestrianLight pl : pedestrianLight ){
			pl.tick();
		}
	}
	
	/**
	 * The daw method, This will call draw on every traffic light and pedestrian light
	 * @param g The graphics to draw to.
	 */
	public void draw(Graphics2D g){
		for ( TrafficLight tl : trafficLight ) {
			tl.draw(g);
		}
		for (PedestrianLight pl : pedestrianLight ){
			pl.draw(g);
		}
	}

	/**
	 * Check to see if the current set of states will be identical to the updated set of state
	 * @param states A 2d array of states, thtat full show the system
	 * @return true if the current state is the same as the new sequence, false otherwise
	 */
	private boolean checkSequence(TrafficLight.State[][] states){
		//if the lengths are not equal, cleary they are not the same, return false.
		if( state.length != states.length ) {
			return false;
		}
		
		for ( int i = 0 ; i < states.length ; i ++ ) {
			for ( int j = 0 ; j < states[i].length ; j++ ) {
				if(states[i][j] != state[i].getTrafficLightState(j)) {
					return false;
				}
			}
		}
		return true;

	}
	
	/**
	 * Get all the starting events for all pedestrian lights
	 * @return An arraylist of the starting events for all pedestrian lights
	 */
	public Event[] getPedestrianLightEvents(){
		//go through all the states.
		//if the state has green straight arrows in it, determine if there are any right green arrows in the same state, if there none, we have ourselves a green man
		//once we find ourselfs a green man, find out the state in which the arrow turns green arrow turns yellow
		//from here we have the total duration of the light
		//we can then set a flashing man, to total time minus a constant that can be changed once we know what we want it to be
		
		int start = 0;
		int end = 1;
		int[][] stateInfo = new int[4][2];
		for( int i = 0 ; i < stateInfo.length ; i++){
			for (int j = 0 ; j < stateInfo[i].length ; j++){
				stateInfo[i][j] = -1;
			}
		}
		
		Event[] events = new Event[NUM_PED_LIGHTS * 3];
		
		int currentTime = 0;
		for( int i = 0 ; i < state.length ; i++ ) {
			//first part of the array will hold the index of the pedestrian light
			//the second part of the array will hold the start state index in its first, the end state index in its second, and the state duration in its third
			
			//only looping though the pedestrian lights.
			//I will get the traffic light by mult the index by multipliying by the index and adding 1 (1 for the straight traffic light)
			for(int j = 0 ; j < pedestrianLight.length ; j ++){
				int straightIndex = 3*j + 1; //the index of the straight light
				//get the state of the traffic light for the current state
				TrafficLight.State tlState = state[i].getTrafficLightState(straightIndex);
				switch(tlState) {
				case GREEN:
					
					//get the state of the right hand traffic light of the opposite road.
					//to get the traffic light on the opposite side we need to add 1 to get the rhs, then add 3, to get the opposite side, then mod 6 to wrap it arround
					int oppRightIndex = ( (straightIndex + 1) + 3 ) % 6;
					TrafficLight.State rightState = state[i].getTrafficLightState(oppRightIndex);
					switch( rightState ) {
					case OFF:
					case RED:
						//if it is red, then we know that it is safe to turn the light on
						//check to see if the we have found the start state yet
						//if we havnt set the start state yet, set it to the current state
						if( stateInfo[j][start] == -1 ) {
							stateInfo[j][start] = currentTime;
						}
						break;
				}
				break;

				case YELLOW:
					//we call the cut off point a yellow, giving the cars a few extra seconds than the pedestrians
					//at this point we want to se the end state to this staet
					
					stateInfo[j][end] = currentTime + state[i].getStateDuration();
					//we can add the increase j's duration by the state duration if we want the state to end exactly as the light turns red
					break;
				}

			}
			currentTime += state[i].getStateDuration();
		}
		
		for( int i = 0 ; i < stateInfo.length ; i++){
			int index = i*3;
			//create the start event
			events[index] = new PedestrianStateChangeEvent(stateInfo[i][start],pedestrianLight[i],totalDuration());
			//create the 'flashing' event
			events[index+1] = new PedestrianStateChangeEvent(stateInfo[i][end] - FLASHING_DELAY ,pedestrianLight[i],totalDuration());
			//create stopped event
			events[index+2] = new PedestrianStateChangeEvent(stateInfo[i][end] ,pedestrianLight[i],totalDuration());
			
		}

		
		return events;
	}
	
	/**
	 * The the x coordinate of a traffic light
	 * @param tid The traffic light id that we are wanting to change
	 * @param x The new x value of the traffic light
	 * @param y The new y value of the traffic light
	 */
	public void setTrafficLightCoords(int tid, int x, int y)
	{
		trafficLight[tid].setX(x);
		trafficLight[tid].setY(y);
	}
	
	public void setPedestrianLightCoords(int pid, int x[], int y[]) 
	{
		pedestrianLight[pid].setX(x);
		pedestrianLight[pid].setY(y);
	}
	
	/**
	 * Add pedestiran lanes to a traffic light
	 * @param road The road with the pedestrian lanes  on it
	 */
	public void addPedestrianLanes(Road road)
	{
		//get the lanes from the road
		ArrayList<PedestrianLane> lanes = road.getPedestrianLanes();
		
		//if the lanes are on north tce the roads corresspon to light 0 or 1
		//else they correspond to light 2 3
		switch( road.getROAD()){
		case NORTH :
			for( int i =  0 ; i < lanes.size() ; i ++ ) {
				pedestrianLight[i/2].addPedestrianLane(lanes.get(i));
			}
			break;
		case FROME :
			for( int i =  0 ; i < lanes.size() ; i ++ ) {
				pedestrianLight[i/2 + 2].addPedestrianLane(lanes.get(i));
			}
			break;
			
		}
	}
	
	/**
	 * Add a box to a traffic light, the traffic light will tell the car whether to start or stop base on whether it is safe for the car tot turn
	 * @param tid The traffic light to add the box to
	 * @param box The box that will be added
	 */
	public void addBox(int tid, Box box)
	{
		trafficLight[tid].addBox(box);
	}

	/**
	 * Get the state referenced by this id
	 * @param i The id of the state
	 * @return The state i
	 */
	public State getState(int i)
	{
		return state[i];
	}
	
	/**
	 * Draw the traffic light defined by state s
	 * @param i The index of the state to draw
	 */
	public void updateState(int stateID)
	{
		for( int i = 0 ; i < trafficLight.length ; i++ ) {
			trafficLight[i].changeState(state[stateID].getTrafficLightState(i));
		}

	}

	/** Set the default states lenghts of sequence 1 */
	private int setDefaultSeqOne(int firstState)
	{
		//if the first state = 0, then we are on north tce,
		//if the first state >0 then we are on frome road
		if( firstState == 0) {
			state[firstState].setStateDuration(ntw_right);
			state[firstState + 1].setStateDuration(nt_yellow);
			state[firstState + 2].setStateDuration(DEFAULT_RED_DELAY);
			state[firstState + 3].setStateDuration(DEFAULT_RED_DELAY);
			state[firstState + 4].setStateDuration(ntw_straight - DEFAULT_RED_DELAY);
			state[firstState + 5].setStateDuration(nt_yellow);
			state[firstState + 6].setStateDuration(DEFAULT_RED_DELAY);
		} else {
			state[firstState].setStateDuration(frn_right);
			state[firstState + 1].setStateDuration(nt_yellow);
			state[firstState + 2].setStateDuration(DEFAULT_RED_DELAY);
			state[firstState + 3].setStateDuration(DEFAULT_RED_DELAY);
			state[firstState + 4].setStateDuration(frn_straight - DEFAULT_RED_DELAY);
			state[firstState + 5].setStateDuration(nt_yellow);
			state[firstState + 6].setStateDuration(DEFAULT_RED_DELAY);
		}
		return 7;
	}

	/** Set the default states lenghts of sequence 1 */
	private int setDefaultSeqTwo(int firstState)
	{
		//if the first state = 0, then we are on north tce,
		//if the first state >0 then we are on frome road
		int stateLength = 9;
		if( firstState == 0) {
			state[firstState].setStateDuration(ntw_right);
			state[firstState + 1].setStateDuration(nt_yellow);
			state[firstState + 2].setStateDuration(DEFAULT_RED_DELAY);
			state[firstState + 3].setStateDuration(ntw_straight - ntw_right -nt_yellow - DEFAULT_RED_DELAY);
			state[firstState + 4].setStateDuration(nt_yellow);
			state[firstState + 5].setStateDuration(nte_straight - nte_right - nt_yellow - state[firstState+3].getStateDuration());
			state[firstState + 6].setStateDuration(nte_right);
			state[firstState + 7].setStateDuration(nt_yellow);
			state[firstState + 8].setStateDuration(DEFAULT_RED_DELAY);
		} else {
			state[firstState].setStateDuration(frn_right);
			state[firstState + 1].setStateDuration(nt_yellow);
			state[firstState + 2].setStateDuration(DEFAULT_RED_DELAY);
			state[firstState + 3].setStateDuration(frn_straight - frn_right -nt_yellow - DEFAULT_RED_DELAY);
			state[firstState + 4].setStateDuration(nt_yellow);
			state[firstState + 5].setStateDuration(frs_straight - frs_right - nt_yellow - state[firstState+3].getStateDuration());
			state[firstState + 6].setStateDuration(frs_right);
			state[firstState + 7].setStateDuration(nt_yellow);
			state[firstState + 8].setStateDuration(DEFAULT_RED_DELAY);
		}
		//for state 2 and 3 we want to set the 3 state as a 
		state[firstState + 2].setAsRed();
		return stateLength;
	}

	/** Set the default states lenghts of sequence 1 */
	private int setDefaultSeqThree(int firstState)
	{
		int stateLength = 9;
		if( firstState == 0) {
			state[firstState].setStateDuration(nte_right);
			state[firstState + 1].setStateDuration(nt_yellow);
			state[firstState + 2].setStateDuration(DEFAULT_RED_DELAY);
			state[firstState + 3].setStateDuration(nte_straight - nte_right -nt_yellow - DEFAULT_RED_DELAY);
			state[firstState + 4].setStateDuration(nt_yellow);
			state[firstState + 5].setStateDuration(ntw_straight - ntw_right - nt_yellow -state[firstState+3].getStateDuration());
			state[firstState + 6].setStateDuration(ntw_right);
			state[firstState + 7].setStateDuration(nt_yellow);
			state[firstState + 8].setStateDuration(DEFAULT_RED_DELAY);
		} else {
			state[firstState].setStateDuration(frs_right);
			state[firstState + 1].setStateDuration(nt_yellow);
			state[firstState + 2].setStateDuration(DEFAULT_RED_DELAY);
			state[firstState + 3].setStateDuration(frs_straight - frs_right -nt_yellow - DEFAULT_RED_DELAY);
			state[firstState + 4].setStateDuration(nt_yellow);
			state[firstState + 5].setStateDuration(frn_straight - frn_right - nt_yellow - state[firstState+3].getStateDuration());
			state[firstState + 6].setStateDuration(frn_right);
			state[firstState + 7].setStateDuration(nt_yellow);
			state[firstState + 8].setStateDuration(DEFAULT_RED_DELAY);
		}
		state[firstState + 2].setAsRed();
		return stateLength;
	}

	/** Set the default states lenghts of sequence 1 */
	private int setDefaultSeqFour(int firstState)
	{
		int stateLength = 4;

		if( firstState == 0) {
			state[firstState + 0].setStateDuration(DEFAULT_RED_DELAY);
			state[firstState + 1].setStateDuration(ntw_straight - DEFAULT_RED_DELAY);
			state[firstState + 2].setStateDuration(nt_yellow);
			state[firstState + 3].setStateDuration(DEFAULT_RED_DELAY);
		} else {
			state[firstState + 0].setStateDuration(DEFAULT_RED_DELAY);
			state[firstState + 1].setStateDuration(frn_straight - DEFAULT_RED_DELAY);
			state[firstState + 2].setStateDuration(nt_yellow);
			state[firstState + 3].setStateDuration(DEFAULT_RED_DELAY);
		}

		return stateLength;
	}

	public int changeStateDuration(int sid, int duration)
	{
		State state = this.state[sid];
		
		int minDuration = getMinDuration(state);
		int maxDuration = getMaxDuration(state);
		System.out.println("MIN: " + minDuration);
		System.out.println("MAX: " + maxDuration);
		//if the duration is < 5 we want to set back it back to its original duration
		if( duration < minDuration ) {
			return state.getStateDuration();
		} else if ( duration > maxDuration ) {
			return state.getStateDuration();
		}

		int oldDuration = state.getStateDuration();
		int durationDifference = duration - oldDuration;
		state.setStateDuration( duration);
		updateTrafficLightDurations(state,durationDifference);
		//after update traffic lights, i want to ensure that all traffic lights have valid length
		if ( !assertTrafficLightDurations() ) {
			System.out.println("Invalid tl");
			updateTrafficLightDurations(state, 0-durationDifference);
			state.setStateDuration(oldDuration);
		}
		return state.getStateDuration();
	}
	
	/** Get the maxium duration that this state can be
	 * @param state The state to query
	 * @return The maximum duration that the state can be
	 */
	private int getMaxDuration(State state) 
	{
		switch ( state.getType()) {
			case GREEN_RIGHT :
			case GREEN :
				return MAX_RIGHT_DURATION;
			case YELLOW :
				return MIN_YELLOW_DURATION;
			case GREEN_OFF :
				//this is where the values start to change
				//if we are in sequence 1 or 4 -> max duration is max_straight - red delay
				//sequence 2 -> max duration = max_straight - either the 3 states above or below
				// how to work out if its above or below
				//we want to check out the state below this one
				//if the next state down is yellow, then we want to go up
				//else if the next state down is green we want to go down
				//this logic comes from how the states were
				
				//to work out what sequence we are in we need to get the north tce and frome rd sequence numbers
				int northTce = sequence.getNorth();
				int fromeRd = sequence.getFrome();
				int firstStage = sequence.getSequenceLength(northTce);
				//work out what state we are in
				int seqNum;
				int id = state.getId();
				if( id < firstStage ) {
					//we are in the first stage
					seqNum = northTce;
				} else {
					//else we are in the second stage
					seqNum = fromeRd;
				}
				
				switch ( seqNum ) {
				case 0 :
					//$FALL_THROUGH$
				case 3 : 
					return MAX_STRAIGHT_DURATION - DEFAULT_RED_DELAY;
				case 1 :
					//$FALL_THROUGH$
				case 2 :
					boolean down;
					State next = this.state[id+1];
					if( next.getType() == State.Type.YELLOW ) {
						//we want to search up
						down = false;
					} else {
						down = true;
						//we want to search down
					}
					//if we are in case 2 swap down and up
					if( seqNum == 2 ) {
						down = !down;
					}
					int difference = 0;
					int count = 0;
					//difference is the sum of the 3 state durations above/below the current one
					while( count < 3 ) {
						id --;
						count ++;
						//if we are going down, the last id is below the original state id
						if( down && count == 3 ) {
							id = id + 4;
						}
						difference += this.state[id].getStateDuration();
					}
					return MAX_STRAIGHT_DURATION - difference;
				}
		}
		System.err.println("ERROR: Failed to update traffic light table");
		return -1;
	}


	/** Get the minimum duration that this state can be
	 * @param state The state to query
	 * @return The minimum duration that the state can be
	 */
	private int getMinDuration(State state)
	{
		int min = -1;
		switch ( state.getType()) {
		case GREEN_RIGHT :
		case GREEN :
			min  =MIN_RIGHT_DURATION;
			break;
		case YELLOW :
			min = MIN_YELLOW_DURATION;
			break;
		case GREEN_OFF :
			//this is where the values start to change
			//if we are in sequence 1 or 4 -> max duration is max_straight - red delay
			//sequence 2 -> max duration = max_straight - either the 3 states above or below
			// how to work out if its above or below
			//we want to check out the state below this one
			//if the next state down is yellow, then we want to go up
			//else if the next state down is green we want to go down
			//this logic comes from how the states were
			
			//to work out what sequence we are in we need to get the north tce and frome rd sequence numbers
			int northTce = sequence.getNorth();
			int fromeRd = sequence.getFrome();
			int firstStage = sequence.getSequenceLength(northTce);
			//work out what state we are in
			int seqNum;
			int id = state.getId();
			if( id < firstStage ) {
				//we are in the first stage
				seqNum = northTce;
			} else {
				//else we are in the second stage
				seqNum = fromeRd;
			}
			
			switch ( seqNum ) {
			case 0 :
				//$FALL_THROUGH$
			case 3 : 
				min = MIN_STRAIGHT_DURATION - DEFAULT_RED_DELAY;
				break;
			case 1 :
				//$FALL_THROUGH$
			case 2 :
				boolean down;
				State next = this.state[id+1];
				if( next.getType() == State.Type.YELLOW ) {
					//we want to search up
					down = false;
				} else {
					down = true;
					//we want to search down
				}
				//if we are in case 2 swap down and up
				if( seqNum == 2 ) {
					down = !down;
				}
				int difference = 0;
				int count = 0;
				//difference is the sum of the 3 state durations above/below the current one
				while( count < 3 ) {
					id --;
					count ++;
					//if we are going down, the last id is below the original state id
					if( down && count == 3 ) {
						id = id + 4;
					}
					difference += this.state[id].getStateDuration();
				}
				min =  MIN_STRAIGHT_DURATION - difference;
			}
		
			
			
		}
		if( min < MIN_STATE_DURATION ) {
			min = MIN_STATE_DURATION;
		}
	return min;
	}


	/** Assert that the traffic light durations are correct 
	 * @return True if all traffic lights that are straight are at least 20 seconds in length and all other 6 seconds in green duration
	 */
	private boolean assertTrafficLightDurations() 
	{
		for( TrafficLight tl : trafficLight ) {
			switch ( tl.TYPE ){
			case LEFT :
				//$FALL_THROUGH$
			case RIGHT:
				if( tl.getGreenDuration() < MIN_RIGHT_DURATION  || tl.getGreenDuration() > MAX_RIGHT_DURATION) {
					System.out.println(tl.getGreenDuration());
					//if we are on 3rd sequence, left and right lanes are off we dont care about the traffic light durations of left and right lanes of 0,2,3,5,
					int tid = tl.getId();
					System.out.println(tid);
					switch ( tid ) {
					case 2:
					case 5:
					case 6:
					case 9:
						System.out.println(sequence.getNorth());
						if( sequence.getNorth() != 3) {
							return false;
						}
					}
					System.out.println("TEST2");
					//frome is not sequence 3, therefore lights, 1,4,9,12 will cause a return mean we have an invalid traffic light
					switch ( tid ) {
					case 0:
					case 3:
					case 8:
					case 11:
						if( sequence.getFrome() != 3) {
							return false;
						}
					} 
				}
				break;
			case STRAIGHT : 
				if( tl.getGreenDuration() < MIN_STRAIGHT_DURATION || tl.getGreenDuration() > MAX_STRAIGHT_DURATION ){
					return false;
				}
			}
		}
		return true;
	}


	/**
	 * Updatedate the traffic light duration based on a state being changed
	 * @param state The state that was changed
	 * @param durationDifference The time that traffic light will increase by
	 */
	private void updateTrafficLightDurations(State state, int durationDifference) 
	{
		for( int i = 0 ; i < this.trafficLight.length ; i ++) {
			//if the traffic light state we are in is yellow, we want to set the duration the states duration

			this.increaseDuration(this.trafficLight[i],state.getTrafficLightState(i), durationDifference);
		}
		saveDurations();
	}
	
	/**
	 * Save the settings of the traffic light controller to a csv
	 * @param filename The name of the csv to save the file to
	 */
	public void saveCsv()
	{
		saveDurations();

		FileWriter fw = null;
		try{
			fw = new FileWriter("config.csv");
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			String write = "";
			write += sequence.getNorth() + "," + sequence.getFrome();
			pw.println(write);
			
			write = ntw_right + ",";
			write += nte_right + ",";
			write += ntw_straight + ",";
			write += nte_straight + ",";
			write += nt_yellow;
			pw.println(write);
			write = frn_right + ",";
			write += frs_right + ",";
			write += frn_straight + ",";
			write += frs_straight + ",";
			pw.println(write);
			pw.close();
			
		} catch (IOException e) {
			//TODO Let the user know that config was not saved
			System.err.println("WARNING: Failed to save traffic light controller config");
		} finally {
			try {
				if( fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				System.err.println("WARNING: Failed to close config file after writing");
				//DONOTHING
			}
		}
		
	}
	/** Save all the durations of the traffic light */
	private void saveDurations() 
	{
		//update all the durations, before saving. 
		//this is hard coded
		ntw_right = trafficLight[2].getGreenDuration();
		nte_right = trafficLight[5].getGreenDuration();
		ntw_straight = trafficLight[1].getGreenDuration();
		nte_straight = trafficLight[4].getGreenDuration();
		nt_yellow = trafficLight[0].getYellowDuration();
		
		frn_right = trafficLight[8].getGreenDuration();
		frs_right = trafficLight[11].getGreenDuration();
		frn_straight = trafficLight[7].getGreenDuration();
		frs_straight = trafficLight[10].getGreenDuration();
	}


	public boolean setYellowDuration(int duration)
	{
		//ensure yellow duration is within the range
		if( duration < MIN_YELLOW_DURATION || duration > MAX_YELLOW_DURATION) {
			return false;
		}
		//for all states with a yellow number in it, set its duration to duration
		for( State s : state ) {
			if( s.getType() == State.Type.YELLOW ) {
				int oldDuration = s.getStateDuration();
				int dif = duration - oldDuration;
				s.setStateDuration(duration);
				for( int i = 0 ; i < trafficLight.length ; i++ ) {
					switch( s.getTrafficLightState(i) ) {
						case RED:
						case GREEN :
						case OFF :
							this.increaseDuration(trafficLight[i], s.getTrafficLightState(i), dif);
							break;
						case YELLOW :
							this.increaseDuration(trafficLight[i], s.getTrafficLightState(i), duration);
							break;
					}
				}

			} 
		}
		return true;
	}
}
