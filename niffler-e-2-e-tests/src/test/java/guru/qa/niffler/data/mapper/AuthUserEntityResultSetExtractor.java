package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthUserEntityResultSetExtractor implements ResultSetExtractor<List<AuthUserEntity>> {

  public static final AuthUserEntityResultSetExtractor instance = new AuthUserEntityResultSetExtractor();

  private AuthUserEntityResultSetExtractor() {
  }

  @Override
  public List<AuthUserEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
    Map<UUID, AuthUserEntity> userMap = new ConcurrentHashMap<>();
    while (rs.next()) {
      AuthUserEntity user = userMap.getOrDefault(rs.getObject("id", UUID.class),
          AuthUserEntityRowMapper.instance.mapRow(rs, 1));
      AuthorityEntity authority = new AuthorityEntity();
      authority.setUser(user);
      authority.setId(rs.getObject("authority_id", UUID.class));
      authority.setAuthority(Authority.valueOf(rs.getString("authority")));
      user.getAuthorities().add(authority);
      userMap.put(user.getId(), user);
    }
    return new ArrayList<>(userMap.values());
  }
}
