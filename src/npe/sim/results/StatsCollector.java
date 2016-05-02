package npe.sim.results;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StatsCollector
{	
	////////////////////////
	//---MISC VARIABLES---//
	////////////////////////
	private int INTERVAL_LENGTH = 60*30;
	private int xScale = 1; // How many values will be combined together to make 1 point
	private String startTime;
	
	//////////////////////
	//---GRAND TOTALS---//
	//////////////////////
	private long totalCars = 0;
	private long totalCarsDeleted = 0;
	private long totalTicks = 0;
	private int crunchTotal = -1;
	
	////////////////////
	//---MAX VALUES---//
	////////////////////
	private int totalYMax = 0;
	private double avgWaitYMax = 0;
	private double maxWaitYMax = 0;
	private double aliveYMax = 0;
	
	///////////////////////////////
	//---Frome Road from North---//
	///////////////////////////////
	private ArrayList<Integer> fromNorthTotal = new ArrayList<Integer>(); // in car units
	private ArrayList<Double> fromNorthQueueTimeAverage = new ArrayList<Double>(); // in seconds
	private ArrayList<Double> fromNorthQueueTimeMax = new ArrayList<Double>(); // in seconds
	private ArrayList<Double> fromNorthTimeInIntersection = new ArrayList<Double>(); // in seconds
	//Intervals
	private int fromNorthTotalInterval = 0;
	private int fromNorthTotalExitInterval = 0;
	private double fromNorthQueueTimeAverageInterval = 0;
	private double fromNorthQueueTimeMaxInterval = 0;	
	private double fromNorthTimeInIntersectionInterval = 0;
	private int fromNorthTotalDeletedInterval = 0;

	///////////////////////////////
	//---Frome Road from South---//
	///////////////////////////////
	private ArrayList<Integer> fromSouthTotal = new ArrayList<Integer>();
	private ArrayList<Double> fromSouthQueueTimeAverage = new ArrayList<Double>();
	private ArrayList<Double> fromSouthQueueTimeMax = new ArrayList<Double>();
	private ArrayList<Double> fromSouthTimeInIntersection = new ArrayList<Double>(); // in seconds
	//Intervals
	private int fromSouthTotalInterval = 0;
	private int fromSouthTotalExitInterval = 0;
	private double fromSouthQueueTimeAverageInterval = 0;
	private double fromSouthQueueTimeMaxInterval = 0;
	private double fromSouthTimeInIntersectionInterval = 0;
	private int fromSouthTotalDeletedInterval = 0;

	/////////////////////////////////
	//---North Terrace from East---//
	/////////////////////////////////
	private ArrayList<Integer> fromEastTotal = new ArrayList<Integer>();
	private ArrayList<Double> fromEastQueueTimeAverage = new ArrayList<Double>();
	private ArrayList<Double> fromEastQueueTimeMax = new ArrayList<Double>();	
	private ArrayList<Double> fromEastTimeInIntersection = new ArrayList<Double>(); // in seconds
	//Intervals
	private int fromEastTotalInterval = 0;
	private int fromEastTotalExitInterval = 0;
	private double fromEastQueueTimeAverageInterval = 0;
	private double fromEastQueueTimeMaxInterval = 0;	
	private double fromEastTimeInIntersectionInterval = 0;
	private int fromEastTotalDeletedInterval = 0;

	/////////////////////////////////
	//---North Terrace from West---//
	/////////////////////////////////
	private ArrayList<Integer> fromWestTotal = new ArrayList<Integer>();
	private ArrayList<Double> fromWestQueueTimeAverage = new ArrayList<Double>();
	private ArrayList<Double> fromWestQueueTimeMax = new ArrayList<Double>();	
	private ArrayList<Double> fromWestTimeInIntersection = new ArrayList<Double>(); // in seconds
	//Intervals
	private int fromWestTotalInterval = 0;
	private int fromWestTotalExitInterval = 0;
	private double fromWestQueueTimeAverageInterval = 0;
	private double fromWestQueueTimeMaxInterval = 0;
	private double fromWestTimeInIntersectionInterval = 0;
	private int fromWestTotalDeletedInterval = 0;
	
	//////////////////
	//---CREATION---//
	//////////////////
	/**Creates a new stats collector.*/
	public StatsCollector() 
	{
		// Initialise the 0 state with 0's
		crunchInterval();
		DateFormat time = new SimpleDateFormat("HH:mm");
		DateFormat date = new SimpleDateFormat("dd/MM/yyyy");
		startTime = time.format(new Date());
		startTime += " on " + date.format(new Date());
		
	}
	
	/////////////////
	//---METHODS---//
	/////////////////
	/**Called when a new car is added to the intersection.
	 * @param direction The direction of the car when it is created.
	 */
	public void newCar(double direction)
	{
		totalCars++;
		// Adds to the correct road total based on direction
		switch ((int)direction) {
			case 180: 
			{
				fromEastTotalInterval++;
				break;
			}
			case 270:
			{
				fromNorthTotalInterval++;
				break;
			}
			case 0:
			{
				fromWestTotalInterval++;
				break;
			}
			case 90:
			{
				fromSouthTotalInterval++;
				break;
			}
		}
		// Some lane stuff goes here
	}
	
	/** 
	 * Called when a car leaves its lane
	 * @param direction the direction of the car when it is created
	 * @param queueTime the total time in seconds that the car had stopped completely
	 */
	public void carExit(double direction, double queueTime)
	{
		// Adds total queue time for the right road and check if max queue time has been changed
		switch ((int)direction) {
			case 180: 
			{
				fromEastQueueTimeAverageInterval += queueTime;
				fromEastTotalExitInterval++;
				if (queueTime > fromEastQueueTimeMaxInterval) {
					fromEastQueueTimeMaxInterval = queueTime;
				}
				break;
			}
			case 90:
			{
				fromNorthQueueTimeAverageInterval += queueTime;
				fromNorthTotalExitInterval++;
				if (queueTime > fromNorthQueueTimeMaxInterval) {
					fromNorthQueueTimeMaxInterval = queueTime;
				}
				break;
			}
			case 0:
			{
				fromWestQueueTimeAverageInterval += queueTime;
				fromWestTotalExitInterval++;
				if (queueTime > fromWestQueueTimeMaxInterval) {
					fromWestQueueTimeMaxInterval = queueTime;
				}
				break;
			}
			case 270:
			{
				fromSouthQueueTimeAverageInterval += queueTime;
				fromSouthTotalExitInterval++;
				if (queueTime > fromSouthQueueTimeMaxInterval) {
					fromSouthQueueTimeMaxInterval = queueTime;
				}
				break;
			}
		}		
	}
	
	public void carDelete(double direction, double timeAlive) {
		totalCarsDeleted++;
		switch ((int)direction) {
			case 180: {
				fromEastTimeInIntersectionInterval += timeAlive;
				fromEastTotalDeletedInterval++;
				break;
			}
			case 90: {
				fromNorthTimeInIntersectionInterval += timeAlive;
				fromNorthTotalDeletedInterval++;
				break;
			}
			case 0: {
				fromWestTimeInIntersectionInterval += timeAlive;
				fromWestTotalDeletedInterval++;
				break;
			}
			case 270: {
				fromSouthTimeInIntersectionInterval += timeAlive;
				fromSouthTotalDeletedInterval++;
				break;
			}
			
		}
	}
	
	/** A tick method called by SimController whenever a tick has passed */
	public void tick()
	{
		totalTicks++;
		if (totalTicks % INTERVAL_LENGTH == 0) {
			crunchInterval();
		}
	}
	
	/**
	 * Gets the total ticks that has passed since the start of the simulation
	 * @return the total ticks that has passed
	 */
	public long getCurrentTick()
	{
		return totalTicks;
	}

	/** Process current data interval and create a point on the graph */
	public void crunchInterval()
	{
		// Store all the data of the interval into the arraylists		
		crunchTotal++;
		System.out.println("Crunch interval " + crunchTotal);
		
		// Storing the total number of cars that entered from each road
		fromNorthTotal.add(fromNorthTotalInterval);
		fromSouthTotal.add(fromSouthTotalInterval);
		fromEastTotal.add(fromEastTotalInterval);
		fromWestTotal.add(fromWestTotalInterval);
		
		// Checking the total cars that entered
		System.out.println("Total: North - " + fromNorthTotalInterval + "  South - " + fromSouthTotalInterval + "  East - " + fromEastTotalInterval + "  West - " + fromWestTotalInterval);
		System.out.print("Avg wait time: ");
		
		// Storing the average wait time for each road but also checking if no cars has entered to avoid dividing by 0
		if (fromNorthTotalExitInterval == 0) {
			fromNorthQueueTimeAverageInterval = 0.0;
			fromNorthQueueTimeAverage.add(0.0);
			System.out.print("North - 0.0 ");
		} else {
			fromNorthQueueTimeAverageInterval = fromNorthQueueTimeAverageInterval/fromNorthTotalExitInterval;
			fromNorthQueueTimeAverage.add(fromNorthQueueTimeAverageInterval);
			System.out.print("North - " + fromNorthQueueTimeAverageInterval + " ");
		}
		
		if (fromSouthTotalExitInterval == 0) {
			fromSouthQueueTimeAverageInterval = 0.0;
			fromSouthQueueTimeAverage.add(0.0);
			System.out.print("South - 0.0 ");
		} else {
			fromSouthQueueTimeAverageInterval = fromSouthQueueTimeAverageInterval/fromSouthTotalExitInterval;
			fromSouthQueueTimeAverage.add(fromSouthQueueTimeAverageInterval);
			System.out.print("South - " + fromSouthQueueTimeAverageInterval + " ");
		}
		
		if (fromEastTotalExitInterval == 0) {
			fromEastQueueTimeAverageInterval = 0.0;
			fromEastQueueTimeAverage.add(0.0);
			System.out.print("East - 0.0 ");
		} else {
			fromEastQueueTimeAverageInterval = fromEastQueueTimeAverageInterval/fromEastTotalExitInterval;
			fromEastQueueTimeAverage.add(fromEastQueueTimeAverageInterval);
			System.out.print("East - " + fromEastQueueTimeAverageInterval + " ");
		}
		
		if (fromWestTotalExitInterval == 0) {
			fromWestQueueTimeAverageInterval = 0.0;
			fromWestQueueTimeAverage.add(0.0);
			System.out.println("West - 0.0 ");
		} else {
			fromWestQueueTimeAverageInterval = fromWestQueueTimeAverageInterval/fromWestTotalExitInterval;
			fromWestQueueTimeAverage.add(fromWestQueueTimeAverageInterval);
			System.out.println("West - " + fromWestQueueTimeAverageInterval + " ");
		}		
		
		// Storing the time a car is alive for
		System.out.print("Average time alive: ");
		if (fromNorthTotalDeletedInterval == 0) {
			fromNorthTimeInIntersectionInterval = 0.0;
			fromNorthTimeInIntersection.add(0.0);
			System.out.print("North - 0.0 ");
		} else {
			fromNorthTimeInIntersectionInterval = fromNorthTimeInIntersectionInterval/fromNorthTotalDeletedInterval;
			fromNorthTimeInIntersection.add(fromNorthTimeInIntersectionInterval);
			System.out.print("North - " + fromNorthTimeInIntersectionInterval + " ");
		}
		
		if (fromSouthTotalDeletedInterval == 0) {
			fromSouthTimeInIntersectionInterval = 0.0;
			fromSouthTimeInIntersection.add(0.0);
			System.out.print("South - 0.0 ");
		} else {
			fromSouthTimeInIntersectionInterval = fromSouthTimeInIntersectionInterval/fromSouthTotalDeletedInterval;
			fromSouthTimeInIntersection.add(fromSouthTimeInIntersectionInterval);
			System.out.print("South - " + fromSouthTimeInIntersectionInterval + " ");
		}
		
		if (fromEastTotalDeletedInterval == 0) {
			fromEastTimeInIntersectionInterval = 0.0;
			fromEastTimeInIntersection.add(0.0);
			System.out.print("East - 0.0 ");
		} else {
			fromEastTimeInIntersectionInterval = fromEastTimeInIntersectionInterval/fromEastTotalDeletedInterval;
			fromEastTimeInIntersection.add(fromEastTimeInIntersectionInterval);
			System.out.print("East - " + fromEastTimeInIntersectionInterval + " ");
		}
		
		if (fromWestTotalDeletedInterval == 0) {
			fromWestTimeInIntersectionInterval = 0.0;
			fromWestTimeInIntersection.add(0.0);
			System.out.println("West - 0.0 ");
		} else {
			fromWestTimeInIntersectionInterval = fromWestTimeInIntersectionInterval/fromWestTotalDeletedInterval;
			fromWestTimeInIntersection.add(fromWestTimeInIntersectionInterval);
			System.out.println("West - " + fromWestTimeInIntersectionInterval + " ");
		}
		
		// Storing the max wait time for each road
		fromNorthQueueTimeMax.add(fromNorthQueueTimeMaxInterval);
		fromSouthQueueTimeMax.add(fromSouthQueueTimeMaxInterval);
		fromEastQueueTimeMax.add(fromEastQueueTimeMaxInterval);
		fromWestQueueTimeMax.add(fromWestQueueTimeMaxInterval);
		
		System.out.println("Max wait time: North - " + fromNorthQueueTimeMaxInterval + "  South - " + fromSouthQueueTimeMaxInterval + "  East - " + fromEastQueueTimeMaxInterval + "  West - " + fromWestQueueTimeMaxInterval);
		System.out.println("Before Average Wait Y Max: " + avgWaitYMax);
		
		// Getting max y-value for total
		if (fromNorthTotalInterval > totalYMax || fromSouthTotalInterval > totalYMax || fromEastTotalInterval > totalYMax || fromWestTotalInterval > totalYMax) {
			totalYMax = Math.max(Math.max(fromNorthTotalInterval, fromSouthTotalInterval), Math.max(fromEastTotalInterval, fromWestTotalInterval));			
		}
		
		// Getting max y-value for avg
		if (fromNorthQueueTimeAverageInterval > avgWaitYMax || fromSouthQueueTimeAverageInterval > avgWaitYMax || fromEastQueueTimeAverageInterval > avgWaitYMax || fromWestQueueTimeAverageInterval > avgWaitYMax) {
			avgWaitYMax = Math.max(Math.max(fromNorthQueueTimeAverageInterval, fromSouthQueueTimeAverageInterval), Math.max(fromEastQueueTimeAverageInterval, fromWestQueueTimeAverageInterval));		
		}
		
		// Getting max y-value for max
		if (fromNorthQueueTimeMaxInterval > maxWaitYMax || fromSouthQueueTimeMaxInterval > maxWaitYMax || fromEastQueueTimeMaxInterval > maxWaitYMax || fromWestQueueTimeMaxInterval > maxWaitYMax) {
			maxWaitYMax = Math.max(Math.max(fromNorthQueueTimeMaxInterval, fromSouthQueueTimeMaxInterval), Math.max(fromEastQueueTimeMaxInterval, fromWestQueueTimeMaxInterval));	
		}
		
		// Getting max y-value for alive time
		if (fromNorthTimeInIntersectionInterval > aliveYMax || fromSouthTimeInIntersectionInterval > aliveYMax || fromEastTimeInIntersectionInterval > aliveYMax || fromWestTimeInIntersectionInterval > aliveYMax) {
			aliveYMax = Math.max(Math.max(fromNorthTimeInIntersectionInterval, fromSouthTimeInIntersectionInterval), Math.max(fromEastTimeInIntersectionInterval, fromWestTimeInIntersectionInterval));		
		}
		
		//System.out.println("Total Y Max: " + totalYMax);
		System.out.println("Average Wait Y Max: " + avgWaitYMax);
		//System.out.println("Max Wait Y Max: " + maxWaitYMax);
		
		// Re-initialise the interval variables
		fromNorthTotalInterval = 0;
		fromNorthTotalExitInterval = 0;
		fromNorthQueueTimeAverageInterval = 0.0;
		fromNorthQueueTimeMaxInterval = 0.0;		
		fromNorthTimeInIntersectionInterval = 0.0;
		fromNorthTotalDeletedInterval = 0;
		//fromNorthQueueLengthInterval = 0;
				
		fromSouthTotalInterval = 0;
		fromSouthTotalExitInterval = 0;
		fromSouthQueueTimeAverageInterval = 0.0;
		fromSouthQueueTimeMaxInterval = 0.0;		
		fromSouthTimeInIntersectionInterval = 0.0;
		fromSouthTotalDeletedInterval = 0;
		//fromSouthQueueLengthInterval = 0;
		
		fromEastTotalInterval = 0;
		fromEastTotalExitInterval = 0;
		fromEastQueueTimeAverageInterval = 0.0;
		fromEastQueueTimeMaxInterval = 0.0;		
		fromEastTimeInIntersectionInterval = 0.0;
		fromEastTotalDeletedInterval = 0;
		//fromEastQueueLengthInterval = 0;
		
		fromWestTotalInterval = 0;
		fromWestTotalExitInterval = 0;
		fromWestQueueTimeAverageInterval = 0.0;
		fromWestQueueTimeMaxInterval = 0.0;		
		fromWestTimeInIntersectionInterval = 0.0;
		fromWestTotalDeletedInterval = 0;
		//fromWestQueueLengthInterval = 0;
	}
	
	/** 
	 * Gets the points for the total entered graph 
	 * road:
	 * 0 - Frome Road from North
	 * 1 - Frome Road from South
	 * 2 - North Terrace from East
	 * 3 - North Terrace from West
	 * 
	 * index acts as the x-value where the value is the y position
	 * @return int[road][index] of points if exist or null if there was no data
	 */
	public int[][] getTotalEnteredPoints() {
		// Checks if no data was collected
		if (crunchTotal < 1) {
			return null;
		}
		getXScale();
		int[][] points = new int[4][(crunchTotal/xScale) + 1];
		
		// Temporary variables, please don't hate me
		int t1 = 0; 
		int t2 = 0; 
		int t3 = 0; 
		int t4 = 0; 
		
		for (int i = 1; i <= crunchTotal - (crunchTotal%xScale); i++) {
			// Temporary total
			t1 += fromNorthTotal.get(i);
			t2 += fromSouthTotal.get(i);
			t3 += fromEastTotal.get(i);
			t4 += fromWestTotal.get(i);
			
			if (i % xScale == 0) {
				// Get the average as the y-values
				points[0][i/xScale] = (t1/xScale);
				points[1][i/xScale] = (t2/xScale);
				points[2][i/xScale] = (t3/xScale);
				points[3][i/xScale] = (t4/xScale);
				
				// Reset temporary total
				t1 = 0;
				t2 = 0;
				t3 = 0;
				t4 = 0;
			}			
		}
		return points;
	}
	
	/** 
	 * Gets the points for the average wait time graph
	 * road:
	 * 0 - Frome Road from North
	 * 1 - Frome Road from South
	 * 2 - North Terrace from East
	 * 3 - North Terrace from West
	 * 
	 * index acts as the x-value where the value is the y position
	 * @return int[road][index] of points if exist or null if there was no data
	 */
	public double[][] getAverageWaitPoints() {
		// Checks if no data was collected
		if (crunchTotal < 1) {
			return null;
		}
		getXScale();
		double[][] points = new double[4][(crunchTotal/xScale) + 1]; // +1 for the zero values
		
		// Temporary variables
		double t1 = 0; 
		double t2 = 0; 
		double t3 = 0; 
		double t4 = 0; 
		for (int i = 1; i <= crunchTotal - (crunchTotal%xScale); i++) {
			
			t1 += fromNorthQueueTimeAverage.get(i);
			t2 += fromSouthQueueTimeAverage.get(i);
			t3 += fromEastQueueTimeAverage.get(i);
			t4 += fromWestQueueTimeAverage.get(i);
			
			if (i % xScale == 0) {
				// Get the average of the average values
				points[0][i/xScale] = (t1/xScale);
				points[1][i/xScale] = (t2/xScale);
				points[2][i/xScale] = (t3/xScale);
				points[3][i/xScale] = (t4/xScale);
				
				// Reset temporary total
				t1 = 0;
				t2 = 0;
				t3 = 0;
				t4 = 0;
			}
		}
		return points;
	}
	
	public double[][] getTimeAlivePoints() {
		// Checks if no data was collected
		if (crunchTotal < 1) {
			return null;
		}
		getXScale();
		double[][] points = new double[4][(crunchTotal/xScale) + 1]; // +1 for the zero values
		
		// Temporary variables
		double t1 = 0; 
		double t2 = 0; 
		double t3 = 0; 
		double t4 = 0; 
		for (int i = 1; i <= crunchTotal - (crunchTotal%xScale); i++) {
			
			t1 += fromNorthTimeInIntersection.get(i);
			t2 += fromSouthTimeInIntersection.get(i);
			t3 += fromEastTimeInIntersection.get(i);
			t4 += fromWestTimeInIntersection.get(i);
			
			if (i % xScale == 0) {
				// Get the average of the average values
				points[0][i/xScale] = (t1/xScale);
				points[1][i/xScale] = (t2/xScale);
				points[2][i/xScale] = (t3/xScale);
				points[3][i/xScale] = (t4/xScale);
				
				// Reset temporary total
				t1 = 0;
				t2 = 0;
				t3 = 0;
				t4 = 0;
			}
		}
		return points;
	}
	
	/** 
	 * Gets the points for the average wait time graph
	 * road:
	 * 0 - Frome Road from North
	 * 1 - Frome Road from South
	 * 2 - North Terrace from East
	 * 3 - North Terrace from West
	 * 
	 * index acts as the x-value where the value is the y position
	 * @return int[road][index] of points if exist or null if there was no data
	 */
	public double[][] getMaxWaitPoints() {
		// Checks if no data was collected
		if (crunchTotal < 1) {
			return null;
		}
		getXScale();
		double[][] points = new double[4][(crunchTotal/xScale)+1]; // +1 for the zero values
		
		// Temporary variables
		double t1 = 0; 
		double t2 = 0; 
		double t3 = 0; 
		double t4 = 0; 
		for (int i = 1; i <= crunchTotal - (crunchTotal%xScale); i++) {
			
			t1 += fromNorthQueueTimeMax.get(i);
			t2 += fromSouthQueueTimeMax.get(i);
			t3 += fromEastQueueTimeMax.get(i);
			t4 += fromWestQueueTimeMax.get(i);
			
			if (i % xScale == 0) {
				// Get the average of the average values
				points[0][i/xScale] = (t1/xScale);
				points[1][i/xScale] = (t2/xScale);
				points[2][i/xScale] = (t3/xScale);
				points[3][i/xScale] = (t4/xScale);
				
				// Reset temporary total
				t1 = 0;
				t2 = 0;
				t3 = 0;
				t4 = 0;
			}
		}
		return points;
	}
	
	public int[] generalNumbers() {
		return null;
	}
	
	/**
	 * The number of minutes combined into the one point
	 * @return the scale of the x-axis
	 */
	public int getXScale() {
		if (crunchTotal == 0) {
			return 1;
		}
		if (crunchTotal > 400) {
			xScale = (crunchTotal-1)/20; // scale is larger than number of points so points will stay at 20 while the scale increases
		} else {
			xScale = (crunchTotal - 1)/21; // determines highest scale that would fit within 20 points
			xScale++;			
		}
		return xScale;
	}
	
	public int getYScale(ResultsPanel.GraphType type) {
		switch (type) {
			case ARRIVAL: {
				return (int)(Math.ceil((Math.ceil(totalYMax/10))/5))*5;
			}
			case MAX_QUEUE_TIME: {
				return (int)(Math.ceil((Math.ceil(maxWaitYMax/10))/5))*5;
			}
			case AVG_QUEUE_TIME: {
				return (int)(Math.ceil((Math.ceil(avgWaitYMax/10))/5))*5;
			}
			case TOTAL_TIME: {
				return (int)(Math.ceil((Math.ceil(aliveYMax/10))/5))*5;
			}
		}
		return 1;
	}
	
	public double[] overallStats() {
		double[] overall = new double[15];
		double temp = 0;
		
		if (crunchTotal < 1) {
			return null;
		}

		overall[0] = totalCars;
		overall[1] = totalCarsDeleted;
		overall[2] = totalCars - totalCarsDeleted;
		
		// North
		temp = 0;
		for (double d : fromNorthQueueTimeAverage) {
			temp += d;
		}		
		overall[3] = temp/fromNorthQueueTimeAverage.size();
		
		for (double d : fromNorthQueueTimeMax) {
			if (d > overall[4]) {
				overall[4] = d;
			}
		}		
		
		temp = 0;
		for (double d : fromNorthTimeInIntersection) {
			temp += d;
		}		
		overall[5] = temp/fromNorthTimeInIntersection.size();
		
		// South
		temp = 0;
		for (double d : fromSouthQueueTimeAverage) {
			temp += d;
		}		
		overall[6] = temp/fromSouthQueueTimeAverage.size();
		
		for (double d : fromSouthQueueTimeMax) {
			if (d > overall[7]) {
				overall[7] = d;
			}
		}		
		
		temp = 0;
		for (double d : fromSouthTimeInIntersection) {
			temp += d;
		}		
		overall[8] = temp/fromSouthTimeInIntersection.size();
		
		// East
		temp = 0;
		for (double d : fromEastQueueTimeAverage) {
			temp += d;
		}		
		overall[9] = temp/fromEastQueueTimeAverage.size();
		
		for (double d : fromEastQueueTimeMax) {
			if (d > overall[10]) {
				overall[10] = d;
			}
		}		
		
		temp = 0;
		for (double d : fromEastTimeInIntersection) {
			temp += d;
		}		
		overall[11] = temp/fromEastTimeInIntersection.size();
		
		// West
		temp = 0;
		for (double d : fromWestQueueTimeAverage) {
			temp += d;
		}		
		overall[12] = temp/fromWestQueueTimeAverage.size();
		
		for (double d : fromWestQueueTimeMax) {
			if (d > overall[13]) {
				overall[13] = d;
			}
		}		
		
		temp = 0;
		for (double d : fromWestTimeInIntersection) {
			temp += d;
		}		
		overall[14] = temp/fromWestTimeInIntersection.size();
		
		return overall;
	}
	
	/**
	 * Returns the timestamp of when the simulation started in the format hh:mm on dd/mm/yy
	 * @return the start time for the simulation in the format described
	 */
	public String getStartTime() {
		return startTime;
	}
	
	public Double getTotalRunTime() {
		return (totalTicks/30.0);
	}
}