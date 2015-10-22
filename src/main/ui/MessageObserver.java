/**
 *
 */
package main.ui;

import java.util.Observable;
import java.util.Observer;

/**
 * @author Dalton
 *
 */
public class MessageObserver implements Observer {
	private static MessageObserver observer = null;
	private MainGUI ui;

	private MessageObserver() {}

	public static MessageObserver getInstance() {
		if (observer == null) {
			observer = new MessageObserver();
		}
		return observer;
	}

	public void setOwner(MainGUI ui) {
		this.ui = ui;
	}

	@Override
    public void update(Observable o, Object data) {
		ui.updateStatusMsg(data.toString());
    }
}
