/**
 *
 */
package main.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

import main.logic.Add;

/**
 * @@author Dalton
 *
 */
@SuppressWarnings("serial")
public class CustomCellRenderer extends JTextArea implements TableCellRenderer {
	private static final Logger logger = Logger.getLogger(CustomCellRenderer.class.getName());
	private static final Color NON_EXPIRED_FONT_COLOR = Color.decode("0x009900");
	private static final Color EXPIRED_FONT_COLOR = Color.RED;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	assert this != null;
    	this.setWrapStyleWord(true);
        this.setLineWrap(true);
        this.setMargin(new Insets(2,10,2,10));
        renderFontSizeRelativeToOS();

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
        	setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        if (value != null) {
	        if (value instanceof Date) {
	        	this.setText(formatDate((Date)value));
	        	setFontColour((Date)value);

        		if (table.getName().equals("Events")) {
        			int modelRow = table.getRowSorter().convertRowIndexToModel(row);
	        		if (column == 2) {
	        			String fromDate = table.getModel().getValueAt(modelRow, 1).toString();
		        		String toDate = table.getModel().getValueAt(modelRow, 2).toString();
		        		if (fromDate.equals(toDate)) {
		        			this.setText(null);
		        		} else {
		        			this.setText(formatDate((Date)value));
		        		}
        			} else {
        				this.setText(formatDate((Date)value));
        			}
        		}
	        } else {
	        	this.setText(value.toString());
	        }
        }

        return this;
    }

    private void renderFontSizeRelativeToOS() {
    	if (System.getProperty("os.name").startsWith("Mac")) {
        	this.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));
        } else {
        	this.setFont(new Font("Dialog UI", Font.BOLD, 13));
        }
    }

    private String formatDate(Date d) {
    	SimpleDateFormat dateFmt = new SimpleDateFormat("EEE, dd MMM yyyy");
		SimpleDateFormat dayFmt = new SimpleDateFormat("h:mm a");
		return dateFmt.format(d) + "\n" + dayFmt.format(d);
    }

    private void setFontColour(Date d) {
    	Calendar now = Calendar.getInstance();
    	Calendar dateTime = Calendar.getInstance();
    	dateTime.setTime(d);

		if (now.compareTo(dateTime) > 0) {
    		this.setForeground(EXPIRED_FONT_COLOR);
    	} else {
    		this.setForeground(NON_EXPIRED_FONT_COLOR);
    	}
    }
}