package Tk_History;

import java.util.*;
import java.net.*;
import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import Tk_utils.Tk_FileUtil;
import Tk_utils.Tk_XmlUtil;
import javax.xml.parsers.*;
import javax.xml.validation.*;
import javax.xml.datatype.*;
import javax.swing.*;
import Tk_Configuration.Tk_TaskCollection;
import Tk_Configuration.Tk_TaskListModel;

/**
 * Tk_TaskHistory represents
 * @author michaelwebster
 *
 */
public class Tk_TaskHistory
{
	/*
	 * Static xml element strings for a TK_History.xsd format file.
	 */
	private final static String TK_History 			= "TK_History";
	private final static String TK_Task				= "TK_Task";
	public  final static String TK_TimeRecord		= "TK_TimeRecord";
	public  final static String TK_StartTime		= "TK_StartTime";
	public	final static String TK_EndTime			= "TK_EndTime";
	public  final static String TK_ElapsedTime		= "TK_ElapsedTime";
	
	/*
	 * Static xml attribute strings for a TK_History.xsd format file.
	 */
	public 	final static String TK_TaskName 		= "name";
	public 	final static String TK_TotalTime		= "TK_TotalTime";
	
	/**
	 * A map from task names to lists of xml document time records for those
	 * tasks.
	 */
	private HashMap<String, Vector<Document>>m_taskHistory = null;
	
	/*
	 * Instance attributes.
	 */
	private String 				m_xmlFileName			= null;
	private String				m_schemaFileName		= null;
	private Document			m_doc					= null;

	public Tk_TaskHistory(String xml_fname, String xsd_fname)
	{
		/*
		 * Create the URI for the xml task list from the supplied uri_string.
		 */
		m_xmlFileName = xml_fname;
		m_schemaFileName = xsd_fname;
		m_doc = Tk_XmlUtil.createDocument(m_xmlFileName, m_schemaFileName);
		
		m_taskHistory = readHistory();
		//printTaskHistory();
	}

	/**
	 * Read in the task history from the TK_History.xml file, and store it in
	 * a HashMap map.
	 *
	 * @return The HashMap mapping between task names and lists of documents.
	 */
	public HashMap<String, Vector<Document>>readHistory()
	{
		HashMap<String, Vector<Document>> hist = null;
		hist = new HashMap<String, Vector<Document>>();
		assert(m_doc != null);
		Element el = m_doc.getDocumentElement();

		if (null != el)
		{
			NodeList task_nodes = el.getElementsByTagName(TK_Task);
			for (int i = 0; i < task_nodes.getLength(); i++)
			{
				Element task = (Element)task_nodes.item(i);
				String task_name = task.getAttribute(TK_TaskName);
				Vector<Document>vd = null;
				if (!hist.containsKey(task_name))
				{
					vd = new Vector<Document>();
					hist.put(task_name, vd);
				}
				else
				{
					vd = hist.get(task_name);
				}
				
				Tk_TimeRecord tr = new Tk_TimeRecord();				
				NodeList record_nodes = task.getElementsByTagName(TK_TimeRecord);
				for (int j = 0; j < record_nodes.getLength(); j++)
				{
					Element record_el = (Element)record_nodes.item(j);
					Element elt = Tk_XmlUtil.getFirstElement(record_el, TK_StartTime);
					String start_time = Tk_XmlUtil.getSimpleElementText(elt);
					elt = Tk_XmlUtil.getFirstElement(record_el, TK_EndTime);
					String end_time = Tk_XmlUtil.getSimpleElementText(elt);
					elt = Tk_XmlUtil.getFirstElement(record_el, TK_ElapsedTime);
					String duration = Tk_XmlUtil.getSimpleElementText(elt);
					tr.setFromStrings(start_time, end_time, duration);
					tr.serializeToDocument();
					Document tr_doc = tr.getXMLDocument();
					vd.add(tr_doc);
				}
			}
		}
		return hist;
	}

	/**
	 * Rewrite the Task list into the DOM, and write the DOM back to the
	 * TK_History.xml file.
	 */
	public void updateDom()
	{
		Document doc = Tk_XmlUtil.createEmptyDom(m_schemaFileName);

		Node root = doc.createElement(TK_History);
		doc.appendChild(root);
		addElements(doc, root, m_taskHistory);
		m_doc = doc;
		writeBackDom();
	}
	
