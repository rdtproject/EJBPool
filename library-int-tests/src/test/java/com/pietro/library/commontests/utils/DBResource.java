package com.pietro.library.commontests.utils;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;

@Path("/DB")
public class DBResource {

	@Inject
	private TestRepositoryEJB testRepositoryEJB;

	@DELETE
	public void deleteAll() {
		testRepositoryEJB.deleteAll();
	}

}
