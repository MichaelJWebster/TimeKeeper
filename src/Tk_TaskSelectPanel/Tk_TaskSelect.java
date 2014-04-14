package Tk_TaskSelectPanel;

import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

// My Imports
import Tk_Configuration.Tk_TaskCollection;

/**
 * A panel that holds the task selection combo box, and 
 * @author michaelwebster
 *
 */
public class Tk_TaskSelect extends JPanel implements ActionListener
{
	// The action commands to supply to the Tk_ComboBox and JButton.
	public static final String SelectString 	= "Select";
	public static final String EditString		= "Edit";

	private Tk_TaskCollection 	m_taskCollection = null;
	private Tk_ComboBox 		m_cb = null;
	private JButton         	m_editButton = null;
	
	// The Tk_EditTasks panel that allows the user to add or remove tasks.
	private Tk_EditTasks    	m_editTasks = null;
	
	public Tk_TaskSelect(String xml_fname, String schema_fname)
	{
		// Create the Task Collection object by reading the xml from the file
		// named uri_string
		m_taskCollection = new Tk_TaskCollection(xml_fname, schema_fname);
		
		// Create the Combo box by providing it with a list of tasks, and add
		// an action listener to it.
		m_cb = new Tk_ComboBox(m_taskCollection.getCurrentList());
		m_cb.setActionCommand(SelectString);
		m_cb.addActionListener(this);
		m_cb.setPrototypeDisplayValue
		(
			"This is a very long task name, perhaps it will help"
		);
		
		// Create the Edit Tasks button, and add an action listener to it.
		m_editButton = new JButton("Edit Tasks");
		m_editButton.setActionCommand(EditString);
		m_editButton.addActionListener(this);
		
		// Use a simple FlowLayout to layout the combo box and button side by
		// side.
		FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
		setLayout(fl);
		add(m_cb);
		add(m_editButton);
		setBorder
		(
			BorderFactory.createTitledBorder
			(
				BorderFactory.createBevelBorder(BevelBorder.RAISED),
				"Select Task:"
			)
		);
	}

	public String getSelected()
	{
		return (String)m_cb.getSelectedItem();
	}
	
	public Tk_ComboBox getComboBox()
	{
		return m_cb;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (SelectString.equals(e.getActionCommand()))
		{
		}
		else if (EditString.equals(e.getActionCommand()))
		{
			m_editTasks = new Tk_EditTasks
			(
					m_taskCollection.getCurrentListUpdate(),
					m_taskCollection.getRetiredListUpdate(),
					m_taskCollection.getDeletedListUpdate()
			);
			m_editTasks.getOKButton().addActionListener(this);
			m_editTasks.getCancelButton().addActionListener(this);
		}
		else if (m_editTasks.OkAction.equals(e.getActionCommand()))
		{
			m_taskCollection.reconcileModels(true);
			m_editTasks.setVisible(false);
			m_editTasks.dispose();
			//m_taskCollection.printDom();
		}
		else if (m_editTasks.CancelAction.equals(e.getActionCommand()))
		{
			m_taskCollection.reconcileModels(false);
			m_editTasks.setVisible(false);
			m_editTasks.dispose();
			//m_taskCollection.printDom();
		}
	}
	
	public Tk_TaskCollection getTaskCollection()
	{
		return m_taskCollection;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		//JFrame.setDefaultLookAndFeelDecorated(false);
		JFrame jf = new JFrame();
		Tk_TaskSelect ts = new Tk_TaskSelect
		(
			"file:///Users/michaelwebster/Documents/workspace/TimeKeeper/src/Tk_Configuration/TestData/TK_TaskList.xml",
			"file:////Users/michaelwebster/Documents/workspace/TimeKeeper/src/Tk_Configuration/TestData/TK_TaskList.xsd"
		);
		jf.add(ts);
		jf.pack();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//
		// Set the JFrame jf into the centre of the screen.
		//
		Dimension dim = jf.getToolkit().getScreenSize();
		jf.setLocation
		(
				dim.width/2 - jf.getWidth()/2,
				dim.height/2 - jf.getHeight()/2
		);
		System.out.println(dim.toString());
		jf.setVisible(true);
		Tk_TaskCollection tc = ts.getTaskCollection();
		System.out.println("The DOM starts like this:");
		tc.printDom();
	}

}
