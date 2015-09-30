/**
 *
 */
package ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 * @author Dalton
 *
 */
public class TasksTableModel extends AbstractTableModel {
	public String[] columnNames = { "ID", "Date", "Start", "End", "Task", "Done" };
	public Class[] columnTypes = { Integer.class, LocalDateTime.class, LocalDateTime.class, LocalDateTime.class, String.class, Boolean.class };

	Vector<DeadlineTask> tasksVector;

	public TasksTableModel(Vector<DeadlineTask> tasksVector) {
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
            case 5:
                return true;
            default:
                return false;
        }
    }

	public void setValueAt(Object value, int row, int col) {
		DeadlineTask t = tasksVector.elementAt(row);

		switch (col) {
			case 0:
					t.setTaskID((Integer) value);
			break;
			case 1:
					t.setDate((LocalDateTime) value);
			break;
			case 2:
					t.setStartTime((LocalDateTime) value);
			break;
			case 3:
					t.setEndTime((LocalDateTime) value);
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
		DeadlineTask t = tasksVector.elementAt(row);

		switch (col) {
			case 0:
					return t.getTaskID();
			case 1:
					return t.getDate().format(DateTimeFormatter.ofPattern("E, d MMM y"));
			case 2:
					return t.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm a"));
			case 3:
					return t.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm a"));
			case 4:
					return t.getTaskDesc();
			case 5:
					return t.isDone();
		}

		return new String();
	}
}