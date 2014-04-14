package Tk_TaskSelectPanel;

import java.awt.event.*;
//import java.awt.*;
import javax.swing.*;

import Tk_Configuration.Tk_TaskCollection;


public class Tk_ComboBox extends JComboBox<String>
{
	
	DefaultComboBoxModel<String>m_model = null;
	/**
	 * Construct a Tk_TaskCollection, and add it's DefaultComboBoxModel
	 * m_currentList as the model of this combo box.
	 *
	 * @param uri_string		The uri of the xml containing the lists of
	 * 							tasks to appear in this combo box.
	 * @param schema_string     The uri of the xml schema for the above
	 *                          xml file.
	 */
	public Tk_ComboBox(DefaultComboBoxModel<String>combo_model)
	{
		//m_taskCollection = new Tk_TaskCollection(uri_string, schema_string); //, this);
		m_model = combo_model;
		setModel(m_model);
		if (m_model.getSize() != 0)
		{
			setSelectedIndex(0);
		}
	}
	
	public DefaultComboBoxModel<String>getCurrentList()
	{
		return m_model;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		JFrame jf = new JFrame();
		jf.setLayout(new BoxLayout(jf.getContentPane(), BoxLayout.X_AXIS));
		Tk_TaskCollection t_col =
		new Tk_TaskCollection
		(
			"file:///Users/michaelwebster/Documents/workspace/TimeKeeper/src/Tk_Configuration/TestData/TK_TaskList.xml",
			"file:////Users/michaelwebster/Documents/workspace/TimeKeeper/src/Tk_Configuration/TestData/TK_TaskList.xsd"
		);
		Tk_ComboBox tc = new Tk_ComboBox(t_col.getCurrentList());
		jf.add(tc);
		jf.pack();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}

}
