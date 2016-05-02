package npe.sim.gui;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import npe.sim.SimProperties;
import npe.sim.road.VehicleLane;

/**
 * The General tab of the GUI, which contains options for the simulation such as time of day.
 */

public class GeneralTab extends JPanel implements ActionListener, ChangeListener {
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**The simulation properties which can be changed by this tab.*/
	private SimProperties sp;
	//GUI Components
	private JLabel lTime;
	private JLabel lSpeed;
	private JComboBox bTime;
	private JSlider bSpeed;
	private JCheckBox bAddLaneNorth;
	private JCheckBox bAddLaneFrome;
	private JCheckBox bTaxiRank;
	private JCheckBox bLeftOnly;
	
	//////////////////
	//---CREATION---//
	//////////////////
	/**Creates a new GeneralTab.
	 * @param sp The simulation properties which this tab can change.
	 */
	public GeneralTab(SimProperties sp)
	{
		this.sp = sp;
		
		setLayout(null);
		
		//---ADD ELEMENTS TO PANEL---//
		
		//Time of day label
		lTime = new JLabel("Time of Day:");
		lTime.setBounds(10, 11, 150, 14);
		add(lTime);
		
		//Time of day combo box
		bTime = new JComboBox();
		bTime.setModel(new DefaultComboBoxModel(new String[] {"Morning (8:30-9:30am)", "Afternoon (4:30-5:30pm)", "Night (7:30-8:30pm)"}));
		bTime.setBounds(10, 36, 225, 26);
		bTime.addActionListener(this);
		add(bTime);
		
		//Speed limit label
		lSpeed = new JLabel("Vehicle Speed Limit (km/h):");
		lSpeed.setBounds(10, 77, 200, 14);
		add(lSpeed);
		
		//Speed limit slider
		bSpeed = new JSlider();
		bSpeed.setMajorTickSpacing(5);
		bSpeed.setSnapToTicks(true);
		bSpeed.setPaintLabels(true);
		bSpeed.setPaintTicks(true);
		bSpeed.setMinimum(40);
		bSpeed.setMaximum(60);
		bSpeed.setBounds(10, 97, 225, 49);
		bSpeed.addChangeListener(this);
		add(bSpeed);	
		
		//Add Lane North Tce check box
		bAddLaneNorth = new JCheckBox("Add lane to North Tce");
		bAddLaneNorth.setBounds(10, 170, 200, 30);
		bAddLaneNorth.addChangeListener(this);
		add(bAddLaneNorth);

		//Add Lane Frome rd check box
		bAddLaneFrome = new JCheckBox("Add lane to Frome Rd");
		bAddLaneFrome.setBounds(10, 200, 200, 30);
		bAddLaneFrome.addChangeListener(this);
		add(bAddLaneFrome);

		//add taxi rank cehck box
		bTaxiRank = new JCheckBox("Enable Taxi Rank");
		bTaxiRank.setBounds(10, 230, 200, 30);
		bTaxiRank.addChangeListener(this);
		add(bTaxiRank);
		
		//add left only lane check box
		bLeftOnly = new JCheckBox("Left only lanes");
		bLeftOnly.setBounds(10, 260, 200, 30);
		bLeftOnly.addChangeListener(this);
		add(bLeftOnly);
		
	}
	
	///////////////////
	//---LISTENERS---//
	///////////////////
	/**Performs actions when a button is pressed.*/
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		if (src == bTime) {
			sp.timeOfDay = bTime.getSelectedIndex();
		}
	}
	/**Performs actions when a slider or spinner or any check box is changed.*/
	public void stateChanged(ChangeEvent e) 
	{
		Object src = e.getSource();
		if (src == bSpeed) {
			sp.speedLimit = bSpeed.getValue();
		}
		
		if (src == bAddLaneNorth) {
			if(bAddLaneNorth.isSelected()){
				sp.numLanesNorthExtra = 1;
				} else if(!bAddLaneNorth.isSelected()){
				sp.numLanesNorthExtra = 0;
			}
		}
		
		if (src == bAddLaneFrome) {
			if(bAddLaneFrome.isSelected()){
				sp.numLanesFromeExtra = 1;
			} else if(!bAddLaneFrome.isSelected()){
				sp.numLanesFromeExtra = 0;
			}
		}
		
		if (src == bTaxiRank) {
			sp.taxiRank = bTaxiRank.isSelected();
		}		
		
		if (src == bLeftOnly) {
			if(bLeftOnly.isSelected()) {
				sp.leftMostLaneType = VehicleLane.Type.LEFT;
				sp.leftMostLaneTypeOpposite = VehicleLane.Type.LEFT_;
			} else if(!bLeftOnly.isSelected()) {
				sp.leftMostLaneType = VehicleLane.Type.LEFT_STRAIGHT;
				sp.leftMostLaneTypeOpposite = VehicleLane.Type.LEFT_STRAIGHT_;
			}
		}
		
	}
	
	/**Enables or disables the components of this panel.
	 * @param enabled True if enabling the components, false if disabling.
	 */
	public void setEnabled(boolean enabled)
	{
		bTime.setEnabled(enabled);
		bSpeed.setEnabled(enabled);
		bAddLaneNorth.setEnabled(enabled);
		bAddLaneFrome.setEnabled(enabled);
		bTaxiRank.setEnabled(enabled);
		bLeftOnly.setEnabled(enabled);
	}
	
}