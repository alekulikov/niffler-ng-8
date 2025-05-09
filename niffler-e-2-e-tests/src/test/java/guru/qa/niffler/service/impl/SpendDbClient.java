package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.hibernate.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;

public class SpendDbClient implements SpendClient {

  private static final Config CFG = Config.getInstance();

  private final SpendRepository spendRepository = new SpendRepositoryHibernate();
  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.spendJdbcUrl());

  public SpendJson createSpend(SpendJson spend) {
    return xaTransactionTemplate.execute(() ->
        SpendJson.fromEntity(spendRepository.create(SpendEntity.fromJson(spend)))
    );
  }

  public CategoryJson createCategory(CategoryJson category) {
    return xaTransactionTemplate.execute(() ->
        CategoryJson.fromEntity(spendRepository.createCategory(CategoryEntity.fromJson(category)))
    );
  }

  public CategoryJson updateCategory(CategoryJson category) {
    return xaTransactionTemplate.execute(() ->
        CategoryJson.fromEntity(spendRepository.updateCategory(CategoryEntity.fromJson(category)))
    );
  }
}
