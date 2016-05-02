package npe.sim;
import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import npe.sim.results.StatsCollector;
import npe.sim.results.ResultsWindow;

/**
 * The panel on which the simulation's graphics are drawn.
 * Controls when and how the simulation is run, in terms of starting, stopping, speed and duration.
 */

public class SimPanel extends JPanel implements Runnable {
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**A Stats Collector lol*/
	private StatsCollector statsCollector = new StatsCollector();
	/**The simulation properties which will be used for the simulation.*/
	private SimProperties simProperties = new SimProperties();
	/**The simulation controller being driven by this panel.*/
	private SimController simController = new SimController(simProperties, statsCollector);
	/**The simulation window which contains this panel.*/
	private SimWindow simWindow;
	//Dimensions
	/**Width of the SimPanel.*/
	public static final int SP_WIDTH = 774;
	/**Height of the SimPanel.*/
	public static final int SP_HEIGHT = 600;
	//LOOP CONTROL
	/**Whether or not the main loop is currently running.*/
	private boolean running = false;
	/**Whether or not the simulation is currently paused.*/
	private boolean paused = false;
	/**The number of Ticks Per Second for "normal" simulation speed.*/
	public static final int TPS = 30;
	/**Simulation speed multiplier. Target FPS = TPS * speed.*/
	private double speed = 1;
	/**The current animation speed in Frames Per Second.*/
	private long fps = 0;
	/**The duration of the simulation, in ticks.*/
	private long duration = 0;
	
