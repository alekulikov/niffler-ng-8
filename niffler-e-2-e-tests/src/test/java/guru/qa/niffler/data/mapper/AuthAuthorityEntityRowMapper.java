package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthAuthorityEntityRowMapper implements RowMapper<AuthorityEntity> {

  public static final AuthAuthorityEntityRowMapper instance = new AuthAuthorityEntityRowMapper();

  private AuthAuthorityEntityRowMapper() {
  }

  @Override
  public AuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    AuthorityEntity result = new AuthorityEntity();
    UserEntity userEntity = new UserEntity();
    userEntity.setId(rs.getObject("user_id", UUID.class));
    result.setId(rs.getObject("id", UUID.class));
    result.setUser(userEntity);
    result.setAuthority(Authority.valueOf(rs.getString("authority")));
    return result;
  }
}
