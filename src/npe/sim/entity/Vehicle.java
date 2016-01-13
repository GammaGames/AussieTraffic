package npe.sim.entity;
import npe.sim.SimWindow;
import npe.sim.Utils;
import npe.sim.road.Box;
import npe.sim.road.VehicleLane;
import npe.sim.results.StatsCollector;
import static npe.sim.Utils.*;
import npe.sim.Sprite;
import java.io.FileNotFoundException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.BasicStroke;
import java.util.ArrayList;

/**
 * An abstraction which describes any type of vehicle in the intersection.
 */
public abstract class Vehicle extends Entity {
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**An enum describing what a vehicle intends to do in the centre of the intersection.*/
	public enum Intention 
	{
		LEFT(0),
		RIGHT(1),
		STRAIGHT(2);
		
		int intent;
		private Intention(int intent) {
			this.intent = intent;
		}
	}
	
	/**An enum describing all the possible states of a vehicle.*/
	public enum State 
	{
		DRIVING(0),
		STOPPING(1),
		STOPPED(2),
		TURNING(3),
		CHANGING(4),
		CREEPING(5);

		int state;
		private State(int state) {
			this.state = state;
		}
	}
	
	/**An enum describing which way a vehicle will change lanes.*/
	public enum LaneChange
	{
		LEFT,
		STRAIGHT,
		RIGHT;
	}
	
	/**The distance the vehicle needs to be from its stopping point for it to start stopping.*/
	private static final double STOP_DISTANCE = convertDistance(50);
	/**The Vehicle's Acceleration.*/
	private double acc;
	/**The next vehicle in the linked list.*/
	private Vehicle NextVehicle = null;
	/** The previous vehicle in the linked list */
	private Vehicle prevVehicle = null;
	/**The maximum speed the Vehicle can travel at.*/
	private double maxSpeed;
	/** The speed at which a vehicle creeps at */
	private double creepingSpeed = Utils.convertSpeed(20);
	
	//The lanes associated with this Vehicle.
	/**The lane that the vehicle enters the intersection in.*/
	private VehicleLane lane;
	/**The lane that the vehicle exits the intersection in.*/
	private VehicleLane exitLane;
	/**The current lane the vehicle is in.*/
	private VehicleLane currentLane;
	
	/**The intention of the Vehicle, what it will do when it enters the middle of the intersection.*/
	private final Intention intent;
	/**The current state of the Vehicle.*/
	private State state;
	/**Defines what a vehicle will do when it changes lanes.*/
	private LaneChange laneChange;
	
	//Variables used for turning
	/**The angle of the vehicle when it started turning.*/
	private double startAngle;
	/**The cosine of the angle of the vehicle when it started turning.*/
	private double startAngleCos;
	/**The sine of the angle of the vehicle when it started turning.*/
	private double startAngleSin;
	/**The angle of the vehicle when it finished turning.*/
	private double endAngle;
	
	/**A randomised distance which Vehicles will add to their stopping point.*/
	private double buffer = 0;
	/**The maximum distance which vehicle will add to their stopping point.*/
	public static final double MAX_BUFFER = Utils.convertDistance(1.5);
	/**The minimum distance which vehicle will add to their stopping point.*/
	public static final double MIN_BUFFER = Utils.convertDistance(1.0);
	
	/**The acceleration of the vehicle when taking off.*/
	private double takeoffAccel = 0;
	
	//Variables describing the point at which this vehicle will stop.
	/**The x coordinate describing the point at which this vehicle will stop.*/
	private double stopX;
	/**The y coordinate describing the point at which this vehicle will stop.*/
	private double stopY;
	/**The distance that a vehicle must creep to get to its correct stopping point.*/
	private double creepDist = 0;
	
	/**Boolean to dertermine if the car is in a box.*/
	private boolean inBox = false;
	
	//Variables used to delay when vehicles behind this vehicle start.
	/**Boolean representing whether the timer is on or off.*/
	private boolean timerOn = false;
	/**The current time of the timer.*/
	private double time = 0;
	/**A randomised amount by which a vehicle will delay its start call on the vehicle behind it.*/
	private double delay = 0;
	/**The maximum time which a vehicle will delay its start call on the vehicle behind it.*/
	public static final double MAX_DELAY = Utils.convertTime(0.5);
	/**The minimum time which a vehicle will delay its start call on the vehicle behind it.*/
	public static final double MIN_DELAY = Utils.convertTime(0.3);
	
	private boolean halfDone = false;
	
	/**Get a stats Collector from sim controller */
	private StatsCollector statsCollector;
	
	//Variables used to determine stopped time
	//private long tickStopped = 0;
	private int totalTickStopped = 0;
	
	//Variables used to determine time in intersection
	private long tickCreated = 0;
	
	//Variables used for changing lanes
	/**Boolean to determine whether a car has changed lanes yet, just a temp for testing.*/
	private boolean changed = false;
	/**The x start position of a lane change.*/
	private double changeLanesX;
	/**The y start position of a lane change.*/
	private double changeLanesY;
	/**The x point at which a point can try to change lanes.*/
	private double startTryLaneChangeX;
	/**The y point at which a vehicle can try to change lanes.*/
	private double startTryLaneChangeY;
	/**The x point at which a point can no longer try to change lanes.*/
	private double stopTryLaneChangeX;
	/**The y point at which a vehicle can no longer try to change lanes.*/
	private double stopTryLaneChangeY;
	
