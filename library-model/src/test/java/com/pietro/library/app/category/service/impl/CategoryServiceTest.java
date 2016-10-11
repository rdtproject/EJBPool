package com.pietro.library.app.category.service.impl;

import static com.pietro.library.app.commontests.category.CategoryForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;

import com.pietro.library.app.category.exception.CategoryExistsException;
import com.pietro.library.app.category.exception.CategoryNotFoundException;
import com.pietro.library.app.category.model.Category;
import com.pietro.library.app.category.repository.CategoryRepository;
import com.pietro.library.app.category.service.CategoryService;
import com.pietro.library.app.common.exception.FieldNotValidException;

public class CategoryServiceTest {
	private CategoryService categoryService;
	private Validator validator;
	private CategoryRepository categoryRepository;

	@Before
	public void initTestCase() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		categoryRepository = mock(CategoryRepository.class);
		categoryService = new CategoryServiceImpl();
		((CategoryServiceImpl) categoryService).validator = validator;
		((CategoryServiceImpl) categoryService).categoryRepository = categoryRepository;
	}

	@Test
	public void addCategoryWithNullName() {
		addCategoryWithInvalidName(null);
	}

	@Test
	public void addCategoryWithToShortName() {
		addCategoryWithInvalidName("A");
	}

	@Test
	public void addCategoryWithToLongName() {
		addCategoryWithInvalidName("This is a long name that will cause an exception to be thrown");
	}

	@Test(expected = CategoryExistsException.class)
	public void addCategoryWithExistingName() {
		when(categoryRepository.alreadyExists(java())).thenReturn(true);

		categoryService.add(java());
	}

	@Test
	public void addValidCategory() {
		when(categoryRepository.alreadyExists(java())).thenReturn(false);
		when(categoryRepository.add(java())).thenReturn(categoryWithId(java(), 1L));

		final Category categoryAdded = categoryService.add(java());
		assertThat(categoryAdded.getId(), is(equalTo(1L)));
	}

	@Test
	public void updateWithNullName() {
		updateCategoryWithInvalidName(null);
	}

	@Test
	public void updateWithShortName() {
		updateCategoryWithInvalidName("A");
	}

	@Test
	public void updateWithLongName() {
		updateCategoryWithInvalidName("This is a long name that will cause an exception to be thrown");
	}

	@Test(expected = CategoryExistsException.class)
	public void updateCategoryWithExistingName() {
		when(categoryRepository.alreadyExists(categoryWithId(java(), 1L))).thenReturn(true);
		categoryService.update(categoryWithId(java(), 1L));
	}

	@Test(expected = CategoryNotFoundException.class)
	public void updateCategoryNotFound() {
		when(categoryRepository.alreadyExists(categoryWithId(java(), 1L))).thenReturn(false);
		when(categoryRepository.exitstsById(1L)).thenReturn(false);

		categoryService.update(categoryWithId(java(), 1L));
	}

	@Test
	public void updateValidCategory() {
		when(categoryRepository.alreadyExists(categoryWithId(java(), 1L))).thenReturn(false);
		when(categoryRepository.exitstsById(1L)).thenReturn(true);

		categoryService.update(categoryWithId(java(), 1L));

		// checks it the method was invoked
		verify(categoryRepository).update(categoryWithId(java(), 1L));
	}

	@Test
	public void findCategoryById() {
		when(categoryRepository.findById(1L)).thenReturn(categoryWithId(java(), 1L));
		final Category category = categoryService.findById(1L);
		assertThat(category, is(notNullValue()));
		assertThat(category.getId(), is(equalTo(1L)));
		assertThat(category.getName(), is(equalTo(java().getName())));
	}

	@Test(expected = CategoryNotFoundException.class)
	public void findCategoryByIdNotFound() {
		when(categoryRepository.findById(1L)).thenReturn(null);
		categoryService.findById(1L);
	}

	@Test
	public void findAllNoCategories() {
		when(categoryRepository.findAll("name")).thenReturn(new ArrayList<>());
		final List<Category> categories = categoryService.findAll();
		assertThat(categories.isEmpty(), is(equalTo(true)));
	}

	@Test
	public void findAllCategories() {
		when(categoryRepository.findAll("name"))
				.thenReturn(Arrays.asList(categoryWithId(java(), 1L), categoryWithId(networks(), 2L)));

		final List<Category> categories = categoryService.findAll();
		assertThat(categories.size(), is(equalTo(2)));
		assertThat(categories.get(0).getName(), is(equalTo(java().getName())));
		assertThat(categories.get(1).getName(), is(equalTo(networks().getName())));
	}

	private void addCategoryWithInvalidName(final String name) {
		try {
			categoryService.add(new Category(name));
			fail("An error should have beed thrown");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo("name")));
		}
	}

	private void updateCategoryWithInvalidName(final String name) {
		try {
			categoryService.update(new Category(name));
			fail("An error should have beed thrown");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo("name")));
		}
	}

}
