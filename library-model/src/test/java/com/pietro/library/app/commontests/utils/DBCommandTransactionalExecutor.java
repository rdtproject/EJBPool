package com.pietro.library.app.commontests.utils;

import javax.persistence.EntityManager;

import org.junit.Ignore;

@Ignore
public class DBCommandTransactionalExecutor {

	private EntityManager em;

	public DBCommandTransactionalExecutor(final EntityManager em) {
		this.em = em;
	}

	public <T> T executeCommand(final DBCommand<T> command) {
		try {
			em.getTransaction().begin();
			final T toReturn = command.execute();
			em.getTransaction().commit();
			em.clear();
			return toReturn;
		} catch (final Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
			throw new IllegalStateException(e);
		}
	}
}
