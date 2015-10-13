/**
 *
 */
package models;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;

import models.FloatingTask;

/**
 * @author Dalton
 *
 */
public class FloatingTasksTableModel extends AbstractTableModel {
	private String[] columnNames = { "ID", "Task Description", "Done" };
	private Class[] columnTypes = { Integer.class, String.class, Boolean.class };

	Vector<FloatingTask> tasksVector;

	public FloatingTasksTableModel(Vector<FloatingTask> tasksVector) {
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
		FloatingTask t = (FloatingTask)tasksVector.elementAt(row);
		switch (col) {
			case 0:
					//t.setTaskID((Integer) value);
			break;
			case 1:
					t.setTaskDesc((String) value);
			break;
			case 2:
					t.setDone((Boolean) value);
			break;
		}
    }

	public Object getValueAt(int row, int col) {
		FloatingTask t = (FloatingTask)tasksVector.elementAt(row);
		switch (col) {
			case 0:
					return t.getTaskID();
			case 1:
					return t.getTaskDesc();
			case 2:
					return t.isDone();
		}

		return new String();
	}
}