package Tk_Configuration;
import javax.swing.*;
import javax.swing.event.*;

import java.lang.IllegalStateException;

/**
 * Tk_TaskListModel is a holder for two lists, one that can be used as the
 * model for a combo box, and one that can be concurrently updated without
 * effecting the first list. These two lists are called m_staticList and
 * m_updateList respectively. They are both of type DefaultComboBoxModel.
 * 
 * @author michaelwebster
 */
public class Tk_TaskListModel implements ListDataListener
{
	// The non-updateable list for use in a combo box.
	DefaultComboBoxModel<String> m_staticList = null;
	// The updateable copy, that can be modified by an external task.
	DefaultComboBoxModel<String> m_updateList = null;
	// The dirty flag indicates that the update list has been obtained by
	// some external object, and that we cannot accept changes to the
	// static list until the update list has been returned, or finished
	// with.
	boolean m_dirtyFlag = true;
	
	/**
	 * Create empty static and update lists, and set this instance clean.
	 */
	public Tk_TaskListModel()
	{
		m_staticList = new DefaultComboBoxModel<String>();
		m_updateList = new DefaultComboBoxModel<String>();
		m_dirtyFlag = false;
	}
	
	/**
	 * Add a task name (string) element to the Task list model if this instance
	 * is not dirty.
	 *
	 * @param task_name		The name of the task to be added to the list.
	 *
	 * @throws IllegalStateException in the case that an addition to the list
	 * is attempted when the model is dirty.
	 */
	public void addElement(String task_name) throws IllegalStateException
	{
		if (!m_dirtyFlag)
		{
			m_staticList.addElement(task_name);
		}
		else
		{
			IllegalStateException ise =
				new IllegalStateException
				(
					"Error: Elements cannot be added to the TkTaskListModel when it is dirty.\n"
					+
					"The state may be returned to clean by calling the reconcileLists() method.\n"
				);
			throw(ise);
		}
	}
	
	/**
	 * Add all the task name strings from task_list into this object's static
	 * list.
	 * @param task_list		A list of strings to be added to this model.
	 *
	 * @throws IllegalStateException in case the update list is out for
	 * possible editing when this method is called.
	 */
	public void setStaticList(DefaultComboBoxModel<String> task_list)
			throws IllegalStateException
	{
		if (!m_dirtyFlag)
		{
			for (int i = 0; i < task_list.getSize(); i++)
			{
				m_staticList.addElement(task_list.getElementAt(i));
			}
		}
		else
		{
			IllegalStateException ise =
				new IllegalStateException
				(
					"Error: Elements cannot be added to the TkTaskListModel when it is dirty.\n"
					+
					"The state may be returned to clean by calling the reconcileLists() method.\n"
				);
			throw(ise);
		}
	}
	
	/**
	 * Return the static list part of the model for use in a Combo Box, or in
	 * some other way.
	 *
	 * @return The static part of this list model.
	 */
	public DefaultComboBoxModel<String>getStaticList()
	{
		return m_staticList;
	}
	
	/**
	 * If the update list is not out for editing, set it up to be the same as
	 * the static list. Before returning the update list, add a list data
	 * listener to it.
	 *  
	 * @return The update list for possible editing.
	 */
	public DefaultComboBoxModel<String>getUpdateList()
	{
		if  (!m_dirtyFlag)
		{
			m_updateList.removeAllElements();
			for (int i = 0; i < m_staticList.getSize(); i++)
			{
				m_updateList.addElement(m_staticList.getElementAt(i));
			}
			m_updateList.addListDataListener(this);
		}
		else
		{
			IllegalStateException ise =
				new IllegalStateException
				(
					"Error: Elements cannot be added to the TkTaskListModel when it is dirty.\n"
					+
					"The state may be returned to clean by calling the reconcileLists() method.\n"
				);
			throw(ise);
		}
		return m_updateList;
	}
	
	/**
	 * The update list has been updated and is valid, so set the static list
	 * to be the same as the changed update list.
	 *
	 * @param valid		true => All the changes to the update list are to be
	 * 							reflected to the static list.
	 * 					false => Don't update the static list.
	 */
	public void reconcileLists(boolean valid)
	{
		if (valid)
		{
			m_staticList.removeAllElements();
			for (int i = 0; i < m_updateList.getSize(); i++)
			{
				m_staticList.addElement(m_updateList.getElementAt(i));
			}
		}
		m_updateList.removeAllElements();
		m_updateList.removeListDataListener(this);
		m_dirtyFlag = false;
	}
	
	/**
	 * The update list has changed, so set the dirty flag. 
	 */
	@Override
	public void intervalAdded(ListDataEvent e)
	{
		m_dirtyFlag = true;
	}

	/**
	 * The update list has changed, so set the dirty flag. 
	 */
	@Override
	public void intervalRemoved(ListDataEvent e)
	{
		m_dirtyFlag = true;		
	}

	/**
	 * The update list has changed, so set the dirty flag. 
	 */
	@Override
	public void contentsChanged(ListDataEvent e)
	{
		m_dirtyFlag = true;		
	}
	
	/**
	 * Debug method for printing out a DefaultComboBox<String> model.
	 *
	 * @param l A default combo box model to be printed.
	 */
	private static void printList(DefaultComboBoxModel<String>l)
	{
		int length = l.getSize();
		
		for (int i = 0; i < length; ++i)
		{
			System.out.printf(">>>>>>> %s <<<<<<<<<<<\n", l.getElementAt(i));
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
		
		Tk_TaskListModel tklm = new Tk_TaskListModel();
		tklm.setStaticList(tc.getCurrentList());
		
		tc.printCurrentList();
		DefaultComboBoxModel<String>dcm = tklm.getUpdateList();
		System.out.println("UpdateList is now:");
		Tk_TaskListModel.printList(dcm);
		
		/*
		 * Remove an element.
		 */
		dcm.removeElementAt(0);
		System.out.println("\nRemoved an element from the update list.");
		System.out.println("UpdateList is now:");
		Tk_TaskListModel.printList(dcm);
		System.out.println("StaticList is now:");
		Tk_TaskListModel.printList(tklm.getStaticList());
		
		/*
		 * Remove a second element.
		 */
		dcm.removeElementAt(2);
		System.out.println("\nRemoved an element from the update list.");
		System.out.println("UpdateList is now:");
		Tk_TaskListModel.printList(dcm);
		System.out.println("StaticList is now:");
		Tk_TaskListModel.printList(tklm.getStaticList());
		
		/**
		 * Reconcile the lists:
		 */
		tklm.reconcileLists(true);
		System.out.println("\n\nReconciled StaticList is now:");
		Tk_TaskListModel.printList(tklm.getStaticList());

	}
}
