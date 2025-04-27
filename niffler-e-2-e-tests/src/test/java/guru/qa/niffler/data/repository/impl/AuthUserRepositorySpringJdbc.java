package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.UserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityResultSetExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

  private static final Config CFG = Config.getInstance();

  @Override
  public UserEntity create(UserEntity user) {
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

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    return Optional.ofNullable(
        jdbcTemplate.query(
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
        ).getFirst()
    );
  }

  @Override
  public Optional<UserEntity> findById(UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    return Optional.ofNullable(
        jdbcTemplate.query(
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
        ).getFirst()
    );
  }

  @Override
  public List<UserEntity> findAll() {
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
}
