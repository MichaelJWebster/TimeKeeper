package Tk_Gui;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * <p>A <code>Tk_ChartEntry</code> is either a <code>Tk_Bar</code> or a
 * <code>Tk_Container</code>. That is, the <code>Tk_ChartEntry</code>
 * class and it's subclasses are an implementation of the Composite design
 * pattern.</p> 
 * 
 * <p>The following parameters give it the information it needs to do its
 * job:
 * <ol> 
 * <li>A direction of growth (Up, Down, Left or Right)</li>
 * <li>An origin point that, depending on the orientation, or direction of
 * growth, will be the position of the top left, bottom left, or top right
 * corner of the bar.</li>
 * <li>The width of the <code>Tk_CharEntry</code>,</li>
 * <li>A Graphics2D instance for drawing into,</li>
 * <li>A minimum and maximum length or height of the Tk_CharEntry.</li>
 * </ol>
 * </p>
 *
 * <p>Along with the above initialization parameters, the
 * <cod>Tk_ChartEntry</code> container must be able to contain children of
 * type <code>Tk_ChartEntry</code>. Due to the fact that a
 * <code>Tk_ChartEntry</code> object may need to contact its parent,
 * <code>Tk_ChartEntry</code> will also include a parent reference.</p>
 * 
 * @author michaelwebster
 */
public abstract class Tk_BarChartEntry implements Tk_ChartEntry
{
	/**
	 * The graph orientation can be vertical or horizontal.
	 */
	private Tk_GraphDirection m_growDirection = Tk_GraphDirection.Illegal;
	
	/**
	 * The parent of this <code>Tk_ChartEntry</code>.
	 */
	private Tk_BarChartEntry m_parent = null;
	
	/**
	 * The origin for this Tk_ChartEntry element.
	 */
	protected Point2D.Double m_origin = null;
	
	/**
	 * The outline rectangle that completely covers this entry.
	 */
	protected Rectangle2D.Double m_oldOutline = null;

	/**
	 * The outline rectangle that completely covers this entry.
	 */
	protected Rectangle2D.Double m_newOutline = null;
	
	/**
	 * The width of the <code>Tk_ChartEntry</code>.
	 */
	protected long m_width				= 0;
	
	/**
	 * The height of the <code>Tk_ChartEntry</code>.
	 */
	protected long m_height 			= 0;
	
	/**
	 * The initial width of the <code>Tk_ChartEntry</code>.
	 */
	private long m_initialWidth			= 0;

	/**
	 * The initial height of the <code>Tk_ChartEntry</code>.
	 */
	private long m_initialHeight 		= 0;
	
	/**
	 * A pseudo random number generator for selecting random TexturePanints
	 * from the m_paints vector.
	 */
	private static Random m_indexGenerater;

	/**
	 * Colours for the bars on this graph. 
	 */
	private Color[] m_colours = 
		{
			Color.blue,
			Color.magenta,
			Color.green,
			Color.red,
			Color.yellow,
			Color.cyan,
			Color.orange,
	        Color.pink,
	        Color.gray,
	        Color.darkGray,
	        Color.black,
	        Color.lightGray
	    };
	
	
	/**
	 * The vector containing all of our Texture paints.
	 */
	private static Vector<TexturePaint>m_texturePaints = null;
	
	/**
	 * A mapping from task strings to TexturePaints.
	 */
	private static HashMap<String, TexturePaint>m_textureMap = null;

	/**
	 * Create a new Tk_BarChartEntry.
	 *
	 * @param dir				The direction of growth for the graph.
	 * @param origin			The coordinate of the bottom left corner of the
	 * 							entry, if dir is left to right.
	 * @param initial_width		The initial width of this  entry.
	 * @param initial_height	The initial height of this entry. 
	 * @param parent
	 */
	protected Tk_BarChartEntry
	(
		Tk_GraphDirection 		dir,
		Point2D.Double			origin,
		long					initial_width,
		long					initial_height,
		Tk_BarChartEntry		parent
	)
	{
		m_growDirection 	= dir;
		m_origin 			= origin;
		m_initialWidth		= initial_width;
		m_width				= initial_width;
		m_initialHeight		= initial_height;
		m_height			= initial_height;
		m_parent			= parent;
		m_oldOutline = new Rectangle2D.Double(m_origin.x, m_origin.y, m_width, m_height);
	}
	
	
	/**
	 * Return the <code>HashMap m_textureMap</code> containing the mapping
	 * between task names and TexturePaints for entries in the bar graph. 
	 * @return
	 */
	public static HashMap<String, TexturePaint>getTextureMap()
	{
		return m_textureMap;
	}
	
