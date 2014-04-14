package Tk_utils;

import java.util.Properties;

public class ListProperties {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Properties p = System.getProperties();
		
		System.out.println("Printing Property List:");
		p.list(System.out);
		
		System.out.println("<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>");
		String ud = System.getProperty("user.home");
		System.out.println("User directory is:");
		System.out.println(ud);
	}

}
