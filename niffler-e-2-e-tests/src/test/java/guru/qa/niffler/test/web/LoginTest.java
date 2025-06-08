package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.NonStaticBrowserExtension;
import guru.qa.niffler.model.UserDataJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static guru.qa.niffler.utils.SelenideUtils.chromeConfig;
import static guru.qa.niffler.utils.SelenideUtils.firefoxConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginTest {

  private static final Config CFG = Config.getInstance();

  @RegisterExtension
  private final NonStaticBrowserExtension browserExtension = new NonStaticBrowserExtension();
  private final SelenideDriver chromeDriver = new SelenideDriver(chromeConfig);

  @User
  @Test
  void mainPageShouldBeDisplayedAfterSuccessLogin(UserDataJson user) {
    browserExtension.drivers().add(chromeDriver);

    chromeDriver.open(CFG.frontUrl());
    new LoginPage(chromeDriver)
        .doLogin(user.username(), user.testData().password())
        .checkMainPageBeenLoad();
  }

  @User
  @Test
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials(UserDataJson user) {
    SelenideDriver firefoxDriver = new SelenideDriver(firefoxConfig);
    browserExtension.drivers().addAll(List.of(chromeDriver, firefoxDriver));

    chromeDriver.open(CFG.frontUrl());
    new LoginPage(chromeDriver).doLogin(user.username(), "123456");
    assertEquals("Login to Niffler", chromeDriver.title());
    firefoxDriver.open(CFG.frontUrl());
    new LoginPage(firefoxDriver).goRegisterPage();
  }
}
