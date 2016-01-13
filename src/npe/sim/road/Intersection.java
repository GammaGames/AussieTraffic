package npe.sim.road;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.*;
import java.awt.geom.*;
import java.awt.Color;
import java.io.FileNotFoundException;
import npe.sim.SimPanel;
import npe.sim.Sprite;
import npe.sim.Utils;
import npe.sim.entity.Pedestrian;
import npe.sim.light.TrafficLightController;
import npe.sim.results.StatsCollector;
import npe.sim.SimProperties;
import static npe.sim.Utils.*;

public class Intersection {
	
	private static final double TURNING_TIME = Utils.convertTime(4);
	
	//The x and y coordinates of the middle part of the intersection
	private double x;
	private double y;
	
	//The bounds in which an entity will be destroyed if it leaves
	private double minX, maxX;
	private double minY, maxY;
	
	//The Width and height of the middle part of the intersection
	private double width = 0;
	private double height = 0;
	
	private Road northTce;
	private Road fromeRd;
	
	private int numLanesNorth ;
	private int numLanesFrome ;
	
	private double speedLimit;
	
	private TrafficLightController trafficLightController;
	
	//Road sign sprites
	private Sprite signNorth;
	private Sprite signFrome;
	private int NorthSignX, NorthSignY;
	private int FromeSignX, FromeSignY;
	
	private StatsCollector statsCollector;
	
	/**The taxi rank of the intersection.*/
	private TaxiRank taxiRank;
	/**The variable to enable the taxi rank.*/
	private boolean taxiRankEnabled = false;	
	
	/**The Lane type .*/
	private VehicleLane.Type leftMostLaneType;	
	/**The Lane type .*/
	private VehicleLane.Type leftMostLaneTypeOpposite;
	

	/**Constructor just initialises a new Arraylist of Roads*/
	public Intersection(SimProperties sp, StatsCollector sc)
	{	
		this.trafficLightController = sp.trafficLightController;
		this.numLanesFrome = sp.numLanesFrome + sp.numLanesFromeExtra ;
		this.numLanesNorth = sp.numLanesNorth + sp.numLanesNorthExtra;
		width = (numLanesFrome * 2 - 1) * VehicleLane.LANE_WIDTH;
		height = (numLanesNorth * 2 - 1) * VehicleLane.LANE_WIDTH;
		x = SimPanel.SP_WIDTH/2 - width/2;
		y = SimPanel.SP_HEIGHT/2 - height/2;
		this.speedLimit = sp.speedLimit;
		this.taxiRankEnabled = sp.taxiRank;
		this.leftMostLaneType = sp.leftMostLaneType;
		this.leftMostLaneTypeOpposite = sp.leftMostLaneTypeOpposite;
		
		statsCollector = sc;
		
		northTce = new Road(Road.Type.NORTH, this, numLanesNorth * 2 - 1, speedLimit);
		fromeRd = new Road(Road.Type.FROME, this, numLanesFrome * 2 - 1, speedLimit);

		//---LOAD THE ROAD SIGN SPRITES---//
		try {
			signNorth = new Sprite("north_tce/roadSign.gif");
			signFrome = new Sprite("frome_rd/roadSign.gif");
		} catch (FileNotFoundException e) {
			System.err.println("ERROR: Unable to load road sign sprites");
		}
		
		//set coordinates for the road signs
		//TODO David : fix these variables
		NorthSignX = 100 - sp.numLanesFromeExtra*(int)VehicleLane.LANE_WIDTH;
		NorthSignY = 98 - sp.numLanesNorthExtra*(int)VehicleLane.LANE_WIDTH;
		FromeSignX = 530 + sp.numLanesFromeExtra*(int)VehicleLane.LANE_WIDTH;;
		FromeSignY = 20 - sp.numLanesNorthExtra*(int)VehicleLane.LANE_WIDTH;
		
		
		createIntersection();

	}
	
	/**
	 * @return List<Road> an ArrayList of the Roads
	 */
	public Road getNorthTce() 
	{
		return northTce;
	}
	
	public Road getFromeRd()
	{
		return fromeRd;
	}
	
