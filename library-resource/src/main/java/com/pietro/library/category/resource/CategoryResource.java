package com.pietro.library.category.resource;

import static com.pietro.library.common.model.StandardsOperationResults.*;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pietro.library.category.exception.CategoryExistsException;
import com.pietro.library.category.exception.CategoryNotFoundException;
import com.pietro.library.category.model.Category;
import com.pietro.library.category.service.CategoryService;
import com.pietro.library.common.exception.FieldNotValidException;
import com.pietro.library.common.json.JsonUtils;
import com.pietro.library.common.json.JsonWriter;
import com.pietro.library.common.json.OperationResultJsonWriter;
import com.pietro.library.common.model.HttpCode;
import com.pietro.library.common.model.OperationResult;
import com.pietro.library.common.model.ResourceMessage;

public class CategoryResource {

	private static final Logger LOG = LoggerFactory.getLogger(CategoryResource.class);
	private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("category");

	CategoryService categoryService;

	CategoryJsonConverter categoryJsonConverter;

	/**
	 * Here could be converion from Category object using JaxRS and JAXB, author
	 * of this tutorial prefers conversion "by hand", it is up to the programmer
	 * which solution does he prefer for this.
	 */
	public Response add(final String body) {
		LOG.debug("Adding a new category with body {}", body);
		Category category = categoryJsonConverter.convertFrom(body);

		HttpCode httpCode = HttpCode.CREATED;
		OperationResult result = null;

		try {
			category = categoryService.add(category);
			result = OperationResult.success(JsonUtils.getJsonElementWithId(category.getId()));
		} catch (final FieldNotValidException e) {
			LOG.error("One of the fields of the category is not valid", e);
			httpCode = HttpCode.VALIDATION_ERROR;
			result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
		} catch (final CategoryExistsException e) {
			LOG.error("There is already a category for the given name", e);
			httpCode = HttpCode.VALIDATION_ERROR;
			result = getOperationResultExistent(RESOURCE_MESSAGE, "name");
		}

		LOG.debug("Returning the operation result after adding category: {}", result);
		// JaxRS already has enum with codes, but we need more codes than
		// proposed there
		return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
	}

	public Response update(final Long id, final String body) {
		LOG.debug("Updating the category {} with body {}", id, body);
		final Category category = categoryJsonConverter.convertFrom(body);
		category.setId(id);

		HttpCode httpCode = HttpCode.OK;
		OperationResult result;

		try {
			categoryService.update(category);
			result = OperationResult.success();
		} catch (final FieldNotValidException e) {
			LOG.error("One of the field of the category is not valid", e);
			httpCode = HttpCode.VALIDATION_ERROR;
			result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
		} catch (final CategoryExistsException e) {
			LOG.error("There is already a category for the given name", e);
			httpCode = HttpCode.VALIDATION_ERROR;
			result = getOperationResultExistent(RESOURCE_MESSAGE, "name");
		} catch (final CategoryNotFoundException e) {
			LOG.error("No category found for the given id", e);
			httpCode = HttpCode.NOT_FOUND;
			result = getOperationResultNotFound(RESOURCE_MESSAGE);
		}

		LOG.debug("Returning the operation result after updating category: {}", result);
		return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
	}

	public Response findById(final Long id) {
		LOG.debug("Find category: {}", id);
		ResponseBuilder responseBuilder;
		try {
			final Category category = categoryService.findById(id);
			final OperationResult result = OperationResult
					.success(categoryJsonConverter.convertToJsonElement(category));
			responseBuilder = Response.status(HttpCode.OK.getCode()).entity(OperationResultJsonWriter.toJson(result));
			LOG.debug("Category found {}", category);
		} catch (final CategoryNotFoundException e) {
			LOG.error("No category found for id: {}", id);
			responseBuilder = Response.status(HttpCode.NOT_FOUND.getCode());
		}
		return responseBuilder.build();
	}

	public Response findAll() {
		LOG.debug("Find all categories");
		final List<Category> categories = categoryService.findAll();
		LOG.debug("Found {} categories", categories.size());
		final JsonElement jsonWithPagingAndEntries = getJsonElementWithPagingAndEntries(categories);

		return Response.status(HttpCode.OK.getCode()).entity(JsonWriter.writeToString(jsonWithPagingAndEntries))
				.build();
	}

	private JsonElement getJsonElementWithPagingAndEntries(final List<Category> categories) {
		final JsonObject jsonWithEntriesAndPaging = new JsonObject();

		final JsonObject jsonPaging = new JsonObject();
		jsonPaging.addProperty("totalRecords", categories.size());

		jsonWithEntriesAndPaging.add("paging", jsonPaging);
		jsonWithEntriesAndPaging.add("entries", categoryJsonConverter.convertToJsonElement(categories));

		return jsonWithEntriesAndPaging;
	}

}
