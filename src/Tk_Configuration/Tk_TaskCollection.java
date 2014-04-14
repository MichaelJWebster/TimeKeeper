/**
 * 
 */
package Tk_Configuration;
import java.util.AbstractSequentialList;
import java.io.*;
import java.net.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

// Imports for xml.
import org.w3c.dom.*;
import org.xml.sax.*;
import Tk_utils.Tk_XmlUtil;
import javax.xml.parsers.*;
import javax.xml.validation.*;

/**
 * <p>Tk_TaskCollection represents current, retired and deleted lists of task
 * names. The class and it's lists are instantiated from the task information
 * recorded in the xml file TK_TaskList.xml. The xml read from, and written
 * back to that file are validated using an xml schema, TK_TaskList.xsd.</p>
 * 
 * <p>The class makes use of <code>Tk_TaskList</code> model's to keep track
 * of tasks. These lists can be shared with a combo box, and also edited at
 * the same time, so that tasks can be added or deleted from the various lists.
 * 
 * Changes to the lists in this class can be, and are, written back to the
 * TK_TaskList.xml file.
 *
 * @author michaelwebster
 *
 * FIXME: Need a finalize method to ensure the current state of the xml is
 * written back to m_file.
 */
public class Tk_TaskCollection // implements ActionListener
{
	/*
	 * Static xml tag srings.
	 */
	private final static String TK_TaskList 		= "TK_TaskList";
	private final static String TK_CurrentTasks 	= "TK_CurrentTasks";
	private final static String TK_RetiredTasks 	= "TK_RetiredTasks";
	private final static String TK_DeletedTasks 	= "TK_DeletedTasks";	
	private final static String TK_TaskName 		= "TK_TaskName";
	
	/*
	 * Instance attributes.
	 */
	private String 				m_xmlFileName			= null;
	private String				m_schemaFileName = null;
	private Document			m_doc					= null;
	
	//The DefaultComboBoxModels containing the tasks for use in the UI combo box.
	private Tk_TaskListModel m_currentList		= null;
	private Tk_TaskListModel m_retiredList		= null;
	private Tk_TaskListModel m_deletedList		= null;	

	/*
	 * Construct the Tk_TaskCollection object by:
	 * - Find the files indicated by uri_string and schema_string.
	 * - Create a Schema object from the schema file.
	 * - Create a validating parser incorporating the schema, and attempt to
	 * 	 parse the file passed in as uri_string.
	 * - Add TK_TaskName entries from the xml file to the m_currentList and
	 *   m_retiredList according to which section they are in.
	 *
	 * @param uri_string		The URI for the xml file containing the lists
	 *                          of tasks for the time keeper.
	 * @param schema_string     The schema for the xml file above.
	 */
	public Tk_TaskCollection(String xml_name, String schema_name)
	{
		/*
		 * Create the URI for the xml task list from the supplied uri_string.
		 */
		m_xmlFileName = xml_name;
		m_schemaFileName = schema_name;
		m_doc = Tk_XmlUtil.createDocument(m_xmlFileName, m_schemaFileName);
		populateTaskLists();
	}

	/**
	 * Extract task name information from the TK_TaskList xml file, and
	 * populate the m_currentList and m_retiredLists. 
	 */
	private void populateTaskLists()
	{
		m_currentList = new Tk_TaskListModel();		//DefaultComboBoxModel<String>();
		m_retiredList = new Tk_TaskListModel();		//DefaultComboBoxModel<String>();
		m_deletedList = new Tk_TaskListModel(); 	//DefaultComboBoxModel<String>();
		
		Element doc_element = m_doc.getDocumentElement();

		// Get the list of current tasks.
		Element el = Tk_XmlUtil.getFirstElement(doc_element, TK_CurrentTasks);
		if (null != el)
		{
			NodeList ct_nodes = el.getElementsByTagName(Tk_TaskCollection.TK_TaskName);
			for (int i = 0; i < ct_nodes.getLength(); i++)
			{
				Element tn_el = (Element)ct_nodes.item(i);
				String task_name = Tk_XmlUtil.getSimpleElementText(tn_el);
				m_currentList.addElement(task_name);
			}
		}
		
		el = Tk_XmlUtil.getFirstElement(doc_element, TK_RetiredTasks);
		if (null != el)
		{
			NodeList ct_nodes = el.getElementsByTagName(Tk_TaskCollection.TK_TaskName);
			for (int i = 0; i < ct_nodes.getLength(); i++)
			{
				Element tn_el = (Element)ct_nodes.item(i);
				String task_name = Tk_XmlUtil.getSimpleElementText(tn_el);
				m_retiredList.addElement(task_name);
			}
		}
		
		el = Tk_XmlUtil.getFirstElement(doc_element, TK_DeletedTasks);
		if (null != el)
		{
			NodeList ct_nodes = el.getElementsByTagName(Tk_TaskCollection.TK_TaskName);
			for (int i = 0; i < ct_nodes.getLength(); i++)
			{
				Element tn_el = (Element)ct_nodes.item(i);
				String task_name = Tk_XmlUtil.getSimpleElementText(tn_el);
				m_deletedList.addElement(task_name);
			}
		}

	}

