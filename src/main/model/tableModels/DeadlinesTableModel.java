package main.model.tableModels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import javafx.beans.Observable;
import main.model.EnumTypes.TASK_TYPE;
import main.model.taskModels.Deadline;
import main.model.taskModels.Task;
import main.storage.Storage;
import main.ui.MainGUI;

/**
 * @@author Dalton
 *
 */
@SuppressWarnings("serial")
public class DeadlinesTableModel extends AbstractTableModel {
	private static DeadlinesTableModel dtm = null;
	private static MainGUI mainGUI = null;
	private List<Task> deadlines = null;
	private final String[] columnNames = { "ID", "Deadline (2)", "Task Description (3)", "Done" };
	private final Class<?>[] columnTypes = { Integer.class, Date.class, String.class, Boolean.class };


	private DeadlinesTableModel(MainGUI ui) {
		super();
		mainGUI = ui;
		deadlines = new ArrayList<Task>();
	}

	public static DeadlinesTableModel getInstance(MainGUI ui) {
		if (dtm == null) {
			assert ui != null;
			dtm = new DeadlinesTableModel(ui);
		}
		return dtm;
	}

	public void setTasks(List<Task> tasks) {
		assert tasks != null;
		deadlines = tasks;
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
		String simulatedCommand = "update " + t.getTaskID() + " " + (col + 1) + " ";
		switch (col) {
			case 1:
				simulatedCommand += (Date)value;
				break;
			case 2:
				simulatedCommand += (String)value;
				break;
			case 3:
				simulatedCommand = ((Boolean)value ?  "done" : "undone") + " " + t.getTaskID();
				break;
			default:
				// impossible case
		}

		mainGUI.sendUserInput(simulatedCommand);
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
			default:
				// impossible case
		}

		return new String();
	}


}