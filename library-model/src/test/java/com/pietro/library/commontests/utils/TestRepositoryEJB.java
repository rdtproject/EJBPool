package com.pietro.library.commontests.utils;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Ignore;

import com.pietro.library.category.model.Category;

@Ignore
@Stateless
public class TestRepositoryEJB {

	@PersistenceContext
	private EntityManager em;

	private static final List<Class<?>> ENTITIES_TO_REMOVE = Arrays.asList(Category.class);

	public void deleteAll() {
		for (final Class<?> entityClass : ENTITIES_TO_REMOVE) {
			deleteAllForEntity(entityClass);
		}
	}

	@SuppressWarnings("unchecked")
	private void deleteAllForEntity(final Class<?> entityClass) {
		final List<Object> rows = em.createQuery("SELECT o FROM " + entityClass.getSimpleName() + " o").getResultList();
		for (final Object row : rows) {
			em.remove(row);
		}
	}
}
