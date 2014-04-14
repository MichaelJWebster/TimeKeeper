package Tk_History;

import java.util.*;
import org.w3c.dom.*;
import javax.xml.datatype.*;
import Tk_History.Tk_TaskHistory;
import Tk_utils.Tk_XmlUtil;

/**
 * Tk_TimeRecord represents the start time, end time and duration of a task.
 *
 * @author michaelwebster
 */
public class Tk_TimeRecord extends Observable
{
	/**
	 * Time the Start Task button was last pressed.
	 */
	protected XMLGregorianCalendar m_start		= null;
	
	/**
	 * The current time, or if the Stop Task button has been pressed, the end
	 * time.
	 */
	protected XMLGregorianCalendar m_end		= null;
	
	/**
	 * The Duration between m_start and m_end.
	 */
	protected Duration m_duration				= null;
	
	/**
	 * The XML DOM element that represents this current time record once it
	 * has completed.
	 */
	private Document m_doc						= null;



	/**
	 * Create a new empty <code>Tk_TimeRecord</code>
	 */
	public Tk_TimeRecord()
	{
		// Do nothing.
	}

	/**
	 * Create the Tk_Time Record with millisecond values for the start and
	 * end times of the Time Record. The duration is given as e - s.
	 * 
	 * @param s	Start Time in ms since the epoch.
	 * @param e End Time in ms since the epoch.
	 * @throws IllegalArgumentException
	 */
	public Tk_TimeRecord(long s, long e)
			throws IllegalArgumentException
	{
		this(s, e, e - s);
	}

	/**
	 * Create a Tk_TimeRecord with millisecond values for the start and end
	 * times, and the duration.
	 * 
	 * Throw Illegal argument exception if d != (e - s).
	 *
	 * @param s	Start Time in ms since the epoch.
	 * @param e End Time in ms since the epoch.
	 * @param d Millisecond value for duration - must equal (e - s).
	 * @throws IllegalArgumentException
	 */
	public Tk_TimeRecord(long s, long e, long d) 
			throws IllegalArgumentException
	{
		if (d != (e -s))
		{
			String err_string = null;
			err_string = String.format
				(
					"Error in %s.\n d(%d) must equal e(%d) - s(%d)\n",
					"Tk_TimeRecord(long s, long e, long d)",
					d,
					e,
					s
				);
			IllegalArgumentException err = new IllegalArgumentException(err_string);
			throw(err);
		}
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

		// Create a Gregorian calendar to set the dates in the
		// XMLGregorianCalendars.
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(s);
		m_start = dtf.newXMLGregorianCalendar(gc);
		
		gc.setTimeInMillis(e);
		m_end = dtf.newXMLGregorianCalendar(gc);
		
		m_duration = dtf.newDuration(d);
	}

	/**
	 * Create a Tk_TimeRecord from xml lexicial dateTime format strings and
	 * an xml lexical duration string.
	 *
	 * @param s xml lexical dateTime string for start time.
	 * @param e xml lexical dateTime string for start time.
	 * @param d xml lexical duration string for duration.
	 */
	public Tk_TimeRecord(String s, String e, String d)
	{
		setFromStrings(s, e, d);
	}
	
	/**
	 * Create a Tk_TimeRecord from a DOM TimeRecord node. If the supplied node
	 * is not a TK_TimeRecord node, throw an IllegalArgumentException.
	 *
	 * @param n	A TK_TimeRecord DOM node.
	 * @throws IllegalArgumentException
	 */
	public Tk_TimeRecord(Node n) throws IllegalArgumentException
	{
		String lname = n.getNodeName();		//n.getLocalName();
		if (!lname.equals(Tk_TaskHistory.TK_TimeRecord))
		{
			String e_string = new String("");
			e_string = "Error: Tk_TimeRecrod(Node n) constructor.\n";
			e_string += "Expected a " + Tk_TaskHistory.TK_TimeRecord;
			e_string += " but got a: " + lname + " element\n";
			IllegalArgumentException e =
				new IllegalArgumentException
				(
					e_string
				);
			throw(e);
		}
		
		String s = Tk_utils.Tk_XmlUtil.getSimpleElementText
				(
					(Element)n,
					Tk_TaskHistory.TK_StartTime
				);
		String e = Tk_utils.Tk_XmlUtil.getSimpleElementText
				(
					(Element)n,
					Tk_TaskHistory.TK_EndTime
				);
		String d = Tk_utils.Tk_XmlUtil.getSimpleElementText
				(
					(Element)n,
					Tk_TaskHistory.TK_ElapsedTime
				);
		
		setFromStrings(s, e, d);
	}

