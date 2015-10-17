package models;

import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

import models.Commands.TASK_TYPE;
import storage.Storage;

/**
 * @author Dalton
 *
 */
public class DeadlineTasksTableModel extends AbstractTableModel {
	private String[] columnNames = { "ID", "Deadline", "Task Description", "Done" };
	private Class<?>[] columnTypes = { Integer.class, Date.class, String.class, Boolean.class };

	ArrayList<DeadlineTask> deadlines;

	public DeadlineTasksTableModel() {
		super();
		this.deadlines = (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.DEADLINE_TASK);
	}

	public DeadlineTasksTableModel(ArrayList<DeadlineTask> deadlines) {
		super();
		this.deadlines = deadlines;
	}

	public int getColumnCount() {
		return columnNames.length;
    }

	public String getColumnName(int col) {
		return columnNames[col];
	}

    public Class<?> getColumnClass(int col) {
		return columnTypes[col];
	}

	public int getRowCount() {
	    return deadlines.size();
    }

	public boolean isCellEditable(int row, int col) {
        switch (col) {
        	case 1:
        		return true;
        	case 2:
        		return true;
        	case 3:
        		return true;
            default:
                return false;
        }
    }

	public void setValueAt(Object value, int row, int col) {
		DeadlineTask t = (DeadlineTask)deadlines.get(row);
		switch (col) {
			case 1:
				t.setDate((Date)value);
				break;
			case 2:
				t.setTaskDesc((String) value);
				break;
			case 3:
				t.setDone((Boolean)value);
				break;
		}
		Storage.saveTaskType(TASK_TYPE.DEADLINE_TASK);
    }

	public Object getValueAt(int row, int col) {
		DeadlineTask t = (DeadlineTask)deadlines.get(row);
		switch (col) {
			case 0:
				return t.getTaskID();
			case 1:
				return t.getDate();
			case 2:
				return t.getTaskDesc();
			case 3:
				return t.isDone();
		}

		return new String();
	}
}