	//////////////////
	//---CREATION---//
	//////////////////
	public Vehicle(double x, double y, double w, double h, double direction, double takeoffAccel, VehicleLane lane, Intention intent)
	{
		super(x,y,w,h);
		this.setDirection(direction);
		this.maxSpeed = convertSpeed(lane.getSpeedLimit());
		setSpeed(maxSpeed); //A vehicle should enter the intersection already moving
		this.lane = lane;
		currentLane = lane;
		this.intent = intent;
		this.takeoffAccel = takeoffAccel;
		state = State.DRIVING;
		
		statsCollector = lane.getStatsCollector();
		statsCollector.newCar(direction);
		tickCreated = statsCollector.getCurrentTick();
		
		laneChange = LaneChange.STRAIGHT;

		//Set the points at which a car can change lanes in
		int xOffset = (int)lane.cosDir();
		int yOffset = (int)lane.sinDir();
		
		double xDif = lane.getEntryX() * xOffset - lane.getExitX() * xOffset;
		double yDif = lane.getEntryY() * yOffset - lane.getExitY() * yOffset;
		//can change lanes from 1/4 into the int til 3/4 into it
		startTryLaneChangeX = lane.getEntryX() - xDif/4 * xOffset;
		stopTryLaneChangeX = lane.getExitX() + xDif/4 * xOffset;
		startTryLaneChangeY = lane.getEntryY() - yDif/4 * yOffset;
		stopTryLaneChangeY = lane.getExitY() + yDif/4 * yOffset;

		//Used for collision detection
		addToCollisionList();
		double width = Math.abs( cosDir()*w() + sinDir()*h() );
		double height = Math.abs( sinDir()*w() + cosDir()*h() );
		collisionBox = new Rectangle2D.Double(x()-width/2, y()-height/2, width, height);
	
		//A vehicles start angle will be the same as the angle when it is created
		startAngle = dirDeg();
		
		//Initialise turning variables based on this Vehicle's Intention
		switch (intent) {
		case LEFT:
			//Exit lane is last element in exitLanes for lane
			exitLane = currentLane.getExitLanes().get(currentLane.getExitLanes().size() - 1);
			//Check if startAngle is 0, if it is, set it to 360 (equivalent but makes calculations easier)
			if (startAngle == 0) {
				startAngle = 360;
				setDirection(360);
			}
			endAngle = startAngle - 90;
			break;
		case RIGHT:
			//Exit lane is last element in exitLanes for lane
			exitLane = currentLane.getExitLanes().get(0);
			
			endAngle = startAngle + 90;
			break;
		case STRAIGHT:
			//Exit lane is first element in exitLanes for lane
			exitLane = currentLane.getExitLanes().get(0);
			
			//Cars going straight have the same start and end angles
			endAngle = startAngle;
			break;
		}
		
		//Set start angle cosine and sine
		startAngleCos = Math.cos(Math.toRadians(startAngle));
		startAngleSin = Math.sin(Math.toRadians(startAngle));
		
		//Used for turning with care
		Box box = getBox();
		if( box != null){
			if( box.insideBox(this) && !inBox){ //check to see if the box contains the vehicle, and check ot see if the box is already in the box
				box.addEntity(this);
				inBox = true;
			}
		}
		
		//Generate some random values for delay and buffer
		delay = Utils.random((int)(MAX_DELAY - MIN_DELAY)) + (int)MIN_DELAY;
		//Set distance that vehicle leaves in front of it (0.5m to 1m)
		buffer = Utils.random((int)(MAX_BUFFER - MIN_BUFFER)) + (int)MIN_BUFFER;
		
		//Initialise stopping point to the point at which it will stop if it were the only vehicle in the lane
		stopX = lane.getExitX() - buffer*cosDir() - w()/2*cosDir();
		stopY = lane.getExitY() - buffer*sinDir() - w()/2*sinDir();
	}
	
	//////////////
	//---LOOP---//
	//////////////
	/**Controls vehicle movement.*/
	public void tick()
	{
		super.tick();
		
		checkForCollisions();
		
		//check to see if the vehicle is in its box
		Box box = getBox();
		if( box != null){
			if( box.insideBox(this) && !inBox ){ //check to see if the box contains the vehicle, and check ot see if the box is already in the box
				box.addEntity(this);
				inBox = true;
			}
		}
		
		//---VARIABLE UPDATE BASED ON CURRENT STATE---//
		switch(state) {
		case DRIVING:
			setAcc(takeoffAccel);
			//Change speed based on current acceleration
			changeSpeed(acc);
			
			//---Change lanes---//
			if( !changed ) {
				if( laneChangeArea() ) {
					switch (laneChange){
					case LEFT :
						if( ifSafeChangeLanes(currentLane.getLeftLane()) ){
							changed = true;
						}
						break;
					case RIGHT : 
						if( ifSafeChangeLanes(currentLane.getRightLane()) ){
							changed = true;
						}
						break;
					}
					
				}
			}
	
			//If we are passed traffic light and not passed the intersection, we turn
			if (passedTl() && !passedIntersection()) {
				changeState(State.TURNING);
			}
			break;
		case STOPPING:
			//Change speed based on current acceleration
			changeSpeed(acc);
			//If our speed is negative or 0, we have come to a complete stop
			if (speed() <= 0.01) {
				changeState(State.STOPPED);
				//Get time the car stopped
				//tickStopped = statsCollector.getCurrentTick();
			}
			
			
			//If we are stopping but we get passed our stopping point, we should come to a complete stop (inaccuracy issues)
			if (passedStop() && !passedIntersection()) {
				changeState(State.STOPPED);
			}
			
			//We are stopping, we should tell the vehicle behind us to also stop
			if (NextVehicle != null) {
				NextVehicle.stop(stopX, stopY, NextVehicle.intent);
			}
			break;
		case STOPPED:
			//Ensure that we are completely stopped
			acc = 0;
			setSpeed(0);
			totalTickStopped++;
			
			//We are stopped, we should tell the vehicle behind us to also stop
			if (NextVehicle != null) {	
				NextVehicle.stop(stopX, stopY, NextVehicle.intent);
			}
			
			break;
		case TURNING:
			//Change speed based on current acceleration but make sure it's not negative (we don't slow down when turning)
			changeSpeed(Math.abs(acc));
			//Turn until we are finished turning
			if (turn()) {
				changeState(State.DRIVING);
				statsCollector.carExit(lane.dirDeg(), totalTickStopped/30);
				//Transfer car to its exit lane
				currentLane.removeVehicle(this);
				getExitLane().insertVehicle(this);
			}
			break;	
		case CHANGING:
			//Change speed based on current acceleration
			changeSpeed(acc);
			//Change lanes until we are finished
			if (changingLanes(changeLanesX, changeLanesY)) {
				changeState(State.DRIVING);
				//Reset variable used for changing lanes
				halfDone = false;
			}
			break;
		case CREEPING:
			//Change speed based on current acceleration
			changeSpeed(acc);
			//If we are creeping but we get passed our stopping point, we should come to a complete stop (inaccuracy issues)
			if (passedStop()) {
				changeState(State.STOPPED);
			}
			//Pass stop to vehicle behind
			if (NextVehicle != null) {
				//NextVehicle.stop(stopX, stopY, NextVehicle.intent);
			}
			break;
		}
		//Constraint check, we don't don't to go faster than the speed limit
		if (speed() > maxSpeed) {
			acc = 0;
			setSpeed(maxSpeed);
		}

		//If timer is on, increment time
		if (timerOn) {
			time++;
		}
		
		//Call tick on the vehicle behind us
		if (NextVehicle != null) {
			NextVehicle.tick();
		}
	}

