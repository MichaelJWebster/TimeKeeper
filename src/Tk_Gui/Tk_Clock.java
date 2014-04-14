package Tk_Gui;

import java.util.*;
import org.w3c.dom.*;
import javax.xml.datatype.*;

import Tk_History.Tk_ActiveTimeRecord;

/**
 * Abstract Clock class. The idea being that it can be instantiated as either a
 * digital or analog clock.
 * 
 * Note: At present we only have the Tk_Digital version.
 *  
 * @author michaelwebster
 */
public abstract class Tk_Clock implements Observer
{
	/**
	 * A map from task names to lists of xml time record Documents..
	 */
	private HashMap<String, Vector<Document>>m_elMap = 
			new HashMap<String, Vector<Document>>();
	
	/**
	 * The name of the current task being timed.
	 */
	private String m_currentTaskName	= null;

	/**
	 * An active time record that keeps track of the time, and can serialize
	 * its time records.
	 */
	protected Tk_ActiveTimeRecord m_tr;
	
	/**
	 * The <code>Duration</code> that records how much time has been spent on
	 * the task across possibly multiple starts and stops of the task.
	 */
	private Duration m_totalDuration		= null;
	
	/**
	 * Periodic update caused by a timer tick. 
	 */
	public abstract void updateDisplay();

	/**
	 * Set the total duration for a newly started task.
	 */
	protected void setTotalDuration()
	{
		if (m_totalDuration == null)
		{
			m_totalDuration = m_tr.getCurrentDuration();
		}
	}
	
	/**
	 * Set the total duration for a newly started task from the supplied
	 * duration value.
	 *
	 * @param d 	The <code>Duration</code> value to be set as total duration
	 * 				for this task. 
	 */
	protected void setTotalDuration(Duration d)
	{
		m_totalDuration = d;
	}
	
	/**
	 * Return the total duration for this task.
	 *
	 * @return		The total <code>Duration</code> this task has been running
	 * 				since the last task switch. 
	 */
	protected Duration getTotalDuration()
	{
		return m_totalDuration;
	}
	
	/**
	 * Return a <code>String</code> representation of the start time of this
	 * task.
	 *
	 * @return		A string representing the start time of this task.
	 */
	protected String getStartTimeString()
	{
		return m_tr.getStartTime();
	}
	
	/**
	 * Return a <code>String</code> representation of the end time of this
	 * task.
	 *
	 * @return		A string representing the end time of this task.
	 */
	protected String getEndTimeString()
	{
		return m_tr.getEndTime();
	}
	
	/**
 	 * Return a <code>String</code> representation of the
 	 * <code>Duration</code> through which this task has been running.
	 *
	 * @return		A string representing the elapsed time of this task.
	 */
	protected String getDurationString()
	{
		return m_tr.getDuration();
	}
	
	/**
 	 * Return a <code>Duration</code> representation of the elapsed time this
	 * task has been running.
	 *
	 * @return		A duration representing the elapsed time of this task.
	 */
	protected Duration getDuration()
	{
		return m_tr.getCurrentDuration();
	}
	
	/**
	 * Start the clock on this task.
	 */
	protected void startTask()
	{
		m_tr.startTask();
	}
	
	/**
	 * Stop the clock on this task.
	 */
	protected void stopTask()
	{
		m_tr.stopTask();
	}
	
	/**
	 * Return a DOM document representation of the current task's time
	 * record.
	 *
	 * @return		A <code>Document</code> that encodes the current time
	 * 				record.
	 */
	Document getXMLTimeRecord()
	{
		return m_tr.getXMLDocument();
	}
	
	/**
	 * Add <code>Duration</code> cur_dur to <code>m_totalDuration</code> and
	 * return the result <code>Duration</code>.
	 *
	 * @param cur_dur	A <code>Duration</code> to be added to the
	 * 					<code>m_totalDuration>.
	 *
	 * @return			The <code>Duration</code> that is the sum of cur_dur
	 * 					and <code>m_totalDuration</code>.
	 */
	protected Duration addDuration(Duration cur_dur)
	{
		int tot_secs = cur_dur.getSeconds() + m_totalDuration.getSeconds();
		int tot_mins = cur_dur.getMinutes() + m_totalDuration.getMinutes();
		int tot_hours = cur_dur.getHours() + m_totalDuration.getHours();
		
		int final_secs =  tot_secs % 60;
		int extra_min = tot_secs / 60;
		int tm = tot_mins + extra_min;
		int final_mins = tm % 60;
		int extra_hour = tm / 60;
		int final_hours = tot_hours + extra_hour;
		DatatypeFactory dtf = null;
		try
		{
			dtf = DatatypeFactory.newInstance();
		}
		catch (DatatypeConfigurationException err)
		{
			System.out.println
			(
				"Error: Tk_TimeRecord(long, long, long) constructor."
			);
			System.out.println("Caught Exception:");
			System.out.println(err.getMessage());
			System.exit(-1);
		}
		Duration d = dtf.newDuration(true, 0, 0, 0, final_hours, final_mins, final_secs);
		return d;
	}

