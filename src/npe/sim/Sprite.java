package npe.sim;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * Defines an image which can be drawn on the screen and stored for later reuse.
 */

public class Sprite {
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**The image corresponding to this sprite.*/
	private Image image;
	/**Storage for all Sprites which have been loaded.*/
	private static HashMap<String,Image> storage = new HashMap<String,Image>();	
	/**The image path.*/
	private String path;
	/**The image filename.*/
	private String filename;
	
	//////////////////
	//---CREATION---//
	//////////////////
	/**Creates a sprite from an externally stored image file.
	 * If the image has already been loaded, it is taken from storage instead of being loaded again.
	 * @param name Filename of sprite to load. Assumed to be in the resources directory.
	 * @throws FileNotFoundException If the image file does not exist.
	 */
	public Sprite(String name) throws FileNotFoundException
	{
		splitNames(name);

		if (storage.containsKey(name)) {
			image = storage.get(name);
		} else {
			//Check to see if file exists
			if (!new File("res/"+name).exists()) {
				throw new FileNotFoundException("res/"+name+" does not exist");
			}
			//Load the image
			image = Toolkit.getDefaultToolkit().getImage("res/"+name);
			storage.put(name, image);
		}
	}
	/**Creates a sprite from an externally stored image file.
	 * If the image has already been loaded, it is taken from storage instead of being loaded again.
	 * @param name Filename of sprite to load. Assumed to be in the resources directory.
	 * @param m The object used to track the loading of this sprite.
	 * @throws FileNotFoundException If the image file does not exist.
	 */
	public Sprite(String name, MediaTracker m) throws FileNotFoundException
	{
		splitNames(name);
		
		if (storage.containsKey(name)) {
			image = storage.get(name);
		} else {
			//Check to see if file exists
			if (!new File("res/"+name).exists()) {
				throw new FileNotFoundException("res/"+name+" does not exist");
			}
			//Load the image
			image = Toolkit.getDefaultToolkit().getImage("res/"+name);
			m.addImage(image, 0);
			storage.put(name, image);
		}
	}
	
	private void splitNames(String name){
		//store the path to the new sprite
		path = name;
		//store the filename of the new sprite
		filename = path.substring(path.lastIndexOf("/")+1);
	}
	
	/////////////////
	//---ACCESS----//
	/////////////////	
	/** Returns the path of the sprite.
	 * @return String The path of the sprite.
	 */
	public String path (){
		return path;
	}
	
	/** Returns the file name of the sprite.
	 * @return String The file name of the sprite.
	 */
	public String filename (){
		return filename;
	}	

	/////////////////
	//---DRAWING---//
	/////////////////
	/**Draws the sprite at the given coordinates.
	 * @param g Graphics contect to draw on.
	 * @param x x-coordinate to draw at.
	 * @param y y-coordinate to draw at.
	 */
	public void draw(Graphics g, double x, double y)
	{
		g.drawImage(image, (int)x, (int)y, null);
	}
	/**Draws the sprite at the given coordinates with the given rotation.
	 * @param g Graphics contect to draw on.
	 * @param x x-coordinate to draw at.
	 * @param y y-coordinate to draw at.
	 * @param rot Number of radians to rotate sprite by.
	 * @param rx x-coordinate of point to rotate about.
	 * @param ry y-coordinate of point to rotate about.
	 */
	public void draw(Graphics2D g, double x, double y, double rot, double rx, double ry)
	{
		AffineTransform at = new AffineTransform();
		//Apply rotation about rotation point
		at.translate(rx, ry);
		at.rotate(rot);
		at.translate(-rx, -ry);
		//Translate to drawing position
		at.translate(x, y);
		//Draw the image
		g.drawImage(image, at, null);
	}
	
}