	/**
	 * @param g
	 */
	public void draw(Graphics2D g)
	{
		//---Middle of intersection---//
		Stroke drawingStroke = new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		
		g.setStroke(drawingStroke);
		Rectangle2D middle = new Rectangle2D.Double(x, y, width, height);	
		g.setColor(Color.gray);
		g.fill(middle);
		g.setColor(Color.white);
		//g.draw(middle);
		
		//Draw each of the roads
		northTce.draw(g);
		fromeRd.draw(g);
		
		//Left Arc
		double arcW = ((fromeRd.getNumLanes()/2 - fromeRd.getNumLanes()/4)*VehicleLane.LANE_WIDTH + PedestrianLane.LANE_WIDTH)*2;
		double arcH = ((northTce.getNumLanes()/4)*VehicleLane.LANE_WIDTH + PedestrianLane.LANE_WIDTH)*2;
		double arcX = northTce.getX() + northTce.getLength() - arcW/2;
		double arcY = northTce.getY() + height + PedestrianLane.LANE_WIDTH - arcH/2;
		Arc2D leftArc = new Arc2D.Double(arcX, arcY, arcW, arcH, 0, 90, 0);
		
		g.setColor(Color.white);
		g.draw(leftArc);
		
		//Right Arc
		arcW = ((fromeRd.getNumLanes()/2 - fromeRd.getNumLanes()/4)*VehicleLane.LANE_WIDTH + PedestrianLane.LANE_WIDTH)*2;
		arcH = ((northTce.getNumLanes()/4)*VehicleLane.LANE_WIDTH + PedestrianLane.LANE_WIDTH)*2;
		arcX = northTce.getX() + northTce.getLength() + width + PedestrianLane.LANE_WIDTH*2 - arcW/2;
		arcY = northTce.getY() - PedestrianLane.LANE_WIDTH - arcH/2;
		Arc2D rightArc = new Arc2D.Double(arcX, arcY, arcW, arcH, 180, 90, 0);
			
		g.setColor(Color.white);
		g.draw(rightArc);
		
		//Bottom Arc
		arcW = ((fromeRd.getNumLanes()/4)*VehicleLane.LANE_WIDTH + PedestrianLane.LANE_WIDTH)*2;
		arcH = ((northTce.getNumLanes()/2 - northTce.getNumLanes()/4)*VehicleLane.LANE_WIDTH + PedestrianLane.LANE_WIDTH)*2;
		arcX = fromeRd.getX() + PedestrianLane.LANE_WIDTH - arcW/2;
		arcY = fromeRd.getY() + fromeRd.getLength() + height + PedestrianLane.LANE_WIDTH*2 - arcH/2;
		Arc2D bottomArc = new Arc2D.Double(arcX, arcY, arcW, arcH, 90, 90, 0);
			
		g.setColor(Color.white);
		g.draw(bottomArc);
		
		//Top Arc
		arcW = ((fromeRd.getNumLanes()/4)*VehicleLane.LANE_WIDTH + PedestrianLane.LANE_WIDTH)*2;
		arcH = ((northTce.getNumLanes()/2 - northTce.getNumLanes()/4)*VehicleLane.LANE_WIDTH + PedestrianLane.LANE_WIDTH)*2;
		arcX = fromeRd.getX() - width - PedestrianLane.LANE_WIDTH - arcW/2;
		arcY = fromeRd.getY() + fromeRd.getLength() - arcH/2;
		Arc2D topArc = new Arc2D.Double(arcX, arcY, arcW, arcH, 270, 90, 0);
			
		g.setColor(Color.white);
		g.draw(topArc);
		
		//Draw the curbs
		g.setColor(Color.LIGHT_GRAY);
		//Top left curb
		g.fillArc((int)(northTce.getX() + northTce.getLength() - PedestrianLane.LANE_WIDTH), (int)(northTce.getY() - PedestrianLane.LANE_WIDTH*2), (int)PedestrianLane.LANE_WIDTH*2, (int)PedestrianLane.LANE_WIDTH*2, 0, -90);
		//Top right curb
		g.fillArc((int)(northTce.getX() + northTce.getLength() + width + PedestrianLane.LANE_WIDTH), (int)(northTce.getY() - PedestrianLane.LANE_WIDTH*2), (int)PedestrianLane.LANE_WIDTH*2, (int)PedestrianLane.LANE_WIDTH*2, 180, 90);
		//Bot left curb
		g.fillArc((int)(northTce.getX() + northTce.getLength() - PedestrianLane.LANE_WIDTH), (int)(northTce.getY() + height), (int)PedestrianLane.LANE_WIDTH*2, (int)PedestrianLane.LANE_WIDTH*2, 0, 90);
		//Bot right curb
		g.fillArc((int)(northTce.getX() + northTce.getLength() + width + PedestrianLane.LANE_WIDTH), (int)(northTce.getY() + height), (int)PedestrianLane.LANE_WIDTH*2, (int)PedestrianLane.LANE_WIDTH*2, 90, 90);
		
		g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
		g.setColor(Color.white);
		//Top left curb
		g.drawArc((int)(northTce.getX() + northTce.getLength() - PedestrianLane.LANE_WIDTH), (int)(northTce.getY() - PedestrianLane.LANE_WIDTH*2), (int)PedestrianLane.LANE_WIDTH*2, (int)PedestrianLane.LANE_WIDTH*2, 0, -90);
		//Top right curb
		g.drawArc((int)(northTce.getX() + northTce.getLength() + width + PedestrianLane.LANE_WIDTH), (int)(northTce.getY() - PedestrianLane.LANE_WIDTH*2), (int)PedestrianLane.LANE_WIDTH*2, (int)PedestrianLane.LANE_WIDTH*2, 180, 90);
		//Bot left curb
		g.drawArc((int)(northTce.getX() + northTce.getLength() - PedestrianLane.LANE_WIDTH), (int)(northTce.getY() + height), (int)PedestrianLane.LANE_WIDTH*2, (int)PedestrianLane.LANE_WIDTH*2, 0, 90);
		//Bot right curb
		g.drawArc((int)(northTce.getX() + northTce.getLength() + width + PedestrianLane.LANE_WIDTH), (int)(northTce.getY() + height), (int)PedestrianLane.LANE_WIDTH*2, (int)PedestrianLane.LANE_WIDTH*2, 90, 90);
				
		//Draw a grid across the panel
		if (Utils.drawGrid) {
			g.setStroke(new java.awt.BasicStroke(0));
			
			//draw a 2D Grid 30px apart
			for(double i = minX-11; i < maxX; i+= 30){
				g.setColor(Color.red);
				g.draw(new Line2D.Double(i,minY,i,maxY));
				g.setColor(Color.BLACK);
				g.drawString(String.format("%.0f",i),(int)i+1,12);			
			}
			
			for(double i = minY-20; i < maxY; i+= 30){
				g.setColor(Color.red);
				g.draw(new Line2D.Double(minX,i,maxX,i));
				g.setColor(Color.BLACK);
				g.drawString(String.format("%.0f",i),12,(int)i-1);			
			}
		}
		
		//Draw boundary
		
		Line2D left = new Line2D.Double(minX, minY, minX, maxY);
		Line2D right = new Line2D.Double(maxX, minY, maxX, maxY);
		Line2D top = new Line2D.Double(minX, minY, maxX, minY);
		Line2D bottom = new Line2D.Double(minX, maxY, maxX, maxY);
		
		g.setColor(Color.red);
		g.draw(left);
		g.draw(right);
		g.draw(top);
		g.draw(bottom);
		
		
		//Draw all the cars
		
		ArrayList<VehicleLane> vLanes = northTce.getVehicleLanes();
		for (VehicleLane v : vLanes) {
			v.drawVehicles(g);
		}
		vLanes = fromeRd.getVehicleLanes();
		for (VehicleLane v : vLanes) {
			v.drawVehicles(g);
		}
			
		//Draw all the pedestrians
		ArrayList<PedestrianLane> pLanes = northTce.getPedestrianLanes();
			for (PedestrianLane p : pLanes) {
				p.drawPedestrians(g);
			}
		pLanes = fromeRd.getPedestrianLanes();
		for (PedestrianLane p : pLanes) {
			p.drawPedestrians(g);
		}
		
		//Draw the two road signs
		signNorth.draw(g, NorthSignX, NorthSignY);
		signFrome.draw(g, FromeSignX, FromeSignY);
		
		//Draw the Taxi Rank
		if (taxiRankEnabled) {	
			taxiRank.draw(g);
		}
	}
	
