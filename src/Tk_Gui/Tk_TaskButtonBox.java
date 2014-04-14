package Tk_Gui;

import javax.swing.*;
import javax.swing.border.*;

/**
 * Simple box containing two buttons labelled "Start Task" and "Stop Task".
 * These buttons can be obtained through getter methods, and have appropriate
 * listeners added to them.
 *
 * @author michaelwebster
 *
 */
public class Tk_TaskButtonBox extends Box
{
	// Action command strings for the two buttons.
	private final static String StartText 	= "Start Task";
	private final static String StopText	= "Stop Task";
	
	// The start and stop JButtons.
	private JButton m_start 	= null;
	private JButton m_stop		= null;
	
	/**
	 * No argument construtor - just create the buttons, add them to this box,
	 * and create a border around them. Make the buttons the same size.
	 */
	public Tk_TaskButtonBox()
	{
		super(BoxLayout.PAGE_AXIS);
		m_start = new JButton(StartText);
		m_stop = new JButton(StopText);
		m_stop.setPreferredSize(m_start.getPreferredSize());
		m_stop.setMinimumSize(m_start.getMinimumSize());
		m_stop.setMaximumSize(m_start.getMaximumSize());
		add(m_start);
		add(m_stop);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
	}

	/**
	 * Return the m_start JButton.
	 *
	 * @return The start button.
	 */
	public JButton getStartButton()
	{
		return m_start;
	}
	
	/**
	 * Return the m_stop JButton.
	 *
	 * @return The stop button.
	 */
	public JButton getStopButton()
	{
		return m_stop;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		JFrame jf = new JFrame("Task Button Box");
		Tk_TaskButtonBox tbb = new Tk_TaskButtonBox();
		
		jf.add(tbb);
		jf.pack();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}

}