	/**
	 * Not used at present.
	 * @param origin
	 * @param w
	 * @param h
	 */
	public void setNewOutline(Point2D.Double origin, long w, long h)
	{
		m_newOutline = new Rectangle2D.Double(origin.x, origin.y, w, h);
	}
	
	/**
	 * If a mapping for name already exists in the <code>m_textureMap</code>,
	 * just return that mapping. Otherwise, randomly select a new
	 * <code>TexturePaint</code> from the <code>m_texturePaints vector</code>,
	 * add a mapping for it, and return it.
	 *
	 * @param name		A task name that for which we want to obtain the 
	 * 					<code>TexturePaint</code> used to render it.
	 * @return			The <code>TexturePaint</code> used to render
	 * 					graph bars for the task "name".
	 */
	public TexturePaint getTexturePaint(String name)
	{
		if (m_textureMap == null)
		{
			Date d = new Date();
			m_indexGenerater = new Random(d.getTime());
			setupTextures();
			m_textureMap = new HashMap<String, TexturePaint>();
		}
		
		if (!m_textureMap.containsKey(name))
		{
			int v_length = m_texturePaints.size();
			int index = (int)(v_length * m_indexGenerater.nextDouble());
			TexturePaint tp = m_texturePaints.remove(index);
			m_textureMap.put(name,  tp);
		}
		return m_textureMap.get(name);
	}
	
