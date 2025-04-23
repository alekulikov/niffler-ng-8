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

  /**
   * Используя ChainedTransactionManager не получится откатить внутреннюю транзакцию при сбое во внешней,
   * так как вызывается метод reverse() и commit() выполняется в обратном порядке, а двойное подтверждение в
   * этом механизме отсутствует. Это означает, что для внутренней транзакции уже может быть выполнен commit(), когда
   * во внешней будет выброшено исключение, и откатить внутреннюю уже не получится. Корректно откатить все изменения
   * получится только если сбой произойдет в самой последней транзакции. На случай ошибки предусмотрен выброс
   * исключения HeuristicCompletionException, которое может иметь состояние STATE_ROLLED_BACK (произошел откат всех
   * транзакций) и STATE_MIXED (произошла ошибка не в самой последней транзакции и данные не консистентны).
   **/
  @Test
  void userSpringJdbcChainedTrxTest() {
    UserDbClient usersDbClient = new UserDbClient();
    UserDataJson user = usersDbClient.createUserSpringJdbcChainedTrx(
        new UserDataJson(
            null,
            randomUsername() + "-springJdbcChainedTrx",
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
}
