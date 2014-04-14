package Tk_Configuration;

public enum Tk_ClockType
{
	Unknown("Unknown"),
	Digital("Digital"),
	Analog("Analog");
	
	private Tk_ClockType(String ctype)
	{
	}

	public static Tk_ClockType getClockType(String clock_string)
	{
		if (clock_string.equals(Digital.name()))
		{
			return Digital;
		}
		else if (clock_string.equals(Analog.name()))
		{
			return Analog;
		}
		else
		{
			return Unknown;
		}
	}

	public static void main(String[] args)
	{
		for (Tk_ClockType c: Tk_ClockType.values())
		{
			System.out.printf("ClockType %s has ordinal %d\n",
					c.name(), c.ordinal());
		}
		
		Tk_ClockType tc = Tk_ClockType.getClockType("Digital");
		System.out.println("Digital gets you " + tc.name());
		tc = Tk_ClockType.getClockType("Analog");
		System.out.println("Analog gets you " + tc.name());
		tc = Tk_ClockType.getClockType("Blibble");
		System.out.println("Blibble gets you " + tc.name());
		
	}
}
