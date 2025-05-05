package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.impl.SpendDbClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

public class CategoryExtension implements
    BeforeEachCallback,
    ParameterResolver,
    AfterTestExecutionCallback {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
  private final SpendDbClient spendDbClient = new SpendDbClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(anno -> {
          if (anno.categories().length > 0) {
            CategoryJson categoryJson = new CategoryJson(
                null,
                randomCategoryName(),
                anno.username(),
                false
            );
            CategoryJson created = spendDbClient.createCategory(categoryJson);
            if (anno.categories()[0].archived()) {
              CategoryJson archivedCategoryJson = new CategoryJson(
                  created.id(),
                  created.name(),
                  created.username(),
                  true
              );
              created = spendDbClient.updateCategory(archivedCategoryJson);
            }
            context.getStore(NAMESPACE).put(context.getUniqueId(), created);
          }
        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
  }

  @Override
  public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    Optional.ofNullable(context.getStore(NAMESPACE)
        .get(context.getUniqueId(), CategoryJson.class)
    ).ifPresent(category -> {
      if (!category.archived()) {
        CategoryJson archivedCategoryJson = new CategoryJson(
            category.id(),
            category.name(),
            category.username(),
            true
        );
        spendDbClient.updateCategory(archivedCategoryJson);
      }
    });
  }
}
