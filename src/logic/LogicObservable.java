/**
 *
 */
package logic;

import java.util.Observable;

import models.Commands;
import ui.MessageObserver;
import ui.TableModelsObserver;

/**
 * @author Dalton
 *
 */
public class LogicObservable extends Observable {
	private static LogicObservable observable = null;

	private LogicObservable() {}

	public static LogicObservable getInstance() {
		if (observable == null) {
			observable = new LogicObservable();
		}
		return observable;
	}

	public void updateStatusMsg(String msg) {
		observable.addObserver(MessageObserver.getInstance());
		setChanged();
		notifyObservers(msg);
		observable.deleteObserver(MessageObserver.getInstance());
	}

	public void updateTables(Commands.TASK_TYPE type) {
		observable.addObserver(TableModelsObserver.getInstance());
		setChanged();
		notifyObservers(type);
		observable.deleteObserver(TableModelsObserver.getInstance());
	}
}
