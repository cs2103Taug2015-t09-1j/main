/**
 *
 */
package main.logic;

import java.util.Observable;
import java.util.Observer;

/**
 * @author Dalton
 *
 */
public class LogicObserver implements Observer {
	private static LogicObserver observer = null;
	private static final Logic logic = Logic.getInstance();

	private LogicObserver() {}

	public static LogicObserver getInstance() {
		if (observer == null) {
			observer = new LogicObserver();
		}
		return observer;
	}

	@Override
    public void update(Observable o, Object data) {
		logic.processCommand(data.toString());
    }
}