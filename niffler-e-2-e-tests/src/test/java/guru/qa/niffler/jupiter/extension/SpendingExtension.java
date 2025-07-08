package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserDataJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);
  private final SpendClient spendClient = new SpendDbClient();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(anno -> {
          if (anno.spendings().length > 0) {
            UserDataJson createdUser = UserExtension.createdUser();
            String username = createdUser != null
                ? createdUser.username()
                : anno.username();
            List<SpendJson> createdSpends = new ArrayList<>();

            for (Spend spend : anno.spendings()) {
              SpendJson spendJson = new SpendJson(
                  null,
                  new Date(),
                  new CategoryJson(
                      null,
                      spend.category(),
                      username,
                      false
                  ),
                  spend.currency(),
                  spend.amount(),
                  spend.description(),
                  username
              );
              SpendJson created = spendClient.createSpend(spendJson);
              createdSpends.add(created);
            }

            if (createdUser != null) {
              createdUser.testData().spends().addAll(createdSpends);
            } else {
              context.getStore(NAMESPACE).put(context.getUniqueId(), createdSpends);
            }
          }
        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return createdSpends().toArray(SpendJson[]::new);
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public static List<SpendJson> createdSpends() {
    final ExtensionContext context = TestsMethodContextExtension.context();
    return Optional.ofNullable(context.getStore(NAMESPACE).get(context.getUniqueId(), List.class))
        .orElse(Collections.emptyList());
  }
}
