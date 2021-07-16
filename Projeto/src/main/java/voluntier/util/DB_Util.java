package voluntier.util;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.Value;

public class DB_Util {
	BiConsumer<Entity, Entity.Builder> action;
	Entity.Builder builder;
	
	public DB_Util(BiConsumer<Entity, Entity.Builder> action) {
		this.action = action;
	}
	
	public Entity.Builder getDeafultBuilder(Entity e) {
		action.accept(e, builder);
		return builder;
	}
	
	public Entity updateListProperty(Entity e, String list_property, ListValue newList) {
		getDeafultBuilder(e);
		return builder.set(list_property, newList).build();
	}
	
	public Entity updateStringProperty(Entity e, String string_property, String newString) {
		getDeafultBuilder(e);
		return builder.set(string_property, newString).build();
	}
	
	public static boolean existsInStringList(Entity e, String list_property, String looking_for) {
		List<String> list = getStringList(e, list_property);
		
		return list.contains(looking_for);
	}
	
	public static List<String> getStringList(Entity e, String list_property){
		List<Value<?>> property = e.getList(list_property);
		List<String> list = new LinkedList<>();
		
		property.forEach(str -> list.add((String) str.get()));
		
		return list;
	}
	
	public static <T> List<T> getJsonList (Entity e, String list_property, Class<T> classOfT){
		List<Value<?>> property = e.getList(list_property);
		List<T> list = new LinkedList<>();
		property.forEach(str -> {
			T obj = JsonUtil.json.fromJson((String) str.get(), classOfT);
			list.add(obj);
		});
		
		return list;
	}

}
