package com.pietro.library.category.resource;

import static com.pietro.library.commontests.category.CategoryForTestsRepository.*;
import static com.pietro.library.commontests.utils.FileTestNameUtils.*;
import static com.pietro.library.commontests.utils.JsonTestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pietro.library.category.exception.CategoryExistsException;
import com.pietro.library.category.model.Category;
import com.pietro.library.category.service.CategoryService;
import com.pietro.library.common.exception.FieldNotValidException;
import com.pietro.library.common.model.HttpCode;

public class CategoryResourceTest {
	private CategoryResource categoryResource;

	private static final String PATH_RESOURCE = "categories";

	@Mock
	private CategoryService categoryService;

	@Before
	public void initTestCase() {
		MockitoAnnotations.initMocks(this);
		categoryResource = new CategoryResource();
		categoryResource.categoryService = categoryService;
		categoryResource.categoryJsonConverter = new CategoryJsonConverter();
	}

	@Test
	public void addValidCategory() {
		when(categoryService.add(java())).thenReturn(categoryWithId(java(), 1L));
		final Response response = categoryResource
				.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "newCategory.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.CREATED.getCode())));
		assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
	}

	@Test
	public void addExistingCategory() {
		when(categoryService.add(java())).thenThrow(new CategoryExistsException());
		final Response response = categoryResource
				.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "newCategory.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
	}

	@Test
	public void addCategoryWithNullName() {
		when(categoryService.add(new Category())).thenThrow(new FieldNotValidException("name", "may not be null"));
		final Response response = categoryResource
				.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "categoryWithNullName.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "categoryErrorNullName.json");
	}

	private void assertJsonResponseWithFile(final Response response, final String fileName) {
		assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, fileName));
	}
}