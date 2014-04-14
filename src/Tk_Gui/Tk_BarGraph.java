package Tk_Gui;
import Tk_utils.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import javax.xml.datatype.*;
import javax.swing.*;

import java.lang.Math;
import java.util.*;

public class Tk_BarGraph<T> extends Tk_Graph<T>
{
	private Vector<String> 			m_categories 		= null;
	private Vector<GlyphVector>		m_xGlyphs			= null;
	private Font 					m_xLabelFont			= null;
	
	private long m_entryWidth							= 0;

	public Tk_BarGraph
	(
		Tk_GraphDirection gd,
		long height_on_screen,
		long start_range,
		long end_range,
		long width_on_screen,
		Vector<String>x_categories,
		long big_y_ticks,
		long small_y_ticks,
		String x_label,
		String y_label,
		Tk_Converter<T, Long> converter,
		long graph_entry_width
	)
	{
		super
		(
			gd,
			height_on_screen,
			start_range,
			end_range,
			width_on_screen,
			0,
			x_categories.size() * graph_entry_width,
			big_y_ticks,
			small_y_ticks,
			x_categories.size(),
			0,
			x_label,
			y_label,
			converter
		);
		m_categories = x_categories;
		m_entryWidth = graph_entry_width;
		createXAxis();
	}
	
	/**
	 * Paint the graph, including any <code>Tk_ChartEntry</code>s that belong
	 * on the graph.
	 */
	public void paintComponent(Graphics g)
    {
    	super.paintComponent(g);
    	Graphics2D g2 = (Graphics2D)g;

    	// If the font is null, create it and the glyph vectors for labelling
    	// the x and y axes.
    	if (null == m_xLabelFont)
    	{
    		m_xLabelFont = new Font("Serif", Font.BOLD, (int)Math.min(getHeightBuffer()/2, getWidthBuffer()/2));
    		m_xGlyphs = new Vector<GlyphVector>();
    		g2.setFont(m_xLabelFont);
    		AffineTransform at = null;
    		String longest_string = null;
    		Rectangle2D longest_rect = null;
    		for (int i = 0; i < m_categories.size(); i++)
    		{
    			String next_label = m_categories.get(i);
    			Rectangle2D string_rec = m_xLabelFont.getStringBounds(next_label, g2.getFontRenderContext());
    			if (longest_rect == null)
    			{
    				longest_rect = string_rec;
    				longest_string = next_label;
    			}
    			else if (longest_rect.getWidth() < string_rec.getWidth())
    			{
    				longest_rect = string_rec;
    				longest_string = next_label;
    			}
    		}
    		double x_scale = 0.7 * m_entryWidth / longest_rect.getWidth();
    		at = AffineTransform.getScaleInstance(x_scale, x_scale);
    		Font scaled_font = m_xLabelFont.deriveFont(at);
    		for (int i = 0; i < m_categories.size(); i++)
    		{
    			String next_label = m_categories.get(i);
    			m_xGlyphs.add
    			(
    				i,
					scaled_font.createGlyphVector(g2.getFontRenderContext(), next_label)
    			);
    		}
    	}

    	for (int i = 0; i < m_xGlyphs.size(); i++)
    	{
    		long next_pos = i * m_entryWidth + m_entryWidth / 2;
    		GlyphVector gv = m_xGlyphs.get(i);
    		Shape s = gv.getOutline();
    		g2.drawGlyphVector
    		(
    			gv,
    			next_pos + getWidthBuffer() - s.getBounds().width/2,
    			getComponentHeight() - getHeightBuffer()/2
    		);
    	}
    }
	

	@Override
	public void createXAxis()
	{
		super.createXAxis();
		long x_val = getEntryWidth();
		m_smallXTicksPath.reset();
		while (x_val <= getEndDomain() - getStartDomain())
		{
			m_smallXTicksPath.moveTo(getWidthBuffer() + x_val, 11 * getHeightBuffer());		//  + x_convert
			m_smallXTicksPath.lineTo(getWidthBuffer() + x_val, 11.2 * getHeightBuffer());	//  + x_convert
			x_val+= getEntryWidth();
		}
	}

