/**
 *
 */
package ui;

import java.util.Observable;
import java.util.Observer;

import models.Commands;

/**
 * @author Dalton
 *
 */
public class TableModelsObserver implements Observer {
	private static TableModelsObserver observer = null;
	private MainGUI ui;

	private TableModelsObserver() {}

	public static TableModelsObserver getInstance() {
		if (observer == null) {
			observer = new TableModelsObserver();
		}
		return observer;
	}

	public void setOwner(MainGUI ui) {
		this.ui = ui;
	}

	@Override
    public void update(Observable o, Object data) {
		ui.reloadTables((Commands.TASK_TYPE)data);
    }
}
