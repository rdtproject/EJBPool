package com.pietro.library.category.resource;

import static com.pietro.library.commontests.category.CategoryForTestsRepository.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pietro.library.category.service.CategoryService;

public class CategoryResourceTest {
	private CategoryResource categoryResource;

	@Mock
	private CategoryService categoryService;

	public void initTestCase() {
		MockitoAnnotations.initMocks(this);
		categoryResource = new CategoryResource();
		categoryResource.categoryService = categoryService;
	}

	@Test
	public void addValidCategory() {
		when(categoryService.add(java())).thenReturn(categoryWithId(java(), 1L));
		// final Response response = categoryResource.add("readfile");
	}
}