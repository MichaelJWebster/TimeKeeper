package Tk_TaskSelectPanel;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.BevelBorder;

import Tk_Configuration.Tk_TaskCollection;

/**
 * TkAdditional Tasks is a panel for the right hand side of the Edit Tasks
 * dialog. It contains:
 * - A scrollable JList that displays the Additional Tasks
 * - A JTextBox that is used to input new tasks to be added.
 * - An AddTask Button to add the task in the textbox to the Additional Tasks
 * JList.
 * - A RemoveTask Button to remove the item selected in the Additional Tasks
 * JList from that JList and it's underlying model.
 * 
 * The class is constructed by supplying two DefaultComboBoxModel instances
 * containing the retired tasks that are displayed in the Additional Tasks
 * JList, and the deleted tasks that are not displayed.
 * @author michaelwebster
 *
 */
public class Tk_AdditionalTasks extends JPanel implements ListSelectionListener
{
	//
	// ActionCommand strings for the Add Task button, the Remove Task button
	// and the Enter Key pressed in the m_addText JTextField.
	//
	private static final String AddString 		= "Add";
	private static final String DeleteString 	= "Delete";
	private static final String EnterString 	= "Enter";

	
	JList<String>					m_retiredTasks 	= null;
	DefaultComboBoxModel<String>    m_listModel     = null;
	DefaultComboBoxModel<String>	m_deletedTasks	= null;
	JScrollPane     				m_sPane			= null;
	Box			    				m_buttonBox		= null;
	JButton							m_addTask		= null;
	JButton							m_removeTask	= null;
	JTextField						m_addText		= null;
	
	private Vector<String>m_addedTasks 		= new Vector<String>();
	private Vector<String>m_removedTasks 	= new Vector<String>();
	
	/**
	 * Create the Tk_AddtionalTasks object with the AdditionalTasks JList
	 * populated from the retTasks DefaultComboBoxModel parameter.
	 *
	 * @param retTasks   A DefaultComboBoxModel that lists the Tasks that
	 * 					 are currently in the retired state.
	 * @param delTasks   A DefaultComboBoxModel that lists the Tasks that
	 * 					 are currently in the deleted state.
	 */
	public Tk_AdditionalTasks
	(
		DefaultComboBoxModel<String> retTasks,
		DefaultComboBoxModel<String> delTasks
	)
	{
		m_listModel = retTasks;
		m_deletedTasks = delTasks;
		
		//
		// Create the JList and add the m_retiredTasks model to it for display
		// of the retired tasks.
		//
		m_retiredTasks = new JList<String>();
		m_retiredTasks.setModel(m_listModel);
		
		// Make this object a listener for selection events from the JList.
		m_retiredTasks.addListSelectionListener(this);
		
		// Create a scrollable pane to place the m_retiredTasks JList into.
		m_sPane = new JScrollPane();
		m_sPane.getViewport().add(m_retiredTasks);
		
		// Create the "Add Task" button, and set it to be disabled.
		m_addTask = new JButton("Add Task");
		m_addTask.setActionCommand(AddString);
		m_addTask.setEnabled(false);
		
		// Create the "Remove Task" button and set it enabled if there are
		// any Tasks in the list.
		m_removeTask = new JButton("Remove Task");
		m_removeTask.setActionCommand(DeleteString);
		if (m_listModel.getSize() > 0)
		{
			m_retiredTasks.setSelectedIndex(0);
			m_removeTask.setEnabled(true);
		}
		else
		{
			m_removeTask.setEnabled(false);
		}
		
		// Create the m_addText JTextField for adding new tasks.
		m_addText = new JTextField(20);
		m_addText.setActionCommand(EnterString);
		
		// Create the Listener object for handling actions from the two buttons
		// and the JTextField, as well as handling Document Events from the
		// JTextField.
		AddListener addListener = new AddListener(m_addTask);
		m_addTask.addActionListener(addListener);
		m_removeTask.addActionListener(addListener);
		m_addText.addActionListener(addListener);
		m_addText.getDocument().addDocumentListener(addListener);
		
		// Add a Box container for the "Add Task" and "Remove Task" buttons,
		// and lay them out horizonally.
		m_buttonBox = new Box(BoxLayout.X_AXIS);
		m_buttonBox.add(m_addTask);
		m_buttonBox.add(m_removeTask);

		// Layout this Container as a Box with Vertical layout.
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		// Add the components to this container.
		add(m_sPane);
		add(m_addText);
		add(m_buttonBox);
		
		// Set this Container's border.
		setBorder
		(
			BorderFactory.createTitledBorder
			(
				BorderFactory.createLineBorder(getForeground()),
				"Additional Tasks:"
			)
		);
	}
	
