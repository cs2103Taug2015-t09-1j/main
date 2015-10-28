/**
 *
 */
package main.model.tableModels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import main.model.EnumTypes.TASK_TYPE;
import main.model.taskModels.Event;
import main.model.taskModels.Task;
import main.storage.Storage;
import main.ui.MainGUI;

/**
 * @author Dalton
 *
 */
public class EventsTableModel extends AbstractTableModel {
	private static EventsTableModel etm = EventsTableModel.getInstance();
	private final String[] columnNames = { "ID", "Start Date", "End Date", "Task Description", "Done" };
	private final Class<?>[] columnTypes = { Integer.class, Date.class, Date.class, String.class, Boolean.class };
	private List<Task> events = new ArrayList<>();
	private MainGUI mainGui;

	private EventsTableModel() {
		super();
	}
	
	public void setTasks(List<Task> tasks) {
		this.events = tasks;
	}

	public static EventsTableModel getInstance() {
		if (etm == null) {
			etm = new EventsTableModel();
		}
		return etm;
	}
	
	public void setMainGui(MainGUI mainGui) {
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
	    return events.size();
    }

	public boolean isCellEditable(int row, int col) {
        switch (col) {
	    	case 1:
	    		return true;
	    	case 2:
	    		return true;
        	case 3:
        		return true;
        	case 4:
        		return true;
        	default:
        		return false;
        }
    }

	public void setValueAt(Object value, int row, int col) {
		Event evt = (Event)events.get(row);
		Boolean shouldProcess = false;
		String fakeCommand = "update " + evt.getTaskID() + " " + (col + 1) + " ";
		switch (col) {
			case 1:
				shouldProcess = true;
				fakeCommand = fakeCommand + (Date)value;
				break;
			case 2:
				shouldProcess = true;
				fakeCommand = fakeCommand + (Date)value;
				break;
			case 3:
				shouldProcess = true;
				fakeCommand = fakeCommand + (String)value;
				break;
			case 4:
				shouldProcess = true;
				fakeCommand = ((Boolean)value ?  "done" : "undone") + " " + evt.getTaskID();
				break;
		}
		if (shouldProcess && mainGui != null) {
			mainGui.fakeInputComeIn(fakeCommand);
		}
    }

	public Object getValueAt(int row, int col) {
		Event evt = (Event)events.get(row);
		switch (col) {
			case 0:
				return evt.getTaskID();
			case 1:
				return evt.getFromDate();
			case 2:
				//return (evt.getFromDate() == evt.getToDate()) ? null : evt.getToDate();
				return evt.getToDate();
			case 3:
				return evt.getTaskDesc();
			case 4:
				return evt.isDone();
		}
		return new String();
	}
}