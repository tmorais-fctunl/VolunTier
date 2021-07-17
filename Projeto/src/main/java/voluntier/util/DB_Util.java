package voluntier.util;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Value;

import voluntier.exceptions.AlreadyExistsException;
import voluntier.exceptions.InexistentElementException;

public class DB_Util {
	Consumer<Entity> action;
	public Entity.Builder builder;
	
	public DB_Util(Consumer<Entity> action) {
		this.action = action;
	}
	
	public Entity.Builder getDeafultBuilder(Entity e) {
		action.accept(e);
		return builder;
	}
	
	public <T extends Value<?>> Entity updateProperty(Entity e, String property_name, T new_value) {
		getDeafultBuilder(e);
		return builder.set(property_name, new_value).build();
	}
	
	public Entity setStringListProperty(Entity e, String list_property, List<String> list) {
		ListValue.Builder newList = ListValue.newBuilder();
		list.forEach(str -> newList.addValue(str));
		
		return updateProperty(e, list_property, newList.build());
	}
	
	public static boolean existsInStringList(Entity e, String list_property, String looking_for) {
		List<String> list = getStringList(e, list_property);
		
		return list.contains(looking_for);
	}
	
	public static <T> T findInJsonList(Entity e, String list_property, Predicate<T> condition, Class<T> typeOfSource) {
		List<T> list = getJsonList(e, list_property, typeOfSource);
		
		T found = null;
		for(T elem : list)
			if(condition.test(elem))
				found = elem;
		
		return found;
	}
	
	public static <T> boolean existsInJsonList(Entity e, String list_property, Predicate<T> condition, Class<T> typeOfSource) {
		return findInJsonList(e, list_property, condition, typeOfSource) != null;
	}
	
	public Entity addUniqueStringToList(Entity e, String list_property, String newElement) throws AlreadyExistsException {
		List<Value<?>> current_list = e.getList(list_property);

		if (current_list.contains(StringValue.of(newElement)))
			throw new AlreadyExistsException();

		ListValue.Builder newList = ListValue.newBuilder().set(current_list);
		newList.addValue(newElement);
		
		return updateProperty(e, list_property, newList.build());
	}
	
	public <T> Entity addUniqueJsonToList(Entity e, String list_property, T newElement) throws AlreadyExistsException {
		return addUniqueStringToList(e, list_property, JsonUtil.json.toJson(newElement));
	}
	
	public Entity removeStringFromList(Entity e, String list_property, String remElement) throws InexistentElementException {
		List<String> string_list = getStringList(e, list_property);
		
		if (!string_list.contains(remElement))
			throw new InexistentElementException("Inesistent element in list: " + remElement);

		string_list.remove(remElement);

		return setStringListProperty(e, list_property, string_list);
	}
	
	public <T> Entity removeJsonFromList(Entity e, String list_property, T remElement) throws InexistentElementException {
		return removeStringFromList(e, list_property, JsonUtil.json.toJson(remElement));
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
