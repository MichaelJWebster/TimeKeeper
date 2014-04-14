package Tk_utils;
import javax.xml.datatype.*;
import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;


public class TestDateStrings
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		Date d = new Date();
		System.out.println(d.toString());
		GregorianCalendar g = new GregorianCalendar(d.getYear(), d.getMonth(), d.getDay());
		System.out.println(g.getTime().toGMTString());
		System.out.println(g.getTime().toLocaleString());
		System.out.println(g.getTime().toString());
		
		//String regex = new String("\\s");
		String[]splits = d.toString().split("\\s");
		for (int i = 0; i < splits.length; i++)
		{
			System.out.printf("splits[%s] is:%s\n", i, splits[i]);
		}
		
		JLabel jl = new JLabel();
		Graphics gr = jl.getGraphics();
		if (gr == null)
		{
			System.out.println("Graphics is null");
		}
		else
		{
			System.out.println("Got a grphics object.");
		}
		
		jl.setVisible(true);
		gr = jl.getGraphics();
		if (gr == null)
		{
			System.out.println("Set label visible: Graphics is null");
		}
		else
		{
			System.out.println("Set Label visible: Got a grphics object.");
		}
		
		JPanel jp = new JPanel();
		jp.add(jl);
		gr = jl.getGraphics();
		if (gr == null)
		{
			System.out.println("Graphics is null");
		}
		else
		{
			System.out.println("Got a grphics object.");
		}
		jp.setVisible(true);
		gr = jl.getGraphics();
		if (gr == null)
		{
			System.out.println("Graphics is null");
		}
		else
		{
			System.out.println("Got a grphics object.");
		}
		
	}

}
