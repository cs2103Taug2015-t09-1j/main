/**
 *
 */
package main.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

/**
 * @author Dalton
 *
 */
@SuppressWarnings("serial")
public class CustomStringCellEditor extends DefaultCellEditor {
	private final String CELL_FONT = "Segoe UI Semibold";
	private final int CELL_FONT_TYPE = Font.PLAIN;
	private final int CELL_FONT_SIZE = 12;
	private final Color BORDER_COLOUR = Color.BLACK;
	private final Color BACKGROUND_COLOUR = Color.YELLOW;

	public CustomStringCellEditor() {
		super(new JTextField());
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
		final JTextField tf = ((JTextField)getComponent());
		tf.setFont(new Font(CELL_FONT, CELL_FONT_TYPE, CELL_FONT_SIZE));
		tf.setBorder(new LineBorder(BORDER_COLOUR));
		tf.setText(value.toString());

		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	tf.selectAll();
            	tf.setBackground(BACKGROUND_COLOUR);
            }
        });

		return tf;
	}

	@Override
	public boolean stopCellEditing() {
		JTextField tf = ((JTextField)getComponent());
		String value = tf.getText();

		if (value.trim().isEmpty()) {
			tf.setBorder(new LineBorder(Color.RED));
			return false;
		}

		return super.stopCellEditing();
	}
}
