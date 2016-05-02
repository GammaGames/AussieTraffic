package npe.sim.results;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.*;
import npe.sim.results.ResultsPanel.GraphType;

/**
 * A window which contains the results collected from a single simulation.
 */

public class ResultsWindow extends JFrame implements ComponentListener {
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**Title of the window.*/
	private static final String title = "Simulation Results";
	/**Current width of the window.*/
	private int width = 900; //Default width
	/**Current height of the window.*/
	private int height = 600; //Default height
	//GUI components
	private GeneralResultsPanel tab0;
	private ResultsPanel tab1;
	private ResultsPanel tab2;
	private ResultsPanel tab3;
	private ResultsPanel tab4;
	private JTabbedPane pTabs;
	private StatsCollector statsCollector;
	
	//////////////////
	//---CREATION---//
	//////////////////
	/**Creates a new results window at the given position.
	 * @param x The x-coordinate of the position to create the window.
	 * @param y The y-coordinate of the position to create the window.
	 */
	public ResultsWindow(int x, int y, StatsCollector st)
	{
		//Set up the window
		super(title);
		//DisplayMode dm = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode(); //Get screen resolution
		//setBounds((dm.getWidth()-width)/2, (dm.getHeight()-height)/2, width, height); //Position window in center of screen
		setBounds(0, 0, width, height);
		setResizable(true);
		statsCollector = st;
		
		// Puts a listener for resize
		this.addComponentListener(this);
		
		//Initialise variables
		tab0 = new GeneralResultsPanel(statsCollector);
		tab1 = new ResultsPanel(statsCollector, GraphType.ARRIVAL);
		tab2 = new ResultsPanel(statsCollector, GraphType.MAX_QUEUE_TIME);
		tab3 = new ResultsPanel(statsCollector, GraphType.AVG_QUEUE_TIME);
		tab4 = new ResultsPanel(statsCollector, GraphType.TOTAL_TIME);
		
		pTabs = new JTabbedPane();
		
		//Set up tabs
		
		pTabs.add("General Information", tab0);
		pTabs.add("Total cars", tab1);
		pTabs.add("Maximum queue time", tab2);
		pTabs.add("Average queue time", tab3);
		pTabs.add("Time in intersection", tab4);
		pTabs.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	    		repaint();
	        }
	    });
		
		//Position and add panels to the window
		setLayout(null);
		pTabs.setBounds(0, 0, width, height);
		add(pTabs);
		
		//Display the window
		setVisible(true);
		pack();
	}
	
	///////////////////
	//---LISTENERS---//
	///////////////////
	/**Called when the window is resized.
	 * Used to resize and graphs being displayed in the window.*/
	public void componentResized(ComponentEvent e)
	{
		Dimension bounds = getSize();
		if (bounds.width < width) {
			bounds.width = width;
		}
		if (bounds.height < height) {
			bounds.height = height;
		}
		setBounds(getX(), getY(), bounds.width, bounds.height);
		pTabs.setBounds(0, 0, bounds.width, bounds.height);
		validate();
		repaint();
	}
	public void componentHidden(ComponentEvent e) {}
    public void componentMoved(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}
    
}