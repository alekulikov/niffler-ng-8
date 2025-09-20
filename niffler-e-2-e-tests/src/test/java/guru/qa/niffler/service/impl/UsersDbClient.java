package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.FriendshipStatus;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.Step;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UsersDbClient implements UsersClient {

  private static final Config CFG = Config.getInstance();
  private static final String defaultPassword = "12345";
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private final AuthUserRepository authUserRepository = AuthUserRepository.getInstance();
  private final UserdataUserRepository userdataUserRepository = UserdataUserRepository.getInstance();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.authJdbcUrl(),
      CFG.userdataJdbcUrl()
  );

  @Override
  @Step("Create user with username '{0}' using SQL INSERT")
  @Nonnull
  public UserJson createUser(String username, String password) {
    return requireNonNull(
        xaTransactionTemplate.execute(
            () -> UserJson.fromEntity(
                createNewUser(username, password),
                null
            ).withPassword(password)
        )
    );
  }

  @Override
  @Step("Add {1} income invitation(s) for user using SQL INSERT")
  public List<UserJson> addIncomeInvitation(UserJson targetUser, int count) {
    final List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      UserEntity targetEntity = userdataUserRepository.findById(
          requireNonNull(targetUser.id())
      ).orElseThrow();

      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
              final String username = randomUsername();
              UserEntity income = createNewUser(username, "12345");
              result.add(UserJson.fromEntity(income, FriendshipStatus.INVITE_RECEIVED));
              userdataUserRepository.addFriendshipRequest(
                  income,
                  targetEntity
              );
              return null;
            }
        );
      }
    }
    return result;
  }

  @Override
  @Step("Add {1} outcome invitation(s) for user using SQL INSERT")
  public List<UserJson> addOutcomeInvitation(UserJson targetUser, int count) {
    final List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      UserEntity targetEntity = userdataUserRepository.findById(
          requireNonNull(targetUser.id())
      ).orElseThrow();

      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
              String username = randomUsername();
              UserEntity outcome = createNewUser(username, "12345");
              result.add(UserJson.fromEntity(outcome, FriendshipStatus.INVITE_SENT));
              userdataUserRepository.addFriendshipRequest(
                  targetEntity,
                  outcome
              );
              return null;
            }
        );
      }
    }
    return result;
  }

  @Override
  @Step("Add {1} friend(s) for user using SQL INSERT")
  public List<UserJson> addFriend(UserJson targetUser, int count) {
    final List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      UserEntity targetEntity = userdataUserRepository.findById(
          requireNonNull(targetUser.id())
      ).orElseThrow();

      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
              String username = randomUsername();
              UserEntity friend = createNewUser(username, "12345");
              result.add(UserJson.fromEntity(friend, FriendshipStatus.FRIEND));
              userdataUserRepository.addFriend(
                  targetEntity,
                  friend
              );
              return null;
            }
        );
      }
    }
    return result;
  }

  @Override
  public List<UserJson> addOtherPeople(int count) {
    final List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
              UserEntity friend = createNewUser(randomUsername(), "12345");
              result.add(UserJson.fromEntity(friend, null));
              return null;
            }
        );
      }
    }
    return result;
  }

  @Nonnull
  private UserEntity createNewUser(String username, String password) {
    AuthUserEntity authUser = authUserEntity(username, password);
    authUserRepository.create(authUser);
    return userdataUserRepository.create(userEntity(username));
  }

  @Nonnull
  private UserEntity userEntity(String username) {
    UserEntity ue = new UserEntity();
    ue.setUsername(username);
    ue.setCurrency(CurrencyValues.RUB);
    return ue;
  }

  @Nonnull
  private AuthUserEntity authUserEntity(String username, String password) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(username);
    authUser.setPassword(pe.encode(password));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);
    authUser.setAuthorities(
        Arrays.stream(Authority.values()).map(
            e -> {
              AuthorityEntity ae = new AuthorityEntity();
              ae.setUser(authUser);
              ae.setAuthority(e);
              return ae;
            }
        ).toList()
    );
    return authUser;
  }
}
