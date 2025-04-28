package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UdUserEntityResultSetExtractor implements ResultSetExtractor<List<UserEntity>> {

  public static final UdUserEntityResultSetExtractor instance = new UdUserEntityResultSetExtractor();

  private UdUserEntityResultSetExtractor() {
  }

  @Override
  public List<UserEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
    Map<UUID, UserEntity> usersMap = new ConcurrentHashMap<>();
    while (rs.next()) {
      UserEntity user = usersMap.getOrDefault(rs.getObject("id", UUID.class),
          UdUserEntityRowMapper.instance.mapRow(rs, 1));
      FriendshipEntity friendship = new FriendshipEntity();
      friendship.setRequester(new UserEntity(rs.getObject("requester_id", UUID.class)));
      friendship.setAddressee(new UserEntity(rs.getObject("addressee_id", UUID.class)));
      friendship.setStatus(Optional.ofNullable(rs.getString("status"))
          .map(FriendshipStatus::valueOf)
          .orElse(null));
      friendship.setCreatedDate(rs.getDate("created_date"));

      if (Objects.equals(friendship.getRequester().getId(), user.getId())) {
        user.getFriendshipRequests().add(friendship);
      } else {
        user.getFriendshipAddressees().add(friendship);
      }
      usersMap.put(user.getId(), user);
    }
    return new ArrayList<>(usersMap.values());
  }
}
