package Tk_Gui;

import java.util.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * <p>Bar Draws a coloured bar of a certain width on a canvas. The following
 * parameters give it the information it needs to do it's job:
 * <ol>
 * <li>An orientation (Horizontal or Vertical)</li>
 * <li>A direction of growth (Up, Down, Left or Right)</li>
 * <li>A colour.</li>
 * <li>An origin point that, depending on the orientation, or dirction of
 *    growth, will be the position of the top left, bottom left, or top right
 *    corner of the bar.</li>
 * <li>The width of the bar,</li>
 * <li>A Graphics2D instance,</li>
 * <li>A minimum and maximum length or height of the bar.</li>
 * <li>A source of events that cause the update of the bar.</li>
 * <li>A relationship between the quantity received in the events and the
 * 	  size to display on screen.</li>
 * </ol>
 * </p>
 * 
 * @author michaelwebster
 *
 */
public class Tk_Bar extends Tk_BarChartEntry
{
	private Rectangle2D.Double m_bar	= null;
	
	/**
	 * The name of the bar chart entry.
	 */
	private String m_name				= null;
	
	/**
	 * The paint used to paint this bar chart entry - if it is a Tk_Bar.
	 */
	protected TexturePaint m_paint		= null;
	
	public static final long MinWidth		= 20;
	public static final long PrefWidth		= 20;
	public static final long MaxWidth		= 40;

	public Tk_Bar
	(
		Tk_GraphDirection 		dir,
		String					name,
		Point2D.Double			origin,
		long					initial_width,
		long					initial_height,
		Tk_BarChartEntry		parent
	)
	{
		super(dir, origin, initial_width, initial_height, parent);
		m_name = name;
		createBar();
	}

	public void createBar()
	{
		m_bar = 
			new Rectangle2D.Double
			(
				getOrigin().x,
				getOrigin().y - getInitialHeight(),
				getWidth(),
				getHeight()
			);
		m_paint = this.getTexturePaint(m_name);
		
	}

	@Override
	public long getMinimumWidth()
	{
		return MinWidth;
	}
	
	@Override
	public long getPreferredWidth()
	{
		return PrefWidth;
	}

	@Override
	public long getMaximumWidth()
	{
		return MaxWidth;
	}

	@Override
	public void paint(Graphics2D g2)
	{
		Stroke s = new BasicStroke(1);
		g2.setStroke(s);
		g2.setColor(Color.black);
		g2.draw(m_bar);
		g2.setPaint(m_paint);
		g2.fill(m_bar);
		g2.setColor(Color.black);
	}
	
	@Override
	public void setWidth(long new_width)
	{
		m_width = new_width;
	}
	
	@Override
	public void setHeight(long new_height)
	{
		m_height = new_height;
	}
	
	@Override
	public void setOrigin(Point2D.Double new_origin)
	{
		m_origin = new_origin;
	}
	
	public void changeBar(long new_width, long new_height, Point2D.Double new_origin)
	{
		setWidth(new_width);
		setHeight(new_height);
		setOrigin(new_origin);
		createBar();
	}
	
	public String getName()
	{
		return m_name;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
