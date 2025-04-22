package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserDataJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class JdbcTest {

  @Test
  void userJdbcTest() {
    UserDbClient userDbClient = new UserDbClient();
    UserDataJson user = userDbClient.createUser(
        new UserDataJson(
            null,
            randomUsername() + "-jdbc",
            CurrencyValues.EUR,
            "",
            "",
            "",
            null,
            null
        )
    );

    System.out.println(user);
  }

  @Test
  void userSpringJdbcTest() {
    UserDbClient usersDbClient = new UserDbClient();
    UserDataJson user = usersDbClient.createUserSpringJdbc(
        new UserDataJson(
            null,
            randomUsername() + "-springJdbc",
            CurrencyValues.RUB,
            null,
            null,
            null,
            null,
            null
        )
    );
    System.out.println(user);
  }

  @Test
  void categoryJdbcTest() {
    SpendDbClient spendDbClient = new SpendDbClient();
    CategoryJson category = spendDbClient.createCategory(
        new CategoryJson(
            null,
            randomCategoryName() + "-jdbc",
            "duck",
            true
        )
    );
    System.out.println(category);
  }

  @Test
  void categorySpringJdbcTest() {
    SpendDbClient spendDbClient = new SpendDbClient();
    CategoryJson category = spendDbClient.createCategorySpringJdbc(
        new CategoryJson(
            null,
            randomCategoryName() + "-springJdbc",
            "duck",
            true
        )
    );
    System.out.println(category);
  }

  @Test
  void spendJdbcTest() {
    SpendDbClient spendDbClient = new SpendDbClient();
    SpendJson spend = spendDbClient.createSpend(
        new SpendJson(
            null,
            new Date(),
            new CategoryJson(
                null,
                "Образование",
                "duck",
                false
            ),
            CurrencyValues.RUB,
            1000.0,
            "spend-name-jdbc",
            "duck"
        )
    );
    System.out.println(spend);
  }

  @Test
  void spendSpringJdbcTest() {
    SpendDbClient spendDbClient = new SpendDbClient();
    SpendJson spend = spendDbClient.createSpendSpringJdbc(
        new SpendJson(
            null,
            new Date(),
            new CategoryJson(
                null,
                "Образование",
                "duck",
                false
            ),
            CurrencyValues.RUB,
            1000.0,
            "spend-name-springJdbc",
            "duck"
        )
    );

    System.out.println(spend);
  }
}
