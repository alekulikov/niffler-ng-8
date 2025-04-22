package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.UserEntity;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserDataJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

public class UserDbClient {

  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
  private static final Config CFG = Config.getInstance();

  private final AuthUserDao authUserDaoSpring = new AuthUserDaoSpringJdbc();
  private final AuthAuthorityDao authAuthorityDaoSpring = new AuthAuthorityDaoSpringJdbc();
  private final UdUserDao udUserDaoSpring = new UdUserDaoSpringJdbc();
  private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
  private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();
  private final UdUserDao udUserDao = new UdUserDaoJdbc();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.authJdbcUrl(),
      CFG.userdataJdbcUrl()
  );

  public UserDataJson createUserSpringJdbc(UserDataJson user) {
    return xaTransactionTemplate.execute(() -> {
          UserEntity authUser = new UserEntity();
          authUser.setUsername(user.username());
          authUser.setPassword(pe.encode("12345"));
          authUser.setEnabled(true);
          authUser.setAccountNonExpired(true);
          authUser.setAccountNonLocked(true);
          authUser.setCredentialsNonExpired(true);

          UserEntity createdAuthUser = authUserDaoSpring.create(authUser);

          AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
              e -> {
                AuthorityEntity ae = new AuthorityEntity();
                ae.setUser(createdAuthUser);
                ae.setAuthority(e);
                return ae;
              }
          ).toArray(AuthorityEntity[]::new);

          authAuthorityDaoSpring.create(authorityEntities);

          return UserDataJson.fromEntity(
              udUserDaoSpring.create(guru.qa.niffler.data.entity.userdata.UserEntity.fromJson(user)));
        }
    );
  }

  public UserDataJson createUser(UserDataJson user) {
    return xaTransactionTemplate.execute(() -> {
          UserEntity authUser = new UserEntity();
          authUser.setUsername(user.username());
          authUser.setPassword(pe.encode("12345"));
          authUser.setEnabled(true);
          authUser.setAccountNonExpired(true);
          authUser.setAccountNonLocked(true);
          authUser.setCredentialsNonExpired(true);

          UserEntity createdAuthUser = authUserDao.create(authUser);

          AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
              e -> {
                AuthorityEntity ae = new AuthorityEntity();
                ae.setUser(createdAuthUser);
                ae.setAuthority(e);
                return ae;
              }
          ).toArray(AuthorityEntity[]::new);

          authAuthorityDao.create(authorityEntities);

          return UserDataJson.fromEntity(
              udUserDao.create(guru.qa.niffler.data.entity.userdata.UserEntity.fromJson(user)));
        }
    );
  }
}
