package main.model.tableModels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import main.model.EnumTypes.TASK_TYPE;
import main.model.taskModels.Deadline;
import main.model.taskModels.Task;
import main.storage.Storage;
import main.ui.MainGui;

/**
 * @author Dalton
 *
 */
public class DeadlinesTableModel extends AbstractTableModel {
	private static DeadlinesTableModel dtm = DeadlinesTableModel.getInstance();
	private final String[] columnNames = { "ID", "Deadline", "Task Description", "Done" };
	private final Class<?>[] columnTypes = { Integer.class, Date.class, String.class, Boolean.class };
	private List<Task> deadlines = new ArrayList<>();
	private MainGui mainGui;

	public DeadlinesTableModel() {
		super();
	}

	public void setTasks(List<Task> tasks) {
		this.deadlines = tasks;
	}
	
	public static DeadlinesTableModel getInstance() {
		if (dtm == null) {
			dtm = new DeadlinesTableModel();
		}
		return dtm;
	}
	
	public void setMainGui(MainGui mainGui) {
		this.mainGui = mainGui;
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
		Boolean shouldProcess = false;
		String fakeCommand = "update " + t.getTaskID() + " " + (col + 1) + " ";
		switch (col) {
			case 1:
				shouldProcess = true;
				fakeCommand = fakeCommand + (Date)value;
				break;
			case 2:
				shouldProcess = true;
				fakeCommand = fakeCommand + (String)value;
				break;
			case 3:
				shouldProcess = true;
				fakeCommand = fakeCommand + (Boolean)value;
				break;
		}
		if (shouldProcess && mainGui != null) {
			mainGui.fakeInputComeIn(fakeCommand);
		}
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