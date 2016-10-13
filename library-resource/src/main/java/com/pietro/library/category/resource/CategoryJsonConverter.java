package com.pietro.library.category.resource;

import com.google.gson.JsonObject;
import com.pietro.library.category.model.Category;
import com.pietro.library.common.json.JsonReader;

public class CategoryJsonConverter {
	public Category convertFrom(final String json) {
		final JsonObject jsonObject = JsonReader.readAsJsonObject(json);

		final Category category = new Category();
		category.setName(JsonReader.getStringOrNull(jsonObject, "name"));

		return category;
	}
}
