package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

  private static final Config CFG = Config.getInstance();

  @Override
  public void create(AuthorityEntity... authority) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
        Statement.RETURN_GENERATED_KEYS
    )) {
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
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM authority"
    )) {
      ps.execute();
      List<AuthorityEntity> authorities = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          AuthUserDao authUserDao = new AuthUserDaoJdbc();
          AuthorityEntity ae = new AuthorityEntity();
          AuthUserEntity ue = authUserDao.findById(rs.getObject("user_id", UUID.class))
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
