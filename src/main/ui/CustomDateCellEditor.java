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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import main.storage.LogFileHandler;

/**
 * The Class CustomDateCellEditor.
 * Controls the rendering of the cell editors containing Date datatype of the JTables
 *
 * @@author Dalton
 */
public class CustomDateCellEditor extends DefaultCellEditor {
	private static final Logger logger = Logger.getLogger(CustomDateCellEditor.class.getName());
	private static SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
	private static PrettyTimeParser parser = new PrettyTimeParser();
	private final String CELL_FONT = "Segoe UI Semibold";
	private final int CELL_FONT_TYPE = Font.PLAIN;
	private final int CELL_FONT_SIZE = 12;

	/**
	 * Instantiates a new custom date cell editor and sets up the logger.
	 */
	public CustomDateCellEditor() {
		super(new JTextField());
		LogFileHandler.getInstance().addLogFileHandler(logger);
	}

	@Override
	public Object getCellEditorValue() {
		String value = ((JTextField)getComponent()).getText();
		String date = parser.parse(value).get(0).toString();

		Date d = null;
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return d;
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
		JTextField tf = (JTextField)getComponent();
		tf.setFont(new Font(CELL_FONT, CELL_FONT_TYPE, CELL_FONT_SIZE));
		tf.setBorder(new LineBorder(Color.BLACK));

		try {
			tf.setText(sdf.format(value));
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
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
		JTextField tf = (JTextField)getComponent();
		List<Date> dates = parser.parse(tf.getText());

		// Sets the colour of the border to RED if value is not a date
		if (dates.size() <= 0) {
			tf.setBorder(new LineBorder(Color.RED));
			return false;
		}

		return super.stopCellEditing();
	}
}