	/**
	 * 
	 */
	private void setupTextures()
	{
		Vector<Lines>nl = setupLines();
		m_texturePaints = new Vector<TexturePaint>();

		// Setup plain colours.
		for (int i = 0; i < m_colours.length; i++)
		{
			Color fg = m_colours[i];
			BufferedImage bi = new BufferedImage(20,20, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = bi.createGraphics();
			g2.setColor(fg);
			g2.fillRect(0,  0,  20,  20);
			Rectangle r = new Rectangle(0, 0, 20, 20);
			TexturePaint tp = new TexturePaint(bi, r);
			m_texturePaints.add(tp);
		}

		Iterator<Lines>lit = nl.iterator();
		while (lit.hasNext())
		{
			Lines ls = lit.next();
			for (int i = 0; i < m_colours.length; i++)
			{
				Color bg = m_colours[i];
				Color fg = Color.black;
				if (bg == Color.black)
				{
					fg = Color.white;
				}
			
				//Iterator<Lines>lit = nl.iterator();
				//while (lit.hasNext())
				//{
				
				TexturePaint tp = ls.getTexture(bg, fg);
				m_texturePaints.add(tp);
			//}
			}
		}
	}
	
	private Vector<Lines>setupLines()
	{
		Vector<Lines>nl = new Vector<Lines>();
		//int box_size = Lines.BoxSize;
		
		Line l1 = new Line(0, 0, 19, 19);
		Line l2 = new Line(0, 19, 19, 0);
		Lines lines = new Lines();
		lines.addLine(l1);
		lines.addLine(l2);
		nl.add(lines);
		
		/*
		 * Diagonal lines with positive gradient.
		 */
		l1 = new Line(0, 0, 9, 19);
		l2 = new Line(10, 0,19 , 19);
		lines = new Lines();
		lines.addLine(l1);
		lines.addLine(l2);
		nl.add(lines);

		/*
		 * Same thing with a negative gradient.
		 */
		l1 = new Line(19, 0, 10, 19);
		l2 = new Line(9, 0,0 , 19);
		lines = new Lines();
		lines.addLine(l1);
		lines.addLine(l2);
		nl.add(lines);
		
		/*
		 * Wavy Lines
		 */
		l1 = new Line(0, 0, 19, 10);
		l2 = new Line(19, 10, 0 , 19);
		lines = new Lines();
		lines.addLine(l1);
		lines.addLine(l2);
		nl.add(lines);
		
		/*
		 * SawTooth
		 */
		l1 = new Line(0, 0, 10, 19);
		l2 = new Line(10, 19, 19 , 0);
		lines = new Lines();
		lines.addLine(l1);
		lines.addLine(l2);
		nl.add(lines);
		
		/**
		 * Paralell Horizontal.
		 */
		l1 = new Line(0, 1, 19, 1);
		l2 = new Line(0, 6, 19 , 6);
		Line l3 = new Line(0, 11, 19, 11);
		Line l4 = new Line(0, 16, 19 , 16);
		lines = new Lines();
		lines.addLine(l1);
		lines.addLine(l2);
		lines.addLine(l3);
		lines.addLine(l4);
		nl.add(lines);
		
		/**
		 * Paralell Vertical.
		 */
		l1 = new Line(1, 0, 1, 19);
		l2 = new Line(6, 0, 6 , 19);
		l3 = new Line(11, 0, 11, 19);
		l4 = new Line(16, 0, 16, 19);
		lines = new Lines();
		lines.addLine(l1);
		lines.addLine(l2);
		lines.addLine(l3);
		lines.addLine(l4);
		nl.add(lines);
		nl.add(lines);
		return nl;
	}

	public Tk_GraphDirection getGrowDirection()
	{
		return m_growDirection;
	}
	
	public Tk_BarChartEntry getParent()
	{
		return m_parent;
	}
	
	public Point2D.Double getOrigin()
	{
		return m_origin;
	}

	public long getWidth()
	{
		return m_width;
	}
	
	public abstract void setWidth(long new_width);
	public abstract void setHeight(long new_height);
	public abstract void setOrigin(Point2D.Double new_origin);
	public abstract void changeBar
		(long new_width, long new_height, Point2D.Double new_origin);

	public long getHeight()
	{
		return m_height;
	}
	
	public long getInitialWidth()
	{
		return m_initialWidth;
	}
	
	public long getInitialHeight()
	{
		return m_initialHeight;
	}
	
	public class Lines
	{
		public static final int BoxSize = 20;
		private int m_boxSize = 0;
		private Vector<Line>m_lines = null;

		public Lines()
		{
			this(BoxSize);
		}

		public Lines(int box_size)
		{
			m_boxSize = box_size;
			m_lines = new Vector<Line>();		
		}

		public void addLine(Line l)
		{
			m_lines.add(l);
		}
		
		public Vector<Line>getLines()
		{
			return m_lines;
		}

		public TexturePaint getTexture(Color bg, Color fg)
		{
			BufferedImage bi = 
				new BufferedImage
				(
					m_boxSize, m_boxSize, BufferedImage.TYPE_INT_ARGB
				);
			Graphics2D g = bi.createGraphics();
			g.setColor(bg);
			g.fillRect(0, 0, m_boxSize, m_boxSize);
			g.setColor(fg);
			g.setStroke(new BasicStroke((float)2.0));
			Iterator<Line>lit = m_lines.iterator();
			while (lit.hasNext())
			{
				Line l = lit.next();
				g.drawLine(l.m_x0,  l.m_y0,  l.m_x1, l.m_y1);
			}
			Rectangle2D r = new Rectangle2D.Double(0, 0, m_boxSize, m_boxSize);
			TexturePaint tp = new TexturePaint(bi, r);
			return tp;
		}
	}

	public class Line
	{
		public int m_x0 = 0;
		public int m_y0 = 0;
		public int m_x1 = 0;
		public int m_y1 = 0;
		
		public Line(int x0, int y0, int x1, int y1)
		{
			m_x0 = x0;
			m_y0 = y0;
			m_x1 = x1;
			m_y1 = y1;
		}
	}
}