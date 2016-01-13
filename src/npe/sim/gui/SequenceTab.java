package npe.sim.gui;
import npe.sim.*;
import npe.sim.road.Road;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * The Sequence tab of the GUI, which allows the user to choose the traffic light sequence.
 */

public class SequenceTab extends JPanel implements ActionListener {
	
	///////////////////
	//---CONSTANTS---//
	///////////////////
	private final static int X_WIDTH = SimWindow.TAB_WIDTH;
	//private final static int Y_HEIGHT = SimWindow.SW_HEIGHT;
	private final static int X_STRUT = 8;
	private final static int Y_STRUT = 12;
	private final static int ELEM_WIDTH = X_WIDTH-3*X_STRUT;	//default element width
	private final static int ELEM_HEIGHT = 25;					//default element height
	private final static int PIC_HEIGHT = X_WIDTH-2*X_STRUT;	//default picture height
	private final static String[] ROADS = {"North Tce","Frome Rd"};
	private final static String[] SEQ_PRESETS = {"1","2","3","4"};
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	private SimProperties simProperties;
	private SimWindow sw;
	//GUI Components
	private JLabel RoadL;
	private JLabel SeqL;
	private JComboBox selRoadC;
	private JComboBox selSeqC;
	private JLabel seqPic;
	
	//////////////////
	//---CREATION---//
	//////////////////
	/**Creates a new SequenceTab.
	 * @param sp The simulation properties which this tab can change.
	 */
	public SequenceTab(SimWindow sw, SimProperties sp)
	{
		this.sw = sw;
		simProperties = sp;
		
		//We are using null layout
		setLayout(null);
		
		//create road label
		RoadL = new JLabel("Road:");
		RoadL.setLocation(X_STRUT,Y_STRUT);
		RoadL.setSize(ELEM_WIDTH,ELEM_HEIGHT);
		this.add(RoadL);
		//Select road combo box, select between frome rd and north tce
		selRoadC = new JComboBox(ROADS);
		selRoadC.setLocation(X_STRUT,Y_STRUT+ELEM_HEIGHT);
		selRoadC.setSize(ELEM_WIDTH,ELEM_HEIGHT);
		selRoadC.addActionListener(this);
		this.add(selRoadC);
		
		//create sequence label
		SeqL = new JLabel("Traffic Light Sequence:");
		SeqL.setLocation(X_STRUT,4*Y_STRUT+2*ELEM_HEIGHT);
		SeqL.setSize(ELEM_WIDTH,ELEM_HEIGHT);
		this.add(SeqL);
		//Select between preset traffic sequences
		selSeqC = new JComboBox(SEQ_PRESETS);
		selSeqC.setLocation(X_STRUT,4*Y_STRUT+3*ELEM_HEIGHT);
		selSeqC.setSize(ELEM_WIDTH,ELEM_HEIGHT);
		selSeqC.addActionListener(this);
		this.add(selSeqC);
		
		//set up picture for traffic sequence
		seqPic = new JLabel();
		seqPic.setFont(seqPic.getFont().deriveFont(Font.ITALIC));
		seqPic.setLocation(X_STRUT,4*Y_STRUT+3*ELEM_HEIGHT+100);
		seqPic.setSize(ELEM_WIDTH,PIC_HEIGHT);
		updateLabel(SEQ_PRESETS[selSeqC.getSelectedIndex()]);
		seqPic.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));

		this.add(seqPic);
	}

	///////////////////
	//---LISTENERS---//
	///////////////////
	/**Performs actions when an item is selected.*/
	public void actionPerformed(ActionEvent e)
	{
		String selectedRoad = ROADS[selRoadC.getSelectedIndex()];
		Road.Type roadType;
		if (selectedRoad.equals("North Tce")){
			roadType = Road.Type.NORTH;
		} else {
			roadType = Road.Type.FROME;
		}
		int sequence = selSeqC.getSelectedIndex();
		if ( simProperties.sequence != null ) {
			simProperties.sequence.setSequence(roadType, sequence);
			sw.updateLights();
		}
		updateLabel(SEQ_PRESETS[selSeqC.getSelectedIndex()]);
	}
	/**Updates the image at the bottom of the tab.
	 * @param name Name of image to use.
	 */
	private void updateLabel(String name)
	{
		String selectedRoad = ROADS[selRoadC.getSelectedIndex()];
		
		ImageIcon icon = new ImageIcon();
		
		for(String road : ROADS){
			if(road.equals(selectedRoad)){
				road = road.replace(' ','_').toLowerCase();							//convert Frome Rd to frome_rd
				icon = new ImageIcon("res/"+ road +"/preset" + name + ".gif");
			}
		}
		
		seqPic.setIcon(icon);
		seqPic.setToolTipText("Animated lights for preset" + name);
		seqPic.setText(null);
	}
	/**Enables or disables the components of this panel.
	 * @param enabled True if enabling the components, false if disabling.
	 */
	public void setEnabled(boolean enabled)
	{
		selRoadC.setEnabled(enabled);
		selSeqC.setEnabled(enabled);
	}
	/**Resets the components of this panel to their initial state.*/
	public void reset()
	{
		selRoadC.setSelectedIndex(0);
		selSeqC.setSelectedIndex(0);
	}
	
}