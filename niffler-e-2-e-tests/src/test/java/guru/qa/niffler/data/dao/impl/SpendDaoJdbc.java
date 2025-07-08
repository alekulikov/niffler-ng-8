package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@SuppressWarnings("resource")
@ParametersAreNonnullByDefault
public class SpendDaoJdbc implements SpendDao {

  private static final Config CFG = Config.getInstance();

  @NotNull
  @Override
  public SpendEntity create(SpendEntity spend) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
            "VALUES ( ?, ?, ?, ?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, spend.getUsername());
      ps.setDate(2, new Date(spend.getSpendDate().getTime()));
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategory().getId());

      ps.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can`t find id in ResultSet");
        }
      }
      spend.setId(generatedKey);
      return spend;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM spend WHERE id = ?"
    )) {
      ps.setObject(1, id);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          CategoryDao categoryDao = new CategoryDaoJdbc();
          SpendEntity se = new SpendEntity();
          CategoryEntity ce = categoryDao
              .findCategoryById(rs.getObject("category_id", UUID.class))
              .orElseThrow(() -> new SQLException("Can`t find category in Spend"));
          se.setId(rs.getObject("id", UUID.class));
          se.setUsername(rs.getString("username"));
          se.setSpendDate(rs.getDate("spend_date"));
          se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          se.setAmount(rs.getDouble("amount"));
          se.setDescription(rs.getString("description"));
          se.setCategory(ce);
          return Optional.of(se);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return Optional.empty();
  }

  @NotNull
  @Override
  public List<SpendEntity> findAllByUsername(String username) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM spend WHERE username = ?"
    )) {
      ps.setString(1, username);
      ps.execute();
      List<SpendEntity> spendEntities = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          CategoryDao categoryDao = new CategoryDaoJdbc();
          SpendEntity se = new SpendEntity();
          CategoryEntity ce = categoryDao
              .findCategoryById(rs.getObject("category_id", UUID.class))
              .orElseThrow(() -> new SQLException("Can`t find category in Spend"));
          se.setId(rs.getObject("id", UUID.class));
          se.setUsername(rs.getString("username"));
          se.setSpendDate(rs.getDate("spend_date"));
          se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          se.setAmount(rs.getDouble("amount"));
          se.setDescription(rs.getString("description"));
          se.setCategory(ce);
          spendEntities.add(se);
        }
      }
      return spendEntities;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteSpend(SpendEntity spend) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        "DELETE FROM spend WHERE id = ?"
    )) {
      ps.setObject(1, spend.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  @Override
  public List<SpendEntity> findAll() {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM spend"
    )) {
      ps.execute();
      List<SpendEntity> spendEntities = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          CategoryDao categoryDao = new CategoryDaoJdbc();
          SpendEntity se = new SpendEntity();
          CategoryEntity ce = categoryDao
              .findCategoryById(rs.getObject("category_id", UUID.class))
              .orElseThrow(() -> new SQLException("Can`t find category in Spend"));
          se.setId(rs.getObject("id", UUID.class));
          se.setUsername(rs.getString("username"));
          se.setSpendDate(rs.getDate("spend_date"));
          se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          se.setAmount(rs.getDouble("amount"));
          se.setDescription(rs.getString("description"));
          se.setCategory(ce);
          spendEntities.add(se);
        }
      }
      return spendEntities;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  @Override
  public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM spend WHERE username = ? AND description = ?"
    )) {
      ps.setString(1, username);
      ps.setString(2, description);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          CategoryDao categoryDao = new CategoryDaoJdbc();
          SpendEntity se = new SpendEntity();
          CategoryEntity ce = categoryDao
              .findCategoryById(rs.getObject("category_id", UUID.class))
              .orElseThrow(() -> new SQLException("Can`t find category in Spend"));
          se.setId(rs.getObject("id", UUID.class));
          se.setUsername(rs.getString("username"));
          se.setSpendDate(rs.getDate("spend_date"));
          se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          se.setAmount(rs.getDouble("amount"));
          se.setDescription(rs.getString("description"));
          se.setCategory(ce);
          return Optional.of(se);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return Optional.empty();
  }

  @NotNull
  @Override
  public SpendEntity update(SpendEntity spend) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        """
            UPDATE spend SET username = ?,
                             spend_date = ?,
                             currency = ?,
                             amount = ?,
                             description = ?,
                             category_id = ?
            WHERE id = ?
            """
    )) {
      ps.setString(1, spend.getUsername());
      ps.setDate(2, new Date(spend.getSpendDate().getTime()));
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategory().getId());
      ps.setObject(7, spend.getId());
      ps.executeUpdate();
      return spend;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
