package Tk_utils;

public abstract class Tk_Converter<T1, T2>
{
	public abstract T2 convertValForward(T1 val);
	
	public abstract T1 convertValBack(T2 val);
	
	public String getStringFirst(T1 val){ return null; }
		
	
	public String getStringSecond(T2 val){ return null; }
}

