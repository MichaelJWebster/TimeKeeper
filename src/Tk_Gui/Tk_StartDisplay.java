package Tk_Gui;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import org.w3c.dom.*;

import Tk_History.Tk_ActiveTimeRecord;

/**
 * A JPanel that contains a Tk_Digital clock that displays task times and
 * durations, and a Tk_ButtonBox containing the "Start Task" and
 * "End Task" buttons.
 *
 * @author michaelwebster
 */
public class Tk_StartDisplay extends JPanel implements ActionListener
{
	// The action commands sent by the "Start Task" and "Stop Task" JButtons.
	public final static String StartCommand			= "start";
	public final static String StopCommand			= "stop";
	
	/**
	 * Clock display for task times, durations and name of current task.
	 */
	private Tk_Digital 			m_clock 			= null;
	
	/**
	 *  Button box containing the start and stop buttons.
	 */
	private Tk_TaskButtonBox	m_switcher			= null;
	
	/**
	 * The name of the current task being timed.
	 */
	private String				m_currentTask		= null;
	
	/**
	 * The name of the task selected in the Tk_TaskSelect combo box.
	 */
	private String				m_selectedTask		= null;
	
	/**
	 * Store task_name as both the current and selected task, and instantiate
	 * and add appropriate listeners to m_clock, and m_switcher.
	 *
	 * @param task_name		The name of the current task selected in the
	 * 						Tk_TaskSelect object's combo box.  
	 */
	Tk_StartDisplay(String task_name)
	{
		m_currentTask = task_name;
		m_selectedTask = task_name;
		m_clock = new Tk_Digital(task_name);
		m_switcher = new Tk_TaskButtonBox();
		m_switcher.getStartButton().setActionCommand(StartCommand);
		m_switcher.getStartButton().addActionListener(this);
		m_switcher.getStopButton().setActionCommand(StopCommand);
		m_switcher.getStopButton().addActionListener(this);
		
		// Set the start and stop buttons enabled as is appropriate for the
		// current state.
		m_switcher.getStartButton().setEnabled(!m_clock.isActive());
		m_switcher.getStopButton().setEnabled(m_clock.isActive());
		add(m_clock.getBox());
		add(m_switcher);
	}

	/**
	 * Handle the Start and Stop button actions, by starting or stopping the
	 * m_clock.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals(StartCommand))
		{
			startClock();
		}
		else if (e.getActionCommand().equals(StopCommand))
		{
			stopClock();
		}
	}

	/**
	 * Start the clock. If the selected task does not equal the current task,
	 * then tell m_clock to switch tasks before starting the clock.
	 * 
	 * Disable the Start button and enable the stop button before starting
	 * the clock and returning. 
	 */
	public void startClock()
	{
		if (!m_currentTask.equals(m_selectedTask))
		{
			m_currentTask = m_selectedTask;
			m_clock.switchTasks(m_currentTask);
		}
		m_switcher.getStartButton().setEnabled(false);
		m_switcher.getStopButton().setEnabled(true);
		m_switcher.getStopButton().grabFocus();
		m_clock.startClock();
	}
	
	/**
	 * Disable the Stop button, enable the start button and give it focus, and
	 * stop the clock. 
	 */
	public void stopClock()
	{
		m_switcher.getStartButton().setEnabled(true);
		m_switcher.getStopButton().setEnabled(false);
		m_switcher.getStartButton().grabFocus();
		m_clock.stopClock();
	}
	
	/**
	 * Set the m_selected task, so that when start is clicked, we can determine
	 * if a task switch is required.
	 * 
	 * @param stask		The newly selected Task name for timing, next time we
	 * 					start the clock.
	 */
	public void setSelectedTask(String stask)
	{
		m_selectedTask = stask;
	}

	/**
	 * Get and return the m_clock's time records.
	 *
	 * @return	A mapping between tasknames and Time Record Documents as
	 * 			recorded by the m_clock.
	 */
	public HashMap<String, Vector<Document>>getTimeRecords()
	{
		return m_clock.getTaskMap();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		JFrame jf = new JFrame("Start Display");
		Date d = new Date();
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(d);
		Tk_StartDisplay tsd = new Tk_StartDisplay("Programming");
		jf.add(tsd);
		jf.pack();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}
}
