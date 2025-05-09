package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserDataJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.UsersDbClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class UserExtension implements
    BeforeEachCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
  private static final String defaultPassword = "12345";
  private final UsersClient usersClient = new UsersDbClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(anno -> {
          if ("".equals(anno.username())) {
            String username = randomUsername();
            UserDataJson user = usersClient.createUser(username, defaultPassword);
            context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                user.withPassword(defaultPassword)
            );
          }
        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UserDataJson.class);
  }

  @Override
  public UserDataJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return createdUser();
  }

  public static @Nullable UserDataJson createdUser() {
    ExtensionContext context = TestsMethodContextExtension.context();
    return context.getStore(NAMESPACE).get(context.getUniqueId(), UserDataJson.class);
  }
}