	public void tick()
	{
		northTce.tick();
		fromeRd.tick();
		if (taxiRankEnabled) {	
			taxiRank.tick();
		}
	}

	/**
	 * This method sets the bounds of the intersection in which entities are destroyed if they exceed
	 * @return the width
	 */
	public void setBounds() {
		minX = x - northTce.getLength() - PedestrianLane.LANE_WIDTH;
		maxX = x + width + northTce.getLength() + PedestrianLane.LANE_WIDTH;
		minY = y - fromeRd.getLength() - PedestrianLane.LANE_WIDTH;
		maxY = y + height + fromeRd.getLength() + PedestrianLane.LANE_WIDTH;
	}
	
	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	
	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	
	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * @return the minX
	 */
	public double getMinX() {
		return minX;
	}

	/**
	 * @return the maxX
	 */
	public double getMaxX() {
		return maxX;
	}

	/**
	 * @return the minY
	 */
	public double getMinY() {
		return minY;
	}

	/**
	 * @return the maxY
	 */
	public double getMaxY() {
		return maxY;
	}
	
	private void createIntersection()
	{
		trafficLightController.addPedestrianLanes(northTce);
		trafficLightController.addPedestrianLanes(fromeRd);
		
		
		//-------EXIT LANE MAP-------//
		// Map from index to which road it corresponds to
		// 0 -> North tce east
		// 1 -> North tce west
		// 2 -> Frome Road South
		// 3 -> Frome Road North

		//--------------NOTE------------//
		//The reason for the map the way it is, is due to the order the exit lanes are created
		//North Tce west lane is created first, and hence an exit lane is created on it's opposite side
		
		
		//We also need to keep a record of the entry lanes that will need to map add the exit lanes
		//------ENTRY LANE MAP-----//
		//-------LEFT Turns-------//
		// 0 -> Frome Road North
		// 1 -> From Road South
		// 2 -> North Tce East
		// 3 -> North Tce West
		
		//--------RIGHT TURNS------//
		// 0 -> From Road South
		// 1 -> From Road North
		// 2 -> North Tce West
		// 3 -> North Tce East

		
		//Add lanes to intersection
		VehicleLane entryLane;
		VehicleLane exitLane;
		double currentWidth = 0;
		
		//create four left most lanes, these will be used for the exit lanes of left turns
		VehicleLane[] leftExit = new VehicleLane[4];
		//likewise creating for rightmost lanes used for right turns
		VehicleLane[] rightExit = new VehicleLane[4];
		
		//create the four lanes that will be turning right and left as well
		VehicleLane[] rightEntry = new VehicleLane[4];
		VehicleLane[] leftEntry = new VehicleLane[4];
		
		//-------EXIT LANE MAP-------//
		// Map from index to which road it corresponds to
		// 0 -> North tce east
		// 1 -> North tce west
		// 2 -> Frome Road South
		// 3 -> Frome Road North

		//--------------NOTE------------//
		//The reason for the map the way it is, is due to the order the exit lanes are created
		//North Tce west lane is created first, and hence an exit lane is created on it's opposite side
		
		
		//We also need to keep a record of the entry lanes that will need to map add the exit lanes
		//------ENTRY LANE MAP-----//
		//-------LEFT Turns-------//
		// 0 -> Frome Road North
		// 1 -> From Road South
		// 2 -> North Tce East
		// 3 -> North Tce West
		
		//--------RIGHT TURNS------//
		// 0 -> From Road South
		// 1 -> From Road North
		// 2 -> North Tce West
		// 3 -> North Tce East
		
		int MAP_NTE = 0;
		int MAP_NTW = 1;
		int MAP_FRS = 2;
		int MAP_FRN = 3;
		
		int LEFT_FRS = 0;
		int LEFT_FRN = 1;
		int LEFT_NTW = 2;
		int LEFT_NTE = 3;
		
		int RIGHT_FRN = 0;
		int RIGHT_FRS = 1;
		int RIGHT_NTE = 2;
		int RIGHT_NTW = 3;
		
		//--------RIGHT BOX MAP----------//
		// 
		
		int tlID = 0; // the current trafficLight id that we are seeting coords for
		
		//Boxes that we need to add lanes to
		Box[] boxes = new Box[4];
		for( int i = 0 ; i < boxes.length ; i++ ){
			boxes[i] = new Box();
		}
		
		
		//-------------------NORTH TERRACE WEST LEFT LANE----------------//
		//Add LEFT_STRAIGHT lane
		entryLane = new VehicleLane(leftMostLaneType, northTce, northTce.getX(), northTce.getY() + currentWidth, 0, statsCollector);
		exitLane = new VehicleLane(leftMostLaneTypeOpposite, northTce, northTce.getX() + northTce.getLength()*2 + width + PedestrianLane.LANE_WIDTH*2, northTce.getY() + currentWidth + VehicleLane.LANE_WIDTH, 180, statsCollector);
		northTce.addLane(entryLane);
		trafficLightController.addLane(entryLane);
		northTce.addLane(exitLane);
		entryLane.addExitLane(exitLane);
		currentWidth += VehicleLane.LANE_WIDTH;

		//store the left most exit lanes
		leftExit[MAP_NTE] = exitLane;
		//store the left turning lane as well
		leftEntry[LEFT_NTW] = entryLane;
		
		//set the x and y coordinates of the traffic light that will correpond to the left lanes
		setTrafficLightCoords(tlID++,entryLane);
		
		//---------------NORTH TERRACE WEST STRAIGHT LANES-----------//
		//Loop to create the straight lanes
		int numLanes = (numLanesNorth- 2);
		for (int i = 0; i < numLanes; i++) {
			entryLane = new VehicleLane(VehicleLane.Type.STRAIGHT, northTce, northTce.getX(), northTce.getY() + currentWidth, 0, statsCollector);
			exitLane = new VehicleLane(VehicleLane.Type.STRAIGHT_, northTce, northTce.getX() + northTce.getLength()*2 + width + PedestrianLane.LANE_WIDTH*2, northTce.getY() + currentWidth + VehicleLane.LANE_WIDTH, 180, statsCollector);
			northTce.addLane(entryLane);
			trafficLightController.addLane(entryLane);
			northTce.addLane(exitLane);
			entryLane.addExitLane(exitLane);
			currentWidth += VehicleLane.LANE_WIDTH;

			//rightmost exit lane is the last straight lane
			if ( i == (numLanes - 1)){
				rightExit[MAP_NTE] = exitLane;
			}
			
			//we want to set the traffic light coordinates in the middle of the straight lanes.
			if( i == numLanes/2){
				setTrafficLightCoords(1,entryLane,(numLanes % 2 == 0));
			}
			
			//add the straight lanes to the box

		}
		
		//-----------NORTH TERRACE RIGHT TURN LANES EAST AND WEST---------//
		//Add Right lanes
		entryLane = new VehicleLane(VehicleLane.Type.RIGHT, northTce, northTce.getX(), northTce.getY() + currentWidth, 0, statsCollector);
		exitLane = new VehicleLane(VehicleLane.Type.RIGHT, northTce, northTce.getX() + northTce.getLength()*2 + width + PedestrianLane.LANE_WIDTH*2, northTce.getY() + currentWidth + VehicleLane.LANE_WIDTH, 180, statsCollector);
		northTce.addLane(entryLane);
		trafficLightController.addLane(entryLane);
		northTce.addLane(exitLane);
		trafficLightController.addLane(exitLane);
		currentWidth += VehicleLane.LANE_WIDTH;

		setTrafficLightCoords(2,entryLane);
		setTrafficLightCoords(5,exitLane);
		//remember the right turning lane for later
		rightEntry[RIGHT_NTW] = entryLane;
		rightEntry[RIGHT_NTE] = exitLane;
		
		//--------------NORTH TERRACE EASTERN STRAIGHT LANES----------//
		//Loop to create the straight_ lanes
		for (int i = 0; i < numLanes; i++) {
			entryLane = new VehicleLane(VehicleLane.Type.STRAIGHT_, northTce, northTce.getX(), northTce.getY() + currentWidth, 0, statsCollector);
			exitLane = new VehicleLane(VehicleLane.Type.STRAIGHT, northTce, northTce.getX() + northTce.getLength()*2 + width + PedestrianLane.LANE_WIDTH*2, northTce.getY() + currentWidth  + VehicleLane.LANE_WIDTH, 180, statsCollector);
			northTce.addLane(entryLane);
			northTce.addLane(exitLane);
			exitLane.addExitLane(entryLane);
			trafficLightController.addLane(exitLane);
			currentWidth += VehicleLane.LANE_WIDTH;
			
			//rightmost exit lane is the first straight lane when on the opposite side of the road
			if ( i == 0){
				rightExit[MAP_NTW] = entryLane;
			}
			
			if( i == numLanes/2 ) {
				setTrafficLightCoords(4,exitLane, (numLanes % 2 == 0));
			}

		}
		
		//-----------NORTH TERRACE EASTERN LEFT LANE---------//////
		//Add LEFT_STRAIGHT_ lane
		entryLane = new VehicleLane(leftMostLaneTypeOpposite, northTce, northTce.getX(), northTce.getY() + currentWidth, 0, statsCollector);
		exitLane = new VehicleLane(leftMostLaneType, northTce, northTce.getX() + northTce.getLength()*2 + width + PedestrianLane.LANE_WIDTH*2, northTce.getY() + currentWidth + VehicleLane.LANE_WIDTH, 180, statsCollector);
		northTce.addLane(entryLane);
		northTce.addLane(exitLane);
		exitLane.addExitLane(entryLane);
		trafficLightController.addLane(exitLane);
		currentWidth += VehicleLane.LANE_WIDTH;
		//this will be the leftmost lane		//Add the lanes to North Terrace:
		leftExit[MAP_NTW] = entryLane;
		//add the leftmost entry lane as well
		leftEntry[LEFT_NTE] = exitLane;
		
		setTrafficLightCoords(3,exitLane);

		
		//Add the lanes to Frome Road:
		currentWidth = 0;
		
		//---------------FROME ROAD NORTH LEFT LANE--------------//
		//Add LEFT_STRAIGHT lane		trafficLightController.setTrafficLightCoords(0, (int)(entryLane.getExitX() + PedestrianLane.LANE_WIDTH),(int)entryLane.getExitY());

		entryLane = new VehicleLane(leftMostLaneType, fromeRd, fromeRd.getX() - currentWidth, fromeRd.getY(), 90, statsCollector);
		exitLane = new VehicleLane(leftMostLaneTypeOpposite, fromeRd, fromeRd.getX() - currentWidth - VehicleLane.LANE_WIDTH, fromeRd.getY() + fromeRd.getLength()*2 + height + PedestrianLane.LANE_WIDTH*2, 270, statsCollector);
		fromeRd.addLane(entryLane);//			System.out.println("Left entry exit pos: " + leftEntry[i].getExitX() + "," + leftEntry[i].getExitY() + "Left Lane dir " + leftEntry[i].direction() +  " Left exit entry post: " + leftLanes[i].getEntryX() + "," + leftLanes[i].getExitY() + " Exit DIRECTION" + leftLanes[i].direction()); 
		trafficLightController.addLane(entryLane);
		fromeRd.addLane(exitLane);
		entryLane.addExitLane(exitLane);
		currentWidth += VehicleLane.LANE_WIDTH;

		setTrafficLightCoords(6,entryLane);
		//this is the leftmost lane of FROME ROaAD		trafficLightController.setTrafficLightCoords(0, (int)(entryLane.getExitX() + PedestrianLane.LANE_WIDTH),(int)entryLane.getExitY());

		leftExit[MAP_FRN] = exitLane;
		leftEntry[LEFT_FRS] = entryLane;
		
		//-------------FROME ROAD NORTH STRAIGHT LANES---------------//
		//Loop to create the straight lanes
		numLanes = (numLanesFrome - 2);
		for (int i = 0; i < numLanes; i++) {
			entryLane = new VehicleLane(VehicleLane.Type.STRAIGHT, fromeRd, fromeRd.getX() - currentWidth, fromeRd.getY(), 90, statsCollector);
			exitLane = new VehicleLane(VehicleLane.Type.STRAIGHT_, fromeRd, fromeRd.getX() - currentWidth - VehicleLane.LANE_WIDTH, fromeRd.getY() + fromeRd.getLength()*2 + height + PedestrianLane.LANE_WIDTH*2, 270, statsCollector);
			fromeRd.addLane(entryLane);
			trafficLightController.addLane(entryLane);
			fromeRd.addLane(exitLane);
			entryLane.addExitLane(exitLane);
			currentWidth += VehicleLane.LANE_WIDTH;
			
			//the last index of the array
			if ( i == numLanes-1){
				rightExit[MAP_FRN] = exitLane;
			}
			if ( i == numLanes/2 ){
				setTrafficLightCoords(7,entryLane,(numLanes % 2 == 0));
			}
		}
		
		//-----------------FROME ROAD RIGHT LANES NORTH AND SOUTH-----------//
		//Add Right lanes
		entryLane = new VehicleLane(VehicleLane.Type.RIGHT, fromeRd, fromeRd.getX() - currentWidth, fromeRd.getY(), 90, statsCollector);
		exitLane = new VehicleLane(VehicleLane.Type.RIGHT, fromeRd, fromeRd.getX() - currentWidth - VehicleLane.LANE_WIDTH, fromeRd.getY() + fromeRd.getLength()*2 + height + PedestrianLane.LANE_WIDTH*2, 270, statsCollector);
		fromeRd.addLane(entryLane);
		trafficLightController.addLane(entryLane);
		fromeRd.addLane(exitLane);
		trafficLightController.addLane(exitLane);
		currentWidth += VehicleLane.LANE_WIDTH;
		
		setTrafficLightCoords(11,exitLane);
		setTrafficLightCoords(8,entryLane);

		
		//add right lanes to the entry lane
		rightEntry[RIGHT_FRS] = entryLane;
		rightEntry[RIGHT_FRN] = exitLane;
		
		//---------------FROME ROAD SOUTH STRAIGHT LANES-----------------//
		//Loop to create the straight_ lanes
		for (int i = 0; i < numLanes; i++) {
			entryLane = new VehicleLane(VehicleLane.Type.STRAIGHT_, fromeRd, fromeRd.getX() - currentWidth, fromeRd.getY(), 90, statsCollector);
			exitLane = new VehicleLane(VehicleLane.Type.STRAIGHT, fromeRd, fromeRd.getX() - currentWidth - VehicleLane.LANE_WIDTH, fromeRd.getY() + fromeRd.getLength()*2 + height + PedestrianLane.LANE_WIDTH*2, 270, statsCollector);
			fromeRd.addLane(entryLane);
			fromeRd.addLane(exitLane);
			exitLane.addExitLane(entryLane);
			trafficLightController.addLane(exitLane);
			currentWidth += VehicleLane.LANE_WIDTH;
			
			//add the first straight road to right lanes exit lane, as the lanes are being created from middle in here
			if ( i == 0 ){
				rightExit[MAP_FRS] = entryLane;
			}
			
			if ( i == numLanes/2 ){
				setTrafficLightCoords(10,exitLane,(numLanes % 2 == 0));
			}
		}
		
		//--------------FROME RAOD SOUTH LEFT LANE----------------//
		//Add LEFT_STRAIGHT_ lanea
		entryLane = new VehicleLane(leftMostLaneTypeOpposite, fromeRd, fromeRd.getX() - currentWidth, fromeRd.getY(), 90, statsCollector);
		exitLane = new VehicleLane(leftMostLaneType, fromeRd, fromeRd.getX() - currentWidth - VehicleLane.LANE_WIDTH, fromeRd.getY() + fromeRd.getLength()*2 + height + PedestrianLane.LANE_WIDTH*2, 270, statsCollector);
		fromeRd.addLane(entryLane);
		fromeRd.addLane(exitLane);
		exitLane.addExitLane(entryLane);
		trafficLightController.addLane(exitLane);
		currentWidth += VehicleLane.LANE_WIDTH;
		
		//add FROM ROAD NORTH exit lane
		leftExit[MAP_FRS] = entryLane;
		//store left entry lane 
		leftEntry[LEFT_FRN] = exitLane;
		setTrafficLightCoords(9,exitLane);

		
		//add all the exit lanes to the entry lanes -> this will be used to determine how a car exits a lane, ie allowing a car to turn
		for( int i = 0 ; i < leftExit.length ; i++ ) {
			leftEntry[i].addExitLane(leftExit[i]);
			rightEntry[i].addExitLane(rightExit[i]);
		}
		
		ArrayList<VehicleLane> vLanes = northTce.getVehicleLanes();
		for( int i = 0 ; i < vLanes.size() ; i ++ ) {
			switch (vLanes.get(i).getType()){
			case LEFT_STRAIGHT:
			case STRAIGHT :
				int dir = (int)vLanes.get(i).dirDeg();
				if ( dir == 0 ) {
					vLanes.get(i).setBox(boxes[1]);
				} else if ( dir == 180 ) {
					vLanes.get(i).setBox(boxes[0]);					
				}
			}
		}
		
		vLanes = fromeRd.getVehicleLanes();
		for( int i = 0 ; i < vLanes.size() ; i ++ ) {
			switch (vLanes.get(i).getType()){
			case LEFT_STRAIGHT:
			case STRAIGHT :
				int dir = (int)vLanes.get(i).dirDeg();
				if ( dir == 90 ) {
					vLanes.get(i).setBox(boxes[3]);
				} else if ( dir == 270 ) {
					vLanes.get(i).setBox(boxes[2]);					
				}
			}
		}

		//add the boxes to the lanes
		addBoxNorth(rightEntry[RIGHT_NTW],boxes[0],2);
		addBoxNorth(rightEntry[RIGHT_NTE], boxes[1],5 );
		addBoxSouth(rightEntry[RIGHT_FRS],boxes[2],8);
		addBoxSouth(rightEntry[RIGHT_FRN],boxes[3],11);
		
		//lets set the coordinates of the pedestrian lights
		ArrayList<PedestrianLane> lanes = northTce.getPedestrianLanes();
			for( int i = 0 ; i < lanes.size() ; i += 2 ){
				//A pedestiran light has two coordinates, 
				//therefore we need to pass ped lanes to the set coords method
				PedestrianLane[] pedLanes = {lanes.get(i), lanes.get(i+1)};
				setPedestrianLightCoords(i/2,pedLanes);
			}
			lanes = fromeRd.getPedestrianLanes();
			for( int i = 0 ; i < lanes.size() ; i += 2 ){
				//A pedestiran light has two coordinates, 
				//therefore we need to pass ped lanes to the set coords method
				PedestrianLane[] pedLanes = {lanes.get(i), lanes.get(i+1)};
				setPedestrianLightCoords(i/2 + 2,pedLanes); //need to add 2 to offest for being on the opposite side of the road
			}
			
			//lets get all the pedestrian lanes and then set boxes to them.
			//there are four kinds of boxes that we want to add.
			//these will correspond to where the exit lanes are
			Box[] pedBoxes = new Box[4];
			for( int i = 0 ; i < pedBoxes.length ; i++){
				pedBoxes[i] = new Box();
			}
			//north terrace east. the box coordinates will be the x and y of the pedestrian lane of frome road
			lanes = fromeRd.getPedestrianLanes();
			PedestrianLane topLane = lanes.get(0);
			int y1 = (int) (topLane.y() + Road.ROAD_LENGTH + PedestrianLane.LANE_WIDTH - Pedestrian.PEDESTRIAN_HEIGHT);
			int y2 = (int) ( y1 + ( numLanesNorth + 1 ) * VehicleLane.LANE_WIDTH * Math.sin(topLane.dirRad()) );
			int x1 = Integer.MIN_VALUE;
			int x2 = Integer.MAX_VALUE;
			pedBoxes[0].setCoords(x1, x2, y1, y2);
			
			PedestrianLane bottomLane = lanes.get(1);
			y1 = (int) (bottomLane.y() - Road.ROAD_LENGTH - PedestrianLane.LANE_WIDTH + Pedestrian.PEDESTRIAN_HEIGHT);
			y2 = (int) ( y1 + (numLanesNorth + 1) * VehicleLane.LANE_WIDTH * Math.sin(bottomLane.dirRad()) );
			pedBoxes[1].setCoords(x1, x2, y1, y2);

			//need to set all the boxes for each lane, so that when an entity is created in the lane it will pass it its' box
			for( int i = 0 ; i < lanes.size(); i ++ ){
				lanes.get(i).setBox(pedBoxes[i / 2 ]);
			}
			
			lanes = northTce.getPedestrianLanes();
			topLane = lanes.get(0);
			x1 = (int) (topLane.x() + Road.ROAD_LENGTH + PedestrianLane.LANE_WIDTH- Pedestrian.PEDESTRIAN_WIDTH);
			x2 = (int) ( x1 +  ( numLanesFrome + 1 ) * VehicleLane.LANE_WIDTH * Math.cos(topLane.dirRad()) );
			y1 = Integer.MIN_VALUE;
			y2 = Integer.MAX_VALUE;
			pedBoxes[2].setCoords(x1, x2, y1, y2);

			bottomLane = lanes.get(1);
			x1 = (int) (bottomLane.x() - Road.ROAD_LENGTH  - PedestrianLane.LANE_WIDTH);
			x2 = (int) ( x1 + (numLanesFrome + 1) * VehicleLane.LANE_WIDTH * Math.cos(bottomLane.dirRad()) );
			pedBoxes[3].setCoords(x1, x2, y1, y2);

			for( int i = 0 ; i < lanes.size(); i ++ ){
				lanes.get(i).setBox(pedBoxes[i/2 + 2]);
			}
			
			
//			//lets add these boxes to the left hand turns
//			for( int i = 0 ; i < pedBoxes.length ; i++){
//				trafficLightController.addBox(i * 3,pedBoxes[i]);
//			}

			trafficLightController.addBox(0 ,pedBoxes[2]);
			trafficLightController.addBox(3 ,pedBoxes[3]);
			trafficLightController.addBox(6 ,pedBoxes[0]);
			trafficLightController.addBox(9 ,pedBoxes[1]);
			
			//add the right hand turn boxes
			trafficLightController.addBox(2 ,pedBoxes[3]);
			trafficLightController.addBox(5 ,pedBoxes[2]);
			trafficLightController.addBox(8 ,pedBoxes[1]);
			trafficLightController.addBox(11 ,pedBoxes[0]);
			
		//Set adjacent lanes of each road, ie set the lanes left and right of each lane
		northTce.setAdjacentLanes();
		fromeRd.setAdjacentLanes();
		
		
		//---Add Taxi Rank---//
		double rankX = x + width + PedestrianLane.LANE_WIDTH + northTce.getLength()/2;
		double rankY = y - PedestrianLane.LANE_WIDTH - TaxiRank.HEIGHT;
		VehicleLane rankLane = northTce.getVehicleLanes().get(1);
		if (taxiRankEnabled) {	
			taxiRank = new TaxiRank(rankX, rankY, rankLane);
		}
			
		//Set the bounds of the intersection
		setBounds();		
	}
	
