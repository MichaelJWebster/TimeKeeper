package Tk_Gui;

import java.awt.*;
//import java.awt.font.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import org.w3c.dom.*;
import javax.xml.datatype.*;

/**
 * <p>Tk_Digital provides a display for a running task clock. It displays the
 * start time for a task, the current time, and a Duration value that
 * represents the difference between the current time and the start time.
 * It also displays a TotalDuration field that displays the Total time
 * spent on this task which will be different to the Duration if the task
 * has been stopped and started again.</p>
 * 
 * <p>The class uses a <code><a href="../Tk_History/Tk_ActiveTimeRecord.html">Tk_ActiveTimeRecord</a></code>
 * to keep track of the time, and updates the time fields in response to
 * updates received from the <code>Tk_ActiverTimeRecord</code>.</p>
 * 
 * <p>When a task is stopped, a DOM Document representation of that task's time
 * record is obtained from the <code>Tk_ActiveTimer</code>, and placed in the
 * <code>m_elMap</code> HashMap.</p>
 * 
 * @author michaelwebster
 */
public class Tk_Digital extends Tk_Clock
{
	/**
	 * The Label for the start time field.
	 */
	private final static String StartTimeString		=
			String.format("Start Time: ");
	/**
	 * The label for the current time field.
	 */
	private final static String CurrentTimeString	=
			String.format("Current Time: ");

	/**
	 * The label for the duration field. 
	 */
	private final static String DurationString		=
			String.format("Duration: ");

	/**
	 * The Label for the Total Duration field. 
	 */
	private final static String TotalDurationString	=
			String.format("Total Duration: ");
	
	/**
	 * A prototype value for setting the size of the Time and duration fields.
	 */
	private final static String TimePrototype = 
			"Wed Dec 11 13:03:03 EST 2013----";

	/**
	 * Prefix for the title of the Border around the <code>Tk_Digital</code>
	 * display.
	 */
	private final static String TaskPrefix = "Task: ";

	/**
	 * A Box to hold the <code>Tk_Digital</code> time display labels.
	 */
	private Box m_box					= null;

	/**
	 * The string containing the time the task was started.
	 */
	private String m_startString		= null;
	
	/**
	 * The label for the tasks start time field.
	 */
	private JLabel m_startLabel			= null;
	
	/**
	 * The label containing the actual time the task was started at.
	 */
	private JLabel m_startTimeLabel		= null;
	
	/**
	 * The string containing the current time.
	 */
	private String m_currentString		= null;
	
	/**
	 * The label for the tasks current time field.
	 */
	private JLabel m_currentLabel		= null;
	
	/**
	 * The label containing the actual current time.
	 */
	private JLabel m_currentTimeLabel	= null;
	
	/**
	 * The string containing the duration since the task was last started.
	 */
	private String m_durationString		= null;
	
	/**
	 * The label for the tasks duration field.
	 */
	private JLabel m_durationLabel		= null;
	
	/**
	 * The label containing the actual duration of the task since it was last
	 * started.
	 */
	private JLabel m_durationTimeLabel	= null;
	
	/**
	 * The string containing the total time the task has been running.
	 */
	private String m_totalDurationString	= null;
	
	/**
	 * The label for the tasks Total Duration field.
	 */
	private JLabel m_totalDurationLabel		= null;
	
	/**
	 * The label containing the actual total duration across starts and stops,
	 * that this task has been running.
	 */
	private JLabel m_totalDurationTimeLabel	= null;
	
