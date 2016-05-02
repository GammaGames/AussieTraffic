package npe.sim.entity;
import java.awt.Color;
import java.awt.Graphics2D;
import npe.sim.Sprite;
import npe.sim.road.Box;

/**
 * An abstraction for any moving, visible object in the simulation.
 */

public abstract class Entity {
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**Whether debugging for entities is enabled.*/
	public static boolean debugging = false;
	/**Sprite for the entity.*/
	protected Sprite sprite = null;
	/**The collision box that the entity belongs to (used for turning with care).*/
	private Box box;
	//Dimensions
	/**The x coordinate of the centre of the entity.*/
	private double x;
	/**The y coordinate of the centre of the entity.*/
	private double y;
	/**The width of the entity.*/
	private double w;
	/**The height of the entity.*/
	private double h;
	//Motion
	/**The horizontal speed of the entity.*/
	public double hspeed = 0;
	/**The vertical speed of the entity.*/
	public double vspeed = 0;
	/**The overall speed of the entity.*/
	private double speed = 0;
	/**The entity's direction, in degrees, anticlockwise from the right.*/
	private double direction = 0;
	/**The cosine of the direction, used to reduce number of calls to Math.cos.*/
	private double cosDir = 0;
	/**The sine of the direction, used to reduce number of calls to Math.sin.*/
	private double sinDir = 0;
	
	//////////////////
	//---CREATION---//
	//////////////////
	/**Creates an entity at the given position with the given dimensions.
	 * @param x x-coordinate to position the entity.
	 * @param y y-coordinate to position the entity.
	 * @param w Width of the entity.
	 * @param h Height of the entity.
	 */
	public Entity(double x, double y, double w, double h)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	/**Sets the sprite for the entity.
	 * @param spriteName Filename of sprite to use.
	 */
	public void setSprite(String spriteName)
	{
		try {
			sprite = new Sprite(spriteName);
		} catch (Exception e) {
			System.err.println("Sprite '"+spriteName+"' does not exist.");
		}
	}
	/**Sets the sprite for the entity.
	 * @param sprite The sprite to use.
	 */
	public void setSprite(Sprite sprite)
	{
		this.sprite = sprite;
	}
	
	//////////////
	//---LOOP---//
	//////////////
	/**Moves the entity to its new position.*/
	public void tick()
	{
		x += hspeed;
		y += vspeed;
		
	}
	/**Draws the entity's sprite at the entity's current position.
	 * If the entity has no sprite, a red rectangle is drawn instead.
	 * @param g Graphics context to draw on.
	 */
	public void draw(Graphics2D g)
	{
		//David : Thomas, clean this up. whatever follows your conventions. 
		//		the deubbing if statment is not inteneded to used simulation wide. but you can if you want
		//		the above is just to help with the debugging of cars
		//David : Thomas, fix this
		if (sprite == null) {
			System.err.println("Sprite is null!. chossing cars/testDummy.jpg sprite");
			try{
				new Sprite("cars/testDummy.jpg").draw(g, x-w/2, y-h/2, dirRad(), x, y);
			} catch (java.io.FileNotFoundException e){
				System.out.println(e.getMessage());
			}
		} else {
			//if nothing is wrong, draw the sprite
			sprite.draw(g, x-w/2, y-h/2, dirRad(), x, y);
		}
		
		
		if(debugging){
			
			g.setColor(Color.BLACK);
			//draw the x and y of each entity
			g.drawString(String.format("x:%.1f",x),(int)(x-w/2+3),(int)(y));
			g.drawString(String.format("y:%.1f",y),(int)(x-w/2+3),(int)(y+10));
		} 
	}
	
	////////////////
	//---ACCESS---//
	////////////////
	//Dimensions
	/**Provides access to the entity's x-coordinate.
	 * @return The entity's x-coordinate.
	 */
	public double x()
	{
		return x;
	}
	/**Provides access to the entity's y-coordinate.
	 * @return The entity's y-coordinate.
	 */
	public double y()
	{
		return y;
	}
	/**Provides access to the entity's width.
	 * @return The entity's width.
	 */
	public double w()
	{
		return w;
	}
	/**Provides access to the entity's height.
	 * @return The entity's height.
	 */
	public double h()
	{
		return h;
	}
	//Movement
	/**Provides access to the entity's direction (in radians).
	 * @return The entity's direction in radians.
	 */
	public double dirRad()
	{
		return Math.toRadians(direction);
	}
	/**Provides access to the entity's direction (in degrees).
	 * @return The entity's direction in degrees.
	 */
	public double dirDeg()
	{
		return direction;
	}
	/**Provides access to the entity's speed.
	 * @return The speed of the entity, in pixels per tick in the entity's direction.
	 */
	public double speed()
	{
		return speed;
	}
	/**Provides access to the entity's sprite.
	 * @return The speed of the entity, in pixels per tick in the entity's direction.
	 */
	public Sprite sprite()
	{
		return sprite;
	}
	/**Method to get the cosine of the entities direction without an extra call to Math.cos
	 * @return cosDir The cosine of the entities direction
	 */
	public double cosDir() 
	{
		return cosDir;
	}
	/**Method to get the sine of the entities direction without an extra call to Math.sin
	 * @return sinDir The sine of the entities direction
	 */
	public double sinDir() 
	{
		return sinDir;
	}
	
	//////////////////
	//---MOVEMENT---//
	//////////////////
	/**Sets the overall speed of the entity.
	 * The horizontal and vertical speed are governed by the entity's speed and direction.
	 * @param newSpeed The new speed of the entity, in pixels per tick.
	 */
	public void setSpeed(double newSpeed)
	{
		//ensure that a car never sets its speed below 0
		if( newSpeed < 0 ) {
			speed = 0;
		} else {
			speed = newSpeed;
		}
		hspeed = speed * cosDir;
		vspeed = speed * sinDir;
	}
	/**Sets the direction of movement of the entity.
	 * The horizontal and vertical speed are governed by the entity's speed and direction.
	 * @param newDir The new direction of the entity, in degrees.
	 */
	public void setDirection(double newDir)
	{
		direction = newDir;
		cosDir = Math.cos(dirRad());
		sinDir = Math.sin(dirRad());
		hspeed = speed * cosDir;
		vspeed = speed * sinDir;
	}
	/**Changes the speed of the entity by a given amount.
	 * @param delta The amount of speed to add to current speed, in pixels per tick.
	 */
	public void changeSpeedBy(double delta)
	{
		setSpeed(speed+delta);
	}
	/**Changes the direction of the entity by a given amount.
	 * @param delta The number of degrees to add to the current direction.
	 */
	public void changeDirectionBy(double delta)
	{
		setDirection(direction+delta);
	}
	
	///////////////
	//---BOXES---//
	///////////////
	/**Sets the collision box for this entity.
	 * @param box The new box to use.
	 */
	public void setBox(Box box)
	{
		this.box = box;
	}
	/**Gets the collision box for this entity.
	 * @return The entity's box.
	 */
	public Box getBox()
	{
		return box;
	}
	
}
