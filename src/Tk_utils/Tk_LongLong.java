package Tk_utils;

public class Tk_LongLong extends Tk_Converter<Long, Long>
{

	@Override
	public Long convertValForward(Long val)
	{
		return val;
	}

	@Override
	public Long convertValBack(Long val)
	{
		return val;
	}


	@Override
	public String getStringFirst(Long val)
	{
		return String.format("%d", val);
	}

	@Override
	public String getStringSecond(Long val)
	{
		return String.format("%d", val);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
	
	}

}
