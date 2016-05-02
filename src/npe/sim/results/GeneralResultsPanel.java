package npe.sim.results;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.*;

/**
 * A panel for displaying general simulation results as numerical summaries.
 */

public class GeneralResultsPanel extends JPanel {

	///////////////////
	//---CONSTANTS---//
	///////////////////
	private final int TOP_LEFT_X = 10;
	private final int TOP_LEFT_Y = 10;
	private final int COLUMN_2_X = 400;
	private final int NEXT_LINE_HEIGHT = 20;
		
	///////////////////
	//---VARIABLES---//
	///////////////////
	/**The data used to generate results info.*/
	private double[] overallData;
	/**The message to display when there are no results.*/
	private JLabel noResultsMessage = new JLabel("Need simulation to run longer to produce any results");
	private double lineNumber = 0;
	private String startTime;
	private double totalRun;
	//////////////////
	//---CREATION---//
	//////////////////
	/**Creates a new general results panel.*/
	public GeneralResultsPanel(StatsCollector sc)
	{
		//Get the stats
		overallData = sc.overallStats();
		startTime = sc.getStartTime();
		totalRun = sc.getTotalRunTime();
		//Set up the panel
		setLayout(null);
		//Display the stats
		if (overallData != null) {
			createResultsSummary();
		}
	}
	/**Creates the GUI elements necessary for displaying the results.*/
	private void createResultsSummary()
	{
		Font titleFont = new Font("Title", Font.BOLD, 30);
		Font header = new Font("Header", Font.BOLD, 22);
		Font header2 = new Font("Header2", Font.BOLD, 18);
		Font text = new Font("Text", Font.PLAIN, 14);
		
		JLabel title = new JLabel("Overall Look");
		title.setBounds(TOP_LEFT_X, TOP_LEFT_Y, 200, 30);
		title.setFont(titleFont);
		this.add(title);
		
		lineNumber += 2.5;		
		JLabel st = new JLabel("Simulation start time: " + startTime);
		st.setBounds(TOP_LEFT_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 500, 30);
		st.setFont(text);
		this.add(st);
		
		lineNumber++;
		JLabel duration;
		int hours = (int)totalRun/3600;
		int minutes = (int)(totalRun/60)%60;
		int seconds = (int)totalRun % 60;
		if (hours > 0) {
			duration = new JLabel("Total runtime: " + Integer.toString(hours) + " hour(s), " + Integer.toString(minutes) + " minute(s) and " + Integer.toString(seconds) + " second(s)");
		} else {
			duration = new JLabel("Total runtime: " + Integer.toString(minutes) + " minute(s) and " + Integer.toString(seconds) + " second(s)");
		}
		
		duration.setBounds(TOP_LEFT_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 500, 30);
		duration.setFont(text);
		this.add(duration);
		
		lineNumber++;
		JLabel total = new JLabel("Total cars that entered intersection: " + (int)overallData[0]);
		total.setBounds(TOP_LEFT_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 500, 30);
		total.setFont(text);
		this.add(total);
		
		lineNumber++;
		JLabel totalOut = new JLabel("Total cars that left the intersection: " + (int)overallData[1]);
		totalOut.setBounds(TOP_LEFT_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 500, 30);
		totalOut.setFont(text);
		this.add(totalOut);
		
		lineNumber++;
		JLabel stillIn = new JLabel("Total cars still in intersection: " + (int)overallData[2]);
		stillIn.setBounds(TOP_LEFT_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 500, 30);
		stillIn.setFont(text);
		this.add(stillIn);
		
		// North Terrace and Frome Road
		lineNumber += 2.5;
		JLabel nt = new JLabel("North Terrace");
		nt.setBounds(TOP_LEFT_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 200, 30);
		nt.setFont(header);
		this.add(nt);
		
		lineNumber += 7;
		JLabel fr = new JLabel("Frome Road");
		fr.setBounds(TOP_LEFT_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 200, 30);
		fr.setFont(header);
		this.add(fr);
		
		// From East and West headers
		lineNumber -= 5.5;
		JLabel fw = new JLabel("From West");
		fw.setBounds(TOP_LEFT_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 200, 30);
		fw.setFont(header2);
		this.add(fw);
		
		JLabel fe = new JLabel("From East");
		fe.setBounds(TOP_LEFT_X + COLUMN_2_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 200, 30);
		fe.setFont(header2);
		this.add(fe);
		
		// From North and South headers
		lineNumber += 7;
		JLabel fn = new JLabel("From North");
		fn.setBounds(TOP_LEFT_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 200, 30);
		fn.setFont(header2);
		this.add(fn);
		
		JLabel fs = new JLabel("From South");
		fs.setBounds(TOP_LEFT_X + COLUMN_2_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 200, 30);
		fs.setFont(header2);
		this.add(fs);
		
		// From west data
		lineNumber -= 6;
		JLabel fwAlive = new JLabel("Average time in intersection: " + Math.round(overallData[14]*100)/100.0 + " seconds");
		fwAlive.setBounds(TOP_LEFT_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 300, 30);
		fwAlive.setFont(text);
		this.add(fwAlive);
		
		lineNumber++;
		JLabel fwAvg = new JLabel("Average wait time: " + Math.round(overallData[12]*100)/100.0 + " seconds");
		fwAvg.setBounds(TOP_LEFT_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 300, 30);
		fwAvg.setFont(text);
		this.add(fwAvg);
		lineNumber--;
		
		lineNumber += 2;
		JLabel fwMax = new JLabel("Maximum wait time: " + Math.round(overallData[13]*100)/100.0 + " seconds");
		fwMax.setBounds(TOP_LEFT_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 300, 30);
		fwMax.setFont(text);
		this.add(fwMax);
		lineNumber -= 2;
		
		// From east data
		JLabel feAlive = new JLabel("Average time in intersection: " + Math.round(overallData[11]*100)/100.0 + " seconds");
		feAlive.setBounds(TOP_LEFT_X  + COLUMN_2_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 300, 30);
		feAlive.setFont(text);
		this.add(feAlive);
		
		lineNumber++;
		JLabel feAvg = new JLabel("Average wait time: " + Math.round(overallData[9]*100)/100.0 + " seconds");
		feAvg.setBounds(TOP_LEFT_X  + COLUMN_2_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 300, 30);
		feAvg.setFont(text);
		this.add(feAvg);
		lineNumber--;
		
		lineNumber += 2;
		JLabel feMax = new JLabel("Maximum wait time: " + Math.round(overallData[10]*100)/100.0 + " seconds");
		feMax.setBounds(TOP_LEFT_X  + COLUMN_2_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 300, 30);
		feMax.setFont(text);
		this.add(feMax);
		lineNumber -= 2;
		
		// From North data
		lineNumber += 7;
		JLabel fnAlive = new JLabel("Average time in intersection: " + Math.round(overallData[5]*100)/100.0 + " seconds");
		fnAlive.setBounds(TOP_LEFT_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), (int)(lineNumber*NEXT_LINE_HEIGHT), 30);
		fnAlive.setFont(text);
		this.add(fnAlive);
		
