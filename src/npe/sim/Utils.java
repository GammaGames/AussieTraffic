package npe.sim;
import java.util.Random;

/**
 * Contains useful utility methods and constants which may be used by any part of the simulation.
 */

public class Utils {
	
	////////////////////////
	//---RANDOM NUMBERS---//
	////////////////////////
	/**The random number generator currently being used.*/
	private static Random random;
	/**The current seed being used by the random generator.*/
	private static long seed;
	//Generator setup
	/**Resets the random number generator, without changing the seed.*/
	public static void resetRandom()
	{
		random = new Random(seed);
	}
	/**Sets the seed of the random number generator.
	 * @param newSeed The new seed to use.
	 */
	public static void setSeed(long newSeed)
	{
		seed = newSeed;
		resetRandom();
	}
	/**Sets the seed used by the random number generator to a random value.*/
	public static void setRandomSeed()
	{
		seed = (long) (Math.random() * 10000);
		resetRandom();
	}
	/**Provides access to the seed being used by the random generator.
	 * @return The seed being used by the random generator.
	 */
	public static long getSeed()
	{
		return seed;
	}
	/**Initialises the random number generator.*/
	static
	{
		setRandomSeed();
		//setSeed(1664);
	}
	//Number generation
	/**Generates a random number between 0.0 and 1.0.
	 * @return The generated random number.
	 */
	public static double random()
	{
		return random.nextDouble();
	}
	/**Generates a random positive integer less than the specified value.
	 * @param n The number of possible values which can be generated.
	 * @return A number between 0 (inclusive) and n (exclusive).
	 */
	public static int random(int n)
	{
		return random.nextInt(n);
	}
	/**Returns true with the specified chance (1/n).
	 * @param n The reciprocal of the chance to return true.
	 * @return True with chance 1/n, false otherwise.
	 */
	public static boolean chance(int n)
	{
		return random.nextInt(n)==0;
	}
	
	/////////////////////
	//---CONVERSIONS---//
	/////////////////////
	/**The number of pixels per metre for the simulation.*/
	public static final double PPM = 8;
	/**Converts a distance from metres into pixels.
	 * @param distance The distance to convert (in metres).
	 * @return The converted distance (in pixels).
	 */
	public static double convertDistance(double distance) 
	{
		return distance*PPM;
	}
	/**Converts a time from seconds to ticks.
	 * @param time The time to convert (in seconds).
	 * @return The converted time (in ticks).
	 */
	public static double convertTime(double time) 
	{
		return time*SimPanel.TPS;
	}
	/**Converts a speed from km/h into pixels/tick.
	 * @param speed The speed to convert (in km/h).
	 * @return The converted speed (in pixels/tick).
	 */
	public static double convertSpeed(double speed) 
	{
		return (speed*1000)/(60*60)*PPM/SimPanel.TPS;
	}
	/**Converts an acceleration from m/s^2 into pixels/tick^2.
	 * @param acc The acceleration to convert (in m/s^2).
	 * @return The converted acceleration (in pixels/tick^2).
	 */
	public static double convertAcceleration(double acc) 
	{
		return (acc*PPM)/(SimPanel.TPS*SimPanel.TPS);
	}
	
	/////////////////////
	//----DEBUGGING----//
	/////////////////////
	/**Enables a general debugging overlay.*/
	public static final boolean debugging = false;
	/**Enables the coloured state circles on the vehicles.*/
	public static final boolean vehicleDebugging = false;
	/**Enables the advanced debugging tools in the Vehicle class.*/
	public static final boolean vehicleDebuggingAdvanced = false;
	/**Enables the collision dection.*/
	public static final boolean collisionDetection = false;
	/**Enables logging of any System.out and System.err calls.*/
	public static final boolean logging = false;	
	/**Enables The Tick and Change State button in the bottom of the GUI.*/
	public static final boolean TickChangeStateDebugging = false;
	/**Enables the drawing FPS in the top left hand conrner of the SimPanel.*/
	public static final boolean FPSDrawing = false;
	/**Enables the drawing of the Seed  in the left hand conrner of the SimPanel.*/
	public static final boolean seedDrawing = false;
	/**Enables the drawing of a coordinate grid on the intersection.*/
	public static final boolean drawGrid = false;
}

/*
David : play around with this logging class


import java.io.PrintStream;
import java.io.File;
public class TeeStream extends PrintStream {
    PrintStream out;
    public TeeStream(PrintStream out1, PrintStream out2) {
        super(out1);
        this.out = out2;
    }
    public void write(byte buf[], int off, int len) {
        try {
            super.write(buf, off, len);
            out.write(buf, off, len);
        } catch (Exception e) {
        }
    }
    public void flush() {
        super.flush();
        out.flush();
    }
}
--------
// import java.io.FileOutputStream;
String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
File logFile = new File("mylogfile_" + dateString +".log");
PrintStream logOut = new PrintStream(new FileOutputStream(logFile, true));

PrintStream teeStdOut = new TeeStream(System.out, logOut);
PrintStream teeStdErr = new TeeStream(System.err, logOut);

System.setOut(teeStdOut);
System.setErr(teeStdErr);
*/