	/**
	 * Serialize the current time record to an xml element, and return it.
	 *
	 * @return A DOM TK_TimeRecord element.
	 */
	protected void serializeToDocument()
	{
		m_doc = Tk_XmlUtil.createEmptyDom();
		Element root = m_doc.createElement(Tk_TaskHistory.TK_TimeRecord);
		m_doc.appendChild(root);
		Element start_time = m_doc.createElement(Tk_TaskHistory.TK_StartTime);
		start_time.appendChild(m_doc.createTextNode(getXMLStartTime()));
		Element end_time = m_doc.createElement(Tk_TaskHistory.TK_EndTime);
		end_time.appendChild(m_doc.createTextNode(getXMLEndTime()));
		Element elapsed_time = m_doc.createElement(Tk_TaskHistory.TK_ElapsedTime);
		elapsed_time.appendChild(m_doc.createTextNode(getXMLDuration()));
		root.appendChild(start_time);
		root.appendChild(end_time);
		root.appendChild(elapsed_time);
	}
	
	/**
	 * Return the xml DOM element created to record thie completed time record.
	 * @return
	 */
	public Document getXMLDocument()
	{
		return m_doc;
	}

	/**
	 * Set the Tk_TimeRecord start, end and duration fields from xml lexical
	 * strings s, e, and d respectively.
	 *
	 * @param s xml lexical timeDate string for the task start time.
	 * @param e xml lexical timeDate string for the end time.
	 * @param d xml lexical duration string for the task duration.
	 */
	public void setFromStrings(String s, String e, String d)
	{
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

		m_start = dtf.newXMLGregorianCalendar(s);
		m_end = dtf.newXMLGregorianCalendar(e);
		m_duration = dtf.newDuration(d);		
	}

	/**
	 * Set the value of the m_start field from the supplied ms value.
	 *
	 * @param ms 	The number of milliseconds from the epoch to the start time
	 * 				of this task.
	 */
	protected void setStartTime(long ms)
	{
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(ms);
		m_start.setTime
		(
			gc.get(Calendar.HOUR_OF_DAY),
			gc.get(Calendar.MINUTE),
			gc.get(Calendar.SECOND),
			gc.get(Calendar.MILLISECOND)
		);
		m_start.setDay(gc.get(Calendar.DAY_OF_MONTH));
		m_start.setMonth(gc.get(Calendar.MONTH) + 1);
		m_start.setYear(gc.get(Calendar.YEAR));
	}

	/**
	 * Set the end time or current time for this task. As a side effect, also
	 * set the duration of this task as the difference between the start and
	 * end times.
	 *
	 * @param ms	The number of since the epoch until now, or until the task
	 * 				was stopped.
	 */
	protected void setEndTime(long ms)
	{
		GregorianCalendar gc_end = new GregorianCalendar();
		gc_end.setTimeInMillis(ms);
		m_end.setTime
		(
			gc_end.get(Calendar.HOUR_OF_DAY),
			gc_end.get(Calendar.MINUTE),
			gc_end.get(Calendar.SECOND),
			gc_end.get(Calendar.MILLISECOND)
		);
		m_end.setDay(gc_end.get(Calendar.DAY_OF_MONTH));
		m_end.setMonth(gc_end.get(Calendar.MONTH) + 1);
		m_end.setYear(gc_end.get(Calendar.YEAR));
		
		DatatypeFactory dtf = null;
		try
		{
			dtf = DatatypeFactory.newInstance();
		}
		catch (DatatypeConfigurationException err)
		{
			System.out.println
			(
				"Error: Tk_TimeRecord.setEndTime(long)"
			);
			System.out.println("Caught Exception:");
			System.out.println(err.getMessage());
			System.exit(-1);
		}
		GregorianCalendar gc_start = m_start.toGregorianCalendar();
		m_duration = dtf.newDuration(gc_end.getTimeInMillis() - gc_start.getTimeInMillis());
	}

