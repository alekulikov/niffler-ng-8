package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserDataJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.List;

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
  void checkStatComponentAfterEditingTest(UserDataJson user, BufferedImage expected) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .editSpending(user.testData().spends().getFirst().description())
        .editAmount(82500.99).save()
        .checkStatisticDiagram(expected)
        .checkStatisticLegend(List.of("Обучение 82500.99 ₽"));
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
  void checkStatComponentTest(UserDataJson user, BufferedImage expected) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .checkStatisticDiagram(expected)
        .checkStatisticLegend(List.of("Обучение 79990.19 ₽"));
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
  void checkStatComponentAfterDeletingSpendTest(UserDataJson user, BufferedImage expected) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .deleteSpending(user.testData().spends().getFirst().description())
        .checkStatisticDiagram(expected);
  }

  @User(
      categories = @Category(
          name = "Еда",
          archived = true
      ),
      spendings = @Spend(
          category = "Еда",
          description = "Кофе",
          amount = 200.00,
          currency = CurrencyValues.RUB
      )
  )
  @ScreenShotTest(value = "img/expected-stat-archived.png", rewriteExpected = true)
  void checkStatComponentWithArchiveCategoryTest(UserDataJson user, BufferedImage expected) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .checkStatisticDiagram(expected)
        .checkStatisticLegend(List.of("Archived 200 ₽"));
  }
}