	/**
	 * Creat a <code>Tk_Digital</code> clock with the supplied task_name as
	 * its border label.
	 * 
	 * @param task_name		The name of the task being timed.
	 */
	public Tk_Digital(String task_name)
	{
		setCurrentTaskName(task_name);
		setUpClock();
		setStartTimeDisplay();
		setCurrentTimeDisplay();
		setDurationDisplay();
		setTotalDurationDisplay();
		Font lfont = setupTitleLabels();
		setupTimeLabels(lfont);
		JPanel startPanel = new JPanel();
		startPanel.add(m_startLabel);
		startPanel.add(m_startTimeLabel);
		JPanel currentPanel = new JPanel();
		currentPanel.add(m_currentLabel);
		currentPanel.add(m_currentTimeLabel);
		JPanel durationPanel = new JPanel();
		durationPanel.add(m_durationLabel);
		durationPanel.add(m_durationTimeLabel);
		JPanel totalDurationPanel = new JPanel();
		totalDurationPanel.add(m_totalDurationLabel);
		totalDurationPanel.add(m_totalDurationTimeLabel);
		m_box = new Box(BoxLayout.PAGE_AXIS);
		m_box.add(startPanel);		
		m_box.add(currentPanel);
		m_box.add(durationPanel);
		m_box.add(totalDurationPanel);
		m_box.setBorder
		(
			BorderFactory.createTitledBorder
			(
				BorderFactory.createBevelBorder(BevelBorder.LOWERED),
				TaskPrefix + getCurrentTaskName()
			)
		);
	}

	/**
	 * Set up the labels that label each of the time and duration fields.
	 * Return the font we are using, so that it can be used in the actual time
	 * and duration fields.
	 *
	 * @return 	The font we have used in these labels.
	 */
	private Font setupTitleLabels()
	{
		m_startLabel 	= new JLabel();
		Graphics g = m_startLabel.getGraphics();
		Font fold = m_startLabel.getFont();
		float size = fold.getSize2D();
		Font fnew = fold.deriveFont(Font.BOLD, (float)1.5 * size);
		m_startLabel.setFont(fnew);
		FontMetrics fm = m_startLabel.getFontMetrics(fnew);
		Rectangle2D r2 = fm.getStringBounds(TotalDurationString, g);
		Dimension d = new Dimension
			(
				Math.round((float)r2.getWidth()),
				Math.round((float)r2.getHeight())
			);
		m_startLabel.setMaximumSize(d);
		m_startLabel.setMinimumSize(d);
		m_startLabel.setPreferredSize(d);
		m_startLabel.setBounds(r2.getBounds());
		m_startLabel.setText(StartTimeString);
		m_startLabel.setHorizontalAlignment(SwingConstants.LEFT);
		m_startLabel.setVerticalAlignment(SwingConstants.CENTER);
		m_startLabel.setOpaque(true);
		m_startLabel.setBackground(Color.white);
		m_currentLabel 	= new JLabel(); //CurrentTimeString + m_currentString, SwingConstants.LEFT);
		m_currentLabel.setFont(fnew);
		m_currentLabel.setMaximumSize(d);
		m_currentLabel.setMinimumSize(d);
		m_currentLabel.setPreferredSize(d);
		m_currentLabel.setBounds(r2.getBounds());
		m_currentLabel.setText(CurrentTimeString);
		m_currentLabel.setHorizontalAlignment(SwingConstants.LEFT);
		m_currentLabel.setVerticalAlignment(SwingConstants.CENTER);
		m_currentLabel.setOpaque(true);
		m_currentLabel.setBackground(Color.white);
		m_durationLabel = new JLabel();
		m_durationLabel.setFont(fnew);
		m_durationLabel.setMaximumSize(d);
		m_durationLabel.setMinimumSize(d);
		m_durationLabel.setPreferredSize(d);
		m_durationLabel.setBounds(r2.getBounds());
		m_durationLabel.setText(DurationString);
		m_durationLabel.setHorizontalAlignment(SwingConstants.LEFT);
		m_durationLabel.setVerticalAlignment(SwingConstants.CENTER);
		m_durationLabel.setOpaque(true);
		m_durationLabel.setBackground(Color.white);

		m_totalDurationLabel = new JLabel();
		m_totalDurationLabel.setFont(fnew);
		m_totalDurationLabel.setMaximumSize(d);
		m_totalDurationLabel.setMinimumSize(d);
		m_totalDurationLabel.setPreferredSize(d);
		m_totalDurationLabel.setBounds(r2.getBounds());
		m_totalDurationLabel.setText(TotalDurationString);
		m_totalDurationLabel.setHorizontalAlignment(SwingConstants.LEFT);
		m_totalDurationLabel.setVerticalAlignment(SwingConstants.CENTER);
		m_totalDurationLabel.setOpaque(true);
		m_totalDurationLabel.setBackground(Color.white);		
		return fnew;
	}

