package com.pietro.library.app.category.service;

import java.util.List;

import com.pietro.library.app.category.exception.CategoryExistsException;
import com.pietro.library.app.category.exception.CategoryNotFoundException;
import com.pietro.library.app.category.model.Category;
import com.pietro.library.app.common.exception.FieldNotValidException;

public interface CategoryService {
	Category add(Category category) throws FieldNotValidException, CategoryExistsException;

	void update(Category category) throws FieldNotValidException, CategoryNotFoundException;

	Category findById(long id) throws CategoryNotFoundException;

	List<Category> findAll();

}
