package Tk_Gui;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import Tk_utils.*;
import javax.xml.datatype.*;

/**
 * <p>A somewhat generic graph. This graph class simply draws the axes, and if
 * <code>Tk_ChartEntry</code> elements are added to it, it will call their
 * paint methods as appropriate.</p>
 *
 * Fixme: We probably want to subclass this more general graph to the
 * specific type we use for the Time Keeper.
 * @author michaelwebster
 *
 */
public class Tk_Graph<T> extends JComponent implements Scrollable
{
	/**
	 * The graph direction can be left to right, right to left top to bottom
	 * or bottom to top. This field also implies a graph orientation. 
	 */
	private Tk_GraphDirection m_growDirection = Tk_GraphDirection.Illegal;
	

	/**
	 * The start of the range of values that can be shown on the y axis.
	 */
	private long m_startRange 			= 0;
	
	/**
	 * The largest of the range of values that can be shown on the y axis.
	 */
	private long m_endRange 			= 0;

	/**
	 * The height on screen as distinct from the height of the graph.
	 */
	private long m_heightOnScreen;
	
	/**
	 * The Conversion factor from range values to height on screen values.
	 */
	private double m_rangeXvFactor;

	/**
	 * The start of the domain of values that can be shown on the x axis.
	 */
	private long m_startDomain			= 0;
	
	/**
	 * The end of the domain of values that can be shown on the x axis.
	 */
	protected long m_endDomain			= 0;
	
	/**
	 * The width on the screen, as distinct from the width of the graph.
	 */
	protected long m_widthOnScreen;

	/**
	 * The Conversion factor from domain values to width on screen values.
	 */
	private double m_domainXvFactor;
	
	/**
	 * The value at which to place big ticks on the y axis.
	 */
	private long m_bigYTicks			= 0;
	
	/**
	 * The value at which to place small ticks on the y-axis
	 */
	private long m_smallYTicks			= 0;
	
	/**
	 * The value at which to place big ticks on the y axis.
	 */
	private long m_bigXTicks			= 0;
	
	/**
	 * The value at which to place small ticks on the y-axis
	 */
	private long m_smallXTicks			= 0;
	
	/**
	 * The label for the y axis.
	 */
	private String m_yLabel				= null;
	
	/**
	 * The label for the x axis.
	 */
	private String m_xLabel				= null;
	
	/**
	 * Width of the graph.
	 */
	private long m_graphWidth			= 0;
	
	/**
	 * Height of the graph.
	 */
	private long m_graphHeight			= 0;
	
	/**
	 * Buffer between the sides of the component and the axes of the graph.
	 */
	private long m_widthBuffer			= 0;
	
	/**
	 * Buffer between the top and bottom of the component and the axes of the
	 * graph.
	 */
	private long m_heightBuffer			= 0;

	/**
	 * The converter for converting between external values and our internal
	 * representation.
	 */
	private Tk_Converter<T, Long> m_converter 	= null;
	
	/**
	 * The current size of the graph.
	 */
	Font m_font = null;
	
	/**
	 * The x axis label's glyph vector.
	 */
	GlyphVector m_xGlyphVector = null;
	
	/**
	 * The y axis label's glyph vector.
	 */
	GlyphVector m_yGlyphVector = null;
	
	/**
	 * The path containing the X axis for the graph.
	 */
	private Path2D.Double m_xAxis = null;
	
	/**
	 * The path containing the Y axis for the graph.
	 */
	private Path2D.Double m_yAxis = null;
	
	/**
	 * The stroke for drawing the axes.
	 */
	private Stroke m_mainStroke			= null;
	
	/**
	 * The path containing the small ticks for the x-axis of the graph.
	 */
	protected Path2D.Double m_smallXTicksPath = null;
	
	/**
	 * The path containing the small ticks for the y axis of the graph.
	 */
	protected Path2D.Double m_smallYTicksPath = null;

	/**
	 * The stroke for drawing the small ticks.
	 */
	private Stroke m_smallTickStroke	= null;
	
	/**
	 * The stroke for drawing the big ticks.
	 */
	private Stroke m_bigTickStroke		= null;
	
	/**
	 * The path containing the big ticks for x-axis of the graph.
	 */
	private Path2D.Double m_bigXTicksPath = null;
	
	/**
	 * The path containing the big ticks for the y axis of the graph.
	 */
	private Path2D.Double m_bigYTicksPath = null;
	
