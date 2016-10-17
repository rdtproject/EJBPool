package com.pietro.library.category.resource;

import static com.pietro.library.common.model.StandardsOperationResults.*;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pietro.library.category.exception.CategoryExistsException;
import com.pietro.library.category.model.Category;
import com.pietro.library.category.service.CategoryService;
import com.pietro.library.common.exception.FieldNotValidException;
import com.pietro.library.common.json.JsonUtils;
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

}
