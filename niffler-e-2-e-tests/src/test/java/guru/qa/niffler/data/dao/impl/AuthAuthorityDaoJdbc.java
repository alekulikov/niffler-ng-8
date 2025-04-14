package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.*;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

  private final Connection connection;

  public AuthAuthorityDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @Override
  public AuthorityEntity create(AuthorityEntity authority) {
    try (PreparedStatement ps = connection.prepareStatement(
        "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
        Statement.RETURN_GENERATED_KEYS)
    ) {
      ps.setObject(1, authority.getUser().getId());
      ps.setString(2, authority.getAuthority().name());
      ps.executeUpdate();

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          authority.setId(rs.getObject("id", UUID.class));
        } else {
          throw new SQLException("Can`t find id in ResultSet");
        }
      }
      return authority;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
