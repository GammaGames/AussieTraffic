package npe.sim.gui;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import npe.sim.SimProperties;
import npe.sim.SimWindow;
import npe.sim.light.TrafficLight;
import npe.sim.light.State;
import npe.sim.light.TrafficLightController;

/**
 * The Lights tab of the GUI, which allows the user to change traffic light durations.
 */

public class TrafficLightTab extends JPanel implements ActionListener, ChangeListener {
	
	///////////////////
	//---CONSTANTS---//
	///////////////////
	private final int NUM_TRAFFIC_LIGHTS = 12;
	//Duration limits
	private final int MIN_GREEN_DURATION = 20;
	private final int MAX_GREEN_DURATION = 100;
	private final int MIN_RIGHT_GREEN_DURATION = 6;
	private final int MAX_RIGHT_GREEN_DURATION = 40; //these values are now controller by trafic light controller
	private final int MIN_YELLOW_DURATION = 4;
	private final int MAX_YELLOW_DURATION = 6;
	//Table constants
	private final int TABLE_WIDTH = 230;
	private final int TABLE_HEIGHT = 420;
	//Step sizes for traffic light durations
	private int greenDurationSS = 1;
	private int yellowDurationSS = 1; 
	//The options in the traffic light selector combo box
	private final String[] TRAFFIC_LIGHT = new String[]{
			"SELECT TRAFFIC LIGHT","Traffic Light 1", "Traffic Light 2",
			"Traffic Light 3", "Traffic Light 4", "Traffic Light 5",
			"Traffic Light 6", "Traffic Light 7", "Traffic Light8",
			"Traffic Light 9", "Traffic Light 10", "Traffic Light 11" , "Traffic Light 12"
			};
	
	///////////////////
	//---VARIABLES---//
	//////////////////
	private SimProperties simProperties;
	private SimWindow simWindow;
	private TrafficLightController trafficLightController;
	private JComboBox selectTrafficLightCB;
	//Duration Labels
	private JLabel greenDurationL;
	private JLabel yellowDurationL;
	private JLabel redDurationL;
	//Duration label units
	private JLabel greenDurationUnitL;
	private JLabel yellowDurationUnitL;
	private JLabel redDurationUnitL;
	private JLabel redDurationTimeL;
	//JSpinners are used to 'spin' through numbers in a list
	private JSpinner greenDurationS;
	private JSpinner yellowDurationS;
	//Spinner Number Model will control what numbers are available to the spinner
	private SpinnerNumberModel greenDurationSNM;
	private SpinnerNumberModel greenRightDurationSNM;
	private SpinnerNumberModel yellowDurationSNM;
	//Traffic Light table
	private JTable trafficLightT;
	
