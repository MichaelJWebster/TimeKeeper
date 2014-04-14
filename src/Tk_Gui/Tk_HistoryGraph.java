package Tk_Gui;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import javax.xml.datatype.*;
import Tk_utils.*;

public class Tk_HistoryGraph extends Tk_BarGraph<Duration>
{
	public Tk_HistoryGraph
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
			x_categories,
			big_y_ticks,
			small_y_ticks,
			x_label,
			y_label,
			new Tk_DurationConverter(),
			graph_entry_width				
		);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}


	/**
	 * @param args
	 */
	public static void main(String[] args)
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
		Vector<String>vs = new Vector<String>();
		for (int i = 0; i < test_data.length; i++)
		{
			vs.add(test_data[i]);
		}
		JFrame jf = new JFrame();

		Tk_HistoryGraph tkb =
			new Tk_HistoryGraph
			(
				Tk_GraphDirection.LeftToRight,
				200,
				0,
				60 * 60 *12,
				400,
				vs,
				60 * 60,
				15 * 60,
				"Task Record Date",
				"Task Time",
				100
			);

		//tkb.createGraph();
		for (int i = 0; i < vs.size(); i++)
		{
			int bar_num = 1 + (int)Math.ceil(10 * Math.random());
			for (int j = 0; j < bar_num; j++)
			{
				long h = (long)Math.ceil(60 * 60 * 12 * Math.random());
				String bar_name = String.format("%s:%s:%d", vs.get(i), "bar", j);
				tkb.addBarElement(vs.get(i), bar_name, tkb.getWidthBuffer() + i * tkb.getEntryWidth(), h);
			}
		}
		JScrollPane jsc = new JScrollPane(tkb);
		jsc.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		jsc.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsc.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		jf.setUndecorated(false);
		jf.add(jsc);
		jf.pack();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}

}
