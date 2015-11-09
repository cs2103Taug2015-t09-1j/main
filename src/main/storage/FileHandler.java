package main.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

//@@author Hiep
public class FileHandler {

	public static void createNewFolderIfNotExisit(String dir) {
		File file = new File(dir);
		if (!file.exists()) {
			try {
				file.mkdirs();
			} catch (Exception e) {
				// error occurs
			}
		}
	}

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

		StringBuilder result = new StringBuilder();
		BufferedReader fr = null;

		try {
			fr = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			String line;
			while ((line = fr.readLine()) != null)
				result.append(line);
		} catch (Exception e){
			// error occurs
		} finally {
			try {
				fr.close();
			} catch (Exception e) {
				// error occurs
			}
		}
		return result.toString();
	}

	public static void writeToFile(String fileName, String data) {
		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
			bw.write(data);
		} catch (IOException e) {
			// error occurs
		} finally {
			try {
				bw.close();
			} catch (IOException e){
				// error occurs
			}
		}
	}
}