	/////////////////
	//---DRAWING---//
	/////////////////
	/**Draws the vehicle on the screen.
	 * @param g Graphics context to draw on.
	 */
	public void draw(Graphics2D g)
	{
		//Draw the vehicle
		super.draw(g);
		
		//Draw special vehicle graphics which depend on current state
		drawCollision(g);
		drawIndicator(g);
		drawDebug(g);
		drawDebugAdvanced(g);
		
		//Draw next vehicle
		if (NextVehicle != null) {
			NextVehicle.draw(g);
		}
	}
	/**Draws the vehicle's indicator on the screen.
	 * @param g Graphics context to draw on.
	 */
	private void drawIndicator(Graphics2D g)
	{
		if(!passedIntersection()){
			try{
				//get the path to and including the folder name of the current sprite
				String indicatorPath = sprite().path().substring(0,sprite().path().lastIndexOf("/")+1);
				//get the version number of the current sprite
				char versionNumber = sprite.filename().charAt(0);
				String.format("%sindicator/%s_%s.gif",indicatorPath,versionNumber,intent.toString().toLowerCase());
				//indicator naming convention:
				//res/<vehicleType>/<indicator>/<vehicle version number>_<intent>.gif
				Sprite indicatorSprite = new Sprite(String.format("%sindicator/%s_%s.gif",indicatorPath,versionNumber,intent.toString().toLowerCase()));
				//draw the indicator over the vehicles sprite.
				indicatorSprite.draw(g, x()-w()/2, y()-h()/2, dirRad(), x(), y());;
			} catch(FileNotFoundException e){
				System.err.println(e.getMessage());
			}
		}
	}
	/**Draws the vehicle's collision box on the screen.
	 * @param g Graphics context to draw on.
	 */
	private void drawCollision(Graphics2D g)
	{
		//Draw collision box if a collision occurred
		if (collided) {
			g.setColor(Color.RED);
			g.draw(collisionBox);
		}
		//Draw collision locator is collision is currently occurring
		if (colliding) {
			g.setColor(Color.GREEN);
			g.drawLine((int)x(), -3000, (int)x(), 3000);
			g.drawLine(-3000, (int)y(), 3000, (int)y());
		}
	}
	/**Draws the vehicle's debug information on the screen.
	 * @param g Graphics context to draw on.
	 */
	private void drawDebugAdvanced(Graphics2D g)
	{
		if (!Utils.vehicleDebuggingAdvanced) {
			return;
		}
		g.drawLine((int) x(),(int) y(), (int) stopX, (int) stopY);
		g.setColor(Color.GREEN);
		g.drawOval((int) stopX,(int) stopY, 2, 2);

		if( getPrevVehicle() == null ) {
			g.drawString("FIRST", (int) x(), (int) y());
		} else {
			g.drawLine((int) x(), (int) y(), (int) getPrevVehicle().x(), (int) getPrevVehicle().y());
		}
		
		g.setColor(Color.GREEN);
		if( getNextVehicle() == null ) {
			g.drawString("LAST", (int) x() + 10, (int) y() + 10);
		} else {
			g.drawLine((int) x() + 10, (int) y() + 10, (int) getNextVehicle().x() + 10, (int) getNextVehicle().y() + 10);
		}
		
		g.setColor(Color.WHITE);
		if( passedIntersection() ) {
			g.drawString("PASSED", (int) x() + 15 ,(int) y() + 15);
		} else {
			g.drawString("NOTPASSED", (int) x() + 15 ,(int) y() + 15);
		}
		
		if( inBox ) {
			g.drawString("IN BOX", (int) x() + 15, (int) y() + 15);
		} else {
			g.drawString("NOT IN BOX", (int) x() + 15, (int) y() + 15);			
		}

		g.setColor(Color.CYAN);
		g.drawString(state.toString(), (int) x() + 20 ,(int) y() + 20);
	}
	/**Draws a simple representation of the vehicle's debug information on the screen.
	 * @param g Graphics context to draw on.
	 */
	private void drawDebug(Graphics2D g)
	{
		//Draw a coloured circle over the car representing its state
		if(Utils.vehicleDebugging){			
			switch(state){
			case DRIVING : 
				g.setColor(Color.GREEN);
				break;
			case STOPPING :
				g.setColor(Color.YELLOW);
				break;
			case STOPPED :
				g.setColor(Color.RED);
				break;
			case TURNING : 
				g.setColor(Color.GRAY);
				break;
			case CHANGING : 
				g.setColor(Color.PINK);
				break;
			case CREEPING : 
				g.setColor(Color.CYAN);
				break;
			}

			Ellipse2D.Double circle = new Ellipse2D.Double(x()-h()/2*.8,y()-h()/2*.8,h()*.8,h()*.8);
			g.fill(circle);
			g.setStroke(new BasicStroke(1));
			g.setColor(Color.BLACK);
			g.draw(circle);
		}
	}
	
