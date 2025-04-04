package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@WebTest
public class RegisterTest {

  private static final Config CFG = Config.getInstance();

  @Test
  void shouldRegisterNewUser() {
    String username = randomUsername();
    String password = "12345";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .goRegisterPage()
        .doRegister(username, password, password)
        .checkMessageText("Congratulations! You've registered!");
  }

  @Test
  void shouldNotRegisterUserWithExistingUsername() {
    String username = "duck";
    String password = "123456";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .goRegisterPage()
        .doRegister(username, password, password)
        .checkErrorMessageText("Username `duck` already exists");
  }

  @Test
  void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
    String username = randomUsername();
    String password = "12345";
    String confirmPassword = "0123456";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .goRegisterPage()
        .doRegister(username, password, confirmPassword)
        .checkErrorMessageText("Passwords should be equal");
  }
}
