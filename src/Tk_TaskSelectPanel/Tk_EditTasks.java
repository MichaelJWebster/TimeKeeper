package Tk_TaskSelectPanel;

import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;

//
// My class imports 
//
import Tk_Configuration.*;

public class Tk_EditTasks extends JFrame implements ActionListener
{
	// The Title for this JFrame
	private static final String EditTitle		= "Edit Tasks";
	//
	// Action commands for the various buttons.
	//
	private static final String LeftAction 		= "left";
	private static final String RightAction 	= "right";
	public	 static final String OkAction		= "ok";
	public static final String CancelAction		= "cancel";
	
	// The supplied DefaultComboBoxModels
	private DefaultComboBoxModel<String> m_curList = null;
	private DefaultComboBoxModel<String> m_retList = null;
	private DefaultComboBoxModel<String> m_delList = null;
	
	// The JList containing the current tasks and a JScrollPane to contain it.
	private JList<String>		m_currentTasks 		= null;
	private JScrollPane			m_currentScroller   = null;

	// The Panel containing the Additional Tasks list, buttons and text bod.
	private Tk_AdditionalTasks	m_additionalTasks 	= null;

	// The Box containing the copy left/right buttons.
	private Tk_LRButtons		m_moveBox		= null;
	private JButton				m_moveLeft		= null;
	private JButton				m_moveRight		= null;

	// The box containing the Ok and Cancel buttons.
	private Tk_ButtonBox		m_buttonBox		= null;
	private JButton 			m_ok 			= null;
	private JButton				m_cancel 		= null;
	
	// A Box to contain the m_currentTasks JList, the m_additionalTasks panel,
	// and the Tk_LRButtons box between the two - layed out horizontally.
	private Box 				m_topBox		= null;
	
	private Vector<String>m_addCurrent	= new Vector<String>();
	private Vector<String>m_removeCurrent = new Vector<String>();

