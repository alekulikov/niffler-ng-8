package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases.XaFunction;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.UserEntity;
import guru.qa.niffler.model.UserAuthJson;
import guru.qa.niffler.model.UserDataJson;

import java.util.Arrays;

import static guru.qa.niffler.data.Databases.dataSource;
import static guru.qa.niffler.data.Databases.xaTransaction;
import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

public class UserDbClient {

  private static final Config CFG = Config.getInstance();

  public UserDataJson createUserSpringJdbc(UserDataJson user) {
    UserEntity authUser = new UserEntity();
    authUser.setUsername(user.username());
    authUser.setPassword("12345");
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);

    UserEntity createdAuthUser = new AuthUserDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
        .create(authUser);

    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
        e -> {
          AuthorityEntity ae = new AuthorityEntity();
          ae.setUser(createdAuthUser);
          ae.setAuthority(e);
          return ae;
        }
    ).toArray(AuthorityEntity[]::new);

    new AuthAuthorityDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
        .create(authorityEntities);

    return UserDataJson.fromEntity(
        new UdUserDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl()))
            .create(
                guru.qa.niffler.data.entity.userdata.UserEntity.fromJson(user)
            )
    );
  }

  public UserDataJson createUser(UserAuthJson userAuth, UserDataJson userData) {
    return xaTransaction(
        TRANSACTION_READ_COMMITTED,
        new XaFunction<>(connection -> {
          UserEntity userEntity = UserEntity.fromJson(userAuth);

          AuthUserDao authUserDao = new AuthUserDaoJdbc(connection);
          authUserDao.create(userEntity);

          AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
              e -> {
                AuthorityEntity ae = new AuthorityEntity();
                ae.setUser(userEntity);
                ae.setAuthority(e);
                return ae;
              }
          ).toArray(AuthorityEntity[]::new);

          AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc(connection);
          authAuthorityDao.create(authorityEntities);

          return null;
        },
            CFG.authJdbcUrl()),
        new XaFunction<>(connection -> {
          UserDao userDao = new UserDaoJdbc(connection);
          return UserDataJson.fromEntity(
              userDao.createUser(guru.qa.niffler.data.entity.userdata.UserEntity.fromJson(userData))
          );
        },
            CFG.userdataJdbcUrl())
    );
  }

}