	////////////////////////
	//---VIEW VARIABLES---//
	////////////////////////
	//Screen Drag Control
	/**The x-coordinate of the start position of the mouse drag.*/
	private double startX = 0;
	/**The x-coordinate of the start position of the mouse drag.*/
	private double startY = 0;
	/**The previous horizontal amount dragged.*/
	private double prevX = 0;
	/**The previous vertical amount dragged.*/
	private double prevY = 0;
	/**The amount by which the graphics object will be translated horizontally.*/
	private double tx = 0;
	/**The amount by which the graphics object will be translated vertically.*/
	private double ty = 0;
	//Scaling Control
	/**The horizontal scaling factor.*/
	private double sx = 1.0;
	/**The vertical scaling factor.*/
	private double sy = 1.0;
	/**The x-coordinate of the centre of the panel.*/
	private double centreX = SP_WIDTH/2;
	/**The x-coordinate of the centre of the panel.*/
	private double centreY = SP_HEIGHT/2;
	/**The minimum allowed scaling factor.*/
	private static final double MIN_SCALE = 0.07;
	/**The increment to scale by each time scaling occurs.*/
	private static final double SCALE_AMOUNT = 0.05;
	//button controll
	/**The state of the left mouse button.*/
	private static boolean leftButtonPressed = false;
	
	
	//////////////////
	//---CREATION---//
	//////////////////
	/**Initialises a SimPanel.*/
	public SimPanel(SimWindow sw, SimProperties sp)
	{
		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{	
				//if the left button is pressed dont do anything. 
				if(leftButtonPressed) return;
				
				if (e.getButton() == MouseEvent.BUTTON1) {
					e.getComponent().setCursor(new Cursor(Cursor.MOVE_CURSOR));
					//Get the starting position of the mouse
					startX = e.getX();
					startY = e.getY();
					//signal that the left mouse button is pressed
					leftButtonPressed = true;
					
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					//Centre the screen
					tx = 0;
					ty = 0;
					prevX = 0;
					prevY = 0;
					sx = 1.0;
					sy = 1.0;
					repaint();
				}
			}
			public void mouseReleased(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1) {
					e.getComponent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					//Add amount by which mouse was dragged
					prevX += e.getX() - startX;
					prevY += e.getY() - startY;
					//signal that the left mouse button is released
					leftButtonPressed = false;
				}
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e)) {
					Point p = e.getPoint();
					//Calculate amount by which we need to translate
					tx = prevX + p.x - startX;
					ty = prevY + p.y - startY;
					//We repaint so we can drag before we start the simulation
					repaint();
				}
			}
		});
		addMouseWheelListener(new MouseWheelListener()
		{
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				//Calculate amount by which to scale
				if (sx > MIN_SCALE || (sx <= MIN_SCALE && e.getWheelRotation()*SCALE_AMOUNT < 0)) {
					sx -= e.getWheelRotation()*SCALE_AMOUNT;
				}
				if (sy > MIN_SCALE || (sy <= MIN_SCALE && e.getWheelRotation()*SCALE_AMOUNT < 0)) {
					sy -= e.getWheelRotation()*SCALE_AMOUNT;
				}
				//We repaint so we can drag before we start the simulation
				repaint();
			}
		});
		//Set up panel variables
		setBackground(Color.GRAY);
		this.simWindow = sw;
		simProperties = sp;
	}
	
	//////////////
	//---LOOP---//
	//////////////
	/**Runs the main simulation loop.*/
	public void run()
	{
		//---SET UP SIMULATION---//
		statsCollector = new StatsCollector();
		simController = new SimController(simProperties, statsCollector);
		
		//---START MAIN LOOP---//
		long prevTime = time(); //Time of previous frame
		long framesSinceFpsCheck = 0; //Number of frames since last frame count
		long timeOfFpsCheck = time(); //Time of last frame count
		while (running) {
			//CHECK FOR DURATION REACHED
			if (duration>0 && simController.t()>duration) {
				simWindow.stopPressed();
			}
			//SIMULATION LOOP
			if (!paused) {
				simController.tick();
			}
			repaint();
			//CALCULATE TICK RATE
			if (time() > timeOfFpsCheck+1000) { //If one second has elapsed
				fps = framesSinceFpsCheck;
				framesSinceFpsCheck = 0;
				timeOfFpsCheck = time();
			} else {
				framesSinceFpsCheck++;
			}
			//GO TO NEXT FRAME
			long timeElapsed = time() - prevTime;
			int frameTime = (int) (1000 / (TPS*speed));
			if (paused) { //Run loop at constand speed if paused
				frameTime = 1000 / TPS;
			}
			if (frameTime > timeElapsed) {
				try {
					Thread.sleep(frameTime - timeElapsed);
				} catch (InterruptedException e) {
					System.err.println("ERROR: Sleep interrupted.");
				}
			}
			prevTime = time();
		}
	}
	/**Gets a precise measurement of time which can be used to find durations.
	 * @return Time in milliseconds.
	 */
	private long time()
	{
		return System.nanoTime() / 1000000;
	}
	/**Performs the drawing which is done at the end of each loop iteration.
	 * @param g Graphics context to draw on.
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		
		//Draw the simulation
		//Scale by the centre of the screen
		g2d.translate(centreX, centreY);
		g2d.scale(sx, sy);
		g2d.translate(-centreX, -centreY);
		//Translate by amount mouse has been dragged
		g2d.translate(tx/sx, ty/sy);
		
		simController.draw(g2d);
		
		//Reverse transforms so text is drawn normally
		g2d.translate(-tx/sx, -ty/sy);
		g2d.translate(centreX, centreY);
		g2d.scale(1/sx, 1/sy);
		g2d.translate(-centreX, -centreY);
		
		//Draw FPS
		int labelY = 0;
		g2d.setColor(new Color(0x44,0xCC,0xFF));
		g.setFont(new Font ("Arial", Font.BOLD, 12));
		
		//Draw FPS
		if(Utils.FPSDrawing){
			g2d.drawString("FPS: "+fps, 16, labelY+=16);
		}
		
		//Draw time left / time simulated
		if (simProperties.duration > 0) {
			long seconds = ( duration - simController.t() ) / TPS;
			long minutes = seconds / 60;
			String secondsString = String.format( "%02d", Math.abs(seconds%60) );
			String timeLeft = minutes + ":" + secondsString;
			g2d.drawString("Time Left (mm:ss): "+ timeLeft, 16, labelY+=16);
		} else {
			long seconds = simController.t() / TPS;
			long minutes = seconds / 60;
			String secondsString = String.format( "%02d", Math.abs(seconds%60) );
			String simTime = minutes + ":" + secondsString;
			g2d.drawString("Time simulated (mm:ss): "+ simTime, 16, labelY+=16);
		}
		
		//Draw seed
		if(Utils.seedDrawing){
			g2d.drawString("Seed: "+Utils.getSeed(), 16, labelY+=16);
		}
	}
	
	/////////////////
	//---CONTROL---//
	/////////////////
	/**Starts a new simulation, or resumes the current simulation.*/
	public void start()
	{
		if (!running) { //If a simulation isn't running
			//Start a new simulation
			running = true;
			paused = false;
			duration = (long) simProperties.duration*60*TPS;
			new Thread(this, "Simulation Thread").start();
		} else { //If a simulation is running
			paused = false; //Unpause the simulation
		}
	}
	/**Pauses the current simulation.*/
	public void pause()
	{
		paused = true;
	}
	/**Stops the current simulation.*/
	public void stop()
	{
		running = false;
		new ResultsWindow(0, 0, statsCollector);
	}
	/**Sets the simulation speed multiplier to the given value.
	 * @param newSpeed The new simulation speed multiplier. Must be greater than zero.
	 */
	public void setSpeed(double newSpeed)
	{
		if (newSpeed > 0) {
			speed = newSpeed;
		}
	}
	/**Performs one iteration of the main loop.*/
	public void tick()
	{
		simController.tick();
	}
	
}