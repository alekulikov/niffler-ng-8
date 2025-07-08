package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.data.mapper.UdUserEntityResultSetExtractor;
import guru.qa.niffler.data.repository.UdUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.*;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@SuppressWarnings("resource")
@ParametersAreNonnullByDefault
public class UdUserRepositoryJdbc implements UdUserRepository {

  private static final Config CFG = Config.getInstance();

  @Nonnull
  @Override
  public UdUserEntity create(UdUserEntity user) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        """
            INSERT INTO "user" (username, currency, firstname, surname, photo, photo_small, full_name)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """,
        Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getCurrency().name());
      ps.setString(3, user.getFirstname());
      ps.setString(4, user.getSurname());
      ps.setBytes(5, user.getPhoto());
      ps.setBytes(6, user.getPhotoSmall());
      ps.setString(7, user.getFullname());
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

  @Nonnull
  @Override
  public Optional<UdUserEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        """
            SELECT * FROM "user" u left join friendship f
                ON u.id = f.requester_id or (u.id = f.addressee_id and status = 'PENDING')
            WHERE u.id = ?
            """
    )) {
      ps.setObject(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        return Optional.ofNullable(UdUserEntityResultSetExtractor
            .instance.extractData(rs).getFirst());
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  public Optional<UdUserEntity> findByUsername(String username) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        """
            SELECT * FROM "user" u left join friendship f
                ON u.id = f.requester_id or (u.id = f.addressee_id and status = 'PENDING')
            WHERE u.username = ?
            """
    )) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        return Optional.ofNullable(UdUserEntityResultSetExtractor
            .instance.extractData(rs).getFirst());
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void remove(UdUserEntity user) {
    try (PreparedStatement userPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        "DELETE FROM \"user\" WHERE id = ?");
         PreparedStatement friendshipPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
             "DELETE FROM friendship WHERE requester_id = ? OR addressee_id = ?"
         )) {
      userPs.setObject(1, user.getId());
      userPs.executeUpdate();
      friendshipPs.setObject(1, user.getId());
      friendshipPs.setObject(2, user.getId());
      friendshipPs.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void sendInvitation(UdUserEntity requester, UdUserEntity addressee) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        """
            INSERT INTO friendship (requester_id, addressee_id, status, created_date)
            VALUES (?, ?, ?, ?)
            """
    )) {
      ps.setObject(1, requester.getId());
      ps.setObject(2, addressee.getId());
      ps.setString(3, FriendshipStatus.PENDING.name());
      ps.setDate(4, new Date(System.currentTimeMillis()));
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void addFriend(UdUserEntity requester, UdUserEntity addressee) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        """
            INSERT INTO friendship (requester_id, addressee_id, status, created_date)
            VALUES (?, ?, ?, ?)
            """
    )) {

      ps.setObject(1, requester.getId());
      ps.setObject(2, addressee.getId());
      ps.setString(3, FriendshipStatus.ACCEPTED.name());
      ps.setDate(4, new Date(System.currentTimeMillis()));
      ps.executeUpdate();

      ps.setObject(1, addressee.getId());
      ps.setObject(2, requester.getId());
      ps.setString(3, FriendshipStatus.ACCEPTED.name());
      ps.setDate(4, new Date(System.currentTimeMillis()));
      ps.executeUpdate();

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  public UdUserEntity update(UdUserEntity user) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        """
            UPDATE "user" SET username = ?,
                            currency = ?,
                            firstname = ?,
                            surname = ?,
                            photo = ?,
                            photo_small = ?,
                            full_name = ?
            WHERE id = ?
            """
    )) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getCurrency().name());
      ps.setString(3, user.getFirstname());
      ps.setString(4, user.getSurname());
      ps.setBytes(5, user.getPhoto());
      ps.setBytes(6, user.getPhotoSmall());
      ps.setString(7, user.getFullname());
      ps.setObject(8, user.getId());
      ps.executeUpdate();
      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
