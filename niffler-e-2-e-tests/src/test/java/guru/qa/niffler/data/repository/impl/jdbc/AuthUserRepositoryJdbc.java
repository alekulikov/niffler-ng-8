package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityResultSetExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@SuppressWarnings("resource")
@ParametersAreNonnullByDefault
public class AuthUserRepositoryJdbc implements AuthUserRepository {

  private static final Config CFG = Config.getInstance();

  @Nonnull
  @Override
  public AuthUserEntity create(AuthUserEntity user) {
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

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
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

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
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

  @Nonnull
  public List<AuthUserEntity> findAll() {
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

  @Nonnull
  @Override
  public AuthUserEntity update(AuthUserEntity user) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        """
            UPDATE "user"  SET username = ?,
                            password = ?,
                            enabled = ?,
                            account_non_expired = ?,
                            account_non_locked = ?,
                            credentials_non_expired = ?
            WHERE id = ?
            """
    )) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getPassword());
      ps.setBoolean(3, user.getEnabled());
      ps.setBoolean(4, user.getAccountNonExpired());
      ps.setBoolean(5, user.getAccountNonLocked());
      ps.setBoolean(6, user.getCredentialsNonExpired());
      ps.setObject(7, user.getId());
      ps.executeUpdate();
      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void remove(AuthUserEntity user) {
    try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "DELETE FROM \"user\" WHERE id = ?");
         PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
             "DELETE FROM authority WHERE user_id = ?")) {
      userPs.setObject(1, user.getId());
      userPs.executeUpdate();
      authorityPs.setObject(1, user.getId());
      authorityPs.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
