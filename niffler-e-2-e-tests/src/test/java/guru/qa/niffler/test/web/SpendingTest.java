package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserDataJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

@WebTest
public class SpendingTest {

  private static final Config CFG = Config.getInstance();

  @User(
      spendings = @Spend(
          category = "Обучение",
          description = "Обучение Niffler 2.0",
          amount = 89000.00,
          currency = CurrencyValues.RUB
      )
  )
  @Test
  void spendingDescriptionShouldBeUpdatedByTableAction(UserDataJson user) {
    final String newDescription = "Обучение Niffler NG";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .editSpending(user.testData().spends().getFirst().description())
        .editDescription(newDescription).save()
        .checkThatTableContains(newDescription);
  }

  @User(
      spendings = @Spend(
          category = "Обучение",
          description = "Обучение Niffler 2.0",
          amount = 89000.00,
          currency = CurrencyValues.RUB
      )
  )
  @ScreenShotTest(value = "img/expected-stat-edit.png", rewriteExpected = true)
  void checkStatComponentAfterEditingTest(UserDataJson user, BufferedImage expected) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .editSpending(user.testData().spends().getFirst().description())
        .editAmount(82500.99).save()
        .checkStatisticDiagram(expected)
        .checkBubbles(new Bubble(Color.yellow, "Обучение 82500.99 ₽"));
  }

  @User(
      spendings = @Spend(
          category = "Обучение",
          description = "Обучение Advanced 2.0",
          amount = 79990.19,
          currency = CurrencyValues.RUB
      )
  )
  @ScreenShotTest(value = "img/expected-stat.png", rewriteExpected = true)
  void checkStatComponentTest(UserDataJson user, BufferedImage expected) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .checkStatisticDiagram(expected)
        .checkBubbles(new Bubble(Color.yellow, "Обучение 79990.19 ₽"));
  }

  @User(
      spendings = @Spend(
          category = "Обучение",
          description = "Обучение Niffler 2.0",
          amount = 89000.00,
          currency = CurrencyValues.RUB
      )
  )
  @ScreenShotTest(value = "img/expected-stat-delete.png", rewriteExpected = true)
  void checkStatComponentAfterDeletingSpendTest(UserDataJson user, BufferedImage expected) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .deleteSpending(user.testData().spends().getFirst().description())
        .checkStatisticDiagram(expected);
  }

  @User(
      categories = {
          @Category(name = "Поездки"),
          @Category(name = "Ремонт", archived = true),
          @Category(name = "Страховка", archived = true)
      },
      spendings = {
          @Spend(
              category = "Поездки",
              description = "В Москву",
              amount = 9500,
              currency = CurrencyValues.RUB
          ),
          @Spend(
              category = "Ремонт",
              description = "Цемент",
              amount = 100,
              currency = CurrencyValues.RUB
          ),
          @Spend(
              category = "Страховка",
              description = "ОСАГО",
              amount = 3000,
              currency = CurrencyValues.RUB
          )
      }
  )
  @ScreenShotTest(value = "img/expected-stat-archived.png", rewriteExpected = true)
  void checkStatComponentWithArchiveCategoryTest(UserDataJson user, BufferedImage expected) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .checkStatisticDiagram(expected)
        .checkBubbles(
            new Bubble(Color.yellow, "Поездки 9500 ₽"),
            new Bubble(Color.green, "Archived 3100 ₽")
        );
  }

  @User(
      spendings = {
          @Spend(
              category = "Поездки",
              description = "В Москву",
              amount = 9500,
              currency = CurrencyValues.RUB
          ),
          @Spend(
              category = "Ремонт",
              description = "Цемент",
              amount = 100,
              currency = CurrencyValues.KZT
          ),
          @Spend(
              category = "Страховка",
              description = "ОСАГО",
              amount = 30.5,
              currency = CurrencyValues.EUR
          )
      }
  )
  @Test
  void checkSpendingTableTest(UserDataJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .checkSpendTable(user.testData().spends().toArray(SpendJson[]::new));
  }
}
