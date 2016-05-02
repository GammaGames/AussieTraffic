package npe.sim.results;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

/**
 * Displays a graph of results based on a particular graph type.
 */

public class ResultsPanel extends JPanel {
	
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**Stores all the different possible types of graphs.*/
	public enum GraphType 
	{
		ARRIVAL,
		MAX_QUEUE_TIME,
		AVG_QUEUE_TIME,
		TOTAL_TIME;
	}
	/**The type of graph being displayed by this panel.*/
	private GraphType graph;
	/**The stats collector which contains the simulation results.*/
	private StatsCollector statsCollector;
	/**The message to display when there are no results.*/
	JLabel title = new JLabel("Need simulation to run longer to produce any results");
	//Statistics taken from the statistics collector
	int[][] totalEntered;
	double[][] averageWait;
	double[][] maxWait;
	double[][] timeAlive;
	//Colours to use for the graph
	private final Color C1 = new Color(0,128,255);
	private final Color C2 = new Color(255,128,64);
	private final Color C3 = new Color(128,128,255);
	private final Color C4 = new Color(40,213,92);
	//Layout variables and constants
	private int midBoxWidth = 0;
	private int midBoxHeight = 0;
	private final int MID_BOX_X = 10;
	private final int MID_BOX_Y = 10;	
	private final int AXIS_X_GAP = 50;
	private final int AXIS_Y_GAP = 50;
	private int ORIGIN_X = MID_BOX_X + AXIS_X_GAP;
	private int ORIGIN_Y = MID_BOX_Y + midBoxHeight - AXIS_X_GAP;	
	private final int LEGEND_BOX_WIDTH = 180;
	private final int LEGEND_BOX_HEIGHT = 140;
	private int legendBoxX = 0;
	private int legendBoxY = 0;
	private int legendSlotY1 = 0;
	private int legendSlotY2 = 0;
	private int legendSlotY3 = 0;
	private int legendSlotY4 = 0;
	private int legendSlotX = 0;
	JTextArea des = new JTextArea("Insert simulation options here", 10, 150);
	
	
	//////////////////
	//---CREATION---//
	//////////////////
	/**Creates a new ResultTab.*/
	public ResultsPanel(StatsCollector st, GraphType g)
	{
		statsCollector = st;
		graph = g;
		getPoints();
		if (totalEntered == null || averageWait == null || maxWait == null || timeAlive == null) {
			// What to do if there is no data
		}
	}
	
	/////////////////
	//---DRAWING---//
	/////////////////
	/**Draws the contents of this panel.
	 * @param g The graphics context to draw on.
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);	
		Graphics2D g2d = (Graphics2D)g;		
		//Draw the results if they exist
		if (totalEntered == null) {
			drawNoResultsMessage();
		} else {
			drawBox(g2d);
			drawScale(g2d);
			drawLegend(g2d);
			drawDescription();
			drawGraphFromPoints(g2d);
		}		
	}	
	/**Draws the box around the graph along with the axes.
	 * @param g Graphics context to draw on.
	 */
	private void drawBox(Graphics2D g)
	{
		// Resize values with magic numbers
		Line2D.Double line = new Line2D.Double();
		Stroke drawingStroke = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);		
		g.setStroke(drawingStroke);
		midBoxWidth = super.getWidth() - 234;
		midBoxHeight = super.getHeight() - 55;
		ORIGIN_X = MID_BOX_X + AXIS_X_GAP;
		ORIGIN_Y = MID_BOX_Y + midBoxHeight - AXIS_X_GAP;
		
		/**
		 * Super hard coded for now 
		 */
		
		// White background
		Rectangle2D middle = new Rectangle2D.Double(MID_BOX_X, MID_BOX_Y, midBoxWidth, midBoxHeight);		
		g.setColor(Color.white);
		g.fill(middle);
		// Drawing borders
		g.setColor(Color.black);
		// Top
		line.setLine(MID_BOX_X, MID_BOX_Y, MID_BOX_X + midBoxWidth, MID_BOX_Y);
		g.draw(line);
		// Left
		line.setLine(MID_BOX_X, MID_BOX_Y, MID_BOX_X, MID_BOX_Y + midBoxHeight);
		g.draw(line);
		// Bottom
		line.setLine(MID_BOX_X, MID_BOX_Y + midBoxHeight,  MID_BOX_X +  midBoxWidth,  MID_BOX_Y + midBoxHeight);
		g.draw(line);
		// Right
		line.setLine(MID_BOX_X +  midBoxWidth, MID_BOX_Y, MID_BOX_X +  midBoxWidth, MID_BOX_Y + midBoxHeight);
		g.draw(line);
		