	//////////////////
	//---CREATION---//
	//////////////////
	/**Creates a new TrafficLightTab.
	 * @param sw The simulation window containing this tab.
	 * @param sp The simulation properties which this tab can change.
	 */
	public TrafficLightTab(SimWindow sw, SimProperties sp)
	{
		simWindow = sw;
		simProperties = sp;
	
		newTrafficLightController();
		
		selectTrafficLightCB = new JComboBox(TRAFFIC_LIGHT);
		ListCellRenderer rend = new DefaultListCellRenderer();
		((JLabel)rend).setHorizontalAlignment(SwingConstants.CENTER);
		selectTrafficLightCB.setRenderer(rend);
		selectTrafficLightCB.addActionListener(this);
		
		greenDurationL = new JLabel("Green duration: ");
		yellowDurationL = new JLabel("Yellow duration: ");
		redDurationL = new JLabel("Red duration: ");
		
		redDurationTimeL = new JLabel("---");
		
		greenDurationUnitL = new JLabel("seconds");
		yellowDurationUnitL = new JLabel("seconds");
		redDurationUnitL = new JLabel("seconds");
		
		
		greenDurationSNM = new SpinnerNumberModel(30,MIN_GREEN_DURATION,MAX_GREEN_DURATION,greenDurationSS);
		yellowDurationSNM = new SpinnerNumberModel(5,MIN_YELLOW_DURATION,MAX_YELLOW_DURATION,yellowDurationSS);
		greenRightDurationSNM = new SpinnerNumberModel(8,MIN_RIGHT_GREEN_DURATION,MAX_RIGHT_GREEN_DURATION,greenDurationSS);
		
		
		greenDurationS = new JSpinner(greenDurationSNM);
		greenDurationS.addChangeListener(this);
		yellowDurationS = new JSpinner(yellowDurationSNM);
		yellowDurationS.addChangeListener(this);
		

		//Initialise Traffic light Panel
		this.setLayout(null);
		
		int yaxis = 10; //used to determin the yaxis to avoid hardcoding
		int height = 20;
		
		
		selectTrafficLightCB.setLocation(10, yaxis);
		selectTrafficLightCB.setSize(220,height);
		this.add(selectTrafficLightCB);
		
		yaxis += 30;
		greenDurationL.setLocation(10,yaxis);
		greenDurationL.setSize(130,height);
		this.add(greenDurationL);
		
		greenDurationS.setLocation(135,yaxis);
		greenDurationS.setSize(40,height);
		greenDurationS.setEnabled(false);
		this.add(greenDurationS);
		
		greenDurationUnitL.setLocation(180,yaxis);
		greenDurationUnitL.setSize(80,height);
		this.add(greenDurationUnitL);
		
		yaxis += 30;
		yellowDurationL.setLocation(10,yaxis);
		yellowDurationL.setSize(130,height);
		this.add(yellowDurationL);
		
		yellowDurationS.setLocation(135,yaxis);
		yellowDurationS.setSize(40,height);
		yellowDurationS.setEnabled(false);		

		this.add(yellowDurationS);
		
		yellowDurationUnitL.setLocation(180,yaxis);
		yellowDurationUnitL.setSize(80,height);
		this.add(yellowDurationUnitL);
		
		yaxis += 30;
		
		redDurationL.setLocation(10,yaxis);
		redDurationL.setSize(200,height);
		this.add(redDurationL);

		redDurationTimeL.setLocation(135 , yaxis);
		redDurationTimeL.setSize(80,height);
		this.add(redDurationTimeL);

		redDurationUnitL.setLocation(180 , yaxis);
		redDurationUnitL.setSize(80,height);
		this.add(redDurationUnitL);
		
		yaxis += 30;
		
		trafficLightT = trafficLightController.getJTable(TABLE_WIDTH , TABLE_HEIGHT);
		trafficLightT.setLocation(10, yaxis);
		trafficLightT.getModel().addTableModelListener(new TableListener());
		this.add(trafficLightT);

	}
	