	/**
	 * Add a <code>Tk_BarContainer</code>  for category <code>cat_name</code>
	 * if there isn't already on in the graph, and add the new Bar with name
	 * <code>bar_name</code> to it. The newly added <code>Tk_BarContainer</code>
	 * is a container for the graph's bars for the givent category name.
	 * Having added the new bar container element to the graph, subsequent
	 * calls to this method for the same String <code>cat_name</code> will
	 * simply add another bar to the <code>Tk_BarContainer</code> element.
	 *
	 * @param cat_name	The name of the category (on the x-axis) where the
	 *                  new bar, and hence the new bar container should appear.
	 * @param bar_name	The name of the new bar - for example a task name.
	 * @param x_origin	The position of the bottom left corner of the new
	 * 					bar container if one needs to be created.
	 * @param h			The height of the graph component, and therefore the
	 * 					height of the bar container, and the maximum height of
	 * 					a bar within that container.
	 */
	public void addBarElement(String cat_name, String bar_name, long x_origin, long h)
	{
		Tk_BarContainer tkb = null;
		if (!hasElement(cat_name))
		{
			tkb = 
				new Tk_BarContainer
				(
					getGrowDirection(),
					cat_name,
					cat_name,
					new Point2D.Double(x_origin, getComponentHeight() - getHeightBuffer()),
					m_entryWidth,
					getGraphHeight(),
					null
				);
			addElement(cat_name, tkb);
			if (m_entryWidth * m_entries.size() > m_widthOnScreen)
			{
				m_widthOnScreen = m_entryWidth * m_entries.size();
				this.setDimensions();
				createXAxis();
			}
		}
		else
		{
			tkb = (Tk_BarContainer)getElement(cat_name); 
		}
		//
		// Fixme: what about making Tk_CharEntry.addBarElement() return a boolean
		// that indicates whether or not the new bar will fit. This would be if the
		// newly calculated width of each bar went below a predetermined minimum.
		//
		if (!tkb.canTakeOneMore())
		{
			m_entryWidth = tkb.getMinimumWidth();
			m_endDomain = m_entryWidth * m_categories.size();//m_widthOnScreen;
			m_widthOnScreen = (long)1.2 * m_endDomain; //m_entryWidth * m_categories.size();
			setDimensions();
			createXAxis();
			Iterator<String>sit = m_entries.keySet().iterator();
			x_origin = 0;
			while (sit.hasNext())
			{
				Tk_BarChartEntry tbc = (Tk_BarChartEntry)m_entries.get(sit.next());
				tbc.changeBar
				(
					m_entryWidth, 
					tbc.getHeight(),
					new Point2D.Double
					(
						getWidthBuffer() + x_origin,
						getComponentHeight() - getHeightBuffer()
					)
				);
				x_origin += m_entryWidth;
			}
		}
		// add the bar.
		tkb.addBarElement(bar_name, convertYVal(h));
		repaint();
	}
	
	public long getEntryWidth()
	{
		return m_entryWidth;
	}

	@Override
	public int getScrollableUnitIncrement
	(
		Rectangle visibleRect,
		int orientation,
		int direction
	)
	{
		if (orientation == SwingConstants.HORIZONTAL)
		{
			return (int)getEntryWidth()/4;
		}
		else
		{
			return (int)getComponentHeight()/20;
		}
	}
	
	@Override
	public int getScrollableBlockIncrement
	(
		Rectangle visibleRect,
		int orientation,
		int direction
	)
	{
		return 2 * getScrollableUnitIncrement(visibleRect, orientation, direction);
	}

	public static void drawBarGraph()
	{
		String test_data[] =
			{
				"Monday",
				"Tuesday",
				"Wednesday",
				"Thursday",
				"Friday",
				"Saturday",
				"Sunday"
			};
				/*,
				"Jan",
				"Feb",
				"Mar",
				"May",
				"Jun",
				"Jul",
				"Aug",
				"Sept",
				"Oct",
				"Nov",
				"Dec",
				"One",
				"Two",
				"Three",
				"Four",
				"Five",
				"Six",
				"Seven",
				"Eight",
				"Nine",
				"zero"
			};*/
		Vector<String>vs = new Vector<String>();
		for (int i = 0; i < test_data.length; i++)
		{
			vs.add(test_data[i]);
		}
		Tk_BarGraph<Duration> tkb =
			new Tk_BarGraph<Duration>
			(
				Tk_GraphDirection.LeftToRight,
				400,	// height on screen.
				0,		// start range
				1000,	// end range
				400,	// width on screen
				vs,
				100,	// big y ticks
				10,		// small y ticks.
				"X Label",
				"Y Label",
				(Tk_Converter)(new Tk_LongLong()),
				200
			);
		int num_bars = 0;
		for (int i = 0; i < vs.size(); i++)
		{
			//int bar_num = 1 + (int)Math.ceil(15 * Math.random());
			int bar_num = 5;
			for (int j = 0; j < bar_num; j++)
			{
				long h = (long)Math.ceil(1000 * Math.random());
				tkb.addBarElement(vs.get(i), vs.get(i), tkb.getWidthBuffer() + i * tkb.m_entryWidth, h);
			}
			num_bars += bar_num;
		}
		JFrame jf = new JFrame();
		JScrollPane jsc = new JScrollPane(tkb);
		jsc.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		jsc.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsc.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		jf.setUndecorated(false); //true);
		jf.add(jsc);
		jf.pack();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		JFrame jf_legend = new JFrame("Graph Legend");
		Tk_GraphLegend tgl = new Tk_GraphLegend(new Rectangle(0,0,200,200));
		jf_legend.add(tgl);
		jf_legend.pack();
		jf_legend.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf_legend.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					drawBarGraph();
				}
			}
		);
	}

}
