package npe.sim.gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import npe.sim.SimWindow;
import npe.sim.SimProperties;
import npe.sim.Utils;

/**
 * The bottom panel of the GUI, which contains controls for the simulation.
 */

public class BottomPanel extends JPanel implements ActionListener, ChangeListener {
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**The simulation properties which can be changed by this panel.*/
	private SimProperties sp;
	/**The simulation window which this panel belongs to.*/
	private SimWindow sw;
	/**Whether the start/pause button is a start button (true), or a pause button (false).*/
	private boolean usingStartButton;
	/**A list of values selectable with the speed slider.*/
	private double[] speedValues;
	//GUI Components
	private JButton bStartPause;
	private JButton bStop;
	private JButton bTick;
	private JButton bChangeState;
	private JSpinner bTime;
	private JSlider bSpeed;
	private JButton bQuit;
	
	//////////////////
	//---CREATION---//
	//////////////////
	/**Creates a new BottomPanel.
	 * @param sw The window that this panel belongs to.
	 * @param sp The simulation properties which this panel can change.
	 */
	public BottomPanel(SimWindow sw, SimProperties sp)
	{
		//Store simulation variables
		this.sw = sw;
		this.sp = sp;
		
		///////////////////////////////
		//---ADD ELEMENTS TO PANEL---//
		///////////////////////////////
		
		//Use a box layout
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		
		add(Box.createHorizontalStrut(45));
		
		//Start / pause button
		bStartPause = new JButton();
		bStartPause.setForeground( new Color(0,180,0) );
		bStartPause.addActionListener(this);
		bStartPause.setPreferredSize( new Dimension(75,20) );
		bStartPause.setMargin( new Insets(1,1,1,1) );
		useStartButton(); //Initially this is a start button
		add(bStartPause);
		
		add(Box.createHorizontalStrut(25));
		
		//Stop button
		bStop = new JButton("Stop");
		bStop.setEnabled(false);
		bStop.setForeground(Color.RED);
		bStop.addActionListener(this);
		add(bStop);
		
		add(Box.createHorizontalStrut(30));
		
		//---DEBUGGING BUTTONS BEGIN HERE---//
		if(Utils.TickChangeStateDebugging) {
			//Tick button
			bTick = new JButton("Tick");
			bTick.setEnabled(true);
			bTick.setForeground(Color.BLACK);
			bTick.addActionListener(this);
			add(bTick);

			add(Box.createHorizontalStrut(10));
			
			//Change state button
			bChangeState = new JButton("Change State");
			bChangeState.setEnabled(true);
			bChangeState.setForeground(Color.BLACK);
			bChangeState.addActionListener(this);
			add(bChangeState);
		}
		//---DEBUGGING BUTTONS END HERE---//
		
		add(Box.createHorizontalGlue());
		
		//Time label
		JLabel lblTime = new JLabel("Time:");
		add(lblTime);
		
		add(Box.createHorizontalStrut(3));
		
		//Time spinner
		SpinnerNumberModel spinModel = new SpinnerNumberModel(0, 0, 10000, 5);
		bTime = new JSpinner(spinModel);
		bTime.setPreferredSize(new Dimension(50,20));
		bTime.setMaximumSize(new Dimension(50,20));
		bTime.addChangeListener(this);
		add(bTime);
		
		add(Box.createHorizontalStrut(2));
		
		//Minutes label
		JLabel lblMin = new JLabel("min");
		add(lblMin);
		
		add(Box.createHorizontalGlue());
		
		//Speed label
		JLabel lblSpeed = new JLabel("Speed:");
		add(lblSpeed);
		
		add(Box.createHorizontalStrut(2));
		
		//Speed selection intervals
		speedValues = new double[7];
		Hashtable<Integer,JLabel> speedLabels = new Hashtable<Integer,JLabel>();
		speedLabels.put(0, new JLabel("0.1x"));
		speedValues[0] = 0.1;
		speedLabels.put(1, new JLabel("0.5x"));
		speedValues[1] = 0.5;
		speedLabels.put(2, new JLabel("1x"));
		speedValues[2] = 1;
		speedLabels.put(3, new JLabel("5x"));
		speedValues[3] = 5;
		speedLabels.put(4, new JLabel("10x"));
		speedValues[4] = 10;
		speedLabels.put(5, new JLabel("30x"));
		speedValues[5] = 30;
		speedLabels.put(6, new JLabel("Max"));
		speedValues[6] = 1000;
		
		//Speed slider
		bSpeed = new JSlider();
		bSpeed.setMaximumSize(new Dimension(200,50));
		bSpeed.setMinimum(0);
		bSpeed.setMaximum(6);
		bSpeed.setValue(2);
		bSpeed.setMajorTickSpacing(1);
		bSpeed.setLabelTable(speedLabels);
		bSpeed.setSnapToTicks(true);
		bSpeed.setPaintLabels(true);
		bSpeed.setPaintTicks(true);
		bSpeed.addChangeListener(this);
		add(bSpeed);
		
		add(Box.createHorizontalGlue());
		
		//Quit button
		bQuit = new JButton("Quit");
		bQuit.addActionListener(this);
		add(bQuit);
		
		add(Box.createHorizontalStrut(20));
	}
	
	///////////////////
	//---LISTENERS---//
	///////////////////
	/**Performs actions when a button is pressed.*/
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		if (src == bStartPause) {
			if (usingStartButton) { //If this is a start button
				sw.startPressed(); //Start the simulation
				//Button enabling / disabling
				bStop.setEnabled(true);
				bTime.setEnabled(false);
			} else { //If this is a pause button
				sw.pausePressed(); //Pause the simulation
			}
		} else if (src == bStop) {
			sw.stopPressed();
			reset();
		} else if (src == bQuit) {
			System.exit(0);
		} else if(src == bTick) {
			sw.tickPressed();
		} else if (src == bChangeState) {
			sp.trafficLightController.changeState();
		}
	}
	/**Performs actions when a slider or spinner is changed.*/
	public void stateChanged(ChangeEvent e) 
	{
		Object src = e.getSource();
		if (src == bSpeed) { //The simulation speed has been changed
			sw.speedChanged( speedValues[bSpeed.getValue()] );
		} else if (src == bTime) { //The simulation time has been changed
			sp.duration = (Integer)bTime.getValue();
		}
	}
	/**Resets the components of this panel to their initial (pre-simulation) state.*/
	public void reset()
	{
		//Button enabling / disabling
		bStop.setEnabled(false);	
		bTime.setEnabled(true);
		//Reset the speed slider
		bSpeed.setValue(2);
	}
	/**Turns the pause button into a start button.*/
	public void useStartButton()
	{
		usingStartButton = true;
		bStartPause.setText("Start");
	}
	/**Turns the start button into a pause button.*/
	public void usePauseButton()
	{
		usingStartButton = false;
		bStartPause.setText("Pause");
	}
	/**Turns the pause button into a resume button.*/
	public void useResumeButton()
	{
		usingStartButton = true; //Start and resume have exactly the same effect
		bStartPause.setText("Resume");
	}
	
}
