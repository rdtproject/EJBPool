package com.pietro.library.category.resource;

import static com.pietro.library.commontests.category.CategoryForTestsRepository.*;
import static com.pietro.library.commontests.utils.FileTestNameUtils.*;
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

import com.google.gson.JsonObject;
import com.pietro.library.common.json.JsonReader;
import com.pietro.library.common.model.HttpCode;
import com.pietro.library.commontests.utils.JsonTestUtils;
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
	}

	@Test
	@RunAsClient
	public void addValidCategoryAndFindIt() {
		final Response response = resourceClient.resourcePath(PATH_RESOURCE).postWithFile(
				getPathFileRequest(PATH_RESOURCE, "category.json"));
		assertThat(response.getStatus(), is(equalTo(HttpCode.CREATED.getCode())));
		final Long id = JsonTestUtils.getIdFromJson(response.readEntity(String.class));

		final Response responseGet = resourceClient.resourcePath(PATH_RESOURCE + "/" + id).get();
		assertThat(responseGet.getStatus(), is(equalTo(HttpCode.OK.getCode())));

		final JsonObject categoryAsJson = JsonReader.readAsJsonObject(responseGet.readEntity(String.class));
		assertThat(JsonReader.getStringOrNull(categoryAsJson, "name"), is(equalTo(java().getName())));
	}

}