	JList<String> getRetiredTasks()
	{
		return m_retiredTasks;
	}
	
	/**
	 * This listener implements ActionListener and DocumentListener.
	 *
	 * The ActionListener is shared by the m_addText field and the m_addTask
	 * and m_removeTask JButtons.
	 * 
	 * The DocumentListener is used to pick up changes in the contents of
	 * the m_AddText JTextBox.
	 * 
	 * The following action commands are handled:
	 * - AddString:    	Received from the m_addTask button. The action is to
	 * 					add the string in the m_addText JTextField to the
	 * 					m_retiredTasks JList model.
	 * - DeleteString:	Received from the m_removeTask button. The action is to
	 * 					remove the currently selected item from the
	 * 					m_retiredTasks JList model, and add it to the
	 * 					m_deletedTasks model.
	 * - EnterString:   Received from the m_addText field when Enter is
	 * 					pressed. The action is to add the string in the
	 * 					m_addText JTextField to the m_retiredTasks JList model.
	 * 
	 * Upon receipt of a DocumentEvent, the m_addTask button is enabled or
	 * disabled according to whether there is text in the m_addText
	 * JTextField, or if it is empty.
	 * 
	 * @author michaelwebster
	 *
	 */
    class AddListener implements ActionListener, DocumentListener
    {
        private boolean alreadyEnabled = false;
        private JButton addButton;
       
        /**
         * Set the addButton attribute from the supplied JButton. This button
         * will be enabled or disabled depending on whether the Document in the
         * JTextField contains any text.
         * 
         * @param ab	The "Add Task" button instance.
         */
        public AddListener(JButton ab)
        {
            this.addButton = ab;
        }

        /**
         * Required by ActionListener interface. This method handles the
         * AddString, EnterString and DeleteString action commands that may
         * be received from the "Add Task" button, the "Remove Task" button
         * or the m_add
         * 
         *  @param e    The ActionEvent to be handled.
         */
        public void actionPerformed(ActionEvent e)
        {
        	if 
        	(
        		e.getActionCommand().equals(AddString)
        		||
        		e.getActionCommand().equals(EnterString)
        	)
        	{
        		String name = m_addText.getText();

        		//User didn't type in a unique name...
        		if (name.equals("") || alreadyInList(name))
        		{
        			Toolkit.getDefaultToolkit().beep();
        			m_addText.requestFocusInWindow();
        			m_addText.selectAll();
        			return;
        		}

        		int index = m_retiredTasks.getSelectedIndex(); //get selected index
        		if (index == -1)
        		{
        			//no selection, so insert at beginning
        			index = 0;
        		}
        		else
        		{           //add after the selected item
        			index++;
        		}

        		m_listModel.insertElementAt(name, index);

        		//If we just wanted to add to the end, we'd do this:
        		//listModel.addElement(employeeName.getText());
        		//Reset the text field.
        		m_addText.requestFocusInWindow();
        		m_addText.selectAll();

        		//Select the new item and make it visible.
        		m_retiredTasks.setSelectedIndex(index);
        		m_retiredTasks.ensureIndexIsVisible(index);
        		m_addedTasks.add(name);
        	}
        	else if (e.getActionCommand().equals(DeleteString))
        	{
        		int index = m_retiredTasks.getSelectedIndex(); //get selected index
        		if (index == -1)
        		{
        			//no selection <= This shouldn't happen...
        			Toolkit.getDefaultToolkit().beep();
        			return;
        		}
        		else
        		{           //add after the selected item
        			String delEl = m_listModel.getElementAt(index);
        			m_listModel.removeElementAt(index);
        			m_deletedTasks.addElement(delEl);
        			if (m_listModel.getSize() == index)
        			{
        				index--;
        			}
        			m_retiredTasks.setSelectedIndex(index);
        			m_retiredTasks.ensureIndexIsVisible(index);
        			m_removedTasks.add(delEl);
        		}
        	}
        }
        
        
        //This method tests for string equality. You could certainly
        //get more sophisticated about the algorithm.  For example,
        //you might want to ignore white space and capitalization.
        /**
         * Check if the String name is already in the m_listModel.
         *
         * @param name	A candidate string for addition to the JList.
         *
         * @return 	true  => name is already in m_listModel - don't add it.
         * 			false => name is not in m_listModel - ok. to add.
         */
        protected boolean alreadyInList(String name)
        {
        	for (int i = 0; i < m_listModel.getSize(); i++)
        	{
        		if (name.equals(m_listModel.getElementAt(i)))
        		{
        			return true;
        		}
        	}
        	return false;
        }

