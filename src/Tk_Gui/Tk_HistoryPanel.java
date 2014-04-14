package Tk_Gui;

import java.util.*;
import java.io.*;
import javax.xml.datatype.*;
import java.awt.*;
import javax.swing.*;
import Tk_History.*;
import Tk_Configuration.*;
import Tk_utils.*;

public class Tk_HistoryPanel// extends JPanel
{
	private Tk_TaskCollection 	m_taskCollection 	= null;
	private Tk_TaskHistory 	  	m_taskHistory 		= null;
	private Tk_HistoryGraph		m_historyGraph		= null;
	Vector<String>m_currentTaskNames				= null;
	
	TreeMap<GregorianCalendar, TreeMap<String, Duration>>
		m_durationMap = null;
	Vector<String>m_dates		= null;
	long m_longestSeconds		= 0;
	
	private JScrollPane			m_sPane				= null;
	private JScrollPane			m_legendPane		= null;
	private JPanel 				m_graphPanel 		= null;
	private Tk_GraphLegend		m_graphLegend		= null;		
	
	public Tk_HistoryPanel
	(
		String task_xml_file,
		String task_xsd_file,
		String history_xml_file,
		String history_xsd_file
	)
	{
		super();
		m_taskCollection = new Tk_TaskCollection(task_xml_file, task_xsd_file);
		m_taskHistory = new Tk_TaskHistory(history_xml_file, history_xsd_file);
		m_currentTaskNames = new Vector<String>();
		
		DefaultComboBoxModel<String>tasks = m_taskCollection.getCurrentList();
		
		for (int i = 0; i < tasks.getSize(); i++)
		{
			m_currentTaskNames.add(i, tasks.getElementAt(i));
		}
		
		getDurationMap();
		m_sPane =
				new JScrollPane
				(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
				);
		m_sPane.setBorder(null);
		m_sPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		createGraph();
		m_graphPanel = new JPanel();
		m_graphPanel.setLayout(new BoxLayout(m_graphPanel, BoxLayout.Y_AXIS));
		m_graphPanel.setMinimumSize(new Dimension(800, 400));
		m_graphPanel.setPreferredSize(new Dimension(1000, 600));
		m_graphPanel.setMaximumSize(new Dimension(1200, 800));
		m_graphPanel.setBackground(Color.white);
		
		m_legendPane =
				new JScrollPane
				(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
				);
		m_legendPane.setBorder(BorderFactory.createDashedBorder(null));
		Rectangle r = m_historyGraph.getBounds();
		m_graphLegend = new Tk_GraphLegend(new Rectangle(0,0,r.width, r.height));
		m_graphLegend.setMinimumSize(new Dimension(600, 50));
		m_graphLegend.setPreferredSize(new Dimension(1000, 50));
		m_graphLegend.setMaximumSize(new Dimension(1200, 50));
		m_graphLegend.add(Box.createRigidArea(new Dimension(1000, 1000)));
		m_graphLegend.setBackground(Color.white);
		m_legendPane.setViewportView(m_graphLegend);
		m_graphPanel.add(m_legendPane);
		m_graphPanel.add(Box.createVerticalStrut(20));
		m_graphPanel.add(m_sPane);
	}
	
	public void setupHistoryGraph()
	{
		m_dates = new Vector<String>();
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
		Duration longest_duration = dtf.newDuration((long)0);

		Iterator<GregorianCalendar>gcit = m_durationMap.keySet().iterator();
		while (gcit.hasNext())
		{
			GregorianCalendar gc = gcit.next();
			Date d = gc.getTime();
			String[] date_fields = d.toString().split("\\s");
			
			m_dates.add
			(String.format
				(
					"%s %s %s %s",
					date_fields[0],
					date_fields[1],
					date_fields[2],
					date_fields[date_fields.length - 1]
				)
			);
			TreeMap<String, Duration>tmap = m_durationMap.get(gc);
			Iterator<Duration>dit = tmap.values().iterator();
			while (dit.hasNext())
			{
				Duration next_duration = dit.next();
				int comparison = longest_duration.compare(next_duration); 
				if (comparison == DatatypeConstants.LESSER)
				{
					longest_duration = next_duration;
				}
			}
			
		}
		
		m_longestSeconds = longest_duration.getHours() * 3600
				+
				longest_duration.getMinutes() * 60
				+
				longest_duration.getSeconds();
		
		m_historyGraph = 
			new Tk_HistoryGraph
			(
				Tk_GraphDirection.LeftToRight,
				300,
				0,
				60 * 60 * 12,
				//m_longestSeconds,
				300,
				m_dates,
				3600, //3600,
				15 * 60, // * 60,
				"Task Record Date",
				"Task Time",
				10
			);
		addBars();
	}
	
	public void createGraph()
	{
		setupHistoryGraph();
		m_sPane.setViewportView(m_historyGraph);
	}

