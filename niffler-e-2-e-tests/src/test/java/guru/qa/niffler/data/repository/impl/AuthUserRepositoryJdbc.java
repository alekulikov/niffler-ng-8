package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.UserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityResultSetExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

  private static final Config CFG = Config.getInstance();

  @Override
  public UserEntity create(UserEntity user) {
    try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        """
            INSERT INTO "user" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)
            VALUES (?, ?, ?, ?, ?, ?)
            """, PreparedStatement.RETURN_GENERATED_KEYS);
         PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
             "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)"
         )) {
      userPs.setString(1, user.getUsername());
      userPs.setString(2, user.getPassword());
      userPs.setBoolean(3, user.getEnabled());
      userPs.setBoolean(4, user.getAccountNonExpired());
      userPs.setBoolean(5, user.getAccountNonLocked());
      userPs.setBoolean(6, user.getCredentialsNonExpired());
      userPs.executeUpdate();

      try (ResultSet rs = userPs.getGeneratedKeys()) {
        if (rs.next()) {
          user.setId(rs.getObject("id", UUID.class));
        } else {
          throw new SQLException("Can`t find id in ResultSet");
        }
      }

      for (AuthorityEntity a : user.getAuthorities()) {
        authorityPs.setObject(1, user.getId());
        authorityPs.setString(2, a.getAuthority().name());
        authorityPs.addBatch();
        authorityPs.clearParameters();
      }
      authorityPs.executeBatch();
      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<UserEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        """
             SELECT a.id as authority_id,
                    authority,
                    user_id as id,
                    username,
                    u.password,
                    u.enabled,
                    u.account_non_expired,
                    u.account_non_locked,
                    u.credentials_non_expired
            FROM "user" u join public.authority a on u.id = a.user_id
            WHERE u.id = ?
            """
    )) {
      ps.setObject(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        return Optional.ofNullable(AuthUserEntityResultSetExtractor.instance
            .extractData(rs).getFirst());
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        """
             SELECT a.id as authority_id,
                    authority,
                    user_id as id,
                    username,
                    u.password,
                    u.enabled,
                    u.account_non_expired,
                    u.account_non_locked,
                    u.credentials_non_expired
            FROM "user" u join public.authority a on u.id = a.user_id
            WHERE u.username = ?
            """
    )) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        return Optional.ofNullable(AuthUserEntityResultSetExtractor.instance
            .extractData(rs).getFirst());
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<UserEntity> findAll() {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        """
             SELECT a.id as authority_id,
                    authority,
                    user_id as id,
                    username,
                    u.password,
                    u.enabled,
                    u.account_non_expired,
                    u.account_non_locked,
                    u.credentials_non_expired
            FROM "user" u join public.authority a on u.id = a.user_id
            """
    )) {
      try (ResultSet rs = ps.executeQuery()) {
        return AuthUserEntityResultSetExtractor.instance.extractData(rs);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
