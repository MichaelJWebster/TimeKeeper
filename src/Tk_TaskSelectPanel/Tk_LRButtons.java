package Tk_TaskSelectPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Rectangle;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.Box.Filler;
import javax.swing.border.*;

@SuppressWarnings("serial")
public class Tk_LRButtons extends Box //JPanel
{
	JButton m_moveLeftButton;
	JButton m_moveRightButton;
	
	public Tk_LRButtons()
	{
		super(BoxLayout.PAGE_AXIS);
		Rectangle r = new Rectangle(0, 0, 40, 210);
		setPreferredSize(r.getSize());
		setMinimumSize(r.getSize());
		setMaximumSize(r.getSize());
		setBounds(r);
		m_moveLeftButton = new BasicArrowButton(BasicArrowButton.WEST);
		m_moveRightButton = new BasicArrowButton(BasicArrowButton.EAST);
		Component strut = createVerticalStrut(70);
		add(strut);
		add(m_moveLeftButton);
		strut = createVerticalStrut(20);
		add(strut);
		add(m_moveRightButton);
		strut = createVerticalStrut(70);
		add(strut);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
	}
	
	public JButton getLeftButton()
	{
		return m_moveLeftButton;
	}
	
	public JButton getRightButton()
	{
		return m_moveRightButton;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		JFrame jf = new JFrame();
		Tk_LRButtons lrb = new Tk_LRButtons();
		jf.add(lrb);
		jf.pack();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);

		ActionListener al =
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if ("left".equals(e.getActionCommand()))
					{
						System.out.println("Left Button Clicked!");
					}
					else if ("right".equals(e.getActionCommand()))
					{
						System.out.println("Right Button Clicked!");
					}
				}
			};
		lrb.getLeftButton().addActionListener(al);
		lrb.getRightButton().addActionListener(al);
	}

}
