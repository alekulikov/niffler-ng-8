package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserDataJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@WebTest
public class SpendingTest {

  private static final Config CFG = Config.getInstance();

  @User(
      username = "duck",
      spendings = @Spend(
          category = "Обучение",
          description = "Обучение Niffler 2.0",
          amount = 89000.00,
          currency = CurrencyValues.RUB
      )
  )
  @Test
  void spendingDescriptionShouldBeUpdatedByTableAction(SpendJson[] spend) {
    final String newDescription = "Обучение Niffler NG";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin("duck", "12345")
        .editSpending(spend[0].description())
        .editDescription(newDescription)
        .save();

    new MainPage().checkThatTableContains(newDescription);
  }

  @User(
      spendings = @Spend(
          category = "Обучение",
          description = "Обучение Advanced 2.0",
          amount = 79990,
          currency = CurrencyValues.RUB
      )
  )
  @ScreenShotTest("img/expected-stat.png")
  void checkStatComponentTest(UserDataJson user, BufferedImage expected) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password());

    Selenide.sleep(3000);
    BufferedImage actual = ImageIO.read($("canvas[role='img']").screenshot());
    assertFalse(new ScreenDiffResult(
        expected,
        actual
    ));
  }
}
