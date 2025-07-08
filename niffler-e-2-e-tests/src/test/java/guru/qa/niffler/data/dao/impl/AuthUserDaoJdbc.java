package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@SuppressWarnings("resource")
@ParametersAreNonnullByDefault
public class AuthUserDaoJdbc implements AuthUserDao {

  private static final Config CFG = Config.getInstance();

  @NotNull
  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        """
            INSERT INTO "user" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)
            VALUES (?, ?, ?, ?, ?, ?)
            """,
        Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getPassword());
      ps.setBoolean(3, user.getEnabled());
      ps.setBoolean(4, user.getAccountNonExpired());
      ps.setBoolean(5, user.getAccountNonLocked());
      ps.setBoolean(6, user.getCredentialsNonExpired());
      ps.executeUpdate();

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          user.setId(rs.getObject("id", UUID.class));
        } else {
          throw new SQLException("Can`t find id in ResultSet");
        }
      }
      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM \"user\" WHERE username = ?"
    )) {
      ps.setString(1, username);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          AuthUserEntity ue = new AuthUserEntity();
          ue.setId(rs.getObject("id", UUID.class));
          ue.setUsername(rs.getString("username"));
          ue.setPassword(rs.getString("password"));
          ue.setEnabled(rs.getBoolean("enabled"));
          ue.setAccountNonExpired(rs.getBoolean("account_non_expired"));
          ue.setAccountNonLocked(rs.getBoolean("account_non_locked"));
          ue.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
          return Optional.of(ue);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM \"user\" WHERE id = ?"
    )) {
      ps.setObject(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          AuthUserEntity ue = new AuthUserEntity();
          ue.setId(rs.getObject("id", UUID.class));
          ue.setUsername(rs.getString("username"));
          ue.setPassword(rs.getString("password"));
          ue.setEnabled(rs.getBoolean("enabled"));
          ue.setAccountNonExpired(rs.getBoolean("account_non_expired"));
          ue.setAccountNonLocked(rs.getBoolean("account_non_locked"));
          ue.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
          return Optional.of(ue);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  @Override
  public List<AuthUserEntity> findAll() {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM \"user\""
    )) {
      ps.execute();
      List<AuthUserEntity> users = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          AuthUserEntity ue = new AuthUserEntity();
          ue.setId(rs.getObject("id", UUID.class));
          ue.setUsername(rs.getString("username"));
          ue.setPassword(rs.getString("password"));
          ue.setEnabled(rs.getBoolean("enabled"));
          ue.setAccountNonExpired(rs.getBoolean("account_non_expired"));
          ue.setAccountNonLocked(rs.getBoolean("account_non_locked"));
          ue.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
          users.add(ue);
        }
      }
      return users;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
