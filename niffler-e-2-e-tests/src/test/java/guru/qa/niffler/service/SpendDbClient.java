package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class SpendDbClient {

  private static final Config CFG = Config.getInstance();

  private final SpendDao spendDao = new SpendDaoJdbc();
  private final CategoryDao categoryDao = new CategoryDaoJdbc();
  private final SpendDao spendDaoSpring = new SpendDaoJdbc();
  private final CategoryDao categoryDaoSpring = new CategoryDaoJdbc();

  private final JdbcTransactionTemplate txTemplate = new JdbcTransactionTemplate(CFG.spendJdbcUrl());
  private final TransactionTemplate txTemplateSpring = new TransactionTemplate(
      new JdbcTransactionManager(DataSources.dataSource(CFG.spendJdbcUrl()))
  );

  public SpendJson createSpendSpringJdbc(SpendJson spend) {
    return txTemplateSpring.execute(status -> {
      SpendEntity spendEntity = SpendEntity.fromJson(spend);
      CategoryEntity categoryEntity = spendEntity.getCategory();
      if (categoryEntity.getId() == null) {
        categoryDaoSpring.findCategoryByUsernameAndCategoryName(
                categoryEntity.getUsername(), categoryEntity.getName())
            .ifPresentOrElse(
                ce -> spendEntity.setCategory(ce),
                () -> spendEntity.setCategory(categoryDaoSpring.create(categoryEntity))
            );
      }
      return SpendJson.fromEntity(spendDaoSpring.create(spendEntity));
    });
  }

  public CategoryJson createCategorySpringJdbc(CategoryJson category) {
    CategoryEntity categoryEntity = categoryDaoSpring.create(CategoryEntity.fromJson(category));
    return CategoryJson.fromEntity(categoryEntity);
  }

  public SpendJson createSpend(SpendJson spend) {
    return txTemplate.execute(() -> {
      SpendEntity spendEntity = SpendEntity.fromJson(spend);
      CategoryEntity categoryEntity = spendEntity.getCategory();
      if (spendEntity.getCategory().getId() == null) {
        categoryDao.findCategoryByUsernameAndCategoryName(
                categoryEntity.getUsername(), categoryEntity.getName())
            .ifPresentOrElse(
                ce -> spendEntity.setCategory(ce),
                () -> spendEntity.setCategory(categoryDao.create(categoryEntity))
            );
      }
      return SpendJson.fromEntity(spendDao.create(spendEntity));
    });
  }

  public CategoryJson createCategory(CategoryJson category) {
    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
    return CategoryJson.fromEntity(categoryDao.create(categoryEntity));
  }

  public CategoryJson updateCategory(CategoryJson category) {
    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
    return CategoryJson.fromEntity(categoryDao.updateCategory(categoryEntity));
  }
}
