/**
 *
 */
package main.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

/**
 * @@author Dalton
 *
 */
public class CustomDateCellEditor extends DefaultCellEditor {
	static SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
	static PrettyTimeParser parser = new PrettyTimeParser();

	public CustomDateCellEditor() {
		super(new JTextField());
	}

	@Override
	public Object getCellEditorValue() {
		String value = ((JTextField)getComponent()).getText();
		String date = parser.parse(value).get(0).toString();

		Date d = null;
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return d;
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
		JTextField tf = ((JTextField)getComponent());
		tf.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
		tf.setBorder(new LineBorder(Color.BLACK));

		try {
			tf.setText(sdf.format(value));
		} catch (Exception e) {
			tf.setText("");
		}

		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	tf.selectAll();
            	tf.setBackground(Color.YELLOW);
            }
        });

		return tf;
	}

	@Override
	public boolean stopCellEditing() {
		String value = ((JTextField)getComponent()).getText();
		List<Date> dates = parser.parse(value);
		if (dates.size() <= 0) {
			((JComponent)getComponent()).setBorder(new LineBorder(Color.RED));
			return false;
		}

		return super.stopCellEditing();
	}
}
