package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UdUserDaoJdbc implements UdUserDao {

  private static final Config CFG = Config.getInstance();

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

  @Override
  public Optional<UdUserEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM \"user\" WHERE id = ?"
    )) {
      ps.setObject(1, id);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          UdUserEntity user = new UdUserEntity();
          user.setId(rs.getObject("id", UUID.class));
          user.setUsername(rs.getString("username"));
          user.setFirstname(rs.getString("firstname"));
          user.setSurname(rs.getString("surname"));
          user.setFullname(rs.getString("full_name"));
          user.setPhoto(rs.getBytes("photo"));
          user.setPhotoSmall(rs.getBytes("photo_small"));
          return Optional.of(user);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return Optional.empty();
  }

  @Override
  public Optional<UdUserEntity> findByUsername(String username) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM \"user\" WHERE username = ?"
    )) {
      ps.setString(1, username);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          UdUserEntity user = new UdUserEntity();
          user.setId(rs.getObject("id", UUID.class));
          user.setUsername(rs.getString("username"));
          user.setFirstname(rs.getString("firstname"));
          user.setSurname(rs.getString("surname"));
          user.setFullname(rs.getString("full_name"));
          user.setPhoto(rs.getBytes("photo"));
          user.setPhotoSmall(rs.getBytes("photo_small"));
          return Optional.of(user);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return Optional.empty();
  }

  @Override
  public void delete(UdUserEntity user) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        "DELETE FROM \"user\" WHERE id = ?"
    )) {
      ps.setObject(1, user.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<UdUserEntity> findAll() {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM \"user\""
    )) {
      ps.execute();
      List<UdUserEntity> users = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          UdUserEntity ue = new UdUserEntity();
          ue.setId(rs.getObject("id", UUID.class));
          ue.setUsername(rs.getString("username"));
          ue.setFirstname(rs.getString("firstname"));
          ue.setSurname(rs.getString("surname"));
          ue.setFullname(rs.getString("full_name"));
          ue.setPhoto(rs.getBytes("photo"));
          ue.setPhotoSmall(rs.getBytes("photo_small"));
          users.add(ue);
        }
      }
      return users;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
