package storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {
	
	public static void createNewFileIfNotExisit(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// error occurs 
			}
		}
	}
	
	public static String readFromFile(String fileName) {
		
		createNewFileIfNotExisit(fileName);
		
		StringBuilder result = new StringBuilder();
		BufferedReader  fr = null;

		try {
			fr = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = fr.readLine()) != null) 
				result.append(line);
		} catch (IOException e){
			// error occurs 
		} finally {
			try {
				fr.close();
			} catch (IOException e) {
				// error occurs 
			}
		}

		return result.toString();
	}
	public static void writeToFile(String fileName, String data) {
		FileWriter fw = null;
		 
		try {
			fw = new FileWriter(fileName);
			fw.write(data);
		} catch (IOException e) {
			// error occurs
		} finally {
			try {
				fw.close();
			} catch (IOException e){
				// error occurs
			}
		}
	}
}