	/////////////////
	//---CONTROL---//
	/////////////////
	/**Tells a vehicle to start moving.
	 *@param intent Only vehicle's with this intention will respond to this message 
	 */
	public void start(Intention intent)
	{
		//Check if the start message is intended for this car based on its intention
		//If not, we pass it back as long as we are turning or driving (not blocking the way)
		if (getIntent() == intent) {	
			switch(getState()) {
			case DRIVING:
				//We are already driving, we don't need to do anything
				break;
			case STOPPING:
				//We receive a start message, we start driving
				changeState(State.DRIVING);
				//Green light, start timer to delay start message to car behind
				timerOn = true;
				break;
			case STOPPED:
				//We receive a start message, we start driving
				changeState(State.DRIVING);
				//totalTickStopped += statsCollector.getCurrentTick() - tickStopped; 
				//Green light, start timer to delay start message to car behind
				timerOn = true;
				break;
			case TURNING:
				//We are turning, we don't need to do anything
				break;
			case CHANGING:
				//We are changing, we don't need to do anything
				break;
			case CREEPING:
				//We receive a start message, we start driving
				changeState(State.DRIVING);
				//Green light, start timer to delay start message to car behind
				timerOn = true;
				break;
			}
			
			if (NextVehicle != null) {
				//Don't send start message until we have delayed
				delayStart(intent);
			}
		} else {
			switch(getState()) {
			case DRIVING:	
				//$FALL_THROUGH$
			case TURNING:
				//Start message was not intended for us, we pass it back only if we are driving or turning
				if (NextVehicle != null) {
					delayStart(intent);
				}
				break;
			}
		}
	}
	
	protected void delayStart(Intention intent)
	{
		if (timerOn) {	
			if (time >= delay) {
				NextVehicle.start(intent);
				//Reset timer
				timerOn = false;
				time = 0;
			}
		} else {
			NextVehicle.start(intent);
		}
	}
	
