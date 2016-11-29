package npe.sim.gui;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import npe.sim.SimProperties;
import npe.sim.road.VehicleLane;
import npe.sim.SimPanel;
import npe.sim.Utils;

/**
 * The General tab of the GUI, which contains options for the simulation such as time of day.
 */

public class GeneralTab extends JPanel implements ActionListener, ChangeListener {
	
	/**
	 * 
	 */
	
	//added to remove warning on class.
	private static final long serialVersionUID = 9173836510122184425L;
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**The simulation properties which can be changed by this tab.*/
	private SimProperties sp;
	private SimPanel pSim;
	//GUI Components
	private JCheckBox bUSmode;
	private JLabel lTime;
	private JLabel lSpeed;
	private JComboBox<String> bTime;
	private JSlider bSpeed;
	private JLabel lAddLaneNorth;
	private JComboBox<String> bAddLaneNorth; // see if I can change to a text box
	private JLabel lAddLaneFrome;
	private JComboBox<String> bAddLaneFrome;
	private JCheckBox bTaxiRank;
	private JCheckBox bLeftOnly;
	
	//////////////////
	//---CREATION---//
	//////////////////
	/**Creates a new GeneralTab.
	 * @param sp The simulation properties which this tab can change.
	 */
	public GeneralTab(SimPanel Panel, SimProperties sp)
	{
		this.pSim = Panel;
		this.sp = sp;
		
		setLayout(null);
		
		//---ADD ELEMENTS TO PANEL---//
				
		//Add USmode check box
		bUSmode = new JCheckBox("US mode");
		bUSmode.setBounds(150, 11, 100, 14);
		bUSmode.addChangeListener(this);
		add(bUSmode);
		
		//Time of day label
		lTime = new JLabel("Time of Day:");
		lTime.setBounds(10, 11, 150, 14);
		add(lTime);
		
		//Time of day combo box
		bTime = new JComboBox<String>();
		bTime.setModel(new DefaultComboBoxModel<String>(new String[] {"Morning (8:30-9:30am)", "Afternoon (4:30-5:30pm)", "Night (7:30-8:30pm)"}));
		bTime.setBounds(10, 36, 225, 26);
		bTime.addActionListener(this);
		add(bTime);
		
		//Speed limit label
		lSpeed = new JLabel("Vehicle Speed Limit (km/h):");
		lSpeed.setBounds(10, 77, 200, 14);
		add(lSpeed);
		
		//Speed limit slider
		bSpeed = new JSlider();
		bSpeed.setMinorTickSpacing(1);
		bSpeed.setMajorTickSpacing(5);
		bSpeed.setSnapToTicks(true);
		bSpeed.setPaintLabels(true);
		bSpeed.setPaintTicks(true);
		bSpeed.setMinimum(40);
		bSpeed.setMaximum(65);
		bSpeed.setBounds(10, 97, 225, 49);
		bSpeed.addChangeListener(this);
		add(bSpeed);	

		//Add Lane West/East Label for lane Numbers
		lAddLaneNorth = new JLabel("Add lane to West/East");
		lAddLaneNorth.setBounds(10, 170, 200, 30);
		add(lAddLaneNorth);
		
		//Add Lane West/East text field for lane numbers
		bAddLaneNorth = new JComboBox<String>();
		bAddLaneNorth.setModel(new DefaultComboBoxModel<String>(new String[] {"4", "5", "6", "7", "8"}));
		bAddLaneNorth.setBounds(10, 200, 200, 30);
		bAddLaneNorth.addActionListener(this);
		add(bAddLaneNorth);
		
		//Add Lane North/South Label for lane Numbers
		lAddLaneFrome = new JLabel("Add lane to North/South");
		lAddLaneFrome.setBounds(10, 230, 200, 30);
		//bAddLaneNorth.addChangeListener(this);
		add(lAddLaneFrome);

		//Add Lane Frome rd text field for lane numbers
		bAddLaneFrome = new JComboBox<String>();
		bAddLaneFrome.setModel(new DefaultComboBoxModel<String>(new String[] {"3", "4", "5", "6", "7", "8"}));
		bAddLaneFrome.setBounds(10, 260, 200, 30);
		bAddLaneFrome.addActionListener(this);
		add(bAddLaneFrome);

		//add taxi rank check box
		bTaxiRank = new JCheckBox("Enable Taxi Rank");
		bTaxiRank.setBounds(10, 290, 200, 30);
		bTaxiRank.addChangeListener(this);
		add(bTaxiRank);
		
		//add left only lane check box
		bLeftOnly = new JCheckBox("Left only lanes");
		bLeftOnly.setBounds(10, 320, 200, 30);
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
		else if (src == bAddLaneNorth){
			sp.numLanesNorth = Integer.parseInt(bAddLaneNorth.getItemAt(bAddLaneNorth.getSelectedIndex()));
		}
		else if (src == bAddLaneFrome){
			sp.numLanesFrome = Integer.parseInt(bAddLaneFrome.getItemAt(bAddLaneFrome.getSelectedIndex()));
		}
	}
	/**Performs actions when a slider or spinner or any check box is changed.*/
	public void stateChanged(ChangeEvent e) 
	{
		Object src = e.getSource();
		if (src == bSpeed) {
			if (bUSmode.isSelected() == true) {
		        	sp.speedLimit = (int)(bSpeed.getValue()/0.621371);
			}
			else {
		        	sp.speedLimit = bSpeed.getValue();
			}
		}
		
		// If US mode is active, change the GUI for the user.
		if (src == bUSmode) {
		     Utils.setUSmode(bUSmode.isSelected());
		    if (bUSmode.isSelected()) {
		        lSpeed.setText("Vehicle Speed Limit (m/h):");
		        bLeftOnly.setText("Right only Lanes");
		        bSpeed.setMajorTickSpacing(3);
	            bSpeed.setPaintLabels(true);
	            bSpeed.setPaintTicks(true);
		        bSpeed.setMinimum(25);
		        bSpeed.setMaximum(40);
		    }
		    else {
		        lSpeed.setText("Vehicle Speed Limit (km/h):");
		        bLeftOnly.setText("Left only lanes");
		        bSpeed.setMajorTickSpacing(5);
		        bSpeed.setPaintLabels(true);
		        bSpeed.setPaintTicks(true);
		        bSpeed.setMinimum(40);
		        bSpeed.setMaximum(65);
		    }
		    pSim.USmode();
		}
		
		/*if (src == bAddLaneNorth) {
			if(bAddLaneNorth.isSelected()){
				sp.numLanesNorthExtra = 1;
				} else if(!bAddLaneNorth.isSelected()){
				sp.numLanesNorthExtra = 0;
			}
		}*/
		
		/*if (src == bAddLaneFrome) {
			if(bAddLaneFrome.isSelected()){
				sp.numLanesFromeExtra = 1;
			} else if(!bAddLaneFrome.isSelected()){
				sp.numLanesFromeExtra = 0;
			}
		}*/
		
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
		bUSmode.setEnabled(enabled);
		bTime.setEnabled(enabled);
		bSpeed.setEnabled(enabled);
		bAddLaneNorth.setEnabled(enabled);
		bAddLaneFrome.setEnabled(enabled);
		bTaxiRank.setEnabled(enabled);
		bLeftOnly.setEnabled(enabled);
	}
	
}