	/**
	 * Set the current task name.
	 *
	 * @param tname		The name of a task to be timed.
	 */
	protected void setCurrentTaskName(String tname)
	{
		m_currentTaskName = tname;
	}
	
	/**
	 * Return the name of the current task.
	 *
	 * @return		The name of the current task.
	 */
	protected String getCurrentTaskName()
	{
		return m_currentTaskName;
	}
	
	/**
	 * Return the m_alMap mapping between task names and lists of documents
	 * that is maintained by this clock.
	 *
	 * @return		A <code>HashMap</code> containing a mapping between tasks
	 * 				and <code>Vector</code>s of documents.
	 */
	public HashMap<String, Vector<Document>>getTaskMap()
	{
		return m_elMap;
	}

	/**
	 * Add a new task name <code>Vector<Document></code> pair to the clock's
	 * map.
	 *
	 * @param tname		The name of a task.
	 * @param vd		A <code>Vector</code> of <code>Document</code>s.
	 */
	protected void putMap(String tname, Vector<Document>vd)
	{
		m_elMap.put(tname, vd);
	}
	
	/**
	 * Return the <code>Vector<Document></code> stored with key = key in this
	 * clock's mapping from tasknames to lists of documents.
	 *
	 * @param key		The name of the task we want to get the 
	 * 					<code>Vector</code> of <code>Document</code>s for.
	 *
	 * @return			A <code>Vector</code> containing <code>Document</code>s.
	 */
	protected Vector<Document>getEl(String key)
	{
		return m_elMap.get(key);
	}

	/**
	 * Create a new Tk_ActiveTime Record for this clock, initialised to the
	 * current time. Add an observer to the clock so that we can update the
	 * current time, the duration, and the total duration strings, when the
	 * active time record ticks.
	 */
	protected void setUpClock()
	{
		Date d = new Date();
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(d);
		m_tr = new Tk_ActiveTimeRecord(gc.getTimeInMillis());
		m_tr.addObserver(this);
	}

	/**
	 * Test if the clock is running - ie. the clock's active time record's
	 * timer is going.
	 *
	 * @return	true => The active time record's timer is running.
	 * 			false => otherwise.
	 */
	public boolean isActive()
	{
		return m_tr.getTimer().isRunning();
	}

	/**
	 * Update displays for the current time, duration and total duration
	 * fields when the active time record notifies of a timer tick.
	 */
	public void update(Observable tatr, Object o)
	{
		if (tatr == m_tr)
		{
			// We've got the right clock.
			updateDisplay();
		}
	}
	
	public void startClock()
	{
		startTask();

	}

	/**
	 * Stop the <code>Tk_ActiveTimeRecord m_tr</code> task timer, and update
	 * the <code>m_totalDuration</code> field. Get an xml Document
	 * serialization of the <code>Tk_ActiveTimeRecord m_tr</code> store it
	 * and then setup the clock for the next time recording. 
	 * 
	 */
	public void stopClock()
	{
		stopTask();
		setTotalDuration(addDuration(getDuration()));
		Document time_record = getXMLTimeRecord();
		
		Vector<Document>vn = null;
		if (!getTaskMap().containsKey(getCurrentTaskName()))
		{
			vn = new Vector<Document>();
			putMap(getCurrentTaskName(), vn);
		}
		else
		{
			vn = getEl(getCurrentTaskName());
		}
		vn.add(time_record);
		setUpClock();
	}
	
	/**
	 * Switch to a new task by creating and setting up the title border with
	 * the new task name, and initializing the <code>m_totalDuration</code>
	 * field.
	 * 
	 * @param new_task		The name of the task to switch to.
	 */
	public void switchTasks(String new_task)
	{
		setCurrentTaskName(new_task);
		setTotalDuration(getDuration());
	}
}
