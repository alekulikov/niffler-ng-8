package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserDataJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

public class CategoryExtension implements
    BeforeEachCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
  private final SpendClient spendClient = new SpendDbClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(anno -> {
          if (anno.categories().length > 0) {
            UserDataJson createdUser = UserExtension.createdUser();
            String username = createdUser != null
                ? createdUser.username()
                : anno.username();
            List<CategoryJson> createdCategories = new ArrayList<>();

            for (Category category : anno.categories()) {
              String categoryName = "".equals(category.name())
                  ? randomCategoryName()
                  : category.name();
              CategoryJson categoryJson = new CategoryJson(
                  null,
                  categoryName,
                  username,
                  false
              );
              CategoryJson created = spendClient.createCategory(categoryJson);
              if (category.archived()) {
                CategoryJson archivedCategoryJson = new CategoryJson(
                    created.id(),
                    created.name(),
                    created.username(),
                    true
                );
                created = spendClient.updateCategory(archivedCategoryJson);
              }
              createdCategories.add(created);
            }

            if (createdUser != null) {
              createdUser.testData().categories().addAll(createdCategories);
            } else {
              context.getStore(NAMESPACE).put(context.getUniqueId(), createdCategories);
            }
          }
        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson[].class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public CategoryJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return (CategoryJson[]) extensionContext.getStore(NAMESPACE)
        .get(extensionContext.getUniqueId(), List.class)
        .toArray(CategoryJson[]::new);
  }
}
