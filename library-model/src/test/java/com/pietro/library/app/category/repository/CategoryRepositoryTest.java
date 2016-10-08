package com.pietro.library.app.category.repository;

import static com.pietro.library.app.commontests.category.CategoryForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pietro.library.app.category.model.Category;
import com.pietro.library.app.commontests.utils.DBCommand;
import com.pietro.library.app.commontests.utils.DBCommandTransactionalExecutor;

public class CategoryRepositoryTest {

	private static final Logger LOG = LoggerFactory.getLogger(CategoryRepositoryTest.class);
	private EntityManagerFactory emf;
	private EntityManager em;
	private CategoryRepository categoryRepository;
	private DBCommandTransactionalExecutor dBCommandTransactionalExecutor;

	@Before
	public void initTestCase() {
		// will be executed before each test case
		// persistence unit that will be used, it is defined in persistence.xml
		emf = Persistence.createEntityManagerFactory("libraryPU");
		em = emf.createEntityManager();

		categoryRepository = new CategoryRepository();
		categoryRepository.em = em;

		dBCommandTransactionalExecutor = new DBCommandTransactionalExecutor(em);
	}

	@After
	public void closeEntityManager() {
		em.close();
		emf.close();
	}

	@Test
	public void addCategoryAndFindIt() {
		final Long categoryAddedId = dBCommandTransactionalExecutor.executeCommand(new DBCommand<Long>() {
			@Override
			public Long execute() {
				return categoryRepository.add(java()).getId();
			}
		});

		final Category category = categoryRepository.findById(categoryAddedId);
		assertThat(category, is(notNullValue()));
		assertThat(category.getName(), is(equalTo(java().getName())));
		LOG.info("Succesfully saved and got Category: {}", category);
	}

	@Test
	public void addCategoryAndFindItJava8() {
		final Long categoryAddedId = dBCommandTransactionalExecutor.executeCommand(() -> {
			return categoryRepository.add(java()).getId();
		});

		final Category category = categoryRepository.findById(categoryAddedId);
		assertThat(category, is(notNullValue()));
		assertThat(category.getName(), is(equalTo(java().getName())));
		LOG.info("Succesfully saved and got Category: {}", category);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findCategoryByNullId() {
		final Category categoryAddedId = categoryRepository.findById(null);
		assertThat(categoryAddedId, is(nullValue()));
	}

	@Test
	public void updateCategory() {
		final Long categoryAddedId = dBCommandTransactionalExecutor.executeCommand(() -> {
			return categoryRepository.add(java()).getId();
		});

		final Category categoryAfterAdd = categoryRepository.findById(categoryAddedId);
		assertThat(categoryAfterAdd.getName(), is(equalTo(java().getName())));

		categoryAfterAdd.setName(cleanCode().getName());
		dBCommandTransactionalExecutor.executeCommand(() -> {
			categoryRepository.update(categoryAfterAdd).getId();
			return null;
		});

		final Category categoryAfterUpdate = categoryRepository.findById(categoryAddedId);
		assertThat(categoryAfterUpdate.getName(), is(equalTo(cleanCode().getName())));
	}

	@Test
	public void findAllCategories() {
		dBCommandTransactionalExecutor.executeCommand(() -> {
			for (final Category category : allCategories()) {
				categoryRepository.add(category);
			}
			return null;
		});

		final List<Category> categories = categoryRepository.findAll("name");
		assertThat(categories.size(), is(equalTo(4)));
		assertThat(categories.get(0).getName(), is(equalTo(architecture().getName())));
		assertThat(categories.get(1).getName(), is(equalTo(cleanCode().getName())));
		assertThat(categories.get(2).getName(), is(equalTo(java().getName())));
		assertThat(categories.get(3).getName(), is(equalTo(networks().getName())));
	}

	@Test
	public void findAllCategoriesJava8() {
		dBCommandTransactionalExecutor.executeCommand(() -> {
			allCategories().forEach(categoryRepository::add);
			return null;
		});

		final List<Category> categories = categoryRepository.findAll("name");
		assertThat(categories.size(), is(equalTo(4)));
		assertThat(categories.get(0).getName(), is(equalTo(architecture().getName())));
		assertThat(categories.get(1).getName(), is(equalTo(cleanCode().getName())));
		assertThat(categories.get(2).getName(), is(equalTo(java().getName())));
		assertThat(categories.get(3).getName(), is(equalTo(networks().getName())));
	}

	@Test
	public void alreadyExistsCategoryWithId() {
		dBCommandTransactionalExecutor.executeCommand(() -> {
			categoryRepository.add(java());
			return null;
		});
		assertThat(categoryRepository.alreadyExists(java()), is(equalTo(true)));
		assertThat(categoryRepository.alreadyExists(cleanCode()), is(equalTo(false)));
	}

	@Test
	public void alreadyExistsCategoryWithIdMoreComplex() {
		final Category javaCategory = dBCommandTransactionalExecutor.executeCommand(() -> {
			categoryRepository.add(cleanCode());
			return categoryRepository.add(java());
		});

		assertThat(categoryRepository.alreadyExists(javaCategory), is(equalTo(false)));

		javaCategory.setName(cleanCode().getName());
		assertThat(categoryRepository.alreadyExists(javaCategory), is(equalTo(true)));

		javaCategory.setName(networks().getName());
		assertThat(categoryRepository.alreadyExists(javaCategory), is(equalTo(false)));
	}

	@Test
	public void existsById() {
		final Long javaCategoryId = dBCommandTransactionalExecutor.executeCommand(() -> {
			return categoryRepository.add(java()).getId();
		});

		assertThat(categoryRepository.exitstsById(javaCategoryId), is(equalTo(true)));
		assertThat(categoryRepository.exitstsById(999L), is(equalTo(false)));
	}

}
