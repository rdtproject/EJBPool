package com.pietro.library.commontests.utils;

import org.junit.Ignore;

/**
 * This is only a helper class used to match the @Path("/categories") in
 * LibraryResource
 * 
 */

@Ignore
public enum ResourceDefinitions {
	CATEGORY("categories");

	private String resourceName;

	private ResourceDefinitions(final String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceName() {
		return resourceName;
	}
}
