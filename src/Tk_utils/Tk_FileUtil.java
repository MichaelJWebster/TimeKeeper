package Tk_utils;

import java.util.*;
import java.io.*;

public class Tk_FileUtil
{
	public static String Tk_XmlDir = File.separator + ".TimeKeeper";

	public static Map<String,String>getXmlFiles
	(
		String t_xml,
		String t_xsd,
		String h_xml,
		String h_xsd
	)
	{
		HashMap<String, String>pmap = new HashMap<String, String>();
		pmap.put(t_xml, null);
		pmap.put(t_xsd, null);
		pmap.put(h_xml, null);
		pmap.put(h_xsd, null);
		String home = System.getProperty("user.home");
		String tk_dir = home.concat(Tk_XmlDir);

		// Check for the user.home/.TimeKeeper directory, and create it if it
		// is not there
		File tkd = new File(tk_dir);
		
		if (!tkd.exists())
		{
			if (!tkd.mkdir())
			{
				System.out.printf("Couldn't Create Directory: %s\n", tkd.getAbsoluteFile());
				System.exit(0);
			}
			tkd.setReadable(true, true);
		}

		Iterator<String> it = pmap.keySet().iterator();
		while (it.hasNext())
		{
			String org_string = it.next();
			String target_file = org_string.substring(t_xml.lastIndexOf(File.separator));
			target_file = tk_dir.concat(target_file);
			pmap.put(org_string, target_file);
			File tfile = new File(target_file);
			if (!tfile.exists())
			{
				try
				{
					tfile.createNewFile();
					
					// Now open the template for this file from the resource, and
					// write the contents of that file into the newly created file.
					InputStream is = Tk_FileUtil.class.getResourceAsStream(org_string);
					FileOutputStream os = new FileOutputStream(tfile);
					
					int read = 0;
					byte[]bytes = new byte[1024];
					while ((read = is.read(bytes)) != -1)
					{
						os.write(bytes, 0, read);
					}
						
				}
				catch (IOException e)
				{
					System.out.println("Tk_FileUtil.getXmlFiles IOException!");
					System.out.println(e.getMessage());
					System.exit(0);
				}
				catch (SecurityException e)
				{
					System.out.println("Tk_FileUtil.getXmlFiles SecurityException!");
					System.out.println(e.getMessage());
					System.exit(0);					
				}
			}
		}		
		return pmap;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String TaskXmlTemplate = "/Tk_Configuration/TestData/TK_TaskList.xml";
		String TaskXmlFile = null;
		String TaskXsdTemplate = "/Tk_configuration/TestData/TK_TaskList.xsd";
		String TaskXsdFile = "null";
		String HistoryXmlTemplate = "/Tk_Configuration/TestData/TK_History.xml";
		String HistoryXmlFile = null;
		String HistoryXsdTemplate = "/Tk_Configuration/TestData/TK_History.xsd";
		String HistoryXsdFile = null;
		
		Map<String, String>fmap =
				getXmlFiles
				(
					TaskXmlTemplate,
					TaskXsdTemplate,
					HistoryXmlTemplate,
					HistoryXsdTemplate
				);
		
		Iterator<String> it = fmap.keySet().iterator();
		while (it.hasNext())
		{
			String c_string = it.next();
			File c_file = new File(fmap.get(c_string));
			System.out.printf("Deleting file: %s\n", c_file.getAbsoluteFile());
			//c_file.delete();
		}
		File f = new File(System.getProperty("user.home").concat(Tk_XmlDir));
		//f.delete();
	}

}