	/**Tells a vehicle to stop moving after travelling a certain distance.
	 * The vehicle will decelerate linearly from its current position to the stopping position.
	 * @param stopX The x coordinate of where the vehicle in front of this vehicle will stop.
	 * @param stopY The y coordinate of where the vehicle in front of this vehicle will stop.
	 * @param intent This stop message applies specifically to vehicles of this intention.
	 */
	public void stop(double stopX, double stopY, Intention intent)
	{
		//Store original values in case we want to tell the vehicle behind us to stop where we would've stopped
		double originalStopX = stopX;
		double originalStopY = stopY;
		
		if (getIntent() == intent) {
			//Get cos and sin of angle
			int xDir = (int)cosDir();
			int yDir = (int)sinDir();
			
			//Modify Stopping points based on buffer
			stopX -= buffer*xDir;
			stopY -= buffer*yDir;
			
			//If there is a vehicle in front of it, we need to take into account it's width when stopping
			double prevWidth = 0;
			if (prevVehicle != null) {
				if (prevVehicle.state != State.TURNING) {
					prevWidth = prevVehicle.w();
				}
			}
			
			//Modify stopping points based on vehicle widths
			stopX -= (w()/2 + prevWidth/2)*xDir;
			stopY -= (w()/2 + prevWidth/2)*yDir;
			
			//Set variables that can be accessed by other vehicles
			this.stopX = stopX;
			this.stopY = stopY;
			
			//Calculate the distance the car needs to stop in
			double distance = Math.abs((x() - stopX)*xDir) + Math.abs((y() - stopY)*yDir);
		
			//Check that the vehicle is less than STOP_DISTANCE to its stopping point, if not, don't stop
			if (distance <= STOP_DISTANCE) {
			//Calculate acceleration based on this distance to acheive a final speed of 0
			double acceleration = (-Math.pow(speed(), 2))/(2*distance);
			
			//Check if the stop message is intended for this car based on its intention
			//If not, we pass the stop message back
			
				switch(getState()) {
				case DRIVING:
					//We are driving, we should start stopping with the acceleration calculated
					//Before you change the state to stopping
					//see if the vehicle is going slower than the creeping speed.
					//If this is the case, go to creeping state
					changeState(State.STOPPING);
					setAcc(acceleration);
					break;
				case STOPPING:
					//We are stopping, update our acceleration
					setAcc(acceleration);
					
					//Calculate time it's going to take for us to stop
					double timeToStop = (0 - speed())/acceleration;
					
					//If it's going to take more than 10 seconds for us to stop, creep
					if (timeToStop > Utils.convertTime(8) && !passedStop()) {
						creepDist = distance;
						changeState(State.CREEPING);
					} else {
						creepDist = 0;
					}
					
					break;
				case STOPPED:
					//Check that we are at before our stopping point, if we are, creep
					if (distance > buffer && !passedStop()) {
						creepDist = distance;
						changeState(State.CREEPING);
					} else {
						creepDist = 0;
					}
					break;
				case TURNING:
					//Don't do anything just pass stop message back with same stopping point
					if (NextVehicle != null) {
						NextVehicle.stop(originalStopX, originalStopY, intent);
					}
					break;
				case CHANGING:
					//If we are changing lanes, ignore a stop message
					break;
				case CREEPING:
					//TODO Creeping needs to be improved
					//Accelerate to 20km/h
					//double speed = creepingSpeed;
					//setAcc((Math.pow(speed, 2)-Math.pow(speed(), 2))/(2*distance));
					setAcc(takeoffAccel);
					//If we have crept to a distance half the creeping distance, start stopping again
					if (distance < creepDist/2) {
						changeState(State.STOPPING);
					}
					//Pass stop to vehicle behind
					if (NextVehicle != null) {
						NextVehicle.stop(stopX, stopY, intent);
					}
					break;
				}
			} else {
				//We are not close enough to our stopping point to start stopping
				//However if we were already stopped or stopping and the distance changed
				//We should start driving again
				switch (state) {
				case STOPPING:
				case STOPPED:
					changeState(State.DRIVING);
					break;
				}
			}
		} else {
			//Don't stop, but pass the stop message back
			switch(getState()) {
			case DRIVING:
				//$FALL_THROUGH
			case TURNING:
				if (NextVehicle != null) {
					NextVehicle.stop(originalStopX, originalStopY, intent);
				}
				break;
			}
		}
	}
	/**Method to incrementally make a vehicle turn from its startAngle to its endAngle.*/
	public boolean turn() 
	{
		//Variables for turning
		double entryX, entryY, exitX, exitY, x, y;
		
		//The entry and exit points of turning including the cars width
		exitLane.cosDir();
		entryX = exitLane.getEntryX() - w()/2*exitLane.cosDir();
		entryY = exitLane.getEntryY() - w()/2*exitLane.sinDir();
		exitX = currentLane.getExitX() - w()/2*startAngleCos;
		exitY = currentLane.getExitY() - w()/2*startAngleSin;
		
		//Turn based on the Vehicle's intention
		switch (intent) {
		//---LEFT TURNING---//
		case LEFT:
			if (dirDeg() > endAngle) {
				switch (currentLane.road().getROAD()) {
				case NORTH:
					//Add an amount to exit point such that it will become the point where the car turns in a perfect arc
					exitX += (Math.abs(entryX - exitX) - Math.abs(entryY - exitY))*startAngleCos;
					
					/*
					//Make left turns cut into pedestrian lane less, but the turn becomes sharper
					exitX += w()*Math.cos(Math.toRadians(startAngle));
					entryY += w()*Math.cos(Math.toRadians(startAngle));
					*/
					
					//If car is past this exit point, we start turning otherwise we do nothing
					if ((x() - exitX)*startAngleCos >= 0) {
						//x and y values analogous to the unit circle
						x = Math.abs(x() - exitX);
						y = Math.abs(y() - entryY);
						
						//Since result of atan is relative, we need to add the starting angle to it
						setDirection(Math.toDegrees(Math.atan(-x/y)) + startAngle);
						
						//Once y becomes very small, we snap to the angle before y starts increasing again (happens because of the abs function)
						if (y/Math.abs(entryY - exitY) < 0.1) {
							setDirection(endAngle);
						}
					}
					break;
				case FROME:
					//Add an amount to exit point such that it will become the point where the car turns in a perfect arc
					exitY += (Math.abs(entryX - exitX) - Math.abs(entryY - exitY))*startAngleSin;
					
					/*
					//Make left turns cut into pedestrian lane less, but the turn becomes sharper
					exitY += w()*Math.sin(Math.toRadians(startAngle));
					entryX -= w()*Math.sin(Math.toRadians(startAngle));
					*/
					
					//If car is past this exit point, we start turning otherwise we do nothing
					if ((y() - exitY)*startAngleSin >= 0) {
						x = Math.abs(x() - entryX);
						y = Math.abs(y() - exitY);
	
						setDirection(Math.toDegrees(Math.atan(-y/x)) + startAngle);
						if (x/Math.abs(entryX - exitX) < 0.1) {
							setDirection(endAngle);
						}
					}
					break;
				}
			} else {
				setDirection(endAngle);
				return true;
			}
			break;
		//---RIGHT TURNING---//
		case RIGHT:
			if (dirDeg() < endAngle) {
				switch (currentLane.road().getROAD()) {
				case NORTH:
					//Add an amount to exit point such that it will become the point where the car turns in a perfect arc
					exitX += (Math.abs(entryX - exitX) - Math.abs(entryY - exitY))*startAngleCos;
					
					//If car is past this exit point, we start turning otherwise we do nothing
					if ((x() - exitX)*startAngleCos >= 0) {
						x = Math.abs(x() - exitX);
						y = Math.abs(y() - entryY);
					
						setDirection(Math.toDegrees(Math.atan(x/y)) + startAngle);
						if (y/Math.abs(entryY - exitY) < 0.1) {
							setDirection(endAngle);
						}
					}
					break;
				case FROME:
					//Add an amount to exit point such that it will become the point where the car turns in a perfect arc
					exitY += (Math.abs(entryY - exitY) - Math.abs(entryX - exitX))*startAngleSin;
					
					//If car is past this exit point, we start turning otherwise we do nothing
					if ((y() - exitY)*startAngleSin >= 0) {
					
						x = Math.abs(x() - entryX);
						y = Math.abs(y() - exitY);
						
						setDirection(Math.toDegrees(Math.atan(y/x)) + startAngle);
						if (x/Math.abs(entryX - exitX) < 0.1) {
							setDirection(endAngle);
						}
					}
					break;
				}
			} else {
				setDirection(endAngle);
				return true;
			}
			break;
		//---GOING STRAIGHT---//
		case STRAIGHT:
			//We don't actually modify our direction but if we are passed the intersection, we are finished turning
			if (passedIntersection()) {
				return true;
			}
			break;
		}
		return false;
	}
	/**Method to incrementally make a vehicle change lanes.
	 *@param startX The x coordinate of where this vehicle will begin to change lanes from
	 *@param startY The y coordinate of where this vehicle will begin to change lanes from
	 */
	public boolean changingLanes(double startX, double startY)
	{
		//Initialise variables used for changing lanes
		double xDir = startAngleCos;
		double yDir = startAngleSin;
		double midX = startX + VehicleLane.LANE_WIDTH*xDir;
		double midY = startY + VehicleLane.LANE_WIDTH*yDir;
		double endX = startX + VehicleLane.LANE_WIDTH*2*xDir;
		double endY =  startY + VehicleLane.LANE_WIDTH*2*yDir;
		double x;
		double y;
		
		switch(laneChange) {
		//---CHANGING LEFT---//
		case LEFT:
			//Additional initialisation which depends on which way the vehicle is changing
			midX += VehicleLane.LANE_WIDTH*yDir;
			midY -= VehicleLane.LANE_WIDTH*xDir;
			
			endX += VehicleLane.LANE_WIDTH*2*yDir;
			endY -= VehicleLane.LANE_WIDTH*2*xDir;
			//If vehicle is not passed the mid point, we start the first part of lane changing
			
			if (dirDeg() > startAngle - 45 && !halfDone) {
				//x and y values analogous to the unit circle
				x = Math.abs(x() - startX)*xDir + Math.abs(x() - midX)*yDir;
				y = Math.abs(y() - midY)*xDir + Math.abs(y() - startY)*yDir;
				
				//Since result of atan is relative, we need to add the starting angle to it
				double angle = Math.toDegrees(Math.atan(-(x*startAngleCos + y*startAngleSin)/(x*startAngleSin + y*startAngleCos))) + startAngle;
				setDirection(angle);
				if (angle < startAngle - 45) {
					halfDone = true;
				}
			} 
			if (dirDeg() < startAngle) { //If vehicle has an angle smaller than it's starting angle, we start the second part of lane changing
				if (halfDone) {
					//x and y values analogous to the unit circle
					x = Math.abs(x() - midX)*xDir + Math.abs(x() - endX)*yDir;
					y = Math.abs(y() - endY)*xDir +  Math.abs(y() - midY)*yDir;
					
					//Since result of atan is relative, we need to add the starting angle to it
					double angle = Math.toDegrees(Math.atan((x*startAngleCos + y*startAngleSin)/(x*startAngleSin + y*startAngleCos))) + startAngle - 45;
					setDirection(angle);
				}
			} else {
				//Finished changing, make sure we have the correct angle
				setDirection(startAngle);
				return true;
			}
			break;
		//---CHANGING RIGHT---//
		case RIGHT:
			//Additional initialisation which depends on which way the vehicle is changing
			midX -= VehicleLane.LANE_WIDTH*yDir;
			midY += VehicleLane.LANE_WIDTH*xDir;
			
			endX -= VehicleLane.LANE_WIDTH*2*yDir;
			endY += VehicleLane.LANE_WIDTH*2*xDir;
		
			if (dirDeg() < startAngle + 45 && !halfDone) {
				//x and y values analogous to the unit circle
				x = Math.abs(x() - startX)*xDir + Math.abs(x() - midX)*yDir;
				y = Math.abs(y() - midY)*xDir + Math.abs(y() - startY)*yDir;
				
				//Since result of atan is relative, we need to add the starting angle to it
				double angle = Math.toDegrees(Math.atan((x*startAngleCos + y*startAngleSin)/(x*startAngleSin + y*startAngleCos))) + startAngle;
				setDirection(angle);
				if (angle > startAngle + 45) {
					halfDone = true;
				}
			}
			if (dirDeg() > startAngle) {
				if (halfDone) {	
					//x and y values analogous to the unit circle
					x = Math.abs(x() - midX)*xDir + Math.abs(x() - endX)*yDir;
					y = Math.abs(y() - endY)*xDir +  Math.abs(y() - midY)*yDir;
					
					//Since result of atan is relative, we need to add the starting angle to it
					double angle = Math.toDegrees(Math.atan(-(x*startAngleCos + y*startAngleSin)/(x*startAngleSin + y*startAngleCos))) + startAngle + 45;
					setDirection(angle);
				}
			} else {
				setDirection(startAngle);
				return true;
			}
			break;
		}
		return false;
	}
	
