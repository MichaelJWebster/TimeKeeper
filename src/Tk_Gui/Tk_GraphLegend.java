package Tk_Gui;

import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.BevelBorder;

/**
 * Create a legend for a task history graph. The legend is a 
 * <code>JPanel</code> that contains other <code>JPanel</code>s for the
 * legend entries.
 *
 * @author michaelwebster
 */
public class Tk_GraphLegend extends JPanel
{
	/**
	 * The legend entries each consist of a <code>JPanel</code> that contains
	 * two <code>JLabel</code>s objects.
	 */
	private Vector<JPanel>m_legendEntries = null;
	
	/**
	 * <code>m_textureMap contains a mapping between the names of tasks, and
	 * the <code>TexturePaint</code>s used to display those tasks in the
	 * history bar graph.
	 */
	private HashMap<String, TexturePaint>m_textureMap = null;

	/**
	 * <p>Create the <code>Tk_GraphLegend</code> with the given size. The legend
	 * is created by:
	 * <ol>
	 * <li>Obtain the static texture map from the <code>Tk_BarChartEntry</code>
	 * class.</li>
	 * <li>Create the legend's <code>JPanel</code>s and store them in the private
	 * vector <code>m_legendEntry</code>s</li>
	 * <li>Add all the <code>JPanel</code>s in <code>m_legendEntry</code> to
	 * this class's panel.</li>
	 * </ol>
	 * </p> 
	 * @param r		The rectangular bounds for this <code>JPanel</code>
	 * 				container.
	 */
	public Tk_GraphLegend(Rectangle r)
	{
		super();
		setMinimumSize(r.getSize());
		setPreferredSize(r.getSize());
		setMaximumSize(r.getSize());
		setBounds(r);
		m_textureMap = Tk_BarChartEntry.getTextureMap();
		setLayout(new FlowLayout());
		createLegendEntries();
		JLabel glegend = new JLabel("Graph Legend:");
		add(glegend);
		Iterator<JPanel>pit = m_legendEntries.iterator();

		while (pit.hasNext())
		{
			JPanel jp = pit.next();
			add(jp);
		}
	}
	
	/**
	 * Create texture label and task name <code>JLabel</code>s for the legend
	 * entries. Add the task name string to the task name panel, and paint 
	 * a square sample of the texture paint corresponding to that task name
	 * into the texture label. Add the texture label an task name label to a
	 * new <code>JPanel</code>, and add that JPanel to the
	 * <code>m_legendEntries Vector</code>. 
	 */
	private void createLegendEntries()
	{
		Iterator<String>sit = m_textureMap.keySet().iterator();
		m_legendEntries = new Vector<JPanel>();
		while (sit.hasNext())
		{
			String n = sit.next();
			JLabel textureLabel 	= new JLabel();
			textureLabel.setOpaque(true);
			Rectangle r = new Rectangle(0,0,20,20);
			textureLabel.setMinimumSize(r.getSize());
			textureLabel.setMaximumSize(r.getSize());
			textureLabel.setPreferredSize(r.getSize());
			textureLabel.setBounds(r);
			BufferedImage bi = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = bi.createGraphics();
			g2.setPaint(m_textureMap.get(n));
			g2.fill(r);
			ImageIcon im = new ImageIcon(bi);
			textureLabel.setIcon(im);
			JLabel nameLabel	= new JLabel(n);
			JPanel jp = new JPanel();
			jp.add(textureLabel);
			jp.add(nameLabel);
			jp.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			m_legendEntries.add(jp);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		JFrame jf = new JFrame("Testing");
		
		Rectangle r = new Rectangle(50,50, 200,200);
		Tk_GraphLegend tgl = new Tk_GraphLegend(r);
		//jf.setUndecorated(true);
		jf.add(tgl);
		jf.pack();
		//tgl.setBounds(new Rectangle(0,0,200,200)); //tgl.getBounds());
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}
