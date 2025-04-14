package guru.qa.niffler.test.web;

import guru.qa.niffler.model.*;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@Disabled
public class JdbcTest {

  @Test
  void txTest() {
    SpendDbClient spendDbClient = new SpendDbClient();

    assertThrowsExactly(RuntimeException.class, () -> spendDbClient.createSpend
        (
            new SpendJson(
                null,
                new Date(),
                new CategoryJson(
                    null,
                    "cat-name-tx-2",
                    "duck",
                    false
                ),
                CurrencyValues.RUB,
                1000.0,
                "spend-name-tx",
                null
            )
        )
    );
  }

  @Test
  void xaTxTest() {
    UserDbClient userDbClient = new UserDbClient();

    userDbClient.createUser(
        new UserAuthJson(
            null,
            randomUsername(),
            "12345",
            true,
            true,
            true,
            true
        ),
        new UserDataJson(
            null,
            "duck",
            CurrencyValues.EUR,
            "",
            "",
            "",
            null,
            null
        )
    );
  }
}
