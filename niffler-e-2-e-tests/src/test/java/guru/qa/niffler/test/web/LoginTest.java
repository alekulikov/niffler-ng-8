package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserDataJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebTest
public class LoginTest {

  private static final Config CFG = Config.getInstance();

  @User
  @Test
  void mainPageShouldBeDisplayedAfterSuccessLogin(UserDataJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .checkMainPageBeenLoad();
  }

  @User
  @Test
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials(UserDataJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), "123456");
    assertEquals("Login to Niffler", Selenide.title());
  }
}
