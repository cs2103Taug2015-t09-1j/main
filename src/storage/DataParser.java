package storage;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
	
	public static <E> String serialize(List<E> tasks) {
		Gson gson = getGson();
		Type listType = new TypeToken<List<E>>() {}.getType();
		return gson.toJson(tasks, listType);
	}
	
	public static <E> List<E> deserialize(String data) {
		Gson gson = getGson();
		Type listType = new TypeToken<List<E>>() {}.getType();
		return gson.fromJson(data, listType);
	}
}
