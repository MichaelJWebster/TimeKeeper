package Tk_Gui;

import java.awt.*;
public interface Tk_ChartEntry
{
	public void paint(Graphics2D g2);

	/**
	 * Get the minimum width of this component.
	 *
	 * @return The minimum width this component can occupy.
	 */
	public long getMinimumWidth();
	
	/**
	 * Get the preferred width of this component.
	 *
	 * @return The preferred width for this component to occupy.
	 */
	public long getPreferredWidth();
	
	/**
	 * Get the maximum width of this component.
	 *
	 * @return The maximum width this component can occupy.
	 */
	public long getMaximumWidth();

}
