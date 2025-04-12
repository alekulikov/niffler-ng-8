package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoJdbc implements SpendDao {

  private static final Config CFG = Config.getInstance();

  @Override
  public SpendEntity create(SpendEntity spend) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
              "VALUES ( ?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
      )) {
        ps.setString(1, spend.getUsername());
        ps.setDate(2, spend.getSpendDate());
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
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl());
         PreparedStatement ps = connection.prepareStatement("SELECT * FROM spend WHERE id = ?")) {
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

  @Override
  public List<SpendEntity> findAllByUsername(String username) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl());
         PreparedStatement ps = connection.prepareStatement("SELECT * FROM spend WHERE username = ?")) {
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
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl());
         PreparedStatement ps = connection.prepareStatement("DELETE FROM spend WHERE id = ?")) {
      ps.setObject(1, spend.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