		lineNumber++;
		JLabel fnAvg = new JLabel("Average wait time: " + Math.round(overallData[3]*100)/100.0 + " seconds");
		fnAvg.setBounds(TOP_LEFT_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 300, 30);
		fnAvg.setFont(text);
		this.add(fnAvg);
		lineNumber--;
		
		lineNumber += 2;
		JLabel fnMax = new JLabel("Maximum wait time: " + Math.round(overallData[4]*100)/100.0 + " seconds");
		fnMax.setBounds(TOP_LEFT_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 300, 30);
		fnMax.setFont(text);
		this.add(fnMax);
		lineNumber -= 2;
		
		// From South data		
		JLabel fsAlive = new JLabel("Average time in intersection: " + Math.round(overallData[8]*100)/100.0 + " seconds");
		fsAlive.setBounds(TOP_LEFT_X  + COLUMN_2_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 300, 30);
		fsAlive.setFont(text);
		this.add(fsAlive);
		
		lineNumber++;
		JLabel fsAvg = new JLabel("Average wait time: " + Math.round(overallData[6]*100)/100.0 + " seconds");
		fsAvg.setBounds(TOP_LEFT_X  + COLUMN_2_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 300, 30);
		fsAvg.setFont(text);
		this.add(fsAvg);		
		lineNumber--;
		
		lineNumber += 2;
		JLabel fsMax = new JLabel("Maximum wait time: " + Math.round(overallData[7]*100)/100.0 + " seconds");
		fsMax.setBounds(TOP_LEFT_X  + COLUMN_2_X, TOP_LEFT_Y + (int)(lineNumber*NEXT_LINE_HEIGHT), 300, 30);
		fsMax.setFont(text);
		this.add(fsMax);
		lineNumber -= 2;
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
		//Draw "No Results" message if there are no results
		if (overallData == null) {
			drawNoResultsMessage();
		}
	}
	/**Draws the "No Results" message in the middle of the panel.*/
	private void drawNoResultsMessage()
	{
		Font font = new Font("Invalid", Font.BOLD, 30);	
		noResultsMessage.setBounds((getWidth()/2) - 380, (getHeight()/2) - 70, 800, 50);
		noResultsMessage.setFont(font);
		this.add(noResultsMessage);
	}
	
}