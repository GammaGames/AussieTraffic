package npe.sim.road;


/**
 * An exception indicating that a {@link Lane} can not be added to a {@link Road}.
 * If the new lane being add is higher on the list (below) than the last lane on the Road, 
 * then the new lane is an invalid lane for that road
 * 
 * - LEFT,
 * - LEFT_STRAIGHT,
 * - STRAIGHT,
 * - RIGHT_STRAIGHT,
 * - RIGHT, (RIGHT_ is automatically added AUSTRALIA ONLY)
 * - RIGHT_STRAIGHT_
 * - STRAIGHT_
 * - LEFT_STRAIGHT_
 * - LEFT_
 */

public class InvalidLaneException extends Exception
{
	/**
	 * Constructs an InvalidLaneException with null as its error message string.
	 */
	public InvalidLaneException()
	{}

	/**
	 * Constructs an InvalidLaneException, saving a reference to the error message
	 * string s for later retrieval by the getMessage method.
	 *
	 * @param s the detail message.
	 */
	public InvalidLaneException(String s)
	{
		super(s) ;
	}
}
