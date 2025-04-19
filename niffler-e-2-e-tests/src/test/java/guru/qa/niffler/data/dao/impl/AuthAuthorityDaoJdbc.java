package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.UserEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

  private final Connection connection;

  public AuthAuthorityDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void create(AuthorityEntity... authority) {
    try (PreparedStatement ps = connection.prepareStatement(
        "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
        Statement.RETURN_GENERATED_KEYS)
    ) {
      for (AuthorityEntity authorityEntity : authority) {
        ps.setObject(1, authorityEntity.getUser().getId());
        ps.setString(2, authorityEntity.getAuthority().name());
        ps.addBatch();
        ps.clearParameters();
      }
      ps.executeBatch();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<AuthorityEntity> findAll() {
    try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM authority")) {
      ps.execute();
      List<AuthorityEntity> authorities = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          AuthUserDao authUserDao = new AuthUserDaoJdbc(connection);
          AuthorityEntity ae = new AuthorityEntity();
          UserEntity ue = authUserDao.findById(rs.getObject("user_id", UUID.class))
              .orElseThrow(() -> new SQLException("Can`t find user in Authority"));
          ae.setId(rs.getObject("id", UUID.class));
          ae.setUser(ue);
          ae.setAuthority(Authority.valueOf(rs.getString("authority")));
          authorities.add(ae);
        }
      }
      return authorities;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