	public void leaveRank()
	{
		//We want to leave the taxi rank, start driving
		changeState(State.DRIVING);
		
		Vehicle nextVehicle = getNextVehicle();
		//If the vehicle behind is not a taxi, tell it to leave the rank
		if (nextVehicle != null && !(nextVehicle instanceof Taxi)) {
			nextVehicle.leaveRank();
		}
	}
	
	////////////////
	//---ACCESS---//
	////////////////
	/**Method to get the vehicle behind this one
	 * @return Vehicle - The vehicle behind this one in the linked list
	 */
	public Vehicle getNextVehicle() 
	{
		return NextVehicle;
	}
	/**Method to get the vehicle in front of this one
	 * @return prevVehicle The vehicle which is in front of this one it terms of the linked list
	 */
	public Vehicle getPrevVehicle()
	{
		return prevVehicle;
	}
	/**Method to get the acceleration of this vehicle
	 * @return acc - A double representing this vehicle's acceleration
	 */
	public double acc() 
	{
		return acc;
	}	
	/**Method to get the current state of the vehicle
	 * @return state - The state of the vehicle
	 */
	public State getState() 
	{
		return state;
	}
	/**Method to get the Intention of the vehicle
	 * @return intent - The intention of this vehicle
	 */
	public Intention getIntent() 
	{
		return intent;
	}	
	/**Method to get the lane this vehicle entered the intersection in
	 * @return lane - The lane of the vehicle
	 */
	public VehicleLane getLane() 
	{
		return lane;
	}	
	/**Method to get the current lane the vehicle is in
	 * @return VehicleLane - The current lane the vehicle is physically driving along
	 */
	public VehicleLane getCurrentLane() 
	{
		return currentLane;
	}	
	/**Method to get the lane this vehicle will exit the intersection in
	 * @return exitLane - The exit lane of this vehicle
	 */
	public VehicleLane getExitLane() 
	{
		return exitLane;
	}
	/**Method to get the x coordinate of where this vehicle will stop
	 * @return stopX - The x stopping coordinate
	 */
	public double getStopX() 
	{
		return stopX;
	}
	/**Method to get the y coordinate of where this vehicle will stop
	 * @return stopY - The y stopping coordinate
	 */
	public double getStopY() 
	{
		return stopY;
	}
	/**Method to determine whether this vehicle is stopped or not
	 * @return boolean - A boolean representing whether this vehicle is stopped.
	 */
	public boolean isStopped() 
	{
		return speed()==0;
	}

	//////////////////////
	//---MODIFICATION---//
	//////////////////////
	/**Set the Vehicles that is behind this Vehicle in the linked list
	 * @param nextVehicle the nextVehicle to set
	 */
	public void setNextVehicle(Vehicle nextVehicle) 
	{
		NextVehicle = nextVehicle;
	}
	/**Set the previous vehicle of this car
	 * @param prevVehicle the previous vehicle to set
	 */
	public void setPrevVehicle(Vehicle prevVehicle)
	{
		this.prevVehicle = prevVehicle;
	}
	/**Method to set the acceleration of this vehicle
	 * @param newAcc - The new acceleration
	 */
	public void setAcc(double newAcc) 
	{
		acc = newAcc;
	}	
	/**Method to change the state of this vehicle
	 * @param state - The new state of this vehicle
	 */
	public void changeState(State state) 
	{
		this.state = state;
	}	
	/**Method to set the current lane the vehicle is in
	 * @param currentLane - The lane the vehicle will be put into
	 */
	public void setCurrentLane(VehicleLane currentLane) 
	{
		this.currentLane = currentLane;
	}
	/**Helper method which calls the changeSpeedBy method in entity
	 * This method calls another method which ensures that this vehicle won't be given an ecceleration that will allow it to crash into the vehicle in front
	 * @param acceleration The amount to change speed by unless modified by capSpeed()
	 */
	public void changeSpeed(double acceleration)
	{
		changeSpeedBy(capSpeed(acceleration));
	}
	/**Sets a cars lane change intention
	 * @param intent The intention of the lane change
	 */
	public void setLaneChange(LaneChange intent) 
	{
		this.laneChange = intent;
	}

