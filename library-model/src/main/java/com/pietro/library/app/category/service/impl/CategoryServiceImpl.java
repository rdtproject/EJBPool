package com.pietro.library.app.category.service.impl;

import java.util.Iterator;
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
		final Set<ConstraintViolation<Category>> errors = validator.validate(category);
		final Iterator<ConstraintViolation<Category>> itErrors = errors.iterator();

		if (itErrors.hasNext()) {
			final ConstraintViolation<Category> violation = itErrors.next();
			throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
		}

		if (categoryRepository.alreadyExists(category)) {
			throw new CategoryExistsException();
		}

		return categoryRepository.add(category);
	}

	@Override
	public void update(Category category) throws FieldNotValidException {
		final Set<ConstraintViolation<Category>> errors = validator.validate(category);
		final Iterator<ConstraintViolation<Category>> itErrors = errors.iterator();

		if (itErrors.hasNext()) {
			final ConstraintViolation<Category> violation = itErrors.next();
			throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
		}		
		
		if (categoryRepository.alreadyExists(category)) {
			throw new CategoryExistsException();
		}
		
		if (!categoryRepository.exitstsById(category.getId())) {
			throw new CategoryNotFoundException();
		}
		
		categoryRepository.update(category);
	}

}