	/**
	 * Add each of the entries in the HashMap to the DOM in doc, rooted at
	 * "root".
	 *
	 * @param doc		The DOM document to be filled out.
	 * @param root		The TK_History element root of the DOM document.
	 * @param docs		A hash map of the records to be poked into the DOM.
	 */
	private void addElements
	(
		Document doc,
		Node root,
		HashMap<String, Vector<Document>>docs
	)
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

		Iterator<String>it = docs.keySet().iterator();
		while (it.hasNext())
		{
			String tname = it.next();
			Element ctask = doc.createElement(TK_Task);
			root.appendChild(ctask);
			ctask.setAttribute(TK_TaskName, tname);
			Iterator <Document>d_it = docs.get(tname).iterator();
			Duration duration = dtf.newDuration(0);
			while (d_it.hasNext())
			{
				Document d = d_it.next();
				Element rec_el = d.getDocumentElement();
				Element start_time = Tk_XmlUtil.getFirstElement(rec_el, TK_StartTime);
				String st = Tk_XmlUtil.getSimpleElementText(start_time);
				Element end_time = Tk_XmlUtil.getFirstElement(rec_el, TK_EndTime);
				String et = Tk_XmlUtil.getSimpleElementText(end_time);
				Element dur_el = Tk_XmlUtil.getFirstElement(rec_el, TK_ElapsedTime);
				String dur_string = Tk_XmlUtil.getSimpleElementText(dur_el);
				duration = duration.add(dtf.newDuration(dur_string));
				Element trec = doc.createElement(TK_TimeRecord);
				ctask.appendChild(trec);
				Element s_rec = doc.createElement(TK_StartTime);
				trec.appendChild(s_rec);
				s_rec.appendChild(doc.createTextNode(st));
				Element e_rec = doc.createElement(TK_EndTime);
				trec.appendChild(e_rec);
				e_rec.appendChild(doc.createTextNode(et));
				Element d_rec = doc.createElement(TK_ElapsedTime);
				trec.appendChild(d_rec);
				d_rec.appendChild(doc.createTextNode(dur_string));
			}
			ctask.setAttribute(TK_TotalTime, duration.toString());
		}
	}

	/**
	 * Write the DOM back to a temporary file.
	 */
	private void writeBackDomTemp()
	{
		int last_slash = m_xmlFileName.lastIndexOf('/');
		String xml_path = m_xmlFileName.substring(0, last_slash);
		try
		{
			File xml_dir = new File(xml_path);
			File xml_file = File.createTempFile("TK_History", ".xml", xml_dir);
			FileWriter fw = new FileWriter(xml_file);
			fw.write(Tk_XmlUtil.formatDoc(m_doc));
			fw.close();
		}
		catch (IOException e)
		{
			System.out.println("Tk_TaskHistory: writeBackDom IOException.");
			System.out.println(e.getMessage());
			System.exit(-1);
		}
	}
	
	/**
	 * Write the DOM back to the task list xml file. 
	 */
	private void writeBackDom()
	{
		try
		{
			File xml_file = new File(m_xmlFileName);
			FileWriter fw = new FileWriter(xml_file);
			fw.write(Tk_XmlUtil.formatDoc(m_doc));
			fw.close();
		}
		catch (IOException e)
		{
			System.out.println("Tk_TaskHistory: writeBackDom IOException.");
			System.out.println(e.getMessage());
			System.exit(-1);
		}
	}
	
	/**
	 * Get the task history for the given task name.
	 *
	 * @param task_name		The name of the task we want to extract history
	 * 						for.
	 *
	 * @return 	A Vector of dom documents representing the history of this
	 * 			task.
	 */
	private Vector<Document>getTaskHistory(String task_name)
	{
		assert(this.m_taskHistory != null);
		if (m_taskHistory.containsKey(task_name))
		{
			return m_taskHistory.get(task_name);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Return a sorted map (TreeMap) between dates and vectors of time records
	 * for tasks that have been performed on those dates.
	 *
	 * @param task_name	The name of the task to be processed.
	 *
	 * @return	A sorted map from GregorianCalendar representations of days
	 * 			that a task has run, to a Vector of times for each of those
	 * 			days.
	 */
	public TreeMap<GregorianCalendar, Vector<Tk_TimeRecord>>getTaskMap(String task_name)
	{
		TreeMap<GregorianCalendar, Vector<Tk_TimeRecord>> tm =
			new TreeMap<GregorianCalendar, Vector<Tk_TimeRecord>>();
		
		Vector<Document> vd = getTaskHistory(task_name);
		if (vd == null)
		{
			return null;
		}
		
		Iterator<Document>dit = vd.iterator();
		while (dit.hasNext())
		{
			Document d = dit.next();
			Element el = d.getDocumentElement();
			Tk_TimeRecord nr = new Tk_TimeRecord(el);
			GregorianCalendar gc = nr.getStartDate();
			if (!tm.containsKey(gc))
			{
				tm.put(gc, new Vector<Tk_TimeRecord>());
			}
			tm.get(gc).add(nr);
		}
		return tm;
	}

	/**
	 * Get the task history.
	 *
	 * @return The HashMap containing the task history.
	 */
	public HashMap<String, Vector<Document>> getHistory()
	{
		assert(this.m_taskHistory != null);
		return m_taskHistory;
	}
	
	/**
	 * Add the document doc into a vector in the m_taskHistory map with the
	 * key given by task_name.
	 * 
	 * @param task_name		Key for recording this time record.
	 * @param doc			The document to be recorded under key = task_name.
	 */
	public void addTimeRecord(String task_name, Document doc)
	{
		Vector<Document>vd = null;
		if 
		(
			!m_taskHistory.containsKey(task_name)
			||
			m_taskHistory.get(task_name) == null
		)
		{
			vd = new Vector<Document>();
			m_taskHistory.put(task_name, vd);
		}
		else
		{
			vd = m_taskHistory.get(task_name);
		}
		vd.add(doc);
	}

	/**
	 * Just print the task history.
	 */
	public void printTaskHistory()
	{
		Iterator<String>it = m_taskHistory.keySet().iterator();
		while (it.hasNext())
		{
			String tname = it.next();
			Vector<Document>docs = m_taskHistory.get(tname);
			System.out.printf
			(
				"\n\n<<<<<<<<<<<<<<< Task = (%s) >>>>>>>>>>>>\n",
				tname
			);
			for (int i = 0; i < docs.size(); i++)
			{
				Document cd = docs.elementAt(i);
				String trec = Tk_XmlUtil.formatDoc(cd);
				System.out.println("Xml our from serialize to node is:");
				System.out.printf("%s", trec);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String home = System.getProperty("user.home");
		String tk_dir = home.concat(Tk_FileUtil.Tk_XmlDir);
		String xml_file = tk_dir.concat(File.separator + "TK_History.xml");
		String xsd_file = tk_dir.concat(File.separator + "TK_History.xsd");
		System.out.printf("xml file is:\n%s\n", xml_file);
		System.out.printf("xsdl file is:\n%s\n", xml_file);
		Tk_TaskHistory th = new Tk_TaskHistory(xml_file, xsd_file);
		//th.readCurrentHistory();
		System.out.println("\n\n<<<<<<<<<<<<<<<<< Task History >>>>>>>>>>>>>>>>>>>>>\n\n");
		th.printTaskHistory();
		
		String[] tasks =
			{
				"Job Search",
				"Java FX for time keeper visualisation",
				"Sleeping",
				"Sudoku creater",
				"Bar graph widget",
				"watching tv",
				"Ios 7 lectures CS 193p"
			};
		
		for (int i = 0; i < tasks.length; i++)
		{
			TreeMap<GregorianCalendar, Vector<Tk_TimeRecord>>gt = 
				th.getTaskMap(tasks[i]);
			System.out.printf
			(
				"Times by day for task ========== %s ===========\n",
				tasks[i]
			);
			
			Iterator<GregorianCalendar>gcit = gt.keySet().iterator();
			while (gcit.hasNext())
			{
				GregorianCalendar gc = gcit.next();
				System.out.printf("Tasks for %s on day = %d %d %d\n", 
						tasks[i],
						gc.get(Calendar.YEAR),
						gc.get(Calendar.MONTH),
						gc.get(Calendar.DAY_OF_MONTH)
				);
				Vector<Tk_TimeRecord>ttr = gt.get(gc);				
				Iterator<Tk_TimeRecord>trit = ttr.iterator();
				while (trit.hasNext())
				{
					Tk_TimeRecord tktr = trit.next();
					System.out.printf("StartTime(%s) EndTime(%s)\n",
							tktr.getStartTime(),
							tktr.getEndTime()
							);
				}
			}
			
		}
	}

}