	/**
	 * Setup the actual time and duration labels using the supplied font.
	 *
	 * @param f		The font that the <code>JLabel</code>s are to use.
	 */
	private void setupTimeLabels(Font f)
	{
		// Setup the start time field.
		Font fnew = f.deriveFont(Font.PLAIN, f.getSize());
		m_startTimeLabel = new JLabel();
		m_startTimeLabel.setFont(fnew);
		Graphics g = m_startTimeLabel.getGraphics();
		FontMetrics fm = m_startTimeLabel.getFontMetrics(f);
		Rectangle2D r2 = fm.getStringBounds(TimePrototype, g);
		Dimension d = new Dimension
			(
				Math.round((float)r2.getWidth()),
				Math.round((float)r2.getHeight())
			);
		m_startTimeLabel.setMaximumSize(d);
		m_startTimeLabel.setMinimumSize(d);
		m_startTimeLabel.setPreferredSize(d);
		m_startTimeLabel.setBounds(r2.getBounds());
		m_startTimeLabel.setText(m_startString);
		m_startTimeLabel.setHorizontalAlignment(SwingConstants.LEFT);
		m_startTimeLabel.setVerticalAlignment(SwingConstants.CENTER);
		m_startTimeLabel.setOpaque(true);
		m_startTimeLabel.setBackground(Color.white);
		
		// Setup the update time field.		
		m_currentTimeLabel 	= new JLabel();
		m_currentTimeLabel.setFont(fnew);
		m_currentTimeLabel.setMaximumSize(d);
		m_currentTimeLabel.setMinimumSize(d);
		m_currentTimeLabel.setPreferredSize(d);
		m_currentTimeLabel.setBounds(r2.getBounds());
		m_currentTimeLabel.setText(m_currentString);
		m_currentTimeLabel.setHorizontalAlignment(SwingConstants.LEFT);
		m_currentTimeLabel.setVerticalAlignment(SwingConstants.CENTER);
		m_currentTimeLabel.setOpaque(true);
		m_currentTimeLabel.setBackground(Color.white);
		
		// Setup the duration field.
		m_durationTimeLabel 	= new JLabel();
		m_durationTimeLabel.setFont(fnew);
		m_durationTimeLabel.setMaximumSize(d);
		m_durationTimeLabel.setMinimumSize(d);
		m_durationTimeLabel.setPreferredSize(d);
		m_durationTimeLabel.setBounds(r2.getBounds());
		m_durationTimeLabel.setText(m_durationString);
		m_durationTimeLabel.setHorizontalAlignment(SwingConstants.LEFT);
		m_durationTimeLabel.setVerticalAlignment(SwingConstants.CENTER);
		m_durationTimeLabel.setOpaque(true);
		m_durationTimeLabel.setBackground(Color.white);
		
		// Setup the total duration field.
		m_totalDurationTimeLabel 	= new JLabel();
		m_totalDurationTimeLabel.setFont(fnew);
		m_totalDurationTimeLabel.setMaximumSize(d);
		m_totalDurationTimeLabel.setMinimumSize(d);
		m_totalDurationTimeLabel.setPreferredSize(d);
		m_totalDurationTimeLabel.setBounds(r2.getBounds());
		m_totalDurationTimeLabel.setText(m_totalDurationString);
		m_totalDurationTimeLabel.setHorizontalAlignment(SwingConstants.LEFT);
		m_totalDurationTimeLabel.setVerticalAlignment(SwingConstants.CENTER);
		m_totalDurationTimeLabel.setOpaque(true);
		m_totalDurationTimeLabel.setBackground(Color.white);
	}

