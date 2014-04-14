package Tk_Gui;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

/**
 * A mouse listener for the MyClock class, that allows the clock window to
 * be moved around by the mouse.
 * @author michaelwebster
 *
 */
public class Tk_MouseListener implements MouseInputListener
{
	// The clock whose mouse this listener is registered for. Note also that
	// clock will be used by this listener to reposition the clock window.
	Component m_parent = null;
	
	// parent_x and parent_y are the current screen coordinates of the clock.
	int parent_x = 0;
	int parent_y = 0;
	
	// start_x and start_y are the last coordinates that the mouse was checked
	// at.
	int start_x = 0;
	int start_y = 0;
	
	// move_x and move_y are the relative change in coordinates for the clock
	// that will move it to the indicated mouse position.
	int move_x = 0;
	int move_y = 0;

	public Tk_MouseListener(Component comp)
	{
		m_parent = comp;
		parent_x = comp.getX();
		parent_y = comp.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		start_x = e.getX();
		start_y = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		move_x = e.getX() - start_x;
		move_y = e.getY() - start_y;
		parent_x += move_x;
		parent_y += move_y;
		m_parent.setLocation(parent_x, parent_y);
		m_parent.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		// TODO Auto-generated method stub
		move_x = e.getX() - start_x;
		move_y = e.getY() - start_y;
		start_x = e.getX();
		start_y = e.getY();
		parent_x += move_x;
		parent_y += move_y;
		m_parent.setLocation(parent_x, parent_y);
		m_parent.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		/*move_x = e.getX() - start_x;
		move_y = e.getY() - start_y;
		start_x = e.getX();
		start_y = e.getY();
		parent_x += move_x;
		parent_y += move_y;
		clock.setLocation(parent_x, parent_y);
		clock.invalidate();*/
	}
}