	/**
	 * The length of the arrow lines on the axes.
	 */
	private int m_arrowLength			= 0;
	
	/**
	 * The vector containing the glyphs for labelling the values on the y-axis. 
	 */
	private Vector<GlyphVector>		m_yGlyphs			= null;
	
	/**
	 * The Font for labelling the values on the y-axis.
	 */
	private Font 					m_yLabelFont			= null;

	/**
	 * A vector of <code>Tk_ChartEntry</code>s that draw the graphed quantities
	 * on the graph.
	 */
	protected HashMap<String, Tk_ChartEntry>m_entries		= null;

	/**
	 * Create the Tk_Graph.
	 * @param gd				The direction of growth.
	 * @param height_on_screen	The height of the graph component.
	 * @param start_range		The beginning of the y-axis range of values.
	 * @param end_range			The end of the y-axis range of values.
	 * @param width_on_screen	The width of the graph component.
	 * @param start_domain		The beginning of the x-axis' domain of values.
	 * @param end_domain		The end of the x-axis' domain of values.
	 * @param big_y_ticks		The distance between big ticks on the y-axis. 
	 * @param small_y_ticks 	The distance between small ticks on the y-axis.
	 * @param big_x_ticks   	The distance between big ticks on the x-axis.
	 * @param small_x_ticks 	The distance between small ticks on the y-axis.
	 * @param x_label			The label for the x axis.
	 * @param y_label			The label for the y axis.
	 * @param converter			A value converter.
	 */
	public Tk_Graph
	(
		Tk_GraphDirection gd,
		long height_on_screen,
		long start_range,
		long end_range,
		long width_on_screen,
		long start_domain,
		long end_domain,
		long big_y_ticks,
		long small_y_ticks,
		long big_x_ticks,
		long small_x_ticks,
		String x_label,
		String y_label,
		Tk_Converter<T, Long> converter
	)
	{
		super();
		m_growDirection 	= gd;
		m_heightOnScreen 	= height_on_screen;
		m_startRange 		= start_range;
		m_endRange			= end_range;
		m_rangeXvFactor		= ((double)m_heightOnScreen)/(double)(m_endRange - m_startRange);
		m_widthOnScreen		= width_on_screen;
		m_startDomain		= start_domain;
		m_endDomain			= end_domain;
		assert((m_bigYTicks % m_smallYTicks) == 0);
		m_bigYTicks			= big_y_ticks;
		m_smallYTicks		= small_y_ticks;
		m_bigXTicks			= big_x_ticks;
		m_smallXTicks		= small_x_ticks;
		assert(m_smallXTicks == 0 || (m_bigYTicks % m_smallYTicks) == 0);
		m_graphWidth = m_widthOnScreen;
		m_widthBuffer = m_graphWidth / 4;
		if (0 == m_smallXTicks)
		{
			m_domainXvFactor = 1.0;
		}
		else
		{
			m_domainXvFactor	= ((double)m_widthOnScreen)/(double)(m_endDomain- m_startDomain);
		}
		m_graphHeight = m_heightOnScreen;
		m_heightBuffer = m_graphHeight / 10;
		m_xLabel			= x_label;
		m_yLabel			= y_label; 
		m_converter			= converter;
		int stroke_width =
				(int)Math.ceil
	    		(
	        		(double)(Math.min(m_heightOnScreen, m_widthOnScreen)) / 300
	       		);
		m_mainStroke = new BasicStroke(stroke_width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		m_smallTickStroke = new BasicStroke(Math.max(stroke_width / 2, 1));
		m_bigTickStroke = new BasicStroke(stroke_width);
		m_arrowLength = Math.max((int)m_graphWidth, (int)m_graphHeight)/50;
		m_entries = new HashMap<String, Tk_ChartEntry>();
		
		this.setOpaque(true);
		m_xAxis = new Path2D.Double();
		m_yAxis = new Path2D.Double();
		createYAxis();
		//createXAxis();
	}

	/**
	 * Reset this component's dimensions based on a change to the width on
	 * screen attribute.
	 */
	protected void setDimensions()
	{
		m_graphWidth = m_widthOnScreen;
		setBounds(new Rectangle(0 ,0, (int)getComponentWidth(), (int)getComponentHeight()));	
	}

	/**
	 * <p>Create the x-axis by:
	 * <ol>
	 * <li>Draw the x-axis,</li>
	 * <li>Draw an arrow on the end of the x-axis,</li>
	 * <li>Draw the big and small ticks on the x-axis.</li>
	 * </ol>
	 * </p>  
	 */
	protected void createXAxis()
	{
		double angle = Math.PI / 4;
		double x_diff = Math.sin(angle) * m_arrowLength;
		double y_diff = Math.cos(angle) * m_arrowLength;
		m_xAxis.reset();
		// Return to the origin.
		m_xAxis.moveTo(getWidthBuffer(), 11 * getHeightBuffer());
		// Draw the X axis.;
		m_xAxis.lineTo(1.8 * getWidthBuffer() + getGraphWidth(), 11 * getHeightBuffer());
		
		// Place an arrow on the end of the x axis.
		m_xAxis.lineTo(1.8 * getWidthBuffer() + getGraphWidth() - x_diff, 11 * getHeightBuffer() + y_diff);
		m_xAxis.moveTo(1.8 * getWidthBuffer() + getGraphWidth(), 11 * getHeightBuffer());
		m_xAxis.lineTo(1.8 * getWidthBuffer() + getGraphWidth() - x_diff, 11 * getHeightBuffer() - y_diff);
		
		m_bigXTicksPath = new Path2D.Double();
		m_smallXTicksPath = new Path2D.Double();
		
		// Place small and large ticks on the x axis.
		if (m_bigXTicks > 0)
		{
			if (m_smallXTicks > 0)
			{
				long x_val = m_smallXTicks;
				while (x_val < m_endDomain - m_startDomain)
				{
					long x_convert = convertXVal(x_val);
					if ((x_val  % m_bigXTicks) == 0)
					{
						m_bigXTicksPath.moveTo(getWidthBuffer() + x_convert, 11 * getHeightBuffer());
						m_bigXTicksPath.lineTo(getWidthBuffer() + x_convert, 11.3 * getHeightBuffer());
					}
					else
					{
						m_smallXTicksPath.moveTo(getWidthBuffer() + x_convert, 11 * getHeightBuffer());
						m_smallXTicksPath.lineTo(getWidthBuffer() + x_convert, 11.1 * getHeightBuffer());
					}
					x_val += (m_smallXTicks > 0) ? m_smallXTicks : m_bigXTicks;
				}
			}
		}
	}

	/**
	 * <p>Create the y-axis by:
	 * <ol>
	 * <li>Draw the y-axis,</li>
	 * <li>Draw an arrow on the end of the y-axis,</li>
	 * <li>Draw the big and small ticks on the y-axis.</li>
	 * </ol>
	 * </p>  
	 */
	protected void createYAxis()
	{
		setDimensions();
		Rectangle r = getBounds();
		
		// Move to the origin for the axes.
		m_yAxis.moveTo(getWidthBuffer(), 11 * getHeightBuffer());
		// Draw the Y axis.
		m_yAxis.lineTo(getWidthBuffer(), getHeightBuffer());
		
		// Place an arrow on the end of the y axis.
		double angle = Math.PI / 4;
		double x_diff = Math.sin(angle) * m_arrowLength;
		double y_diff = Math.cos(angle) * m_arrowLength;
		m_yAxis.lineTo(getWidthBuffer() - x_diff, getHeightBuffer() + y_diff);
		m_yAxis.moveTo(getWidthBuffer(), getHeightBuffer());
		m_yAxis.lineTo(getWidthBuffer() + x_diff, getHeightBuffer() + y_diff);
		
		
		m_bigYTicksPath = new Path2D.Double();
		m_smallYTicksPath = new Path2D.Double();
		// Place small and large ticks on the y axis.
		long y_val = m_smallYTicks;
		while (y_val < m_endRange - m_startRange)
		{
			long y_convert = convertYVal(y_val);
			if ((y_val  % m_bigYTicks) == 0)
			{
				//getComponentHeight()  - getHeightBuffer() - y_convert;	    		
//				m_bigYTicksPath.moveTo(getWidthBuffer(), getHeightBuffer() + y_convert);
//				m_bigYTicksPath.lineTo(0.7 * getWidthBuffer(), getHeightBuffer() + y_convert);
				m_bigYTicksPath.moveTo
				(
					getWidthBuffer(),
					getComponentHeight()  - getHeightBuffer() - y_convert
				);
				m_bigYTicksPath.lineTo
				(
					0.7 * getWidthBuffer(),
					getComponentHeight()  - getHeightBuffer() - y_convert
				);
			}
			else
			{
				//m_smallYTicksPath.moveTo(getWidthBuffer(), getHeightBuffer() + y_convert);
				//m_smallYTicksPath.lineTo(0.9 * getWidthBuffer(), getHeightBuffer() + y_convert);
				m_smallYTicksPath.moveTo
				(
					getWidthBuffer(),
					getComponentHeight()  - getHeightBuffer() - y_convert
				);
				m_smallYTicksPath.lineTo
				(
					0.9 * getWidthBuffer(),
					getComponentHeight()  - getHeightBuffer() - y_convert
				);
			}
			y_val += m_smallYTicks;
		}
	}

	/**
	 * Convert between a value in the graph's range, and a screen height
	 * value.
	 * @param yval		A value from the range <code>[m_endRange - m_startRange]</code>
	 * @return			A screen height value on the graph y-axis. 
	 */
	protected long convertYVal(long yval)
	{
		return (long)Math.ceil(m_rangeXvFactor * yval);
	}
	
	/**
	 * Convert between a value in the graph's domain and a screen width
	 * value.
	 * @param xval		A value from the domain <code>[m_endDomain - m_startDomain]</code>
	 * @return			A screen width value on the graph x-axis. 
	 */
	protected long convertXVal(long xval)
	{
		return (long)Math.ceil(m_domainXvFactor * xval);
	}

	/**
	 * Has this category been seen before.
	 * 
	 * @param cat_name	The name of a category.
	 *
	 * @return	true => the category has been seen before.
	 * 			false otherwise.
	 */
	protected boolean hasElement(String cat_name)
	{
		return m_entries.containsKey(cat_name);
	}
	
	/**
	 * Add a <code>Tk_ChartEntry</code> with category name cat_name.
	 * 
	 * @param cat_name		The category name fot his char entry.
	 * @param tke			The chart entry itself.
	 */
	protected void addElement(String cat_name, Tk_ChartEntry tke)
	{
		m_entries.put(cat_name, tke);
	}
	
	/**
	 * Return the <code>Tk_CharEntry</code> with category name = cat_name.
	 *
	 * @param cat_name	The name of the <code>Tk_ChartEntry</code> we awant to
	 * 					retrieve.
	 *
	 * @return	The requested <code>Tk_ChartEntry</code>.
	 */
	protected Tk_ChartEntry getElement(String cat_name)
	{
		return m_entries.get(cat_name);
	}

	/**
	 * Paint the graph, including any <code>Tk_ChartEntry</code>s that belong
	 * on the graph.
	 */
	@Override
	public void paintComponent(Graphics g)
    {
    	super.paintComponent(g);
    	Graphics2D g2 = (Graphics2D)g;

    	// If the font is null, create it and the glyph vectors for labelling
    	// the x and y axes.
    	if (null == m_font)
    	{
    		m_font = new Font("Serif", Font.BOLD, (int)(m_heightBuffer/2));
    		g2.setFont(m_font);
    		m_xGlyphVector = m_font.createGlyphVector(g2.getFontRenderContext(), m_xLabel);
    		m_yGlyphVector = m_font.createGlyphVector(g2.getFontRenderContext(), m_yLabel);
    	}
    	g2.setStroke(m_mainStroke);
    	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    			RenderingHints.VALUE_ANTIALIAS_ON);
    	g2.draw(m_yAxis);
    	g2.draw(m_xAxis);
    	// Add the x-axis label.
    	g2.drawGlyphVector
    	(
    		m_xGlyphVector,
    		(float)getComponentWidth()/3,
    		(float)(getComponentHeight())// - getHeightBuffer() / 8)
    	);
    	
    	// Add the y-axis label.
    	g2.drawGlyphVector
    	(
    		m_yGlyphVector,
    		(float)getWidthBuffer()/2,
    		(float)(getHeightBuffer() / 2)
    	);
    	
    	// Render the big ticks.
    	g2.setStroke(m_bigTickStroke);
    	g2.draw(m_bigYTicksPath);
    	g2.draw(m_bigXTicksPath);
    	
    	// Render the small ticks.
    	g2.setStroke(m_smallTickStroke);
    	g2.draw(m_smallYTicksPath);
    	g2.draw(m_smallXTicksPath);
    	
    	Iterator<String> it = m_entries.keySet().iterator();
    	while (it.hasNext())
    	{
    		Tk_ChartEntry tce = m_entries.get(it.next());
    		//tce.paint();
    		tce.paint(g2);
    	}
    	
    	// If the font is null, create it and the glyph vectors for labelling
    	// the x and y axes.
    	if (null == m_yLabelFont)
    	{
    		m_yLabelFont = new Font("Serif", Font.BOLD, (int)Math.min(getHeightBuffer()/2, getWidthBuffer()/2));
    		m_yGlyphs = new Vector<GlyphVector>();
    		g2.setFont(m_yLabelFont);
    		// Place small and large ticks on the y axis.
    		long y_val = m_bigYTicks;
    		AffineTransform at = null;
    		String longest_string = null;
    		Rectangle2D longest_rect = null;
    		while (y_val < m_endRange - m_startRange)
    		{
    			String next_label = m_converter.getStringSecond(y_val);
    			Rectangle2D string_rec = 
    					m_yLabelFont.getStringBounds(next_label, g2.getFontRenderContext());
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
    			y_val += m_bigYTicks;
    		}
    		double y_scale = 0.5 * getWidthBuffer() / longest_rect.getWidth();
    		at = AffineTransform.getScaleInstance(y_scale, y_scale);
    		Font scaled_font = m_yLabelFont.deriveFont(at);
    		
    		y_val = m_bigYTicks;
    		while (y_val < m_endRange - m_startRange)
    		{
    			long y_convert = convertYVal(y_val);
    			String next_label = m_converter.getStringSecond(y_val);
    			m_yGlyphs.add
    			(
   					scaled_font.createGlyphVector(g2.getFontRenderContext(), next_label)
    			);
    			y_val += m_bigYTicks;
    		}
    	}

    	for (int i = 0; i < m_yGlyphs.size(); i++)
    	{
    		long y_convert = convertYVal(m_bigYTicks * (i + 1));
    		long next_y_pos = getComponentHeight()  - getHeightBuffer() - y_convert;
    		GlyphVector gv = m_yGlyphs.get(i);
    		Shape s = gv.getOutline();
    		g2.drawGlyphVector
    		(
    			gv,
    			(float)(getWidthBuffer())/8, // - 1.2 * s.getBounds().width),
    			(float)next_y_pos + s.getBounds().height/2
    		);
    	}
    }
	