	//////////////////////
	//---POSITIONING---///
	//////////////////////
	/** 
	 * Check to see if a vehicle is passed a point
	 * @param x The x point to check for
	 * @param y The y point to check for
	 * @return True of the vehicle is passed the point x,y
	 */
	public boolean passedPoint( double x, double y )
	{
		int xOffset = (int) cosDir();
		int yOffset = (int) sinDir();
		
		x *= xOffset;
		y *= yOffset;
		
		//vehicle x and y poisition after offset
		double vX = x() + w()/2 * xOffset;
		double vY = y() + w()/2 * yOffset;
		
		vX *= xOffset;
		vY *= yOffset;
		
		if (vX <= x && vY <= y) {
			return false;
		} else {
			return true;
		}
 	}
	/**
	 * Method to determine whether a car has passed the traffic light
	 * @return
	 */
	public boolean passedTl() 
	{
		return passedPoint( lane.getExitX(),lane.getExitY());
	}
	/**
	 * Method to determine whether a car has passed the intersection (middle bit)
	 * @return
	 */
	public boolean passedIntersection() 
	{
		if( exitLane == lane ) {
			System.out.println("WHOOPS");
		}
		int xOffset = (int) exitLane.cosDir();
		int yOffset = (int) exitLane.sinDir();
		double stoppingPointX = exitLane.getEntryX() * xOffset;
		double stoppingPointY = exitLane.getEntryY() * yOffset;
		
		double x = x() - w()/2 * xOffset;
		double y = y() - w()/2 * yOffset;
		
		//multiply everything by xoffest, this will mean that we can use less than for all cases
		//ie 3 < 4
		// we want to use > than on the othe side
		//if 4 > 3
		//-4 < -3 -> TODA
		x *= xOffset;
		y *= yOffset;
		
		//use equals to as well due to the fact that stopping point x or y will be 0
		if( x >= stoppingPointX && y >= stoppingPointY ) {
			return false;
		} else {
			return true;
		}
	}
	/**Method to check whether a car is passed it's stopping point
	 * @return boolean A boolean which determines whether this vehicle is passed it's stopping point or not
	 */
	public boolean passedStop()
	{
		if (passedPoint(stopX + (w()/2)*cosDir(), stopY + (w()/2)*sinDir())) {
			return true;
		}
		return false;
	}
	/**Method determining if the vehicle is passed the point in the lane where it would stop at if it were the first vehicle
	 * @return boolean
	 */
	private boolean passedFinalStop()
	{
		//Beginning at the exit point of the lane
		double x = lane.getExitX();
		double y = lane.getExitY();
		
		//Subtract buffer
		x -= buffer*cosDir();
		y -= buffer*sinDir();
		
		if (passedPoint(x, y)) {
			return true;
		}
		return false;
	}
	
