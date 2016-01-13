package npe.sim.road;
import java.awt.*;
import java.util.*;
import java.awt.geom.*;
import npe.sim.entity.Vehicle.Intention;

/**
 *
 */
public class Road {

	/**

	 *
	 */
	public enum Type {
		NORTH(0),
		FROME(6);

		private int roadNum;
		private Type(int roadNum) {
			this.roadNum = roadNum;
		}
		public int getRoadNum(){
			return roadNum;
		}
	}

	/**
	 * Which road it is
	 */
	private final Type ROAD;

	public static final int ROAD_LENGTH = 1600;

	private Intersection intersection;
	private ArrayList<VehicleLane> vLanes = new ArrayList<VehicleLane>();
	private ArrayList<PedestrianLane> pLanes = new ArrayList<PedestrianLane>(8);

	private double x;
	private double y;

	private double speedLimit;
	
	//roadWidth is the size of the road with all lanes added
	private double roadWidth = 0;

	//width gives the current width in regards to the number of lanes currently in the road
	private double width = 0;
	private double length = 0;
	private int numLanes = 0;

	private PedestrianLane left;
	private PedestrianLane right;
	private PedestrianLane botRight;
	private PedestrianLane botLeft;
	
	/**Graphics variables.*/
	private Rectangle2D leftSide;
	private Rectangle2D rightSide;
	private Line2D topRoadSeparator;
	private Line2D botRoadSeparator;
	private Line2D leftSeparator;
	private Line2D rightSeparator;
	private Stroke drawingStroke = new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
	private Stroke dashedStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{9}, 0);

	private int test = 1;

	//Road signs
	//	 Sprite northTceRoadSign = new Sprite("/north_tce/roadSign.gif");
	//	 Sprite fromeRdRoadSign = new Sprite("/frome_rd/roadSign.gif");

	public Road(Type roadType, Intersection intersection, int numLanes, double speedLimit) 
	{
		ROAD = roadType;
		this.intersection = intersection;
		this.numLanes = numLanes;
		
		this.speedLimit = speedLimit;

		roadWidth = numLanes*VehicleLane.LANE_WIDTH;

		//Hard-code road positions
		if(ROAD == Road.Type.NORTH) {
			length = 1600;
			x = intersection.getX() - length - PedestrianLane.LANE_WIDTH;
			y = intersection.getY();
			//System.out.println(Double.toString(x) + " " + Double.toString(y) + " " + Double.toString(length) + " " + Double.toString(roadWidth));
			right = new PedestrianLane(this,x,y-PedestrianLane.LANE_WIDTH,0);
			left = new PedestrianLane(this,x+(length + PedestrianLane.LANE_WIDTH)*2 + intersection.getWidth(),y,180);
			botRight = new PedestrianLane(this,x,y + roadWidth,0);
			botLeft = new PedestrianLane(this,x+(length + PedestrianLane.LANE_WIDTH)*2 + intersection.getWidth(),y + roadWidth + PedestrianLane.LANE_WIDTH,180);
			pLanes.add(right);
			pLanes.add(left);
			pLanes.add(botRight);
			pLanes.add(botLeft);
			
			//Set the graphics components
			leftSide = new Rectangle2D.Double(x, y, length, roadWidth);	
			rightSide = new Rectangle2D.Double(x + length + intersection.getWidth() + PedestrianLane.LANE_WIDTH*2, y, length, roadWidth);
			topRoadSeparator = new Line2D.Double(x, y, x + (length + PedestrianLane.LANE_WIDTH)*2 + intersection.getWidth(), y);
			botRoadSeparator= new Line2D.Double(x, y + roadWidth, x + (length + PedestrianLane.LANE_WIDTH)*2 + intersection.getWidth(), y + roadWidth);
			leftSeparator = new Line2D.Double(x, y + (numLanes/2 + 1)*VehicleLane.LANE_WIDTH, x + length, y + (numLanes/2 + 1)*VehicleLane.LANE_WIDTH);
			rightSeparator= new Line2D.Double(x + length + intersection.getWidth() + PedestrianLane.LANE_WIDTH*2, y + (numLanes/2)*VehicleLane.LANE_WIDTH, x + length + length + intersection.getWidth() + PedestrianLane.LANE_WIDTH*2, y + (numLanes/2)*VehicleLane.LANE_WIDTH);
		}

		if(ROAD == Road.Type.FROME) {
			length = 1600;
			x = intersection.getX() + roadWidth;
			y = intersection.getY() - length - PedestrianLane.LANE_WIDTH;
			//System.out.println(Double.toString(x) + " " + Double.toString(y) + " " + Double.toString(length) + " " + Double.toString(roadWidth));
			right = new PedestrianLane(this,x + PedestrianLane.LANE_WIDTH,y,90);
			left = new PedestrianLane(this,x,y + (length + PedestrianLane.LANE_WIDTH)*2+ intersection.getHeight(),270);
			botRight = new PedestrianLane(this,x - roadWidth,y,90);
			botLeft = new PedestrianLane(this,x - intersection.getWidth() - PedestrianLane.LANE_WIDTH,y + (length + PedestrianLane.LANE_WIDTH)*2 + intersection.getHeight(),270);
			pLanes.add(right);
			pLanes.add(left);
			pLanes.add(botRight);
			pLanes.add(botLeft);
			
			//Set the graphics components
			leftSide = new Rectangle2D.Double(x - roadWidth, y, roadWidth, length);	
			rightSide = new Rectangle2D.Double(x - roadWidth, y + length + intersection.getHeight() + PedestrianLane.LANE_WIDTH*2, roadWidth, length);	
			topRoadSeparator = new Line2D.Double(x, y, x, y + (length + PedestrianLane.LANE_WIDTH)*2 + intersection.getHeight());
			botRoadSeparator= new Line2D.Double(x - roadWidth, y, x - roadWidth, y + (length + PedestrianLane.LANE_WIDTH)*2 + intersection.getHeight());
			leftSeparator = new Line2D.Double(x - (numLanes/2 + 1)*VehicleLane.LANE_WIDTH, y, x - (numLanes/2 + 1)*VehicleLane.LANE_WIDTH, y + length);
			rightSeparator= new Line2D.Double(x - (numLanes/2)*VehicleLane.LANE_WIDTH, y + length + intersection.getHeight() + PedestrianLane.LANE_WIDTH*2, x - (numLanes/2)*VehicleLane.LANE_WIDTH, y + length + length + intersection.getHeight() + PedestrianLane.LANE_WIDTH*2);
		}

	}

	/**
	 * @return the roadWidth
	 */
	public double getRoadWidth() 
	{
		return roadWidth;
	}

	/**
	 * @return the length
	 */
	public double getLength() 
	{
		return length;
	}

	
	 /**
	 * @return the x
	 */
	public double getX() 
	{
		return x;
	}

	/**
	 * @return the y
	 */
	public double getY() 
	{
		return y;
	}
	
	/**
	 * @return the rOAD
	 */
	public Type getROAD() 
	{
		return ROAD;
	}
	/**

	/**
	 * @return the intersection
	 */
	public Intersection getIntersection() 
	{
		return intersection;
	}

	/**
	 * Adds a lane to the road
	
	 * @param VehicleLane to add to the Road
	 */
	public void addLane(VehicleLane lane) 
	{
		vLanes.add(lane);
	}	
	
	/**
	 * returns the number of lanes in the road

	 * @return get total number of lanes in the road 
	 */
	public int getNumLanes() {
		return vLanes.size();
	}		
	
	public double getSpeedLimit()
	{
		return speedLimit;
	}
	
	/**
	 * returns the lanes in the road

	 * @return lanes in the road 
	 */
	public ArrayList<VehicleLane> getVehicleLanes() {
		return vLanes;
	}
	
	/**
	 * returns the lanes in the road

	 * @return lanes in the road 
	 */
	public ArrayList<PedestrianLane> getPedestrianLanes() {
		return pLanes;
	}	

	/**
	 * returns width of the road

	 * @return width of the road 
	 */
	public double getWidth() {
		return width;
	}
	
	/**
	 * Draws the road and its {@link Lane}s

	 * @param g Graphics
	 */
	public void draw(Graphics2D g) { 
		g.setStroke(drawingStroke);
		g.setColor(Color.gray);
		g.fill(leftSide);
		g.fill(rightSide);
	
		g.setColor(Color.white);
		
		g.setStroke(dashedStroke);
		g.draw(topRoadSeparator);
		g.draw(botRoadSeparator);
		
		g.setStroke(drawingStroke);
		g.draw(leftSide);
		g.draw(leftSeparator);
		g.draw(rightSide);
		g.draw(rightSeparator);
		
		for(VehicleLane l : vLanes){
			l.draw(g);
		}
		
		for(PedestrianLane p : pLanes){
			p.draw(g);
		}	
	}
	
	public void tick()
	{
		
//		Random generator = new Random();
		for(VehicleLane l : vLanes) {
			l.tick();
		}
		
		for (PedestrianLane p : pLanes) {
			p.tick();
		}

		/*
		for(VehicleLane l : vLanes) {
			
			//Add a car every 4 - 10 seconds
			if (test % ((generator.nextInt(7) + 4)*30) == 0) {
				switch(l.getType()){ 
					case LEFT : l.addCar(Intention.LEFT); break;
					case LEFT_STRAIGHT : 
						int chance = generator.nextInt(2);
						l.addCar(Intention.LEFT); 
						/*
						if (chance == 0) {
							l.addCar(Intention.LEFT); 
						} else {
							l.addCar(Intention.STRAIGHT); 
						}
						
						break;
					case STRAIGHT : 
						double busChance = generator.nextDouble();
						if (busChance < 0.02) {
							l.addBus(Intention.STRAIGHT);
						} else {
							l.addCar(Intention.STRAIGHT);
						} 
						break;
					case RIGHT_STRAIGHT : l.addCar(Intention.RIGHT); break;
					case RIGHT : l.addCar(Intention.RIGHT);
						break;
				}
				
			}
		}
		for (PedestrianLane p : pLanes) {
			if (test % ((generator.nextInt(7) + 4)*120) == 0) {
				p.addPedestrian();
			}
		}
		*/
		test++;
	}
	
	/**
	 * Set all the adjacent lanes of all the lanes on a road
	 */
	public void setAdjacentLanes()
	{
		//if we have 1 left, 2 straight 1 right on 1 side of the road.
		//we have 12 lanes + 2 right lanes
		//14 lanes in total
		//n/2 = 7
		//if we have only 1 straight lane
		//10 lanes in total
		//n/2 = 5
		
		
		//let n = number of lanes on the road
		//for the first n/2 lanes
		//the right lane is the lane at n+2
		//the left lane is the lane at n-2.
		//if ( n + 2 > n/2 ) set the right lane to null
		//if (n -2 < 0 ) set the left lane to null
		
		//for lanes n/2 -> n 
		//the left lane is at n+2
		//the right lane is at n-2
		//if n-2 < n/2 set right Lane to null
		//if n+2 > n set left lane to null
		
		int n = vLanes.size();
		for( int i = 0 ; i < n/2 ; i++ ) {
			VehicleLane lane = vLanes.get(i);
			if( i -2 < 0 ) {
				lane.setLeftLane(null);
			} else {
				lane.setLeftLane(vLanes.get(i-2));
			}
			if( i + 2 > n/2 ) {
				lane.setRightLane(null);
			} else {
				lane.setRightLane(vLanes.get(i+2));
			}
		}
		
		for( int i = n/2 ; i < n ; i++ ) {
			VehicleLane lane = vLanes.get(i);
			if( i -2 < n/2 ) {
				lane.setRightLane(null) ;
			} else {
				lane.setRightLane(vLanes.get(i-2));
			}
			if( i + 2 >= n ) {
				lane.setLeftLane(null);
			} else {
				lane.setLeftLane(vLanes.get(i+2));
			}
		}
	}
}