	/**
	 * The Tk_EditTasks class has just finished running, and we need to
	 * reconcile our lists with the updated versions. If the Ok Button was
	 * clicked, then we also need to update the Dom, and write it out
	 * to our xml file.
	 *
	 * @param ok_clicked   	true  => 	The Tk_EditTasks dialog had its ok
	 * 									button clicked.
	 * 						false =>	The Tk_EditTasks dialog was cancelled.
	 * 					
	 */
	public void reconcileModels(boolean ok_clicked)
	{
		m_currentList.reconcileLists(ok_clicked);
		m_retiredList.reconcileLists(ok_clicked);
		m_deletedList.reconcileLists(ok_clicked);
		DefaultComboBoxModel<String>dcb = 	m_currentList.getStaticList();
		if (dcb.getSize() > 0)
		{
			dcb.setSelectedItem(dcb.getElementAt(0));
		}
		
		if (ok_clicked)
		{
			updateDom();
			writeBackDom();
		}
	}
	
	/**
	 * Rewrite the Current, Retired and Deleted task lists into the DOM, and
	 * write the DOM back to it's file.
	 */
	private void updateDom()
	{
		Document doc = Tk_XmlUtil.createEmptyDom(m_schemaFileName);

		Node root = doc.createElement(TK_TaskList);
		doc.appendChild(root);
		Node c_tasks = doc.createElement(TK_CurrentTasks);
		root.appendChild(c_tasks);
		Node r_tasks = doc.createElement(TK_RetiredTasks);
		root.appendChild(r_tasks);
		Node d_tasks = doc.createElement(TK_DeletedTasks);
		root.appendChild(d_tasks);
		
		// Create and add the task name nodes for the current list.
		DefaultComboBoxModel<String>tasks = m_currentList.getStaticList();
		for (int i = 0; i < tasks.getSize(); i++)
		{
			String tname = tasks.getElementAt(i);
			Node tname_node = doc.createElement(TK_TaskName);
			c_tasks.appendChild(tname_node);
			tname_node.appendChild(doc.createTextNode(tname));
		}

		// Create and add the task name nodes for the retired list.
		tasks = m_retiredList.getStaticList();
		for (int i = 0; i < tasks.getSize(); i++)
		{
			String tname = tasks.getElementAt(i);
			Node tname_node = doc.createElement(TK_TaskName);
			r_tasks.appendChild(tname_node);
			tname_node.appendChild(doc.createTextNode(tname));
		}
		
		// Create and add the task name nodes for the retired list.
		tasks = m_deletedList.getStaticList();
		for (int i = 0; i < tasks.getSize(); i++)
		{
			String tname = tasks.getElementAt(i);
			Node tname_node = doc.createElement(TK_TaskName);
			d_tasks.appendChild(tname_node);
			tname_node.appendChild(doc.createTextNode(tname));
		}
		m_doc = doc;
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
			fw.write(Tk_XmlUtil.formatDoc(getDom()));
			fw.close();
		}
		catch (IOException e)
		{
			System.out.println("Tk_TaskCollection: writeBackDom IOException.");
			System.out.println(e.getMessage());
			System.exit(-1);
		}
	}

	/*
	 * Create getter and or setter methods for these attributes:
	 *
	 * private String 				m_taskListURIString		= null;
	 * private String				m_schemaURIString		= null;
	 * private URI         		m_uri					= null;
	 * private URI					m_schemaURI				= null;
	 * private File				m_file					= null;
	 * private File				m_schemaFile			= null;
	 * private Document			m_doc					= null;
	 * private DocumentBuilder 	m_docBuilder			= null;
	 * private Schema      		m_schema				= null;
	 * private DefaultComboBoxModel<String>m_currentList	= null;
	 * private DefaultComboBoxModel<String>m_retiredList	= null;
	 */
	/**
	 * Return the Dom that represents the current state of the task lists.
	 *
	 * @return The Dom object that represents the current state of the task
	 * lists.
	 */
	public Document getDom()
	{
		return m_doc;
	}

	/**
	 * Return the name of the TK_TaskList.xml file.
	 *
	 * @return	The filename for the task list xml file.
	 */
	public String getTaskListFileName()
	{
		return m_xmlFileName;
	}
	
	/**
	 * Get the xml schema file's name.
	 *
	 * @return 	The name of the file containing the task list xsd schema.
	 */
	public String getSchemaFileName()
	{
		return m_schemaFileName;
	}

	/**
	 * Get the up to date, and not for editing version of the current task
	 * list.
	 *
	 * @return The up to date and non-changing version of the current list.
	 */
	public DefaultComboBoxModel<String>getCurrentList()
	{
		return m_currentList.getStaticList();
	}
	
	/**
	 * Get the updatable version of current task list.
	 *
	 * @return The up to date and updatable version of the current task list.
	 */
	public DefaultComboBoxModel<String>getCurrentListUpdate()
	{
		return m_currentList.getUpdateList();
	}
	
	/**
	 * Get the up to date, and not for editing version of the retired task
	 * list.
	 *
	 * @return The up to date and non-changing version of the retired list.
	 */
	public DefaultComboBoxModel<String>getRetiredList()
	{
		return m_retiredList.getStaticList();
	}
	
	/**
	 * Get the updatable version of retired task list.
	 *
	 * @return The up to date and updatable version of the retired task list.
	 */
	public DefaultComboBoxModel<String>getRetiredListUpdate()
	{
		return m_retiredList.getUpdateList();
	}

	/**
	 * Get the up to date, and not for editing version of the deleted task
	 * list.
	 *
	 * @return The up to date and non-changing version of the deleted list.
	 */
	public DefaultComboBoxModel<String>getDeletedList()
	{
		return m_deletedList.getStaticList();
	}
	
	/**
	 * Get the updatable version of deleted task list.
	 *
	 * @return The up to date and updatable version of the deleted task list.
	 */
	public DefaultComboBoxModel<String>getDeletedListUpdate()
	{
		return m_deletedList.getUpdateList();
	}

	/**
	 * Debug method for printing the static version of the current task list to
	 * System.out.
	 */
	public void printCurrentList()
	{
		int length = m_currentList.getStaticList().getSize();
		
		for (int i = 0; i < length; ++i)
		{
			System.out.printf
			(
				"%s\n", 
				m_currentList.getStaticList().getElementAt(i)
			);
		}
	}
	
	/**
	 * Debug method for printing out the contents of the static version of the
	 * retired task list.
	 */
	public void printRetiredList()
	{
		int length = m_retiredList.getStaticList().getSize();
		
		for (int i = 0; i < length; ++i)
		{
			System.out.printf
			(
				"%s\n",
				m_retiredList.getStaticList().getElementAt(i)
			);
		}
	}

	/**
	 * Debug method for printing out the contents of the static version of the
	 * deleted task list.
	 */
	public void printDeletedList()
	{
		int length = m_deletedList.getStaticList().getSize();
		
		for (int i = 0; i < length; ++i)
		{
			System.out.printf
			(
				"%s\n",
				m_deletedList.getStaticList().getElementAt(i)
			);
		}
	}
	
	/**
	 * Debug method for printing this task collection's dom representation.
	 */
	public void printDom()
	{
		System.out.printf("%s", Tk_XmlUtil.formatDoc(getDom()));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String xml_file =
				"/Users/michaelwebster/Documents/workspace/TimeKeeper/src/Tk_Configuration/TestData/Tk_TaskList.xml"; 
		String xsd_file =
				"/Users/michaelwebster/Documents/workspace/TimeKeeper/src/Tk_Configuration/TestData/Tk_TaskList.xsd";
		
		Tk_TaskCollection tc = new Tk_TaskCollection(xml_file, xsd_file);
		
		System.out.println("The current DOM is:");
		System.out.printf("%s", Tk_XmlUtil.formatDoc(tc.getDom()));
		
		// Try changing the lists.
		DefaultComboBoxModel<String>dc = tc.getCurrentListUpdate();
		System.out.printf("\ncurrent: Element at 1 is: %s\n", dc.getElementAt(1));
		dc.removeElementAt(1);
		dc = tc.getRetiredListUpdate();
		System.out.printf("retired: Element at 1 is: %s\n", dc.getElementAt(1));
		dc.removeElementAt(1);
		dc = tc.getDeletedListUpdate();
		System.out.printf("deleted: Element at 1 is: %s\n", dc.getElementAt(1));
		dc.removeElementAt(1);
		
		tc.reconcileModels(true);
		System.out.println("The updated DOM is:");
		System.out.printf("%s", Tk_XmlUtil.formatDoc(tc.getDom()));
	}
}
