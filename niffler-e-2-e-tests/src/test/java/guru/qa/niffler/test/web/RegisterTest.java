package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class RegisterTest {

  private static final Config CFG = Config.getInstance();
  private final Faker faker = new Faker();

  @Test
  void shouldRegisterNewUser() {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .goRegisterPage()
        .doRegister(faker.name().username(), faker.internet().password(4, 8))
        .checkMessageText("Congratulations! You've registered!");
  }

  @Test
  void shouldNotRegisterUserWithExistingUsername() {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .goRegisterPage()
        .doRegister("duck", faker.internet().password(4, 8))
        .checkErrorMessageText("Username `duck` already exists");
  }

  @Test
  void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .goRegisterPage()
        .doRegister(faker.name().username(), faker.internet().password(4, 8), faker.internet().password(4, 8))
        .checkErrorMessageText("Passwords should be equal");
  }
}
