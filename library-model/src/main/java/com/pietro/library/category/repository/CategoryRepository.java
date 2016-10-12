package com.pietro.library.category.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.pietro.library.category.model.Category;

public class CategoryRepository {

	// package for the need of the unit tests
	EntityManager em;

	public Category add(final Category category) {
		em.persist(category);
		return category;
	}

	public Category findById(final Long id) {
		return em.find(Category.class, id);
	}

	public Category update(final Category categoryAfterAdd) {
		return em.merge(categoryAfterAdd);
	}

	@SuppressWarnings("unchecked")
	public List<Category> findAll(final String orderField) {
		return em.createQuery("SELECT c FROM Category c ORDER BY c." + orderField).getResultList();
	}

	public boolean alreadyExists(final Category category) {
		final StringBuilder jpql = new StringBuilder();
		jpql.append("SELECT 1 FROM Category c where c.name = :name");
		if (category.getId() != null) {
			jpql.append(" AND c.id != :id");
		}

		final Query query = em.createQuery(jpql.toString());
		query.setParameter("name", category.getName());
		if (category.getId() != null) {
			query.setParameter("id", category.getId());
		}

		return query.setMaxResults(1).getResultList().size() > 0;
	}

	public boolean exitstsById(final Long javaCategoryId) {
		return em.createQuery("SELECT 1 FROM Category c WHERE c.id = :id")
				.setParameter("id", javaCategoryId)
				.setMaxResults(1)
				.getResultList().size() > 0;
	}

}
