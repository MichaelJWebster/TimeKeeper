package Tk_Gui;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.geom.Point2D.Double;

public class Tk_BarContainer extends Tk_BarChartEntry
{
	public static final double BarPart	= 0.8;
	public static final double GapPart	= 0.2;

	/**
	 * The children of this <code>Tk_ChartEntry</code>.
	 * Fixme: This should go in the Tk_BarContainer child. 
	 */
	private Vector<Tk_ChartEntry>m_children = null;
	
	private String m_catName				= null;
	
	/**
	 * 
	 */
	private long m_barWidth						= 0;
	
	private long m_barGap;
	/**
	 * 
	 * @param dir
	 * @param name
	 * @param canvas
	 * @param origin
	 * @param initial_width
	 * @param initial_height
	 * @param parent
	 */
	
	public Tk_BarContainer
	(
		Tk_GraphDirection 		dir,
		String					cat_name,
		String					name,
		Point2D.Double			origin,
		long					initial_width,
		long					initial_height,
		Tk_BarChartEntry		parent
	)
	{
		super(dir, origin, initial_width, initial_height, parent);
		m_catName = cat_name;
		m_barWidth = (long)Math.ceil(0.8 * initial_width);
		m_barGap   = initial_width - m_barWidth;
		m_children = new Vector<Tk_ChartEntry>();
	}
	
	public void setChildProperties()
	{
		if (m_children.size() > 0)
		{
			long total_bar_width = (long)Math.ceil(0.8 * getWidth());
			m_barWidth = (long)Math.floor(total_bar_width/(m_children.size() + 1));
			long total_bar_gap   = getWidth() - total_bar_width;
			m_barGap = (long)Math.floor(total_bar_gap/(m_children.size() + 1));
		}
	}
	
	public boolean canTakeOneMore()
	{
		if (m_children.size() == 0)
		{
			return true;
		}
		long total_bar_width = (long)Math.ceil(0.8 * getWidth());
		m_barWidth = (long)Math.floor(total_bar_width/(m_children.size() + 1));
		long total_bar_gap   = getWidth() - total_bar_width;
		m_barGap = (long)Math.floor(total_bar_gap/(m_children.size() + 1));
		if ((total_bar_width + total_bar_gap) < getMinimumWidth())
		{
			return false;
		}
		long single_bar_width = (long)Math.ceil((double)total_bar_width/(m_children.size() + 1));
		
		if (single_bar_width < m_children.get(0).getMinimumWidth())
		{
			return false;
		}
		return true;
	}
	
	public void addBarElement(String name, long h)
	{
		//boolean bar_ok = getNewBarWidth();
		long origin_x = (long)getOrigin().x + m_barGap/2;
		for (int i = 0; i < m_children.size(); i++)
		{
			Point2D.Double new_origin = new Point2D.Double(origin_x, m_origin.y);
			Tk_BarChartEntry tbc = (Tk_BarChartEntry)m_children.get(i); 
			tbc.changeBar(m_barWidth, tbc.getHeight(), new_origin);
			origin_x +=  (m_barWidth + m_barGap);
		}

		Tk_Bar tb =
			new Tk_Bar
			(
				getGrowDirection(),
				name,
				new Point2D.Double(origin_x, m_origin.y),
				m_barWidth,
				h,
				this
			);
		addChild(tb);
	}
	
	private void addChild(Tk_BarChartEntry tbc)
	{
		m_children.add(tbc);
	}

	@Override
	public long getMinimumWidth()
	{
		// assume all children have the same width properties, and return
		// the width as (1 / BarPart) * minimum width of all children.
		long min_child_width = m_children.size() > 0 ?
				m_children.get(0).getMinimumWidth() : 0;
		long bar_container_min =
				(long)Math.floor(((m_children.size() + 1) * min_child_width)/BarPart);
		return bar_container_min;
	}
	
	@Override
	public long getPreferredWidth()
	{
		// assume all children have the same width properties, and return
		// the width as (1 / BarPart) * preferred width of all children.
		long pref_child_width = m_children.size() > 0 ?
				m_children.get(0).getPreferredWidth() : 0;
		long bar_container_pref =
				(long)Math.floor((m_children.size() * pref_child_width)/BarPart);
		return bar_container_pref;
	}

	@Override
	public long getMaximumWidth()
	{
		// assume all children have the same width properties, and return
		// the width as (1 / BarPart) * maximum width of all children.
		long max_child_width = m_children.size() > 0 ?
				m_children.get(0).getMaximumWidth() : 0;
		long bar_container_max =
				(long)Math.floor((m_children.size() * max_child_width)/BarPart);
		return bar_container_max;
	}

	
	@Override
	public void paint(Graphics2D g2)
	{
		//super.paint(g2);
		Iterator<Tk_ChartEntry> it = m_children.iterator();
		while (it.hasNext())
		{
			it.next().paint(g2);
		}

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

	@Override
	public void changeBar(long new_width, long new_height, Point2D.Double new_origin)
	{
		setWidth(new_width);

		m_barWidth = (long)Math.ceil(0.8 * getWidth()/(m_children.size() + 1));
		m_barGap = (long)Math.floor(0.2 * getWidth()/(m_children.size() + 1));

		setHeight(new_height);
		setOrigin(new_origin);
		setNewOutline(m_origin, new_width, new_height);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}



}
