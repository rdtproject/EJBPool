package com.pietro.library.app.commontests.category;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;

import com.pietro.library.app.category.model.Category;

@Ignore
public class CategoryForTestsRepository {

	public static Category java() {
		return new Category("Java");
	}

	public static Category cleanCode() {
		return new Category("Clean code");
	}

	public static Category architecture() {
		return new Category("Architecture");
	}

	public static Category networks() {
		return new Category("Networks");
	}

	public static Category categoryWithId(final Category category, final Long id) {
		category.setId(id);
		return category;
	}

	public static List<Category> allCategories() {
		return Arrays.asList(java(), cleanCode(), architecture(), networks());
	}
}
