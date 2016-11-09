package com.pietro.library.commontests.category;

import static com.pietro.library.commontests.category.CategoryForTestsRepository.*;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.pietro.library.category.service.CategoryService;

@Path("/DB/categories")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResourceDB {

	@Inject
	private CategoryService categoryService;

	@POST
	public void addAll() {
		allCategories().forEach(categoryService::add);
	}

}