	///////////////////
	//---LISTENERS---//
	///////////////////
	/**Performs actions when a button is pressed.*/
	public void actionPerformed(ActionEvent e)
	{
		int selected = selectTrafficLightCB.getSelectedIndex();
		if ( selected != 0 ) {
			int id = selected - 1;
			
			//if it is a right hand traffic light -> set max and min of green spinner box to right turn values
			if( (id % 3) == 1 ) {
				greenDurationS.setModel(greenDurationSNM);
			} else {
				greenDurationS.setModel(greenRightDurationSNM);
			}
			greenDurationS.setEnabled(true);
			greenDurationS.setValue(trafficLightController.getTrafficLightGreenDuration(id));
			yellowDurationS.setEnabled(true);
			yellowDurationS.setValue(trafficLightController.getTrafficLightYellowDuration(id));
			redDurationTimeL.setText("" + trafficLightController.getTrafficLightRedDuration(id));
		} else {
			greenDurationS.setEnabled(false);
			yellowDurationS.setEnabled(false);
			redDurationTimeL.setText("---");
		}
	}
	/**Performs actions when a slider or spinner is changed.*/
	public void stateChanged(ChangeEvent e)
	{
		Object src = e.getSource();
		if( src == greenDurationS ) {
			//get the currently set model of the spinner box. This is because left and right turns have different models to straight lights
			SpinnerNumberModel model = (SpinnerNumberModel) greenDurationS.getModel();
			int duration = model.getNumber().intValue();
			int newDuration = trafficLightController.changeLight(selectTrafficLightCB.getSelectedIndex() - 1, TrafficLight.State.GREEN, duration);
			model.setValue(newDuration);
			
		} else if ( src == yellowDurationS ) {
			trafficLightController.setYellowDuration(yellowDurationSNM.getNumber().intValue());
		}
		updatePanel();
	}
	/**Enables or disables the components of this panel.
	 * @param enabled True if enabling the components, false if disabling.
	 */
	public void setEnabled(boolean enabled)
	{
		selectTrafficLightCB.setSelectedIndex(0);
		selectTrafficLightCB.setEnabled(enabled);
		greenDurationS.setEnabled(false);
		yellowDurationS.setEnabled(false);
		trafficLightT.setEnabled(false);
	}
	/**Updates the panel with a new traffic light table.*/
	public void updatePanel()
	{
		remove(trafficLightT);
		trafficLightT = trafficLightController.getJTable( TABLE_WIDTH , TABLE_HEIGHT );
		trafficLightT.setLocation( 10 , 130 );
		trafficLightT.getModel().addTableModelListener(new TableListener());
		add(trafficLightT);
		simWindow.redrawTabs();
	}
	/**Creates a new TrafficLightController and sequence for this tab to use.
	 * Disacards the previous TLC to ensure nothing from previous simulation carries over into new one.*/
	public void newTrafficLightController()
	{
		trafficLightController = new TrafficLightController(NUM_TRAFFIC_LIGHTS);
		simProperties.trafficLightController = trafficLightController;
		simProperties.sequence = trafficLightController.getSequence();
	}
	
	private class TableListener implements TableModelListener{

		public void tableChanged(TableModelEvent e) {
			int row = e.getFirstRow();
			int col = e.getColumn();
			DefaultTableModel model = (DefaultTableModel) e.getSource();
			int stateDuration;
			State state = trafficLightController.getState(row-1);
			try { 
				stateDuration = new Integer ( (String) model.getValueAt(row, col) );
			} catch (NumberFormatException e1 ) {
				model.setValueAt(Integer.toString(state.getStateDuration()), row, col);
				return;
			}

			//Base case needed as we call set value at from this, which will trigger another event
			//Base Case: check to see if the number has even been changed
			if( stateDuration == state.getStateDuration() ) {
				return;
			}
			//if its yellow use the set Yellow duration method
			if( state.getType() == State.Type.YELLOW ) {
				if( !trafficLightController.setYellowDuration(stateDuration)) {
					model.setValueAt(Integer.toString(state.getStateDuration()), row, col);
				}

			}
			/*
			switch( state.getType() ) {
			case GREEN_OFF :
				//ensure that a valid number was entered
				//we need to update make a difference of the state duration that will be changed and the sum of all the other state durations that the light is green in consecutively
				trafficLightController.getDurationDifference(row-1);
				
				if( stateDuration < MIN_GREEN_DURATION || stateDuration > MAX_GREEN_DURATION ) {
					model.setValueAt(Integer.toString(state.getStateDuration()), row, col);
					//TODO pop up window saying why the number was not valid
					return;
				}

				break;
			case GREEN_RIGHT :
				if( stateDuration < MIN_RIGHT_GREEN_DURATION || stateDuration > MAX_RIGHT_GREEN_DURATION ) {
					model.setValueAt(Integer.toString(state.getStateDuration()), row, col);
					return;
				}
				
				break;
			case YELLOW :
				if( stateDuration < MIN_YELLOW_DURATION || stateDuration > MAX_YELLOW_DURATION ) {
					model.setValueAt(Integer.toString(state.getStateDuration()), row, col);
					return;
				}
				break;

			}
			*/
			int newDuration = trafficLightController.changeStateDuration(row-1,stateDuration);
			model.setValueAt(Integer.toString(newDuration), row, col);
			updatePanel();
			//draw all traffic lights how they should be
			trafficLightController.updateState(row-1);

		}
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}
	
}