		// Drawing Axes
		drawingStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		g.setStroke(drawingStroke);
		// Y-axis
		line.setLine(MID_BOX_X + AXIS_X_GAP, MID_BOX_Y + AXIS_Y_GAP/2, MID_BOX_X + AXIS_X_GAP, MID_BOX_Y + midBoxHeight - AXIS_Y_GAP/2);
		g.draw(line);	
		
		// Arrow Tip
		line.setLine(MID_BOX_X + AXIS_X_GAP, MID_BOX_Y + AXIS_Y_GAP/2, MID_BOX_X + AXIS_X_GAP + 10, MID_BOX_Y + AXIS_Y_GAP/2 + 15);
		g.draw(line);			
		line.setLine(MID_BOX_X + AXIS_X_GAP, MID_BOX_Y + AXIS_Y_GAP/2, MID_BOX_X + AXIS_X_GAP - 10, MID_BOX_Y + AXIS_Y_GAP/2 + 15);
		g.draw(line);	
		
		// X-axis
		line.setLine(MID_BOX_X + AXIS_Y_GAP/2, MID_BOX_Y + midBoxHeight - AXIS_X_GAP, MID_BOX_X + midBoxWidth - AXIS_Y_GAP/2, MID_BOX_Y + midBoxHeight - AXIS_X_GAP);
		g.draw(line);
		// Arrow tip
		line.setLine(MID_BOX_X + midBoxWidth - AXIS_Y_GAP/2, MID_BOX_Y + midBoxHeight - AXIS_X_GAP, MID_BOX_X + midBoxWidth - AXIS_Y_GAP/2 - 15, MID_BOX_Y + midBoxHeight - AXIS_X_GAP + 10);
		g.draw(line);			
		line.setLine(MID_BOX_X + midBoxWidth - AXIS_Y_GAP/2, MID_BOX_Y + midBoxHeight - AXIS_X_GAP, MID_BOX_X + midBoxWidth - AXIS_Y_GAP/2 - 15, MID_BOX_Y + midBoxHeight - AXIS_X_GAP - 10);
		g.draw(line);	
		
		new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		g.setStroke(drawingStroke);
		
		// Axis labels
		g.setColor(Color.black);
		g.rotate(Math.toRadians(-90));
		
