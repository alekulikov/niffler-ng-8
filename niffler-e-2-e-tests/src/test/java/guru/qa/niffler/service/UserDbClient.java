package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases.XaFunction;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.UserEntity;
import guru.qa.niffler.model.UserAuthJson;
import guru.qa.niffler.model.UserDataJson;

import static guru.qa.niffler.data.Databases.xaTransaction;
import static guru.qa.niffler.data.entity.auth.Authority.read;
import static guru.qa.niffler.data.entity.auth.Authority.write;
import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

public class UserDbClient {

  private static final Config CFG = Config.getInstance();

  public UserDataJson createUser(UserAuthJson userAuth, UserDataJson userData) {
    return xaTransaction(
        TRANSACTION_READ_COMMITTED,
        new XaFunction<>(connection -> {
          UserEntity userEntity = UserEntity.fromJson(userAuth);
          AuthorityEntity authorityEntity = new AuthorityEntity();
          authorityEntity.setUser(userEntity);

          AuthUserDao authUserDao = new AuthUserDaoJdbc(connection);
          authUserDao.create(userEntity);

          AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc(connection);
          authorityEntity.setAuthority(read);
          authAuthorityDao.create(authorityEntity);
          authorityEntity.setAuthority(write);
          authAuthorityDao.create(authorityEntity);

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
