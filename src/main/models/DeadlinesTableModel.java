package main.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import main.models.EnumTypes.TASK_TYPE;
import main.storage.Storage;

/**
 * @author Dalton
 *
 */
public class DeadlinesTableModel extends AbstractTableModel {
	private static DeadlinesTableModel dtm = DeadlinesTableModel.getInstance();
	private final String[] columnNames = { "ID", "Deadline", "Task Description", "Done" };
	private final Class<?>[] columnTypes = { Integer.class, Date.class, String.class, Boolean.class };
	private List<Task> deadlines;

	public DeadlinesTableModel() {
		super();
		this.deadlines = Storage.getInstance().getAllTask(EnumTypes.TASK_TYPE.DEADLINE);
	}

	public static DeadlinesTableModel getInstance() {
		if (dtm == null) {
			dtm = new DeadlinesTableModel();
		}
		return dtm;
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
		Deadline t = (Deadline)deadlines.get(row);
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
		Storage.getInstance().saveTaskType(TASK_TYPE.DEADLINE);
    }

	public Object getValueAt(int row, int col) {
		Deadline t = (Deadline)deadlines.get(row);
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