	public void addBars()
	{
		Iterator<GregorianCalendar>gcit = m_durationMap.keySet().iterator();
		int i = 0;
		while (gcit.hasNext())
		{
			GregorianCalendar category = gcit.next();
			TreeMap<String, Duration>tmap = m_durationMap.get(category);
			Iterator<String>sit = tmap.keySet().iterator();
			while (sit.hasNext())
			{
				String bar_name = sit.next();
				Duration d = tmap.get(bar_name);
				long seconds = d.getHours() * 3600 + d.getMinutes() * 60; // + d.getSeconds();
				//long minutes = d.getHours() * 60 + d.getMinutes(); // + d.getSeconds();
				m_historyGraph.addBarElement
				(
						String.format
						(
							"%02d:%02d:%02d",
							category.get(Calendar.YEAR),
							category.get(Calendar.MONTH + 1),
							category.get(Calendar.DAY_OF_MONTH)
						),
					bar_name, 
					m_historyGraph.getWidthBuffer() + i * m_historyGraph.getEntryWidth(),
					seconds
				);
			}
		i++;
		}
	}

	/**
	 * setup the m_durationMap TreeMap with a mapping from GregorianCalendars
	 * to a TaskName to duration map.
	 * 
	 * FIXME: We need to check that the end time of a task record is on the
	 * same day as the start time. If not, we should create one duration for
	 * the start day that ends at 12:00 AM, and another for the next day
	 * starting at 12:00AM.
	 */
	private void getDurationMap()
	{
		m_durationMap = 
				new TreeMap<GregorianCalendar, TreeMap<String, Duration>>();
		
		Iterator<String>sit = m_currentTaskNames.iterator();
		while (sit.hasNext())
		{
			String task_name = sit.next();
			TreeMap<GregorianCalendar, Vector<Tk_TimeRecord>> tm =
					m_taskHistory.getTaskMap(task_name);
			if (tm == null)
			{
				continue;
			}
			Iterator<GregorianCalendar>git = tm.keySet().iterator();
			while (git.hasNext())
			{
				GregorianCalendar gc = git.next();
				TreeMap<String, Duration>curr_record = null;
				Duration total_duration = null;
				
				Iterator<Tk_TimeRecord>trit = tm.get(gc).iterator();
				Tk_TimeRecord next_record = null;
				if (trit.hasNext())
				{
					 next_record = trit.next();
					 total_duration = next_record.getCurrentDuration();
					 if (!m_durationMap.containsKey(gc))
					 {
						 curr_record = new TreeMap<String, Duration>();
						 m_durationMap.put(gc, curr_record);
					 }
					 else
					 {
						 curr_record = m_durationMap.get(gc);
					 }
				}
				while (trit.hasNext())
				{
					next_record = trit.next();
					total_duration = total_duration.add(next_record.getCurrentDuration());
				}
				curr_record.put(task_name, total_duration);
			}
		}
	}

	public String getDurationMapString()
	{
		assert(m_durationMap != null);
		Iterator<GregorianCalendar>git = m_durationMap.keySet().iterator();
		String rval = "";
		while (git.hasNext())
		{
			GregorianCalendar gc = git.next();
			TreeMap<String, Duration>t_durations = m_durationMap.get(gc);
			rval += "\n==========================================\n";
			rval += String.format("Duration Records for day %s of month %s %s are:\n",
					gc.get(Calendar.DAY_OF_MONTH),
					gc.get(Calendar.MONTH),
					gc.get(Calendar.YEAR)
			);
			
			Iterator<String>task_it = t_durations.keySet().iterator();
			while (task_it.hasNext())
			{
				String tname = task_it.next();
				rval += String.format("Task Name %s has duration Records:\n", tname);
				Duration d = t_durations.get(tname);
				d = Tk_TimeRecord.normalizeDuration(d);
				String dur = String.format
						(
							"%02d:%02d:%02d",
							d.getHours(),
							d.getMinutes(),
							d.getSeconds()
						);
				rval += String.format("Total Duration(%s)\n", dur);
			}
		}
		return rval;
	}

	public JPanel getGraphPanel()
	{
		return m_graphPanel;
	}

	public static void runPanel()
	{
		String home = System.getProperty("user.home");
		String tk_dir = home.concat(Tk_FileUtil.Tk_XmlDir);
		String task_xml_file = tk_dir.concat(File.separator + "TK_TaskList.xml");
		String task_xsd_file = tk_dir.concat(File.separator + "TK_TaskList.xsd");
		String history_xml_file = tk_dir.concat(File.separator + "TK_History.xml");
		String history_xsd_file = tk_dir.concat(File.separator + "TK_History.xsd");
		System.out.printf("xml file is:\n%s\n", task_xml_file);
		System.out.printf("xsd file is:\n%s\n", task_xsd_file);
		System.out.printf("xml file is:\n%s\n", history_xml_file);
		System.out.printf("xsd file is:\n%s\n", history_xsd_file);
		Tk_HistoryPanel thp = new Tk_HistoryPanel
				(
					task_xml_file,
					task_xsd_file,
					history_xml_file,
					history_xsd_file
				);
		System.out.printf("The width of the graph is: %d \n", thp.m_historyGraph.getWidth());
		JFrame jf = new JFrame();
		jf.setUndecorated(false);
		jf.add(thp.m_graphPanel);
		jf.pack();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					runPanel();
				}
			}
		);
	}

}