	/**
	 * Set the start time string to the start time obtained from the active
	 * Time record.
	 */
	public void setStartTimeDisplay()
	{
		m_startString = getStartTimeString();
	}

	/**
	 * Set the current time string to the end time obtained from the
	 * active time record.
	 */
	public void setCurrentTimeDisplay()
	{
		m_currentString = getEndTimeString();
	}

	/**
	 * Set the duration string to the value obtaines from the active time
	 * record.
	 */
	public void setDurationDisplay()
	{
		m_durationString = getDurationString();
	}
	
	/**
	 * Set the total duration display: <code>m_totalDurationString</code>,
	 * by creating a string from the addition of the existing total duration
	 * and the current duration.
	 */
	public void setTotalDurationDisplay()
	{
		setTotalDuration();
		Duration d = addDuration(getDuration());
		m_totalDurationString = getTotalDurationString(d); 
	}

	/**
	 * Return a formatted string representing the current duration.
	 *
	 * @return	A formatted string containing the current duration.
	 */
	public String getTotalDurationString()
	{
		String dur = String.format
				(
					"%02d:%02d:%02d",
					getTotalDuration().getHours(),
					getTotalDuration().getMinutes(),
					getTotalDuration().getSeconds()
				);
				return dur;		
	}
	
	/**
	 * Return a formmatted string representation of the <code>Duration</code>
	 * in <code>d</code>.
	 *
	 * @param d		The <code>Duration</code> to be copied into a formatted
	 * 				string.
	 * @return		The formmatted string for <code>Duration d</code>.
	 */
	public static String getTotalDurationString(Duration d)
	{
		String dur = String.format
				(
					"%02d:%02d:%02d",
					d.getHours(),
					d.getMinutes(),
					d.getSeconds()
				);
				return dur;		
	}

	/**
	 * Start the <code>Tk_ActiveTimeRecord m_tr</code>, and grab the start and
	 * current time strings from it. Also setup the start time display, and then
	 * update the current time, duration and total duration displays.
	 */
	@Override
	public void startClock()
	{
		super.startClock();
		setStartTimeDisplay();
		setCurrentTimeDisplay();
		m_startTimeLabel.setText(m_startString);
		startDisplay();
	}
	
	/**
	 * Switch to a new task by creating and setting up the title border with
	 * the new task name, and initializing the <code>m_totalDuration</code>
	 * field.
	 * 
	 * @param new_task		The name of the task to switch to.
	 */
	@Override
	public void switchTasks(String new_task)
	{
		super.switchTasks(new_task);
		m_box.setBorder
		(
			BorderFactory.createTitledBorder
			(
				BorderFactory.createBevelBorder(BevelBorder.LOWERED),
				TaskPrefix + getCurrentTaskName()
			)
		);
	}

	/**
	 * Update the displays to their current values.
	 */
	@Override
	public void updateDisplay()
	{
		setCurrentTimeDisplay();
		setDurationDisplay();
		setTotalDurationDisplay();
		m_currentTimeLabel.setText(m_currentString);
		m_durationTimeLabel.setText(m_durationString);
		m_totalDurationTimeLabel.setText(m_totalDurationString);
	}
	
	/**
	 * Set the displayed values for current time, duration and total duration.
	 */
	public void startDisplay()
	{
		m_currentTimeLabel.setText(m_currentString);
		m_durationTimeLabel.setText(m_durationString);
		m_totalDurationTimeLabel.setText(m_totalDurationString);
	}
	
	/**
	 * Return the box that contains the <code>Tk_Digital</code>'s display.
	 * 
	 * @return		The Swing <code>Box</code> that contains the time display.
	 */
	public Box getBox()
	{
		return m_box;
	}

	public static void main(String[] args)
	{
		Tk_Digital td = new Tk_Digital("Programming");
		JFrame jf = new JFrame();
		jf.add(td.getBox());
		jf.pack();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		td.startClock();
	}

}
