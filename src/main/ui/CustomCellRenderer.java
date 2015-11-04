/**
 *
 */
package main.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

/**
 * @@author Dalton
 *
 */
public class CustomCellRenderer extends JTextArea implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	this.setWrapStyleWord(true);
        this.setLineWrap(true);
        this.setMargin(new Insets(2,10,2,10));

        if (System.getProperty("os.name").startsWith("Mac")) {
        	this.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));
        } else {
        	this.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        }

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
        		long currentTime = System.currentTimeMillis();
        		long dateTime = ((Date)value).getTime();
        		if (dateTime < currentTime) {
	        		this.setForeground(Color.RED);
	        	} else {
	        		this.setForeground(Color.decode("0x009900"));
	        	}

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

    private String formatDate(Date d) {
    	SimpleDateFormat dateFmt = new SimpleDateFormat("EEE, dd MMM yyyy");
		SimpleDateFormat dayFmt = new SimpleDateFormat("h:mm a");
		return dateFmt.format(d) + "\n" + dayFmt.format(d);
    }
}