	////////////////////////
	//---LANE CHANGING---///
	////////////////////////
	/**Method to cause a vehicle to change lanes instead of it just randomly occurring, used by taxis.*/
	public void forceChangeLanes()
	{
		laneChange = LaneChange.RIGHT;
		ifSafeChangeLanes(currentLane.getRightLane());
	}
	/**
	 * Check to see if it safe to change into the next lane
	 * IF it is safe, the car will change lanes.
	 * This is all put into one method, to avoid searching a linked list twice
	 * @param lane The lane that the car will turn into
	 */
	public boolean ifSafeChangeLanes(VehicleLane lane)
	{
		//let x1 = left most point in lane, where we need no cars to be
		//x2 to be right most point in lane where no cars can be
		//likewise for y1 and y2.
		
		//we want a buffer 1 second infront of vehicle and behind vehicle
		double frontBuffer = maxSpeed * Utils.convertTime(0.5);
		double backBuffer = maxSpeed * Utils.convertTime(1.5);
		
		//Scale the backbuffer based on current speed
		double proportion = (maxSpeed*maxSpeed)/(speed()*speed());
		backBuffer = backBuffer * proportion;
		
		//x1 = ( x() - buffer ) * |cos(dir)|
		//multiplying by cos of dir, because if a car is going at up or down we dont care about its x pos.
		//taking the abolute of the dir, becuase we dont want a neg x1 value
		
		int xDir = (int) cosDir();
		int yDir = (int) sinDir();
		//using xOffest and y offest to avoid recaculating something multiple times
		double xOffset = Math.abs( xDir ); 
		double yOffset = Math.abs( yDir ); 

		double x1 = ( x() +  frontBuffer + buffer*xDir) * xOffset; 
		double x2 = ( x() - backBuffer - buffer*xDir) * xOffset;
		
		//if we are travelling the opposite direction swap x1 and x2, this means that x1 will be the entry point of danger zone and x2 will be exit point
		if( xDir == -1 ) {
			double temp = x1;
			x1 = x2;
			x2 = temp;
		}
		
		//likewise for y1 and y2
		double y1 = ( y() + frontBuffer + buffer*yDir ) * yOffset;
		double y2 = ( y() - backBuffer - buffer*yDir ) * yOffset;
		
		if( yDir == -1 ) {
			double temp = y1;
			y1 = y2;
			y2 = temp;
		}
		

		//get the first vehicle of the searching lane
		//search through the vehicles until we reach null || or until current vehicle is >= x2 && y2 
		//if the vehicle lanes x,y position is > x1 || y1 return false
		//if we get to the end of the loop return true
		
		Vehicle current = lane.getFirstVehicle();
		//previous vehicle, used for appending the next vehicle into the lane, if it is safe
		Vehicle prev = current;
		boolean isSafe = true;
		while ( current != null ) {
			//using equals to as well here, because, yOffset or xOffset will = 0
			//therefore one of those statements will always be true, which is what we want, as we only care about 1 direction
			//ie if going right, yOffest = 0, therefore we only care about the x position
			if( current.x() * xDir <= x2 * xDir && current.y() * yDir <= y2 * yDir ) {
				//if we pass this statement, then we have passed the danger zone, so return true
				isSafe = true;
				prev = current;
				break;
			}
			if( current.x() * xDir <= x1 * xDir && current.y() * yDir <= y1 * yDir ) {
				isSafe = false;
				break;
			}
			prev = current;
			current = current.getNextVehicle();
		}
		
		if(prevVehicle != null) {
			double x = prevVehicle.x();
			double y = prevVehicle.y();
			double w = prevVehicle.w()/2 + w()/2;
			
			double pointX = x - (w + buffer)*cosDir();
			double pointY = y - (w + buffer)*sinDir();
			
			double distance = Math.abs((x() - pointX)*cosDir()) + Math.abs((y() - pointY)*sinDir());
			//Don't change lanes if distance is less than the combination of half the widths 
			if (distance < w/2) {
				isSafe = false;
			}
		}
		
		//if we get to a null vehicle, return true.
		//if prev == null then we insert into the start of the list
		//an attempt to change lanes
		if( isSafe ) {
			changeState(State.CHANGING);
			changeLanesX = x();
			changeLanesY = y();
			startAngle = dirDeg();
			startAngleCos = Math.cos(Math.toRadians(startAngle));
			startAngleSin = Math.sin(Math.toRadians(startAngle));
			if( current == null ) {
				currentLane.removeVehicle(this);
				lane.insertVehicle(this);		
			} else {
				currentLane.removeVehicle(this);
				lane.insertVehicle(this,current);
			}
			//Set acceleration, (needed if the vehicle is stopped or stopping
			setAcc(takeoffAccel);
			
			//Update exit lane
			ArrayList<VehicleLane> exitLanes = currentLane.getExitLanes();
			if (exitLanes.size() > 0) {
				exitLane = exitLanes.get(0);
			}
		}
		return isSafe;
	}
	/**Method to ensure that this Vehicle will not overlap the vehicle in front of it
	 * @param acceleration The acceleration that the vehicle will have
	 * @return acceleration The acceleration that the vehicle should have to ensure it doesn't overlap the vehicle in front of it
	 */
	public double capSpeed(double acceleration)
	{
		//If there is a vehicle in front of us, we need to check that we arn't travelling faster than it
		if(prevVehicle != null) {
			double speed = prevVehicle.speed();
			double x = prevVehicle.x();
			double y = prevVehicle.y();
			double w = prevVehicle.w()/2 + w()/2;
			
			//Check if this vehicle's speed is greater than the speed of the vehicle in front of it as long as the vehicle in front isn't stopped
			if (speed() > speed){//&& prevVehicle.state != State.TURNING) {
				//Calculate the distance the car needs to stop in
				
				//Speed is greater, calculate distance between vehicles
				double pointX = x - (w + buffer)*cosDir();
				double pointY = y - (w + buffer)*sinDir();
				
				double distance = Math.abs((x() - pointX)*cosDir()) + Math.abs((y() - pointY)*sinDir());
				//Only cap the speed if distance is less than the combination of half the widths 
				if (distance < w) {
					acceleration = ((Math.pow(speed, 2) - Math.pow(speed(), 2))/(2*distance));
				}
			}
		}
		return acceleration;
	}
	public boolean laneChangeArea()
	{
		//if we have passed the start point
		//we are in the lane change area only if we have not passed the end point
		if ( passedPoint (startTryLaneChangeX,startTryLaneChangeY) ){
			if( passedPoint( stopTryLaneChangeX,stopTryLaneChangeY)){
				return false;
			} else {
				return true;
			}
		//if we have not passed the start point,
		//we are not in the area
		} else {
			return false;
		}
	}

	////////////////////////////
	//---COLLISION DETECTON---//
	////////////////////////////
	//Dirty hack intended for quick debugging - NOT FOR USE IN RELEASE
	//Variables
	/**Whether or not collision detection is enabled.*/
	private static boolean collisionDetection = false;
	/**A list of all vehicles which have been created.*/
	private static ArrayList<Vehicle> allVehicles;
	/**The window which is able to pause the simulation when a collision occurs.*/
	private static SimWindow sw;
	/**Whether or not the car has collided.*/
	private boolean collided = false;
	/**Whether or not the car is colliding this step.*/
	private boolean colliding = false;
	/**The bounding box for collision checking this vehicle.*/
	private Rectangle2D collisionBox;
	//Methods
	/**Enables collision detection for all vehicles (DO NOT CALL THIS UNLESS DEBUGGING).*/
	public static void useCollisionDetection(SimWindow s)
	{
		sw = s;
		collisionDetection = true;
		allVehicles = new ArrayList<Vehicle>(200);
	}
	/**Checks to see if the vehicle has collided with any other vehicles.
	 * Pauses the simulation and prints an error if a collision occurs.
	 */
	private void checkForCollisions()
	{
		//Update collision box
		double width = Math.abs( cosDir()*w() + sinDir()*h() );
		double height = Math.abs( sinDir()*w() + cosDir()*h() );
		collisionBox = new Rectangle2D.Double(x()-width/2, y()-height/2, width, height);
		
		//Check to see if we should be checking for collisions
		if (!collisionDetection || collided  || state==State.TURNING) {
			colliding = false;
			return;
		}
		
		//Check for collisions
		for (Vehicle v : allVehicles) {
			if (v != this && v.getState()!=State.TURNING) {
				if ( collisionBox.intersects(v.collisionBox) ) {
					collided = true;
					v.collided = true;
					colliding = true;
					sw.pausePressed();
				}
			}
		}
	}
	/**Adds the vehicle to the collision detection list.*/
	private void addToCollisionList()
	{
		if (collisionDetection) {
			allVehicles.add(this);
		}
	}
	/**Removes the vehicle from the collision detection list.*/
	public void removeFromCollisionList()
	{
		if (collisionDetection) {
			allVehicles.remove(this);
		}
	}
	
	public void deletingVehicle() {
		statsCollector.carDelete(lane.dirDeg(), (statsCollector.getCurrentTick() - tickCreated)/30);
	}	
}
