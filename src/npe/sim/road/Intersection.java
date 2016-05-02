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

	private int fromeExitLaneCount;

	private int northExitLaneCount;

	private int fromeTotalLaneCount;

	private int northTotalLaneCount;
	

	/**Constructor just initialises a new Arraylist of Roads*/
	public Intersection(SimProperties sp, StatsCollector sc)
	{	
		this.trafficLightController = sp.trafficLightController;
		this.numLanesFrome = sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft
				+ sp.numLanesFromeStraight + sp.numLanesFromeStraightRight
				+ sp.numLanesFromeRight;
		
		this.numLanesNorth = sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft
				+ sp.numLanesNorthStraight + sp.numLanesNorthStraightRight
				+ sp.numLanesNorthRight;
		
		int fromeExitLaneCount = Math.max(Math.max(sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft,
				sp.numLanesNorthRight + sp.numLanesNorthStraightRight),
				sp.numLanesFromeStraight + sp.numLanesFromeStraightRight + sp.numLanesFromeStraightLeft);
		
		int northExitLaneCount = Math.max(Math.max(sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft,
				sp.numLanesFromeRight + sp.numLanesFromeStraightRight),
				sp.numLanesNorthStraight + sp.numLanesNorthStraightRight + sp.numLanesNorthStraightLeft);
		
		int fromeTotalLaneCount = numLanesNorth + northExitLaneCount;
		int northTotalLaneCount = numLanesFrome + fromeExitLaneCount;
		
		
		width = fromeTotalLaneCount * VehicleLane.LANE_WIDTH;
		height = northTotalLaneCount * VehicleLane.LANE_WIDTH;
		x = SimPanel.SP_WIDTH/2 - width/2;
		y = SimPanel.SP_HEIGHT/2 - height/2;
		this.speedLimit = sp.speedLimit;
		this.taxiRankEnabled = sp.taxiRank;
		this.leftMostLaneType = sp.leftMostLaneType;
		this.leftMostLaneTypeOpposite = sp.leftMostLaneTypeOpposite;
		
		statsCollector = sc;
		
		northTce = new Road(Road.Type.NORTH, this, northTotalLaneCount, speedLimit);
		fromeRd = new Road(Road.Type.FROME, this, fromeTotalLaneCount, speedLimit);

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
		
		
		createIntersection(sp);
		
		

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
	
	private enum Side
	{
		NORTH(0),
		SOUTH(1),
		EAST(2),
		WEST(3);
		
		int side;
		Side(int side) {
			this.side = side;
		}
	}
	
	/** Creates a lane to be added to the intersection.
	 * @param laneType LEFT, LEFT_, RIGHT, ETC
	 * @param laneNumber Starting with the left turn lane as 0, the index of the lane
	 * @param side The IntersectionSide of the intersection to put the lane. NORTH means the lane
	 * physically located at the north end.
	 * @return A lane with the correct type and position set for the lane number given
	 */
	public VehicleLane createLane(VehicleLane.Type laneType, int laneNumber, Side side, boolean reverseIndex) {
		VehicleLane lane = null;
		double currentOffset = laneNumber * VehicleLane.LANE_WIDTH;
		double reverseOffsetFrome = (fromeTotalLaneCount - laneNumber - 1) * VehicleLane.LANE_WIDTH;
		double reverseOffsetNorth = (fromeTotalLaneCount - laneNumber - 1) * VehicleLane.LANE_WIDTH;
		
		switch(side) {
		case NORTH:
			lane = new VehicleLane(laneType, fromeRd,
					fromeRd.getX() - (reverseIndex ? reverseOffsetFrome : currentOffset),
					fromeRd.getY(),
					90, statsCollector);
			break;
			
		case SOUTH:
			lane = new VehicleLane(laneType, fromeRd,
					fromeRd.getX() + (reverseIndex ? reverseOffsetFrome : currentOffset) - VehicleLane.LANE_WIDTH * fromeTotalLaneCount,
					fromeRd.getY() + fromeRd.getLength() * 2 + height + PedestrianLane.LANE_WIDTH * 2,
					270, statsCollector);
			break;
			
		case EAST:
			lane = new VehicleLane(laneType, northTce,
					northTce.getX() + 2 * northTce.getLength() + width + PedestrianLane.LANE_WIDTH * 2,
					northTce.getY() - (reverseIndex ? reverseOffsetFrome : currentOffset) + northTotalLaneCount * VehicleLane.LANE_WIDTH,
					180, statsCollector);
			break;
			
		case WEST:
			lane = new VehicleLane(laneType, northTce,
					northTce.getX(),
					northTce.getY() + (reverseIndex ? reverseOffsetFrome : currentOffset),
					0, statsCollector);
			break;
		}
		
		return lane;
	}
	
	private void createIntersection(SimProperties sp)
	{
		trafficLightController.addPedestrianLanes(northTce);
		trafficLightController.addPedestrianLanes(fromeRd);
		
		fromeExitLaneCount = Math.max(Math.max(sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft,
				sp.numLanesNorthRight + sp.numLanesNorthStraightRight),
				sp.numLanesFromeStraight + sp.numLanesFromeStraightRight + sp.numLanesFromeStraightLeft);
		
		northExitLaneCount = Math.max(Math.max(sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft,
				sp.numLanesFromeRight + sp.numLanesFromeStraightRight),
				sp.numLanesNorthStraight + sp.numLanesNorthStraightRight + sp.numLanesNorthStraightLeft);
		
		fromeTotalLaneCount = numLanesNorth + northExitLaneCount;
		northTotalLaneCount = numLanesFrome + fromeExitLaneCount;
		
		// TODO: FROME
		// LEFT TURN LANES ON FROME
		for (int i = 0; i < sp.numLanesFromeLeft; i++) {
			// TOP
			VehicleLane topLane = createLane(VehicleLane.Type.LEFT, i, Side.NORTH, false);
			VehicleLane topExitLane = createLane(VehicleLane.Type.LEFT_,
					i, Side.EAST, true);
			
			fromeRd.addLane(topLane); // Add entry to frome
			northTce.addLane(topExitLane); // Add exit to north tce
			topLane.addExitLane(topExitLane); // Add exit to entry lane
			
			trafficLightController.addLane(topLane); // Add entry lane to traffic lights
			setTrafficLightCoords(6, topLane); // Set traffic light #6 to be positioned near this lane
			
			// BOTTOM
			VehicleLane bottomLane = createLane(VehicleLane.Type.LEFT,
					i, Side.SOUTH, false);
			VehicleLane bottomExitLane = createLane(VehicleLane.Type.LEFT_,
					i, Side.WEST, true);
			
			fromeRd.addLane(bottomLane);
			northTce.addLane(bottomExitLane);
			bottomLane.addExitLane(bottomExitLane);
			
			trafficLightController.addLane(bottomLane);
			setTrafficLightCoords(9, bottomLane);
		}
		
		// LEFT TURN STRAIGHT LANES ON FROME
		for (int i = 0; i < sp.numLanesFromeStraightLeft; i++) {
			// TOP
			VehicleLane topLane = createLane(VehicleLane.Type.LEFT_STRAIGHT,
					i + sp.numLanesFromeLeft, Side.NORTH, false);
			VehicleLane topLaneExitLeft = createLane(VehicleLane.Type.LEFT_STRAIGHT_,
					i + sp.numLanesFromeLeft, Side.EAST, true);
			VehicleLane topLaneExitStraight = createLane(VehicleLane.Type.LEFT_STRAIGHT_,
					i + sp.numLanesFromeLeft, Side.SOUTH, true);
			
			fromeRd.addLane(topLane);
			northTce.addLane(topLaneExitLeft);
			fromeRd.addLane(topLaneExitStraight);
			
			topLane.addExitLane(topLaneExitStraight); // Straight has to be index 0 because cars look at exitLanes[0] for the straight exit
			topLane.addExitLane(topLaneExitLeft);
			
			trafficLightController.addLane(topLane);
			setTrafficLightCoords(6, topLane);
			
			// BOTTOM
			VehicleLane bottomLane = createLane(VehicleLane.Type.LEFT_STRAIGHT,
					i + sp.numLanesFromeLeft, Side.SOUTH, false);
			VehicleLane bottomLaneExitLeft = createLane(VehicleLane.Type.LEFT_STRAIGHT_,
					i + sp.numLanesFromeLeft, Side.WEST, true);
			VehicleLane bottomLaneExitStraight = createLane(VehicleLane.Type.LEFT_STRAIGHT_,
					i + sp.numLanesFromeLeft, Side.NORTH, true);
			
			fromeRd.addLane(bottomLane);
			northTce.addLane(bottomLaneExitLeft);
			fromeRd.addLane(bottomLaneExitStraight);
			
			bottomLane.addExitLane(bottomLaneExitStraight); // Straight has to be index 0 because cars look at exitLanes[0] for the straight exit
			bottomLane.addExitLane(bottomLaneExitLeft);
			
			trafficLightController.addLane(bottomLane);
			setTrafficLightCoords(9, bottomLane);
		}
		
		// STRAIGHT LANES ON FROME
		for (int i = 0; i < sp.numLanesFromeStraight; i++) {
			// TOP
			VehicleLane topLane = createLane(VehicleLane.Type.STRAIGHT,
					i + sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft, Side.NORTH, false);
			VehicleLane topLaneExitStraight = createLane(VehicleLane.Type.STRAIGHT_,
					i + sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft, Side.SOUTH, true);
			
			fromeRd.addLane(topLane);
			fromeRd.addLane(topLaneExitStraight);
			topLane.addExitLane(topLaneExitStraight);
			
			trafficLightController.addLane(topLane);
			setTrafficLightCoords(7, topLane);
			
			// BOTTOM
			VehicleLane bottomLane = createLane(VehicleLane.Type.STRAIGHT,
					i + sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft, Side.SOUTH, false);
			VehicleLane bottomLaneExitStraight = createLane(VehicleLane.Type.STRAIGHT_,
					i + sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft, Side.NORTH, true);
			
			fromeRd.addLane(bottomLane);
			fromeRd.addLane(bottomLaneExitStraight);
			bottomLane.addExitLane(bottomLaneExitStraight);
			
			trafficLightController.addLane(bottomLane);
			setTrafficLightCoords(10, bottomLane);
		}
		
		// STRAIGHT RIGHT LANES ON FROME
		for (int i = 0; i < sp.numLanesFromeStraightRight; i++) {
			// TOP
			VehicleLane topLane = createLane(VehicleLane.Type.RIGHT_STRAIGHT,
					i + sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft + sp.numLanesFromeStraight, Side.NORTH, false);
			VehicleLane topLaneExitRight = createLane(VehicleLane.Type.RIGHT_STRAIGHT_,
					i + sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft + sp.numLanesFromeStraight, Side.WEST, true);
			VehicleLane topLaneExitStraight = createLane(VehicleLane.Type.RIGHT_STRAIGHT_,
					i + sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft + sp.numLanesFromeStraight, Side.SOUTH, true);
			
			fromeRd.addLane(topLane);
			northTce.addLane(topLaneExitRight);
			fromeRd.addLane(topLaneExitStraight);
			
			topLane.addExitLane(topLaneExitStraight); // Straight has to be index 0 because cars look at exitLanes[0] for the straight exit
			topLane.addExitLane(topLaneExitRight);
			
			trafficLightController.addLane(topLane);
			setTrafficLightCoords(8, topLane);
			
			// BOTTOM
			VehicleLane bottomLane = createLane(VehicleLane.Type.RIGHT_STRAIGHT,
					i + sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft + sp.numLanesFromeStraight, Side.SOUTH, false);
			VehicleLane bottomLaneExitRight = createLane(VehicleLane.Type.RIGHT_STRAIGHT_,
					i + sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft + sp.numLanesFromeStraight, Side.EAST, true);
			VehicleLane bottomLaneExitStraight = createLane(VehicleLane.Type.RIGHT_STRAIGHT_,
					i + sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft + sp.numLanesFromeStraight, Side.NORTH, true);
			
			fromeRd.addLane(bottomLane);
			northTce.addLane(bottomLaneExitRight);
			fromeRd.addLane(bottomLaneExitStraight);
			
			bottomLane.addExitLane(bottomLaneExitStraight);
			bottomLane.addExitLane(bottomLaneExitRight);
			
			trafficLightController.addLane(bottomLane);
			setTrafficLightCoords(11, bottomLane);
		}
		
		// RIGHT LANES ON FROME
		for (int i = 0; i < sp.numLanesFromeRight; i++) {
			// TOP
			VehicleLane topLane = createLane(VehicleLane.Type.RIGHT,
					i + sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft + sp.numLanesFromeStraight + sp.numLanesFromeStraightRight,
					Side.NORTH, false);
			VehicleLane topLaneExitRight = createLane(VehicleLane.Type.RIGHT_,
					i + sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft + sp.numLanesFromeStraight + sp.numLanesFromeStraightRight,
					Side.WEST, true);
			
			fromeRd.addLane(topLane);
			northTce.addLane(topLaneExitRight);
			topLane.addExitLane(topLaneExitRight);
			
			trafficLightController.addLane(topLane);
			setTrafficLightCoords(8, topLane);
			
			// BOTTOM
			VehicleLane bottomLane = createLane(VehicleLane.Type.RIGHT,
					i + sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft + sp.numLanesFromeStraight + sp.numLanesFromeStraightRight,
					Side.SOUTH, false);
			VehicleLane bottomLaneExitRight = createLane(VehicleLane.Type.RIGHT_,
					i + sp.numLanesFromeLeft + sp.numLanesFromeStraightLeft + sp.numLanesFromeStraight + sp.numLanesFromeStraightRight,
					Side.EAST, true);
			
			fromeRd.addLane(bottomLane);
			northTce.addLane(bottomLaneExitRight);
			bottomLane.addExitLane(bottomLaneExitRight);
			
			trafficLightController.addLane(bottomLane);
			setTrafficLightCoords(11, bottomLane);
		}
		
		// TODO: NORTH TERRACE
		// LEFT TURN ON NORTH TERRACE
		for (int i = 0; i < sp.numLanesNorthLeft; i++) {
			// LEFT
			VehicleLane leftLane = createLane(VehicleLane.Type.LEFT, i, Side.WEST, false);
			VehicleLane leftExitLane = createLane(VehicleLane.Type.LEFT_,
					i, Side.NORTH, true);
			
			northTce.addLane(leftLane);
			fromeRd.addLane(leftExitLane);
			leftLane.addExitLane(leftExitLane); 
			
			trafficLightController.addLane(leftLane);
			setTrafficLightCoords(0, leftLane);
			
			// RIGHT
			VehicleLane rightLane = createLane(VehicleLane.Type.LEFT,
					i, Side.EAST, false);
			VehicleLane rightExitLane = createLane(VehicleLane.Type.LEFT_,
					i, Side.SOUTH, true);
			
			northTce.addLane(rightLane);
			fromeRd.addLane(rightExitLane);
			rightLane.addExitLane(rightExitLane);
			
			trafficLightController.addLane(rightLane);
			setTrafficLightCoords(3, rightLane);
		}
		
		// LEFT TURN STRAIGHT LANES ON NTCE
		for (int i = 0; i < sp.numLanesNorthStraightLeft; i++) {
			// LEFT
			VehicleLane leftLane = createLane(VehicleLane.Type.LEFT_STRAIGHT,
					i + sp.numLanesNorthLeft, Side.WEST, false);
			VehicleLane leftLaneExitLeft = createLane(VehicleLane.Type.LEFT_STRAIGHT_,
					i + sp.numLanesNorthLeft, Side.NORTH, true);
			VehicleLane leftLaneExitStraight = createLane(VehicleLane.Type.LEFT_STRAIGHT_,
					i + sp.numLanesNorthLeft, Side.EAST, true);
			
			northTce.addLane(leftLane);
			fromeRd.addLane(leftLaneExitLeft);
			northTce.addLane(leftLaneExitStraight);
			
			leftLane.addExitLane(leftLaneExitStraight); // Straight has to be index 0 because cars look at exitLanes[0] for the straight exit
			leftLane.addExitLane(leftLaneExitLeft);
			
			trafficLightController.addLane(leftLane);
			setTrafficLightCoords(1, leftLane);
			
			// RIGHT
			VehicleLane rightLane = createLane(VehicleLane.Type.LEFT_STRAIGHT,
					i + sp.numLanesNorthLeft, Side.EAST, false);
			VehicleLane rightLaneExitLeft = createLane(VehicleLane.Type.LEFT_STRAIGHT_,
					i + sp.numLanesNorthLeft, Side.SOUTH, true);
			VehicleLane rightLaneExitStraight = createLane(VehicleLane.Type.LEFT_STRAIGHT_,
					i + sp.numLanesNorthLeft, Side.WEST, true);
			
			northTce.addLane(rightLane);
			fromeRd.addLane(rightLaneExitLeft);
			northTce.addLane(rightLaneExitStraight);
			
			rightLane.addExitLane(rightLaneExitStraight);
			rightLane.addExitLane(rightLaneExitLeft);
			
			trafficLightController.addLane(rightLane);
			setTrafficLightCoords(4, rightLane);
		}
		
		// STRAIGHT LANES ON NTCE
		for (int i = 0; i < sp.numLanesNorthStraight; i++) {
			// LEFT
			VehicleLane leftLane = createLane(VehicleLane.Type.STRAIGHT,
					i + sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft, Side.WEST, false);
			VehicleLane leftLaneExitStraight = createLane(VehicleLane.Type.STRAIGHT_,
					i + sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft, Side.EAST, true);
			
			northTce.addLane(leftLane);
			northTce.addLane(leftLaneExitStraight);
			leftLane.addExitLane(leftLaneExitStraight);
			
			trafficLightController.addLane(leftLane);
			setTrafficLightCoords(1, leftLane);
			
			// RIGHT
			VehicleLane rightLane = createLane(VehicleLane.Type.STRAIGHT,
					i + sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft, Side.EAST, false);
			VehicleLane rightLaneExitStraight = createLane(VehicleLane.Type.STRAIGHT_,
					i + sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft, Side.WEST, true);
			
			northTce.addLane(rightLane);
			northTce.addLane(rightLaneExitStraight);
			rightLane.addExitLane(rightLaneExitStraight);
			
			trafficLightController.addLane(rightLane);
			setTrafficLightCoords(4, rightLane);
		}
		
		// STRAIGHT RIGHT LANES ON NTCE
		for (int i = 0; i < sp.numLanesNorthStraightRight; i++) {
			// LEFT
			VehicleLane leftLane = createLane(VehicleLane.Type.RIGHT_STRAIGHT,
					i + sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft + sp.numLanesNorthStraight, Side.WEST, false);
			VehicleLane leftLaneExitRight = createLane(VehicleLane.Type.RIGHT_STRAIGHT_,
					i + sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft + sp.numLanesNorthStraight, Side.SOUTH, true);
			VehicleLane leftLaneExitStraight = createLane(VehicleLane.Type.RIGHT_STRAIGHT_,
					i + sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft + sp.numLanesNorthStraight, Side.EAST, true);
			
			northTce.addLane(leftLane);
			fromeRd.addLane(leftLaneExitRight);
			northTce.addLane(leftLaneExitStraight);
			
			leftLane.addExitLane(leftLaneExitStraight); // Straight has to be index 0 because cars look at exitLanes[0] for the straight exit
			leftLane.addExitLane(leftLaneExitRight);
			
			trafficLightController.addLane(leftLane);
			setTrafficLightCoords(2, leftLane);
			
			// RIGHT
			VehicleLane rightLane = createLane(VehicleLane.Type.RIGHT_STRAIGHT,
					i + sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft + sp.numLanesNorthStraight, Side.EAST, false);
			VehicleLane rightLaneExitRight = createLane(VehicleLane.Type.RIGHT_STRAIGHT_,
					i + sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft + sp.numLanesNorthStraight, Side.NORTH, true);
			VehicleLane rightLaneExitStraight = createLane(VehicleLane.Type.RIGHT_STRAIGHT_,
					i + sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft + sp.numLanesNorthStraight, Side.WEST, true);
			
			northTce.addLane(rightLane);
			fromeRd.addLane(rightLaneExitRight);
			northTce.addLane(rightLaneExitStraight);
			
			trafficLightController.addLane(rightLane);
			setTrafficLightCoords(5, rightLane);
		}
		
		// RIGHT LANES ON NTCE
		for (int i = 0; i < sp.numLanesNorthRight; i++) {
			// LEFT
			VehicleLane leftLane = createLane(VehicleLane.Type.RIGHT,
					i + sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft + sp.numLanesNorthStraight + sp.numLanesNorthStraightRight,
					Side.WEST, false);
			VehicleLane leftLaneExitRight = createLane(VehicleLane.Type.RIGHT_,
					i + sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft + sp.numLanesNorthStraight + sp.numLanesNorthStraightRight,
					Side.SOUTH, true);
			
			northTce.addLane(leftLane);
			fromeRd.addLane(leftLaneExitRight);
			leftLane.addExitLane(leftLaneExitRight);
			
			trafficLightController.addLane(leftLane);
			setTrafficLightCoords(2, leftLane);
			
			// RIGHT
			VehicleLane rightLane = createLane(VehicleLane.Type.RIGHT,
					i + sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft + sp.numLanesNorthStraight + sp.numLanesNorthStraightRight,
					Side.EAST, false);
			VehicleLane rightLaneExitRight = createLane(VehicleLane.Type.RIGHT_,
					i + sp.numLanesNorthLeft + sp.numLanesNorthStraightLeft + sp.numLanesNorthStraight + sp.numLanesNorthStraightRight,
					Side.NORTH, true);
			
			northTce.addLane(rightLane);
			fromeRd.addLane(rightLaneExitRight);
			rightLane.addExitLane(rightLaneExitRight);
			
			trafficLightController.addLane(rightLane);
			setTrafficLightCoords(5, rightLane);
		}
		
//		//Boxes that we need to add lanes to
//		Box[] boxes = new Box[4];
//		for( int i = 0 ; i < boxes.length ; i++ ){
//			boxes[i] = new Box();
//		}
//		
//		ArrayList<VehicleLane> vLanes = northTce.getVehicleLanes();
//		for( int i = 0 ; i < vLanes.size() ; i ++ ) {
//			switch (vLanes.get(i).getType()){
//			case LEFT_STRAIGHT:
//			case STRAIGHT :
//				int dir = (int)vLanes.get(i).dirDeg();
//				if ( dir == 0 ) {
//					vLanes.get(i).setBox(boxes[1]);
//				} else if ( dir == 180 ) {
//					vLanes.get(i).setBox(boxes[0]);					
//				}
//			}
//		}
//		
//		vLanes = fromeRd.getVehicleLanes();
//		for( int i = 0 ; i < vLanes.size() ; i ++ ) {
//			switch (vLanes.get(i).getType()){
//			case LEFT_STRAIGHT:
//			case STRAIGHT :
//				int dir = (int)vLanes.get(i).dirDeg();
//				if ( dir == 90 ) {
//					vLanes.get(i).setBox(boxes[3]);
//				} else if ( dir == 270 ) {
//					vLanes.get(i).setBox(boxes[2]);					
//				}
//			}
//		}
//
//		//add the boxes to the lanes
//		addBoxNorth(rightEntry[RIGHT_NTW], boxes[0],2);
//		addBoxNorth(rightEntry[RIGHT_NTE], boxes[1],5 );
//		addBoxSouth(rightEntry[RIGHT_FRS], boxes[2],8);
//		addBoxSouth(rightEntry[RIGHT_FRN], boxes[3],11);
		
		
		// TODO: PEDESTRIAN STUFF
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
