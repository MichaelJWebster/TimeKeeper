package Tk_TaskSelectPanel;

import javax.swing.*;


public class Tk_ButtonBox extends Box
{
	private JButton		m_okButton		= null;
	private JButton		m_cancelButton 	= null;
	public Tk_ButtonBox()
	{
		super(BoxLayout.LINE_AXIS);
		m_okButton = new JButton("Ok");
		m_cancelButton = new JButton("Cancel");
		add(m_okButton);
		add(m_cancelButton);
	}
	
	public JButton getOkButton()
	{
		return m_okButton;
	}
	
	public JButton getCancelButton()
	{
		return m_cancelButton;
	}
	
	public static void main(String[] args)
	{
		Tk_ButtonBox tbb = new Tk_ButtonBox();
		JFrame jf = new JFrame("Button Box Test!");
		jf.add(tbb);
		jf.pack();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}
}