	private void setTrafficLightCoords(int tl,VehicleLane lane)
	{
		int x = (int)(lane.getExitX() + ((PedestrianLane.LANE_WIDTH + VehicleLane.LANE_WIDTH/2) * Math.cos(lane.dirRad())));
		int y = (int)(lane.getExitY() + ((PedestrianLane.LANE_WIDTH + VehicleLane.LANE_WIDTH/2) * Math.sin(lane.dirRad())));

		trafficLightController.setTrafficLightCoords(tl, x,y);					
	}

	private void setPedestrianLightCoords(int tl,PedestrianLane[] lanes)
	{
		int x[] = new int[2];
		int y[] = new int[2];
		
		for( int i = 0 ; i < x.length ; i++ ) {
			x[i] = (int) ( lanes[i].x() + ( (PedestrianLane.LANE_WIDTH + Road.ROAD_LENGTH) * Math.cos(lanes[i].dirRad())) + ( ( PedestrianLane.LANE_WIDTH /2 )  * Math.sin(-lanes[i].dirRad()))) ;;
			y[i] = (int) ( lanes[i].y() + ( (PedestrianLane.LANE_WIDTH + Road.ROAD_LENGTH) * Math.sin(lanes[i].dirRad())) + ( ( PedestrianLane.LANE_WIDTH /2 ) * Math.cos(lanes[i].dirRad()))) ;
		}
		trafficLightController.setPedestrianLightCoords(tl, x,y);					
	}

