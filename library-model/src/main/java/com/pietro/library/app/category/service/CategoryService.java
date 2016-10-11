package com.pietro.library.app.category.service;

import com.pietro.library.app.category.exception.CategoryExistsException;
import com.pietro.library.app.category.model.Category;
import com.pietro.library.app.common.exception.FieldNotValidException;

public interface CategoryService {
	Category add(Category category) throws FieldNotValidException, CategoryExistsException;

	void edit(Category category) throws FieldNotValidException;

}
