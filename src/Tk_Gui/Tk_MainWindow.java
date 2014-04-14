package Tk_Gui;

import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.*;
import org.w3c.dom.*;
import javax.swing.*;
import Tk_TaskSelectPanel.*;
import Tk_History.Tk_TaskHistory;
import Tk_utils.Tk_FileUtil;

/**
 * The main window for the TimeKeeper. This window contains and manages
 * the Tk_StartDisplay and Tk_TaskSelect panels, and handles the closing
 * of the application via the window close control, and the End button.
 *
 * @author michaelwebster
 *
 */
public class Tk_MainWindow extends JFrame implements ActionListener
{

	/**
	 * Location of the <code>TK_TaskList.xml</code> file template within the
	 * resources of this program.
	 */
	private static final String TaskXmlTemplate =
			"/Tk_Configuration/TestData/TK_TaskList.xml";
	
	/**
	 * Location of the <code>TK_TaskList.xsd</code> file template within the
	 * resources of this program.
	 */
	private static final String TaskXsdTemplate =
			"/Tk_Configuration/TestData/TK_TaskList.xsd";

	/**
	 * Location of the <code>TK_History.xml</code> file template within the
	 * resources of the program.
	 */
	public static final String HistoryXmlTemplate =
			"/Tk_Configuration/TestData/TK_History.xml";

	/**
	 * Location of the TK_History.xsd file template within this programs
	 * resources.
	 */
	public static final String HistoryXsdTemplate =
			"/Tk_Configuration/TestData/TK_History.xsd";

	/**
	 * Location of the <code>TK_TaskList.xml</code> file.
	 */
	private String m_taskXmlFile = null;

	/**
	 * Location of the <code>TK_TaskList.xsd</code> file.
	 */
	private String m_taskXsdFile = "null";

	/**
	 * Location of the <code>TK_History.xml</code> file.
	 */
	private String m_historyXmlFile = null;
	
	/**
	 * Location of the TK_History.xsd file.
	 */
	private String m_historyXsdFile = null;

	/**
	 * Dummy task name for when the TaskList is empty.
	 */
	public static final String NoTask			= "NoTask";
	
	/**
	 * Action command for the button that ends the time keeper session.
	 */
	private static final String EndTimeKeeper 	= "End";
	
	/**
	 * Action command for the button that displays the task history.
	 */
	private static final String ShowHistory 	= "Show";
	
	/**
	 * Action command for the button that switches to the bar graph view.
	 */
	private static final String GraphView = "Bar";

	/**
	 * The part of the GUI that handles task selection and editing.
	 */
	private Tk_TaskSelect		m_select		= null;
	
	/**
	 * The display that contains the time and duration for tasks, along with
	 * the Task switching buttons.
	 */
	private Tk_StartDisplay		m_start			= null;
	
	/**
	 * The button that ends the time keeping session.
	 */
	private JButton				m_endSession 	= null;
	
	/**
	 * The button that displays the task history.
	 */
	private JButton				m_showHistory	= null;
	
	/**
	 * The button that switches to the bar graph view.
	 */
	private JButton				m_showBarGraph;
	
	/**
	 * The task that is currently selected for timing.
	 */
	private String				m_currentTask	= null;
	
	/**
	 * Box to layout the <code>m_select</code> task select panel, the
	 * <code>m_start</code> display panel, and the <code>m_endSession</code>
	 * and <code>m_showHistory</code> JButtons.
	 */
	private Box					m_box			= null;
	
	/**
	 * <code>m_taskHistory</code> is used to record the task timing session
	 * to the TK_History.xml file when this task is terminated.
	 */
	private Tk_TaskHistory		m_taskHistory 	= null;
	
	/**
	 * A map containing a task name to xml time record  element lists
	 */
	private HashMap<String, Vector<Document>>m_timeRecordMap = null;

