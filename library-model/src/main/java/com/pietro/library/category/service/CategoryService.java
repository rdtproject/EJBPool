package com.pietro.library.category.service;

import java.util.List;

import com.pietro.library.category.exception.CategoryExistsException;
import com.pietro.library.category.exception.CategoryNotFoundException;
import com.pietro.library.category.model.Category;
import com.pietro.library.common.exception.FieldNotValidException;

public interface CategoryService {
	Category add(Category category) throws FieldNotValidException, CategoryExistsException;

	void update(Category category) throws FieldNotValidException, CategoryNotFoundException;

	Category findById(long id) throws CategoryNotFoundException;

	List<Category> findAll();

}