	/**
	 * Add the supplied millisecond value to the duration.
	 *
	 * @param ms	A millisecond value indicating a duration.
	 */
	protected void addToDuration(long ms)
	{
		DatatypeFactory dtf = null;
		try
		{
			dtf = DatatypeFactory.newInstance();
		}
		catch (DatatypeConfigurationException err)
		{
			System.out.println
			(
				"Error: Tk_TimeRecord.setEndTime(long)"
			);
			System.out.println("Caught Exception:");
			System.out.println(err.getMessage());
			System.exit(-1);
		}
		Duration d = dtf.newDuration(ms);
		m_duration = m_duration.add(d);
		m_end.add(d);
	}

	/**
	 * Return a human readable string for this task's start time.
	 *
	 * @return A human readable string for the start time.
	 */
	public String getStartTime()
	{
		GregorianCalendar cal = m_start.toGregorianCalendar();
		return cal.getTime().toString();
	}
	
	/**
	 * Return the Start time as a lexical xml dateTime string.
	 * 
	 * @return	lexical xml dateTime string for the task's start time.
	 */
	public String getXMLStartTime()
	{
		return m_start.toXMLFormat();
	}
	
	/**
	 * Return the Date this task started.
	 *
	 * @return	The gregorian calendar representation of the start time of
	 * 			the task, with all the hours, minutes and seconds zeroed.
	 */
	public GregorianCalendar getStartDate()
	{
		GregorianCalendar g = m_start.toGregorianCalendar();
		g.clear(Calendar.HOUR_OF_DAY);
		g.clear(Calendar.MINUTE);
		g.clear(Calendar.SECOND);
		g.clear(Calendar.HOUR);
		g.clear(Calendar.MILLISECOND);
		return g;
	}
	
	/**
	 * Return a human readable string for this task's end time.
	 *
	 * @return A human readable string for the end time.
	 */
	public String getEndTime()
	{
		GregorianCalendar cal = m_end.toGregorianCalendar();
		return cal.getTime().toString();
	}

	/**
	 * Return the end time as a lexical xml dateTime string.
	 * 
	 * @return	lexical xml dateTime string for the task's end time.
	 */
	public String getXMLEndTime()
	{
		return m_end.toXMLFormat();
	}

	/**
	 * Return the Date this task ended.
	 *
	 * @return	The gregorian calendar representation of the end time of
	 * 			the task, with all the hours, minutes and seconds zeroed.
	 */
	public GregorianCalendar getEndDate()
	{
		GregorianCalendar g = m_end.toGregorianCalendar();
		g.clear(GregorianCalendar.HOUR_OF_DAY);
		g.clear(GregorianCalendar.MINUTE);
		g.clear(GregorianCalendar.SECOND);
		g.clear(Calendar.HOUR);
		g.clear(Calendar.MILLISECOND);
		return g;
	}

	/**
	 * Return a human readable duration string.
	 * 
	 * @return Human readable duration string.
	 */
	public String getDuration()
	{
		String dur = String.format
		(
			"%02d:%02d:%02d",
			m_duration.getHours(),
			m_duration.getMinutes(),
			m_duration.getSeconds()
		);
		return dur;
	}


