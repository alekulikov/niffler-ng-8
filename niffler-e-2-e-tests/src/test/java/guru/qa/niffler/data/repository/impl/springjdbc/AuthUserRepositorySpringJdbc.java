package guru.qa.niffler.data.repository.impl.springjdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityResultSetExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.jdbc.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

  private static final Config CFG = Config.getInstance();

  @Nonnull
  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
          "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
              "VALUES (?,?,?,?,?,?)",
          Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getPassword());
      ps.setBoolean(3, user.getEnabled());
      ps.setBoolean(4, user.getAccountNonExpired());
      ps.setBoolean(5, user.getAccountNonLocked());
      ps.setBoolean(6, user.getCredentialsNonExpired());
      return ps;
    }, kh);

    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    user.setId(generatedKey);

    jdbcTemplate.batchUpdate(
        "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)",
        new BatchPreparedStatementSetter() {

          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setObject(1, user.getId());
            ps.setString(2, user.getAuthorities().get(i).getAuthority().name());
          }

          @Override
          public int getBatchSize() {
            return user.getAuthorities().size();
          }
        }
    );

    return user;
  }

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    return jdbcTemplate.query(
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
            """,
        AuthUserEntityResultSetExtractor.instance,
        username
    ).stream().findFirst();
  }

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    return jdbcTemplate.query(
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
            """,
        AuthUserEntityResultSetExtractor.instance,
        id
    ).stream().findFirst();
  }

  @Nonnull
  public List<AuthUserEntity> findAll() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    return jdbcTemplate.query(
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
            """,
        AuthUserEntityResultSetExtractor.instance
    );
  }

  @Nonnull
  @Override
  public AuthUserEntity update(AuthUserEntity user) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    jdbcTemplate.update(
        """
            UPDATE "user"  SET username = ?,
                            password = ?,
                            enabled = ?,
                            account_non_expired = ?,
                            account_non_locked = ?,
                            credentials_non_expired = ?
            WHERE id = ?
            """, user.getUsername(), user.getPassword(), user.getEnabled(),
        user.getAccountNonExpired(), user.getAccountNonLocked(), user.getCredentialsNonExpired(), user.getId()
    );
    return user;
  }

  @Override
  public void remove(AuthUserEntity user) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    jdbcTemplate.update("DELETE FROM \"user\" WHERE id = ?", user.getId());
    jdbcTemplate.update("DELETE FROM authority WHERE user_id = ?", user.getId());
  }
}