	//String frame_title,
	public Tk_EditTasks
	(
		DefaultComboBoxModel<String> curTasks,
		DefaultComboBoxModel<String> retTasks,
		DefaultComboBoxModel<String> delTasks
	)
	{
		super(EditTitle);
		m_curList = curTasks;
		m_retList = retTasks;
		m_delList = delTasks;

		// Setup the current tasks JList
		m_currentTasks = new JList<String>();
		m_currentTasks.setModel(m_curList);
		m_currentTasks.setPrototypeCellValue("This is a relatively long task name!");

		// Set up the JScrollbar for the current task list.
		m_currentScroller = new JScrollPane();
		m_currentScroller.getViewport().add(m_currentTasks);
		m_currentScroller.setBorder
		(
			BorderFactory.createTitledBorder
			(
				BorderFactory.createLineBorder(getForeground()),
				"Current Tasks:"
			)
		);

		// Set up the Tk_AddtionalTasks panel.
		m_additionalTasks = new Tk_AdditionalTasks
			(
					m_retList,
					m_delList
			);
		
		// Set up the left and right buttons, setup their action commands, and
		// wire them up to this as an ActionListener.
		m_moveBox = new Tk_LRButtons();
		m_moveLeft = m_moveBox.getLeftButton();
		m_moveRight = m_moveBox.getRightButton();
		m_moveLeft.setActionCommand(LeftAction);
		m_moveLeft.addActionListener(this);
		m_moveRight.setActionCommand(RightAction);
		m_moveRight.addActionListener(this);
		
		// Set up the Ok and Cancel buttons, setup their action commands, and
		// wire them up to this as their ActionListener.
		m_buttonBox		= new Tk_ButtonBox();
		m_ok 			= m_buttonBox.getOkButton();
		m_cancel 		= m_buttonBox.getCancelButton();
		m_ok.setActionCommand(OkAction);
		m_ok.addActionListener(this);
		m_cancel.setActionCommand(CancelAction);
		m_cancel.addActionListener(this);
		m_buttonBox.setBorder(BorderFactory.createEtchedBorder());
		
		// Setup the TopBox to contain the Current tasks JList, the arrow
		// buttons, and the Tk_AdditionalTasks object.
		m_topBox = new Box(BoxLayout.LINE_AXIS);
		m_topBox.add(m_currentScroller);
		m_topBox.add(m_moveBox);
		m_topBox.add(m_additionalTasks);
		
		// Layout this JFrame with m_topBox at the top, and m_buttonBox at
		// the bottom.
		Box myBox = Box.createVerticalBox();
		myBox.add(m_topBox);
		myBox.add(m_buttonBox);
		Color c1 = new Color(86, 86, 86);
	    Color c2 = new Color(192, 192, 192);
	    Color c3 = new Color(204, 204, 204);
	    Border b1 = new BevelBorder(EtchedBorder.RAISED, c3, c1);
	    Border b2 = new MatteBorder(3,3,3,3,c2);
	    Border b3 = new BevelBorder (EtchedBorder.LOWERED, c3, c1);
	    myBox.setBorder(new CompoundBorder(new CompoundBorder(b1, b2), b3));
	    add(myBox);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	public JButton getOKButton()
	{
		return m_ok;
	}
	
	public JButton getCancelButton()
	{
		return m_cancel;
	}

	public Vector<String>getAddCurrent()
	{
		return m_addCurrent;
	}
	
	public Vector<String>getRemoveCurrent()
	{
		return m_removeCurrent;
	}
	
	public Vector<String>getAddedTasks()
	{
		return this.m_additionalTasks.getAddedTasks();
	}
	
	public Vector<String>getRemovedTasks()
	{
		return m_additionalTasks.getRemovedTasks();
	}

	/**
	 * FIXME: Need to disable left button when the right list is empty. Also need
	 * the action listener to reenable it.
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (OkAction.equals(e.getActionCommand()))
		{
		}
		else if (CancelAction.equals(e.getActionCommand()))
		{
		}
		else if (LeftAction.equals(e.getActionCommand()))
		{
			JList<String>retTasks = m_additionalTasks.getRetiredTasks();
			int    index = retTasks.getSelectedIndex();
			String copy_el = m_retList.getElementAt(index);
			m_retList.removeElementAt(index);
			if (m_retList.getSize() <= index && index > 0)
			{
				index--;
			}
			retTasks.setSelectedIndex(index);
			retTasks.ensureIndexIsVisible(index);
			index = m_curList.getSize();
			m_curList.insertElementAt(copy_el, index);
			m_currentTasks.setSelectedIndex(index);
			m_currentTasks.ensureIndexIsVisible(index);
			m_addCurrent.add(copy_el);
		}
		else if (RightAction.equals(e.getActionCommand()))
		{	
			int index = m_currentTasks.getSelectedIndex();
			String copy_el = m_curList.getElementAt(index);
			m_curList.removeElementAt(index);
			if (m_curList.getSize() <= index && index > 0)
			{
				index--;
			}
			m_currentTasks.setSelectedIndex(index);
			m_currentTasks.ensureIndexIsVisible(index);
			index = m_retList.getSize();
			m_retList.insertElementAt(copy_el, index);
			m_removeCurrent.add(copy_el);
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String xml_file = 
				"file:///Users/michaelwebster/Documents/workspace/TimeKeeper/src/Tk_Configuration/TestData/TK_TaskList.xml";
		String xsd_file = 
				"file:////Users/michaelwebster/Documents/workspace/TimeKeeper/src/Tk_Configuration/TestData/TK_TaskList.xsd";
		Tk_TaskCollection tc = 
			new Tk_TaskCollection
			(
				xml_file,
				xsd_file
			);
		DefaultComboBoxModel<String>cur_tasks = tc.getCurrentListUpdate();
		DefaultComboBoxModel<String>ret_tasks = tc.getRetiredListUpdate();
		DefaultComboBoxModel<String>del_tasks = tc.getDeletedListUpdate();
		
		Tk_EditTasks tet = new Tk_EditTasks
		(
			cur_tasks,
			ret_tasks,
			del_tasks
		);
	}
}
