package npe.sim;
import java.awt.*;
import javax.swing.*;

import npe.sim.gui.*;

/**
 * Houses the GUI components for a window which contains the simulation.
 */

public class SimWindow extends JFrame {
	
	///////////////////
	//---CONSTANTS---//
	///////////////////
	/**Title of the SimWindow.*/
	private static final String TITLE = "NPE Simtersection 2012";
	/**Total width of the SimWindow.*/
	public static final int SW_WIDTH = 1024;
	/**Total height of the SimWindow.*/
	public static final int SW_HEIGHT = 700;
	/**Width of the tab bar on the left side of the SimWindow.*/
	public static final int TAB_WIDTH = 250;
	/**With of the bar on the bottom of the SimWindow.*/
	public static final int BOT_HEIGHT = 100;
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**Stores the simulation options being selected in the GUI.*/
	private SimProperties simProperties;
	//GUI panels / tabs
	private GeneralTab tGeneral;
	private TrafficLightTab tLights;
	private SequenceTab tSequence;
	private JTabbedPane pTabs;
	private BottomPanel pBot;
	private SimPanel pSim;
	
	//////////////////
	//---CREATION---//
	//////////////////
	/**Sets up a new simulation window.*/
	public SimWindow()
	{
		//Set up the window
		super(TITLE);
		DisplayMode dm = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode(); //Get screen resolution
		setBounds((dm.getWidth()-SW_WIDTH)/2, (dm.getHeight()-SW_HEIGHT)/2, SW_WIDTH, SW_HEIGHT); //Position window in center of screen
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Initialise variables
		simProperties = new SimProperties();
		tGeneral = new GeneralTab(simProperties);
		tLights = new TrafficLightTab(this, simProperties);
		tSequence = new SequenceTab(this, simProperties);
		pTabs = new JTabbedPane();
		pBot = new BottomPanel(this, simProperties);
		pSim = new SimPanel(this, simProperties);
		
		//Set up tabs
		pTabs.add("General", tGeneral);
		pTabs.add("Lights", tLights);
		pTabs.add("Sequence", tSequence);
		
		//Position and add panels to the window
		setLayout(null);
		pTabs.setBounds(0, 0, TAB_WIDTH, SW_HEIGHT-BOT_HEIGHT);
		add(pTabs);
		pSim.setBounds(TAB_WIDTH, 0, SW_WIDTH-TAB_WIDTH, SW_HEIGHT-BOT_HEIGHT);
		add(pSim);
		pBot.setBounds(0, SW_HEIGHT-BOT_HEIGHT, SW_WIDTH, BOT_HEIGHT-25);
		add(pBot);
		
		//Display the window
		setVisible(true);
	}
	
	/////////////////////
	//---GUI ACTIONS---//
	/////////////////////
	/**Called when the start button is pressed.
	 * Starts the simulation, and prevents the user from changing any simulation
	 * options while the simulation is running.
	 */
	public void startPressed()
	{	
		simProperties.trafficLightController.saveCsv();
		pSim.start(); //Start the simulation
		setTabsEnabled(false); //Prevent fields being changed during simulation
		pBot.usePauseButton(); //Change the start button into a pause button
	}
	/**Called when the stop button is pressed.
	 * Stops the simulation, re-enables tabs so that the user can change simulation
	 * options again, and removes leftover objects from the previous simulation.
	 */
	public void stopPressed()
	{
		pSim.stop(); //Stop the simulation
		pBot.reset(); //Reset the simulation controls
		pBot.useStartButton(); //Change the stop button into a start button
		setTabsEnabled(true); //Re-enable user input in tabs
		//Display the results
		//new ResultWindow(0, 0);
		//Remove the old traffic light controller (to prevent it leaking into new simulations)
		tLights.newTrafficLightController();
		tLights.updatePanel();
	}
	/**Called when the pause button is pressed.
	 * Sends a pause signal to the current simulation.
	 */
	public void pausePressed()
	{
		pSim.pause(); //Pause the simulation
		pBot.useResumeButton(); //Change the pause button to a resume button
	}
	/**Called when the tick button is pressed.
	 * Sends a tick signal to the current simulation.*/
	public void tickPressed()
	{
		pSim.tick();
	}
	/**Called when the simulation speed slider's position is changed.
	 * Changes the speed of the current simulation.
	 * @param newSpeed The new speed selected by the speed slider.
	 */
	public void speedChanged(double newSpeed)
	{
		pSim.setSpeed(newSpeed);
	}
	
	////////////////////
	//---GUI ACCESS---//
	////////////////////
	/**Redraws the tabs bar. Used to refresh the traffic light duration table.*/
	public void redrawTabs()
	{
		pTabs.repaint();
	}
	/**Updates the traffic light tab based on the newly selected sequence.*/
	public void updateLights()
	{
		tLights.updatePanel();
	}
	/**Enables or disables the components in all of the tabs.
	 * @param enabled True if enabling tabs, false if disabling.
	 */
	private void setTabsEnabled(boolean enabled)
	{
		tGeneral.setEnabled(enabled);
		tSequence.setEnabled(enabled);
		tLights.setEnabled(enabled);
	}
	
}