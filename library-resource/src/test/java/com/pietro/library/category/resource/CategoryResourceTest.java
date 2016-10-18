package com.pietro.library.category.resource;

import static com.pietro.library.commontests.category.CategoryForTestsRepository.*;
import static com.pietro.library.commontests.utils.FileTestNameUtils.*;
import static com.pietro.library.commontests.utils.JsonTestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pietro.library.category.exception.CategoryExistsException;
import com.pietro.library.category.exception.CategoryNotFoundException;
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

	@Test
	public void updateValidCategory() {
		final Response response = categoryResource.update(1L,
				readJsonFile(getPathFileRequest(PATH_RESOURCE, "category.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertThat(response.getEntity().toString(), is(equalTo("")));

		verify(categoryService).update(categoryWithId(java(), 1L));
	}

	@Test
	public void updateCategoryWithNameBelongingToOtherCategory() {
		doThrow(new CategoryExistsException()).when(categoryService).update(categoryWithId(java(), 1L));
		final Response response = categoryResource.update(1L,
				readJsonFile(getPathFileRequest(PATH_RESOURCE, "category.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
	}

	@Test
	public void updateCategoryWithNullName() {
		doThrow(new FieldNotValidException("name", "may not be null")).when(categoryService)
				.update(categoryWithId(new Category(), 1L));
		final Response response = categoryResource.update(1L,
				readJsonFile(getPathFileRequest(PATH_RESOURCE, "categoryWithNullName.json")));

		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "categoryErrorNullName.json");
	}

	@Test
	public void updateCategoryNotFound() {
		doThrow(new CategoryNotFoundException()).when(categoryService).update(categoryWithId(java(), 2L));
		final Response response = categoryResource.update(2L,
				readJsonFile(getPathFileRequest(PATH_RESOURCE, "category.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
		assertJsonResponseWithFile(response, "categoryNotFound.json");
	}

	@Test
	public void findCategory() {
		when(categoryService.findById(1L)).thenReturn(categoryWithId(java(), 1L));
		final Response response = categoryResource.findById(1L);
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertJsonResponseWithFile(response, "categoryFound.json");
	}

	@Test
	public void findCategoryNotFound() {
		when(categoryService.findById(1L)).thenThrow(new CategoryNotFoundException());

		final Response response = categoryResource.findById(1L);
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	}

	@Test
	public void findAllNoCategory() {
		when(categoryService.findAll()).thenReturn(new ArrayList<>());
		final Response response = categoryResource.findAll();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertJsonResponseWithFile(response, "emptyListOfCategories.json");
	}

	@Test
	public void findAllTwoCategories() {
		when(categoryService.findAll())
				.thenReturn(Arrays.asList(categoryWithId(java(), 1L), categoryWithId(networks(), 2L)));
		final Response response = categoryResource.findAll();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertJsonResponseWithFile(response, "twoCategories.json");
	}

	private void assertJsonResponseWithFile(final Response response, final String fileName) {
		assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, fileName));
	}
}