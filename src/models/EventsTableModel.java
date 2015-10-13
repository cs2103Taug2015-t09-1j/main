/**
 *
 */
package models;

import java.text.SimpleDateFormat;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

import models.Event;

/**
 * @author Dalton
 *
 */
public class EventsTableModel extends AbstractTableModel {
	private String[] columnNames = { "ID", "Start Date", "End Date", "Task Description", "Done" };
	private Class[] columnTypes = { Integer.class, String.class, String.class, String.class, Boolean.class };

	Vector<Event> tasksVector;

	public EventsTableModel(Vector<Event> tasksVector) {
		super();
		this.tasksVector = tasksVector;
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
	    return tasksVector.size();
    }

	public boolean isCellEditable(int row, int col) {
        switch (col) {
            default:
                return false;
        }
    }

	public void setValueAt(Object value, int row, int col) {
		Event t = (Event)tasksVector.elementAt(row);
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
					//t.setEndTime((String) value);
			break;
			case 4:
					t.setTaskDesc((String) value);
			break;
			case 5:
					t.setDone((Boolean) value);
			break;
		}
    }

	public Object getValueAt(int row, int col) {
		Event t = (Event)tasksVector.elementAt(row);
		switch (col) {
			case 0:
					return t.getTaskID();
			case 1:
					//return t.getDate().format(DateTimeFormatter.ofPattern("E, d MMM y"));
					//return t.getDate();
				return new SimpleDateFormat("EEE, d MMM yyyy h:mm a").format(t.getFromDate()).toString();
			case 2:
					//return t.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm a"));
					//return t.getStartTime();
				return new SimpleDateFormat("EEE, d MMM yyyy h:mm a").format(t.getToDate()).toString();
			case 3:
				return t.getTaskDesc();
			case 4:
					//return t.getTaskDesc();
				return t.isDone();
			case 5:
					return null;
		}

		return new String();
	}
}