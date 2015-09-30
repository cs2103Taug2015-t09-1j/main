package storage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import model.Todo;

public class DataParser {
	
	private static volatile Gson gson = null;
	
	public static Gson getGson() {
        if (gson == null) {
            synchronized (DataParser.class) {
                if (gson == null) {
                	gson = new Gson();
                }
            }
        }
        return gson;
	}
	
	public static String serialize(List<Todo> todos) {
		Gson gson = getGson();
		Type listType = new TypeToken<List<Todo>>() {}.getType();
		return gson.toJson(todos, listType);
	}
	
	public static List<Todo> deserialize(String data) {
		Gson gson = getGson();
		Type listType = new TypeToken<List<Todo>>() {}.getType();
		return gson.fromJson(data, listType);
	}
}
