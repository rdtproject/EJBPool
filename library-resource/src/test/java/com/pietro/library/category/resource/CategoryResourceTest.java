package com.pietro.library.category.resource;

import static com.pietro.library.commontests.category.CategoryForTestsRepository.categoryWithId;
import static com.pietro.library.commontests.category.CategoryForTestsRepository.java;
import static com.pietro.library.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static com.pietro.library.commontests.utils.JsonTestUtils.assertJsonMatchesExpectedJson;
import static com.pietro.library.commontests.utils.JsonTestUtils.readJsonFile;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pietro.library.category.service.CategoryService;

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
		assertThat(response.getStatus(), is(equalTo(201)));
		assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
	}
}