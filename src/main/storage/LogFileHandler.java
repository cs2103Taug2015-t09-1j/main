/**
 *
 */
package main.storage;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Dalton
 *
 */
public class LogFileHandler {
	public static LogFileHandler lfHandler = null;
	private static final Logger logger = Logger.getLogger(LogFileHandler.class.getName());
	private static Handler fileHandler = null;

	public LogFileHandler() {
		try {
			fileHandler = new FileHandler("debug.log", true);
			logger.addHandler(fileHandler);
			logger.setLevel(Level.FINE);
			fileHandler.setFormatter(new SimpleFormatter());
		} catch (SecurityException e1) {
			logger.log(Level.SEVERE, e1.getMessage(), e1);
		} catch (IOException e2) {
			logger.log(Level.SEVERE, e2.getMessage(), e2);
		}
	}

	public static LogFileHandler getInstance() {
		if (lfHandler == null) {
			lfHandler = new LogFileHandler();
		}
		return lfHandler;
	}

	public void addLogFileHandler(Logger l) {
		l.addHandler(fileHandler);
	}
}