	private void addBoxNorth(VehicleLane lane,Box box, int tid){
		int x1 =  (int) (lane.getExitX() + ( PedestrianLane.LANE_WIDTH + width/2) * Math.cos(lane.dirRad()));
		int x2 = (int) (x1 + convertSpeed(speedLimit) * TURNING_TIME * Math.cos(lane.dirRad()));
		int y1 = Integer.MIN_VALUE;
		int y2 = Integer.MAX_VALUE;
		box.setCoords(x1,x2,y1,y2);
		trafficLightController.addBox(tid, box);
	}
	
	private void addBoxSouth(VehicleLane lane,Box box, int tid){
		int x1 = Integer.MIN_VALUE;
		int x2 = Integer.MAX_VALUE;
		int y1 =  (int) (lane.getExitY() + ( PedestrianLane.LANE_WIDTH + height/2) * Math.sin(lane.dirRad()));
		int y2 = (int) (y1 + convertSpeed(speedLimit) * TURNING_TIME * Math.sin(lane.dirRad()));
		box.setCoords(x1,x2,y1,y2);
		trafficLightController.addBox(tid, box);
	}

	private void setTrafficLightCoords(int tl,VehicleLane lane, boolean middle)
	{
		int x = (int)(lane.getExitX() + ((PedestrianLane.LANE_WIDTH + VehicleLane.LANE_WIDTH/2) * Math.cos(lane.dirRad())));
		int y = (int)(lane.getExitY() + ((PedestrianLane.LANE_WIDTH + VehicleLane.LANE_WIDTH/2) * Math.sin(lane.dirRad())));

		if( middle){
			if ( Math.round(lane.dirDeg()) == 0 || Math.round(lane.dirDeg()) == 180 ) {
				y -= VehicleLane.LANE_WIDTH/2;
			} else {
				x += VehicleLane.LANE_WIDTH/2;
			}
		} 	
		trafficLightController.setTrafficLightCoords(tl, x,y);					
	}
	
}
