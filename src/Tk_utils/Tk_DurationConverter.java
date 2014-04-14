package Tk_utils;

import javax.xml.datatype.*;

import java.util.Date;

/**
 * For a conversion from Duration to Long, we'll simply return the number of
 * seconds represented by the duration, and convert a seconds number into
 * a Duration.
 *
 * @author michaelwebster
 *
 */
public class Tk_DurationConverter extends Tk_Converter<Duration, Long>
{
	/**
	 * Convert from a Duration to a value in seconds to the closest minute.
	 */
	@Override
	public Long convertValForward(Duration val)
	{
		long seconds = val.getTimeInMillis(new Date()) / 1000;
		seconds = seconds - (seconds % 60);
		return seconds;
	}

	/**
	 * Convert from a value in seconds to a duration to the nearest minute.
	 */
	@Override
	public Duration convertValBack(Long val)
	{
		long seconds = val - val % 60;
		DatatypeFactory dtf = null;
		try
		{
			dtf = DatatypeFactory.newInstance();
		}
		catch (DatatypeConfigurationException err)
		{
			System.out.println
			(
				"Error: Tk_DurationConverter(long)."
			);
			System.out.println("Caught Exception:");
			System.out.println(err.getMessage());
			System.exit(-1);
		}
		return dtf.newDuration(1000 * seconds);
	}
	
	

	@Override
	public String getStringFirst(Duration val)
	{
		String dur = String.format
				(
					"%02d:%02d",
					val.getHours(),
					val.getMinutes()
					//val.getSeconds()
				);
		return dur;
	}

	@Override
	public String getStringSecond(Long val)
	{
		long hours = val / 3600;
		long mins = (val % 3600) / 60;
				
		return String.format("%02d:%02d", hours, mins);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Tk_DurationConverter tdc = new Tk_DurationConverter();
		
		Duration d1 = tdc.convertValBack((long)1);
		String s = tdc.getStringFirst(d1);
		System.out.printf("1 second converted to a duration is:\n%s\n", s);
		
		Duration d2 = tdc.convertValBack((long)60);
		s = tdc.getStringFirst(d2);
		System.out.printf("60 second converted to a duration is:\n%s\n", s);
		
		Duration d3 = tdc.convertValBack((long)60*60);
		s = tdc.getStringFirst(d3);
		System.out.printf("1 hour second converted to a duration is:\n%s\n", s);
		
		Duration d4 = tdc.convertValBack((long)(60*60 * 3 + 60 * 33 + 45));
		s = tdc.getStringFirst(d4);
		System.out.printf("3hrs, 33 minutes and 45 seconds converted to a duration is:\n%s\n", s);
		
		System.out.println("Now convert back longs....");
		long l = tdc.convertValForward(d1);
		s = tdc.getStringSecond(l);
		System.out.printf("1 second converted back to long is:\n%s\n", s);
		
		l = tdc.convertValForward(d2);
		s = tdc.getStringSecond(l);
		System.out.printf("60 second converted back to long is:\n%s\n", s);
		
		l = tdc.convertValForward(d3);
		s = tdc.getStringSecond(l);
		System.out.printf("1 hour converted to a long is:\n%s\n", s);
		
		l = tdc.convertValForward(d4);
		s = tdc.getStringSecond(l);
		System.out.printf("3 hours 33 minutes and 45 seconds converted to a long is:\n%s\n", s);
	}

}
