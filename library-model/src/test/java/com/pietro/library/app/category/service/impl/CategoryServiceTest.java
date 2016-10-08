package com.pietro.library.app.category.service.impl;

import static com.pietro.library.app.commontests.category.CategoryForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pietro.library.app.category.exception.CategoryExistsException;
import com.pietro.library.app.category.model.Category;
import com.pietro.library.app.category.repository.CategoryRepository;
import com.pietro.library.app.category.service.CategoryService;
import com.pietro.library.app.common.exception.FieldNotValidException;

public class CategoryServiceTest {
	private static final Logger LOG = LoggerFactory.getLogger(CategoryServiceTest.class);
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

	private void addCategoryWithInvalidName(final String name) {
		try {
			categoryService.add(new Category(name));
			fail("An error should have beed thrown");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo("name")));
			LOG.info("addCategoryWithInvalidName - " + e.getMessage());
		}
	}

}
