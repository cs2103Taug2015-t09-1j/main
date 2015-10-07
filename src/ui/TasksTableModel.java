/**
 *
 */
package ui;

import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

import models.Events;
import models.FloatingTask;

/**
 * @author Dalton
 *
 */
public class TasksTableModel extends AbstractTableModel {
	private String[] eventCols = { "ID", "Date", "Start", "End", "Task Description", "Done" };
	private Class[] eventTypes = { Integer.class, String.class, String.class, String.class, String.class, Boolean.class };
	private String[] floatingCols = { "ID", "Task Description", "Done" };
	private Class[] floatingTypes = { Integer.class, String.class, Boolean.class };

	Vector tasksVector;
	String taskType;

	public TasksTableModel(Vector tasksVector, String taskType) {
		super();
		this.tasksVector = tasksVector;

		this.taskType = taskType;
	}

	public int getColumnCount() {
		if (taskType.equals("Event")) {
			return eventCols.length;
		} else {
			return floatingCols.length;
		}
    }

	public String getColumnName(int col) {
		if (taskType.equals("Event")) {
			return eventCols[col];
		} else {
			return floatingCols[col];
		}
	}

    public Class getColumnClass(int col) {
    	if (taskType.equals("Event")) {
			return eventTypes[col];
		} else {
			return floatingTypes[col];
		}
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
		if (taskType.equals("Event")) {
			Events t = (Events)tasksVector.elementAt(row);
			switch (col) {
				case 0:
						t.setTaskID((Integer) value);
				break;
				case 1:
						//t.setDate((Date) value);
				break;
				case 2:
						t.setStartTime((String) value);
				break;
				case 3:
						t.setEndTime((String) value);
				break;
				case 4:
						t.setTaskDesc((String) value);
				break;
				case 5:
						t.setDone((Boolean) value);
				break;
			}
		} else {
			FloatingTask t = (FloatingTask)tasksVector.elementAt(row);
			switch (col) {
				case 0:
						t.setTaskID((Integer) value);
				break;
				case 1:
						t.setTaskDesc((String) value);
				break;
				case 2:
						t.setDone((Boolean) value);
				break;
			}
		}
    }

	public Object getValueAt(int row, int col) {
		if (taskType.equals("Event")) {
			Events t = (Events)tasksVector.elementAt(row);
			switch (col) {
				case 0:
						return t.getTaskID();
				case 1:
						//return t.getDate().format(DateTimeFormatter.ofPattern("E, d MMM y"));
						return new Date();
				case 2:
						//return t.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm a"));
				case 3:
						//return t.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm a"));
				case 4:
						//return t.getTaskDesc();
				case 5:
						return t.isDone();
			}
		} else {
			FloatingTask t = (FloatingTask)tasksVector.elementAt(row);
			switch (col) {
				case 0:
						return t.getTaskID();
				case 1:
						return t.getTaskDesc();
				case 2:
						return t.isDone();
			}
		}

		return new String();
	}
}