	/**
	 * Add the supplied <code>Tk_ChartEntry</code> to this graph.
	 * 
	 * @param tkc 		A new entry for rendering on this graph.
	 */
	public void addGraphElement(String name, Tk_ChartEntry tkc)
	{
		m_entries.put(name, tkc);
	}
	
	@Override
	public void setSize(Dimension d)
	{
		super.setSize(d);
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension((int)getComponentWidth(), (int)getComponentHeight());
	}

	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension((int)getComponentWidth(), (int)getComponentHeight());
	}
	
	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension((int)getComponentWidth(), (int)getComponentHeight());
	}
	
	/**
	 * Return the <code>Tk_GraphDirection</code> direction of growth of this
	 * <code>Tk_Graph</code>.
	 *
	 * @return	A <code>Tk_GraphDirection</code> enumeration value indicating
	 * 			the direction in which this graph grows.
	 */
	public Tk_GraphDirection getGrowDirection()
	{
		return m_growDirection;
	}

	/**
	 * Return the start value for the y-axis of this graph.
	 *
	 * @return	The start value on the y-axis of this graph.
	 */
	public long getStartRange()
	{
		return m_startRange;
	}
	
	/**
	 * Return the last value displayed on the y-axis of this graph.
	 *
	 * @return	The last value displayed on the y-axis of the graph.
	 */
	public long getEndRange()
	{
		return m_endRange;
	}
	
	/**
	 * Return the height on screen that has been requested for this graph.
	 *
	 * @return		The height on screen that has been requested for this
	 * 				graph.
	 */
	public long getHeightOnScreen()
	{
		return m_heightOnScreen;
	}

	/**
	 * Return the conversion factor used to convert from value ranges to
	 * screen height values.
	 *
	 * @return		The conversion factor mulitplier for range values, to
	 * 				get screen values.
	 */
	public double getRangeConversionFactor()
	{
		return this.m_rangeXvFactor;
	}
	
	/**
	 * Return the start value for the x-axis of this graph.
	 *
	 * @return	The start value on the x-axis of this graph.
	 */
	public long getStartDomain()
	{
		return m_startDomain;
	}
	
	/**
	 * Return the last value displayed on the x-axis of this graph.
	 *
	 * @return	The last value displayed on the x-axis of the graph.
	 */
	public long getEndDomain()
	{
		return m_endDomain;
	}
	
	/**
	 * Return the conversion factor used to convert from domain values to
	 * screen width values.
	 *
	 * @return		The conversion factor mulitplier for domain values, to
	 * 				get screen values.
	 */
	public double getDomainConversionFactor()
	{
		return this.m_domainXvFactor;
	}

	/**
	 * Return the value at which to place big ticks on the y axis.
	 * 
	 * @return The distance between big ticks on the y-axis of the graph.
	 */
	public long getBigYTicks()
	{
		return m_bigYTicks;
	}
	
	/**
	 * Return the value at which to place small ticks on the y-axis
	 *
	 * @return The distance between small ticks on the y-axis of the graph.
	 */
	public long getSmallYTicks()
	{
		return m_smallYTicks;
	}
	
	/**
	 * Return the value at which to place big ticks on the x axis.
	 *
	 * @return The distance between big ticks on the x-axis of the graph.
	 */
	public long getBigXTicks()
	{
		return m_bigXTicks;
	}
	
	/**
	 * Return the value at which to place small ticks on the x-axis
	 *
	 * @return The distance between small ticks on the x-axis of the graph.
	 */
	private long getSmallXTicks()
	{
		return m_smallXTicks;
	}
	
	/**
	 * Return  the label for the y axis.
	 *
	 * @return The label for the y axis of this graph.
	 */
	public String getYLabel()
	{
		return m_yLabel;
	}
	
	/**
	 * Return the label for the x axis.
	 *
	 * @return The label for the x-axis of this graph.
	 */
	public String getXLabel()
	{
		return m_xLabel;
	}
	
	/**
	 * Return the height of this graph.
	 *
	 * @return	The height of this graph.
	 */
	public long getGraphHeight()
	{
		return m_graphHeight;
	}
	
	/**
	 * Return the width of this graph.
	 *
	 * @return	The width of this graph.
	 */
	public long getGraphWidth()
	{
		return m_graphWidth;
	}
	
	/**
	 * Return the buffer between the edge of the component and the y-axis of
	 * the graph.
	 *
	 * @return	The distance between the edge of the component and the y-axis
	 * 			of this graph.
	 */
	public long getWidthBuffer()
	{
		return m_widthBuffer;
	}
	
	/**
	 * Return the buffer between the edge of the component and the x-axis of
	 * the graph.
	 *
	 * @return	The distance between the edge of the component and the x-axis
	 * 			of this graph.
	 */
	public long getHeightBuffer()
	{
		return m_heightBuffer;
	}
	
	/**
	 * Return the height of the component, including the graph height, and the
	 * two height buffers.
	 *
	 * @return	The height of the entire component.
	 */
	public long getComponentHeight()
	{
		return m_graphHeight + 2 * m_heightBuffer;
	}
	
	/**
	 * Return the width of the component, including the graph width, and the
	 * two width buffers.
	 *
	 * @return	The width of the entire component.
	 */
	public long getComponentWidth()
	{
		return m_graphWidth + 2 * m_widthBuffer;
	}

	/**
	 * Just a main method to be called from the event thread.
	 */
	public static void drawGraph()
	{
		JFrame jf = new JFrame();
		Tk_Graph<Duration> tkb =
			new Tk_Graph<Duration>
			(
				Tk_GraphDirection.LeftToRight,
				600,
				0,
				1000,
				900,
				0,
				800,
				50,
				10,
				50,
				10,
				"X Label",
				"Y Label",
				(Tk_Converter)(new Tk_LongLong())
			);
		tkb.createYAxis();
		tkb.createXAxis();
		jf.setUndecorated(false);
		jf.add(tkb);
		Tk_MouseListener tml = new Tk_MouseListener(jf);
		jf.addMouseListener(tml);		
		jf.pack();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}
	
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					drawGraph();
				}
			}
		);
	}

	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		Dimension d = new Dimension((int)getComponentWidth(), (int)(1.05 * getComponentHeight()));
		return d;
	}

	@Override
	public int getScrollableUnitIncrement
	(
		Rectangle visibleRect,
		int orientation,
		int direction
	)
	{
		return 0;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		// TODO Auto-generated method stub
		return false;
	}

}

class EmptyUI extends ComponentUI
{
	private static final EmptyUI sharedInstance = new EmptyUI();
	public static ComponentUI createUI(JComponent c)
	{
		return sharedInstance;
	}
}