		// Magic numbers - even I don't know what they are but they somehow centres the text
		switch (graph) {
			case ARRIVAL: {
				g.drawString("Total arrivals per interval (cars)", -midBoxHeight/2 - 80, MID_BOX_X + 16);
				break;
			}
			case MAX_QUEUE_TIME: {
				g.drawString("Max Queue Time (seconds)", -midBoxHeight/2 - 70, MID_BOX_X + 16);
				break;
			}
			case AVG_QUEUE_TIME: {
				g.drawString("Average Queue Time (seconds)", -midBoxHeight/2 - 80, MID_BOX_X + 16);
				break;
			}
		}
		g.rotate(Math.toRadians(90));
		g.drawString("Time Interval (minutes)", MID_BOX_X + midBoxWidth/2 - 50, MID_BOX_Y + midBoxHeight - AXIS_Y_GAP/2 + 18);
	}
	/**Draws the legend on the graph. 
	 * @param g Graphics context to draw on.
	 */
	private void drawLegend(Graphics2D g)
	{
		legendBoxX = midBoxWidth + ((super.getWidth() - midBoxWidth - LEGEND_BOX_WIDTH)/2);
		//legendBoxY = super.getHeight()/2 - 25 - LEGEND_BOX_HEIGHT/2;
		legendBoxY = 300;
		
		Stroke drawingStroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);	
		Line2D.Double line = new Line2D.Double();
		g.setStroke(drawingStroke);		
		Rectangle2D legend = new Rectangle2D.Double(legendBoxX, legendBoxY, LEGEND_BOX_WIDTH, LEGEND_BOX_HEIGHT);
		Rectangle2D dot;
		
		g.setColor(Color.white);
		g.fill(legend);
		g.setColor(Color.black);
		
		// Top boundary
		line.setLine(legendBoxX, legendBoxY, legendBoxX + LEGEND_BOX_WIDTH, legendBoxY);
		g.draw(line);
		
		// Bottom boundary
		line.setLine(legendBoxX, legendBoxY + LEGEND_BOX_HEIGHT, legendBoxX + LEGEND_BOX_WIDTH, legendBoxY  + LEGEND_BOX_HEIGHT);
		g.draw(line);
		
		// Left boundary
		line.setLine(legendBoxX, legendBoxY, legendBoxX, legendBoxY + LEGEND_BOX_HEIGHT);
		g.draw(line);
		
		// Right boundary
		line.setLine(legendBoxX + LEGEND_BOX_WIDTH, legendBoxY, legendBoxX + LEGEND_BOX_WIDTH, legendBoxY + LEGEND_BOX_HEIGHT);
		g.draw(line);
		
		drawingStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		g.setStroke(drawingStroke);		
		
		legendSlotX = legendBoxX + 30;
		legendSlotY1 = legendBoxY + 30;
		legendSlotY2 = legendBoxY + 60;
		legendSlotY3 = legendBoxY + 90;
		legendSlotY4 = legendBoxY + 120;
		
		g.setColor(C1);
		dot = new Rectangle2D.Double(legendSlotX - 21, legendSlotY1 - 12, 15, 15);
		g.fill(dot);
		g.drawString("Frome Road from North", legendSlotX, legendSlotY1);
		
		g.setColor(C2);
		dot = new Rectangle2D.Double(legendSlotX - 21, legendSlotY2 - 12, 15, 15);
		g.fill(dot);
		g.drawString("Frome Road from South", legendSlotX, legendSlotY2);
		
		g.setColor(C3);
		dot = new Rectangle2D.Double(legendSlotX - 21, legendSlotY3 - 12, 15, 15);
		g.fill(dot);
		g.drawString("North Terrace from East", legendSlotX, legendSlotY3);
		
		g.setColor(C4);
		dot = new Rectangle2D.Double(legendSlotX - 21, legendSlotY4 - 12, 15, 15);
		g.fill(dot);
		g.drawString("North Terrace from West", legendSlotX, legendSlotY4);
	}
	/**Draws the scale on the graph.
	 * @param g Graphics context to draw on.
	 */
	private void drawScale(Graphics2D g)
	{
		Stroke drawingStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);	
		g.setStroke(drawingStroke);
		double xAxisLength = midBoxWidth - AXIS_Y_GAP*2;
		double yAxisLength = midBoxHeight - AXIS_X_GAP*2;
		int xScale = statsCollector.getXScale();
		int yScale = statsCollector.getYScale(graph);
		//scale = 300000;
		
		
		// X-axis
		Line2D.Double line = new Line2D.Double();
		int yLength = 10;
		int xLength = totalEntered[0].length -1;
		if (xLength < 10) {
			xLength = 10;
		}
		double xGap = xAxisLength/xLength;
		double yGap = yAxisLength/yLength;
		
		float offset = 1;
		

		// for loop based on how many points there are
		for (int i = 0; i < xLength; i++) {
			line.setLine(ORIGIN_X + xGap*(i+1), ORIGIN_Y, ORIGIN_X + xGap*(i+1), ORIGIN_Y + 8);
			g.draw(line);
			
			if (i % 2 == 1 && i > 0) {
				int value = (i+1)*xScale;
				offset = 1;
				while (value % 10 != value) {
					offset++;
					value = value/10;
				}
				//if ((i+1)*scale < 10) {
					g.drawString(Integer.toString((i+1)*xScale), (float)((ORIGIN_X + xGap*(i+1)) - 3.75*offset), (float)(ORIGIN_Y + 25));
				//} else {
					//g2d.drawString(Integer.toString((i+1)*scale), (float)((ORIGIN_X + xGap*(i+1)) - 7), (float)(ORIGIN_Y + 25));
				//}				
			}
		}
		// Y-axis
		for (int i = 0; i < yLength; i++) {
			line.setLine(ORIGIN_X, ORIGIN_Y - yGap*(i+1), ORIGIN_X - 8, ORIGIN_Y - yGap*(i+1));
			g.draw(line);
			
			if (i % 2 == 1 && i > 0) {
				int value = (i+1)*yScale;
				offset = 1;
				while (value % 10 != value) {
					offset++;
					value = value/10;
				}
				g.drawString(Integer.toString((i+1)*yScale), (float)(ORIGIN_X - 18 - 4*offset), (float)(ORIGIN_Y - yGap*(i+1)) + 4);
			}
		}
	}
	/**Draws the "No Results" message in the middle of the panel.*/
	private void drawNoResultsMessage()
	{
		Font font = new Font("Invalid", Font.BOLD, 30);		
		title.setBounds((super.getWidth()/2) - 380, (super.getHeight()/2) - 70, 800, 50);
		title.setFont(font);
		this.add(title);
	}
	
	//////////////////
	//---PLOTTING---//
	//////////////////
	/**Draws a line between two points on a graph.
	 * @param g Graphics context to draw on.
	 * @param x1 x-coordinate of first point.
	 * @param y1 y-coordinate of first point.
	 * @param x2 x-coordinate of second point.
	 * @param y2 y-coordinate of second point.
	 * @param colour The colour of the line to draw.
	 */
	private void connectPoints(Graphics2D g, double x1, double y1, double x2, double y2, int colour)
	{
		Stroke drawingStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);	
		g.setStroke(drawingStroke);
		Line2D.Double line = new Line2D.Double();
		line.setLine(ORIGIN_X + x1, ORIGIN_Y - y1, ORIGIN_X + x2, ORIGIN_Y - y2);
		switch(colour) {
			case 0:
				g.setColor(C1);
				break;
			case 1:
				g.setColor(C2);
				break;
			case 2:
				g.setColor(C3);
				break;
			case 3:
				g.setColor(C4);
				break;
		}
		g.draw(line);
	}
	/**Draws a graph based on the points collected and the graph type selected.
	 * @param g Graphics context to draw on.
	 */
	private void drawGraphFromPoints(Graphics2D g)
	{

		double xAxisLength = midBoxWidth - AXIS_Y_GAP*2;
		double yAxisLength = midBoxHeight - AXIS_X_GAP*2;
		double yScale;
		
		int xLength = totalEntered[0].length - 1;
		if (xLength < 10) {
			xLength = 10;
		}
		double xGap = xAxisLength/xLength;
		double yGap = yAxisLength/10;
		
		yScale = statsCollector.getYScale(graph);
		
		switch (graph) {
			case ARRIVAL: {
				for (int j = 0; j < 4; j++) {
					for (int i = 1; i < totalEntered[j].length; i++) {
						if (i > 0) {
							connectPoints(g, (i-1)*xGap, (totalEntered[j][i-1]/yScale)*yGap, i*xGap, (totalEntered[j][i]/yScale)*yGap, j);
						}
					}
				}
				break;
			}
			case MAX_QUEUE_TIME: {
				for (int j = 0; j < 4; j++) {
					for (int i = 0; i < maxWait[j].length; i++) {
						if (i > 0) {
							connectPoints(g, (i-1)*xGap, (maxWait[j][i-1]/yScale)*yGap, i*xGap, (maxWait[j][i]/yScale)*yGap, j);
						}
					}
				}
				break;
			}
			case AVG_QUEUE_TIME: {
				for (int j = 0; j < 4; j++) {
					for (int i = 0; i < maxWait[j].length; i++) {
						if (i > 0) {
							connectPoints(g, (i-1)*xGap, (averageWait[j][i-1]/yScale)*yGap, i*xGap, (averageWait[j][i]/yScale)*yGap, j);
						}
					}
				}
				break;
			}
			case TOTAL_TIME: {
				for (int j = 0; j < 4; j++) {
					for (int i = 0; i < timeAlive[j].length; i++) {
						if (i > 0) {
							connectPoints(g, (i-1)*xGap, (timeAlive[j][i-1]/yScale)*yGap, i*xGap, (timeAlive[j][i]/yScale)*yGap, j);
						}
					}
				}
			}
		}
	}
	
	private void drawDescription() {
		
		
		des.setEditable(false);
		des.setBounds(legendBoxX, 10, 180, 250);
		des.setLineWrap(true);
		des.setWrapStyleWord(true);
		this.add(des);
	}
	
	/**Stores the points which are needed in order to draw the graph.*/
	private void getPoints()
	{
		totalEntered = statsCollector.getTotalEnteredPoints();
		averageWait = statsCollector.getAverageWaitPoints();
		maxWait = statsCollector.getMaxWaitPoints();
		timeAlive = statsCollector.getTimeAlivePoints();
	}
	
}