	/**
	 * Create the Main window by:
	 * - Instantiate the Tk_TaskSelect panel and add an action listener for
	 *   task selections in the combo box.
	 * - Instantiate the Tk_StartDisplay panel and 
	 * - Instantiate the m_endSession JButton.
	 * - Instantiate the m_showHistory JButton.
	 * - Setup a WindowAdapter to catch and handle the window closing event.
	 */
	public Tk_MainWindow()
	{
		super("TimeKeeper v0.1");
		Map<String, String>fmap =
			Tk_FileUtil.getXmlFiles
			(
				TaskXmlTemplate,
				TaskXsdTemplate,
				HistoryXmlTemplate,
				HistoryXsdTemplate
			);
		
		m_taskXmlFile 		= fmap.get(TaskXmlTemplate);
		m_taskXsdFile 		= fmap.get(TaskXsdTemplate);
		m_historyXmlFile 	= fmap.get(HistoryXmlTemplate);
		m_historyXsdFile 	= fmap.get(HistoryXsdTemplate);


		m_select = new Tk_TaskSelect(m_taskXmlFile, m_taskXsdFile);
		m_taskHistory = new Tk_TaskHistory(m_historyXmlFile, m_historyXsdFile);
		m_select.getComboBox().addActionListener(this);
		m_currentTask = m_select.getSelected();
	
		if (null == m_currentTask)
		{
			m_currentTask = NoTask;
		}
		
		m_start = new Tk_StartDisplay(m_currentTask);
		m_endSession = new JButton("End Time Keeper Session...");
		m_endSession.setActionCommand(EndTimeKeeper);
		m_endSession.addActionListener(this);
		m_showHistory = new JButton("Display History");
		m_showHistory.setMinimumSize(m_endSession.getMinimumSize());
		m_showHistory.setPreferredSize(m_endSession.getPreferredSize());
		m_showHistory.setMaximumSize(m_endSession.getMaximumSize());
		m_showHistory.setActionCommand(ShowHistory);
		m_showHistory.addActionListener(this);
		
		JPanel jp = new JPanel();
		jp.setLayout(new FlowLayout(FlowLayout.CENTER));
		jp.add(m_endSession);
		jp.add(m_showHistory);
		m_box = new Box(BoxLayout.PAGE_AXIS);
		m_box.add(m_select);
		m_box.add(m_start);
		m_box.add(jp);
		
		//m_box.add(m_showBarGraph);
		add(m_box);
		pack();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new CloseAdapter());
		setVisible(true);
	}

	/**
	 * Handle:
	 * - the action indicating that a new task has been selected in m_select's
	 * Task Combo box.
	 * - Handle the EndTimeKeeper message from the m_endSession JButton.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if
		(
			e.getSource().equals(m_select.getComboBox())
			&&
			e.getActionCommand().equals(Tk_TaskSelect.SelectString)
		)
		{
			m_start.setSelectedTask(m_select.getSelected());
		}
		else if (e.getActionCommand().equals(EndTimeKeeper))
		{
			updateHistory();
			m_taskHistory.updateDom();
			this.dispose();
			System.exit(0);
		}
		else if (e.getActionCommand().equals(ShowHistory))
		{
			showHistory();
		}
		else if (e.getActionCommand().equals(GraphView))
		{
			System.out.println("Got action command:" + GraphView);
			this.setVisible(false);
		}		
	}
	
	/**
	 * A Window Adapter to catch the windowClosing event, and, if the user
	 * confirms the close operation, get any remaining task time records
	 * from the m_start panel, and write them to the Tk_History.xml file.
	 * 
	 * @author michaelwebster
	 *
	 */
	class CloseAdapter extends WindowAdapter
	{
		/**
		 * Catch the window closing event, display a confirmation dialog, and
		 * if window closing is confirmed, grab any remaining time records from
		 * m_start, and write them back to the Tk_History file.
		 */
		public void windowClosing(WindowEvent e)
		{
			int confirm = JOptionPane.showOptionDialog
			(
				Tk_MainWindow.this,
				"Really Exit?",
				"Exit Confirmation",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				null,
				null
			);
            
			if (confirm == 0)
			{
				updateHistory();
				m_taskHistory.updateDom();
				System.exit(0);
			}
		}
	}

	private void showHistory()
	{
		Tk_HistoryPanel hp =
			new Tk_HistoryPanel
			(
				m_taskXmlFile,
				m_taskXsdFile,
				m_historyXmlFile,
				m_historyXsdFile
			);
		JFrame jf = new JFrame();
		jf.setUndecorated(false);
		jf.add(hp.getGraphPanel());
		jf.pack();
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.setVisible(true);
	}

	/**
	 * Get the time records from the m_start panel, and add them to the
	 * <code>Tk_TaskHistory</code> object for writing out to the history xml
	 * file.
	 */
	private void updateHistory()
	{
		m_timeRecordMap = m_start.getTimeRecords();
		Iterator<String>it = m_timeRecordMap.keySet().iterator();
		while (it.hasNext())
		{
			String tname = it.next();
			Vector<Document>docs = m_timeRecordMap.get(tname);
			for (int i = 0; i < docs.size(); i++)
			{
				Document cd = docs.elementAt(i);
				m_taskHistory.addTimeRecord(tname, cd);
			}
		}
	}
	
	/**
	 * For debugging purposes, print the current task history to stdout.
	 */
	public void printHistory()
	{
		m_taskHistory.printTaskHistory();
	}

	/**
	 * Testing.
	 * @param args no args.
	 */
	public static void main(String[] args)
	{
		Tk_MainWindow tmw = new Tk_MainWindow();
	}
}
