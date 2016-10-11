package com.pietro.library.app.category.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.pietro.library.app.category.exception.CategoryExistsException;
import com.pietro.library.app.category.exception.CategoryNotFoundException;
import com.pietro.library.app.category.model.Category;
import com.pietro.library.app.category.repository.CategoryRepository;
import com.pietro.library.app.category.service.CategoryService;
import com.pietro.library.app.common.exception.FieldNotValidException;

public class CategoryServiceImpl implements CategoryService {

	Validator validator;
	CategoryRepository categoryRepository;

	@Override
	public Category add(final Category category) {
		validateCategory(category);

		return categoryRepository.add(category);
	}

	@Override
	public void update(final Category category) throws FieldNotValidException {
		validateCategory(category);

		if (!categoryRepository.exitstsById(category.getId())) {
			throw new CategoryNotFoundException();
		}

		categoryRepository.update(category);
	}

	@Override
	public Category findById(final long id) throws CategoryNotFoundException {
		final Category category = categoryRepository.findById(id);

		if (category == null) {
			throw new CategoryNotFoundException();
		}

		return category;
	}

	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll("name");
	}

	private void validateCategory(final Category category) {
		validateCategoryFields(category);

		if (categoryRepository.alreadyExists(category)) {
			throw new CategoryExistsException();
		}
	}

	private void validateCategoryFields(final Category category) {
		final Set<ConstraintViolation<Category>> errors = validator.validate(category);
		final Iterator<ConstraintViolation<Category>> itErrors = errors.iterator();

		if (itErrors.hasNext()) {
			final ConstraintViolation<Category> violation = itErrors.next();
			throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
		}
	}
}
