package com.pietro.library.category.resource;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pietro.library.category.model.Category;
import com.pietro.library.common.json.JsonReader;

/**
 * @ApplicationScoped - JBoss automatycznie zinstancjonuje klase i bedzie ona
 *                    dostepna w calej aplikacji (tylko 1 instancja!)
 *
 */

@ApplicationScoped
public class CategoryJsonConverter {
	public Category convertFrom(final String json) {
		final JsonObject jsonObject = JsonReader.readAsJsonObject(json);

		final Category category = new Category();
		category.setName(JsonReader.getStringOrNull(jsonObject, "name"));

		return category;
	}

	public JsonElement convertToJsonElement(final Category category) {
		final JsonObject object = new JsonObject();
		object.addProperty("id", category.getId());
		object.addProperty("name", category.getName());
		return object;
	}

	public JsonElement convertToJsonElement(final List<Category> categories) {
		final JsonArray jsonArray = new JsonArray();
		for (final Category category : categories) {
			jsonArray.add(convertToJsonElement(category));
		}
		return jsonArray;
	}
}
