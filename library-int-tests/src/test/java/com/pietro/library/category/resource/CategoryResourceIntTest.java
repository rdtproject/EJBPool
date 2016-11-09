package com.pietro.library.category.resource;

import static com.pietro.library.commontests.category.CategoryForTestsRepository.*;
import static com.pietro.library.commontests.utils.FileTestNameUtils.*;
import static com.pietro.library.commontests.utils.JsonTestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pietro.library.category.model.Category;
import com.pietro.library.common.json.JsonReader;
import com.pietro.library.common.model.HttpCode;
import com.pietro.library.commontests.utils.IntTestUtils;
import com.pietro.library.commontests.utils.ResourceClient;
import com.pietro.library.commontests.utils.ResourceDefinitions;

@RunWith(Arquillian.class)
public class CategoryResourceIntTest {

	@ArquillianResource
	private URL url;

	private ResourceClient resourceClient;

	private static final String PATH_RESOURCE = ResourceDefinitions.CATEGORY.getResourceName();

	@Deployment
	public static WebArchive createDeployment() {
		return ShrinkWrap
				.create(WebArchive.class)
				.addPackages(true, "com.pietro.library")
				.addAsResource("persistence-integration.xml", "META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.setWebXML(new File("src/test/resources/web.xml"))
				.addAsLibraries(
						Maven.resolver().resolve("com.google.code.gson:gson:2.3.1", "org.mockito:mockito-core:1.9.5")
								.withTransitivity().asFile());
	}

	@Before
	public void initTestCase() {
		this.resourceClient = new ResourceClient(url);

		resourceClient.resourcePath("/DB").delete();
	}

	@Test
	@RunAsClient
	public void addValidCategoryAndFindIt() {
		final Long id = addCategoryAndGetId("category.json");
		findCategoryAndAssertResponseWithCategory(id, java());
	}

	@Test
	@RunAsClient
	public void addCategoryWithNullName() {
		final Response response = resourceClient.resourcePath(PATH_RESOURCE).postWithFile(
				getPathFileRequest(PATH_RESOURCE, "categoryWithNullName.json"));

		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "categoryErrorNullName.json");
	}

	@Test
	@RunAsClient
	public void addExistentCategory() {
		resourceClient.resourcePath(PATH_RESOURCE).postWithFile(getPathFileRequest(PATH_RESOURCE, "category.json"));

		final Response response = resourceClient.resourcePath(PATH_RESOURCE).postWithFile(
				getPathFileRequest(PATH_RESOURCE, "category.json"));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
	}

	@Test
	@RunAsClient
	public void updateValidCategory() {
		final Long id = addCategoryAndGetId("category.json");
		findCategoryAndAssertResponseWithCategory(id, java());

		final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + id).putWithFile(
				getPathFileRequest(PATH_RESOURCE, "categoryCleanCode.json"));
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

		findCategoryAndAssertResponseWithCategory(id, cleanCode());
	}

	@Test
	@RunAsClient
	public void updateCategoryWithNameBelongingToOtherCategory() {
		final Long javaCategoryId = addCategoryAndGetId("category.json");
		addCategoryAndGetId("categoryCleanCode.json");

		final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + javaCategoryId).putWithFile(
				getPathFileRequest(PATH_RESOURCE, "categoryCleanCode.json"));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
	}

	@Test
	@RunAsClient
	public void updateCategoryNotFound() {
		final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/999").putWithFile(
				getPathFileRequest(PATH_RESOURCE, "category.json"));
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	}

	@Test
	@RunAsClient
	public void findCategoryNotFound() {
		final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/999").get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	}

	@Test
	@RunAsClient
	public void findAllCategories() {
		resourceClient.resourcePath("DB/" + PATH_RESOURCE).postWithContent("");

		final Response response = resourceClient.resourcePath(PATH_RESOURCE).get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertResponseContainsTheCategories(response, 4, architecture(), cleanCode(), java(), networks());
	}

	private void assertResponseContainsTheCategories(final Response response, final int expectedTotalRecords,
			final Category... expectedCategories) {
		final JsonObject result = JsonReader.readAsJsonObject(response.readEntity(String.class));

		final int totalRecords = result.getAsJsonObject("paging").get("totalRecords").getAsInt();
		assertThat(totalRecords, is(equalTo(expectedTotalRecords)));

		final JsonArray categoriesList = result.getAsJsonArray("entries");
		assertThat(categoriesList.size(), is(equalTo(expectedCategories.length)));

		for (int i = 0; i < expectedCategories.length; i++) {
			final Category expectedCategory = expectedCategories[i];
			assertThat(categoriesList.get(i).getAsJsonObject().get("name").getAsString(),
					is(equalTo(expectedCategory.getName())));
		}

	}

	private Long addCategoryAndGetId(final String fileName) {
		return IntTestUtils.addElementWithFileAndGetId(resourceClient, PATH_RESOURCE, PATH_RESOURCE, fileName);
	}

	private void assertJsonResponseWithFile(final Response response, final String fileName) {
		assertJsonMatchesFileContent(response.readEntity(String.class), getPathFileResponse(PATH_RESOURCE, fileName));
	}

	private void findCategoryAndAssertResponseWithCategory(final Long categoryIdToBeFound,
			final Category expectedCategory) {
		final String json = IntTestUtils.findById(resourceClient, PATH_RESOURCE, categoryIdToBeFound);

		final JsonObject categoryAsJson = JsonReader.readAsJsonObject(json);
		assertThat(JsonReader.getStringOrNull(categoryAsJson, "name"), is(equalTo(expectedCategory.getName())));
	}

}