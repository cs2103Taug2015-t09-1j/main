/**
 *
 */
package main.ui;
import java.util.Observable;
import main.logic.LogicObserver;

/**
 * @author Dalton
 *
 */
public class InputObservable extends Observable {
	private static InputObservable observable = null;

	private InputObservable() {}

	public static InputObservable getInstance() {
		if (observable == null) {
			observable = new InputObservable();
			observable.addObserver(LogicObserver.getInstance());
		}
		return observable;
	}

	public void sendUserInput(String input) {
		setChanged();
		notifyObservers(input);
	}
}