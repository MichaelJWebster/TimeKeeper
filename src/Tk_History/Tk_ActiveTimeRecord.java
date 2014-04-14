package Tk_History;

import javax.swing.Timer;
import java.util.GregorianCalendar;
import java.util.Date;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.w3c.dom.*;

/**
 * A Time record with a timer attached to keep the record updated.
 * 
 * @author michaelwebster
  */
public class Tk_ActiveTimeRecord extends Tk_TimeRecord implements ActionListener
{
	private final static int MSEC_DELAY = 1000;
	private Timer m_timer = null;
	private long m_lastEventTime = 0;

	/**
	 * Setup the active timer starting at time = start_time.
	 *
	 * @param start_time	The millisecond valur for the start time.
	 */
	public Tk_ActiveTimeRecord(long start_time)
	{
		super(start_time, start_time, 0);
		m_timer = new Timer(MSEC_DELAY, this);
		m_timer.setInitialDelay(MSEC_DELAY);
		m_timer.setCoalesce(true);
	}

	/**
	 * Start the ActiveTimeRecord running, by starting up the timer.
	 */
	public void startTask()
	{
		assert(m_timer.isRunning() == false);
		Date d = new Date();
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(d);
		m_lastEventTime = gc.getTimeInMillis();
		setStartTime(m_lastEventTime);
		setEndTime(m_lastEventTime);
		m_timer.start();
	}
	
	/**
	 * Stop the active time record from running, and record the data for this
	 * run of the time record to an XML document.
	 */
	public void stopTask()
	{
		assert(m_timer.isRunning());
		m_timer.stop();
		serializeToDocument();
	}
	
	/**
	 * Update the appropriate time fields based on the current time, and
	 * notify any observers of the change.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == m_timer)
		{
			Date d = new Date();
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(d);
			setEndTime(gc.getTimeInMillis());
			setChanged();
			notifyObservers();
			clearChanged();
		}
	}

	/**
	 * 
	 * @return
	 */
	public Timer getTimer()
	{
		return m_timer;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Date d = new Date();
		System.out.printf("Date is %s\n",d.toString());
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(d);
		System.out.printf("gc is now %s\n", gc.getTime().toString());
		System.out.printf("gc is %d\n", gc.getTimeInMillis());
		Tk_ActiveTimeRecord tat = new Tk_ActiveTimeRecord(gc.getTimeInMillis());
		tat.startTask();
		System.out.println("Create Tk_ActiveTimeRecord with:");
		//tat.printTimeRecord();
		
		for (int i = 0; i < 5000; i++)
		{
			try
			{
				Thread.sleep(2010);
			}
			catch (InterruptedException e)
			{
				System.out.println("Got interrupted!");
				System.out.println(e.getMessage());
			}
		}
		tat.stopTask();
	}

}
