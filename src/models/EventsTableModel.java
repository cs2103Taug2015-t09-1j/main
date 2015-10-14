/**
 *
 */
package models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

import models.Event;

/**
 * @author Dalton
 *
 */
public class EventsTableModel extends AbstractTableModel {
	private String[] columnNames = { "ID", "Start Date", "End Date", "Task Description", "Done" };
	private Class[] columnTypes = { Integer.class, String.class, String.class, String.class, Boolean.class };

	ArrayList<Event> events;

	public EventsTableModel(ArrayList<Event> events) {
		super();
		this.events = events;
	}

	public int getColumnCount() {
		return columnNames.length;
    }

	public String getColumnName(int col) {
		return columnNames[col];
	}

    public Class getColumnClass(int col) {
		return columnTypes[col];
	}

	public int getRowCount() {
	    return events.size();
    }

	public boolean isCellEditable(int row, int col) {
        switch (col) {
            default:
                return false;
        }
    }

	public void setValueAt(Object value, int row, int col) {
		Event evt = (Event)events.get(row);
		switch (col) {
			case 0:
					//t.setTaskID((Integer) value);
			break;
			case 1:
					//t.setDate((String) value);
			break;
			case 2:
					//t.setStartTime((String) value);
			break;
			case 3:
					evt.setTaskDesc((String) value);
			break;
			case 4:
					evt.setDone((Boolean) value);
			break;
		}
    }

	public Object getValueAt(int row, int col) {
		Event evt = (Event)events.get(row);
		switch (col) {
			case 0:
					return evt.getTaskID();
			case 1:
				String fromDate = new SimpleDateFormat("EEE, d MMM yyyy").format(evt.getFromDate()).toString();
				String fromTime = new SimpleDateFormat("h:mm a").format(evt.getFromDate()).toString();
				return "<html>" + fromDate + "<br/>" + fromTime + "</html>";
			case 2:
				String toDate = new SimpleDateFormat("EEE, d MMM yyyy").format(evt.getToDate()).toString();
				String toTime = new SimpleDateFormat("h:mm a").format(evt.getToDate()).toString();
				return "<html>" + toDate + "<br/>" + toTime + "</html>";
			case 3:
				StringBuffer sb = new StringBuffer("<html>" + evt.getTaskDesc() + "</html>");
				if (sb.length() > 60) {
					sb.insert(60, "<br/>");
				}
				return sb.toString();
			case 4:
				return evt.isDone();
		}
		return new String();
	}
}