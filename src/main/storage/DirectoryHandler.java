package main.storage;

//@@author Hiep
public class DirectoryHandler {
	public static String fixDir(String dir) {
		return dir.replace("\\", "/");
	}
	
	public static String getCurrentDir() {
		return System.getProperty("user.dir");
	}
}