        //Required by DocumentListener.
        /**
         * The document has been inserted to. The "Add Task" button should
         * now be enabled.
         * 
         * @param e		The DocumentEvent sent from the m_addText JTextField.
         */
        public void insertUpdate(DocumentEvent e)
        {
            enableAddButton();
        }

        //Required by DocumentListener.
        /**
         * The document has had content removed. Disable the the "Add Task"
         * button if the Document is now empty.
         * 
         * @param e		The DocumentEvent sent from the m_addText JTextField.
         */
        public void removeUpdate(DocumentEvent e)
        {
            handleEmptyTextField(e);
        }

        //Required by DocumentListener.
        /**
         * The document has had content changed in some way. Disable, or enable
         * the the "Add Task" button depending on whether the Document is 
         * empty or not.
         * 
         * @param e		The DocumentEvent sent from the m_addText JTextField.
         */        
        public void changedUpdate(DocumentEvent e)
        {
            if (!handleEmptyTextField(e))
            {
                enableAddButton();
            }
        }

        /**
         * Enable the "Add Task" button if it has not already been enabled.
         */
        private void enableAddButton()
        {
            if (!alreadyEnabled)
            {
                addButton.setEnabled(true);
                alreadyEnabled = true;
            }
        }

        /**
         * Disable the "Add Task" button if the Document is empty.
         *
         * @param e		The Document Event.
         * @return		true => the Document in the m_addTextField, is empty.
         *				false => the Document is not empty.
         */
        private boolean handleEmptyTextField(DocumentEvent e)
        {
            if (e.getDocument().getLength() <= 0)
            {
                addButton.setEnabled(false);
                alreadyEnabled = false;
                return true;
            }
            return false;
        }
    }

    //This method is required by ListSelectionListener.
    /**
     * Disable or enable the "Remove Task" button, depending on whether the
     * list is not empty and an entry is selected.
     *
     * @param e 	The ListSelectionEvent from the m_retiredTasks JList.
     */
    public void valueChanged(ListSelectionEvent e)
    {
        if (e.getValueIsAdjusting() == false)
        {
            if (m_retiredTasks.getSelectedIndex() == -1)
            {
            	//No selection, disable fire button.
                m_removeTask.setEnabled(false);

            }
            else
            {
            	//Selection, enable the fire button.
                m_removeTask.setEnabled(true);
            }
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
		DefaultComboBoxModel<String>ret_tasks = tc.getRetiredListUpdate();
		DefaultComboBoxModel<String>del_tasks = tc.getDeletedListUpdate();
		
		Tk_AdditionalTasks tat = new Tk_AdditionalTasks(ret_tasks, del_tasks);
		
		JFrame jf = new JFrame();
		jf.add(tat);
		jf.pack();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		
	}

	public Vector<String> getAddedTasks()
	{
		// TODO Auto-generated method stub
		return m_addedTasks;
	}

    public Vector<String>getRemovedTasks()
    {
    	return m_removedTasks;
    }

}