	/**
	 * Return the duration as a lexical xml duration string.
	 * 
	 * @return	lexical xml duration string for the task's duration.
	 */
	public String getXMLDuration()
	{
		return m_duration.toString();
	}
	
	/**
	 * Return the m_duration field.
	 *
	 * @return	the m_duration Duration attribute.
	 */
	public Duration getCurrentDuration()
	{
		return m_duration;
	}
	
	public static Duration normalizeDuration(Duration d)
	{
		int secs = d.getSeconds();
		int mins = d.getMinutes();
		int hours = d.getHours();
			
		int final_secs =  secs % 60;
		int extra_min = secs / 60;
		int tm = mins + extra_min;
		int final_mins = tm % 60;
		int extra_hour = tm / 60;
		int final_hours = hours + extra_hour;
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
		return dtf.newDuration(true, 0, 0, 0, final_hours, final_mins, final_secs);

	}

	/**
	 * Print the timeRecord to stdout. For debugging.
	 */
	public void printTimeRecord()
	{
		System.out.printf("Start Time = %s\n", getStartTime());
		System.out.printf("End Time = %s\n", getEndTime());
		System.out.printf("Duration = %s\n", getDuration());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		GregorianCalendar cal1 = new GregorianCalendar(); //Calendar.getInstance();
		cal1.set(2015, 11, 15, 18, 01, 33);
		GregorianCalendar cal2 = new GregorianCalendar(); //Calendar.getInstance();
		cal2.set(2016, 01, 15, 12, 30, 45);
		Tk_TimeRecord tr = new Tk_TimeRecord
			(
				cal1.getTimeInMillis(),
				cal2.getTimeInMillis(),
				cal2.getTimeInMillis() - cal1.getTimeInMillis()
			);
		
		System.out.println("TimerRecord.");
		System.out.printf("StartTime = %s\n", tr.getStartTime());
		System.out.printf("EndTime = %s\n", tr.getEndTime());
		System.out.printf("Duration = %s\n", tr.getDuration());
		
		Tk_TimeRecord tr1 = new Tk_TimeRecord
				(
					cal1.getTimeInMillis(),
					cal2.getTimeInMillis()
				);
			
		System.out.println("TimerRecord.");
		System.out.printf("StartTime = %s\n", tr1.getStartTime());
		System.out.printf("EndTime = %s\n", tr1.getEndTime());
		System.out.printf("Duration = %s\n", tr1.getDuration());
		
		System.out.println("TimerRecord in XML format.");
		System.out.printf("StartTime = %s\n", tr1.getXMLStartTime());
		System.out.printf("EndTime = %s\n", tr1.getXMLEndTime());
		System.out.printf("Duration = %s\n", tr1.getXMLDuration());
		
		Tk_TimeRecord tr2 = new Tk_TimeRecord
				(
					tr1.getXMLStartTime(),
					tr1.getXMLEndTime(),
					tr1.getXMLDuration()
				);
		System.out.println("TimerRecord from lexical strings.");
		System.out.printf("StartTime = %s\n", tr2.getStartTime());
		System.out.printf("EndTime = %s\n", tr2.getEndTime());
		System.out.printf("Duration = %s\n", tr2.getDuration());
		
		System.out.println("TimerRecord from lexical strings in XML format.");
		System.out.printf("StartTime = %s\n", tr2.getXMLStartTime());
		System.out.printf("EndTime = %s\n", tr2.getXMLEndTime());
		System.out.printf("Duration = %s\n", tr2.getXMLDuration());
		
		GregorianCalendar gc = tr2.getStartDate();
		System.out.printf("Start DATE is: %s %s %s : %s:%s:%s\n",
				gc.get(Calendar.YEAR),
				gc.get(Calendar.MONTH),
				gc.get(Calendar.DAY_OF_MONTH),
				gc.get(Calendar.HOUR),
				gc.get(Calendar.MINUTE),
				gc.get(Calendar.SECOND)
				);
	}

}
