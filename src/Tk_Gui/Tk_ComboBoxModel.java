/**
 * Manages data for the Tk_Gui's JComboBox.
 */
package Tk_Gui;

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;

/**
 * @author michaelwebster
 *
 */
public class Tk_ComboBoxModel extends DefaultComboBoxModel {

	/*public TkComboBoxModel()*/
	/**
	 * Ban the use of this constructor.
	 */
	private Tk_ComboBoxModel() {
		super();
	}

	/**
	 * Ban the use of this contructor by making it private.
	 *
	 * @param items 	A list of elements for the combo box.
	 */
	private Tk_ComboBoxModel(Object[] items) {
		super(items);
	}

	/**
	 * Ban the use of this constructor by making it private.
	 *
	 * @param v A vector of elements for the list in the combo box.
	 */
	private Tk_ComboBoxModel(Vector v) {
		super(v);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
