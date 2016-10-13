package com.pietro.library.category.resource;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pietro.library.category.model.Category;
import com.pietro.library.category.service.CategoryService;
import com.pietro.library.common.json.JsonUtils;
import com.pietro.library.common.json.OperationResultJsonWriter;
import com.pietro.library.common.model.HttpCode;
import com.pietro.library.common.model.OperationResult;

public class CategoryResource {

	private static final Logger LOG = LoggerFactory.getLogger(CategoryResource.class);

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
		category = categoryService.add(category);
		final OperationResult result = OperationResult.success(JsonUtils.getJsonElementWithId(category.getId()));

		LOG.debug("Returning the operation result after adding category: {}", result);
		// JaxRS already has enum with codes, but we need more codes than
		// proposed there
		return Response.status(HttpCode.CREATED.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
	}

}
