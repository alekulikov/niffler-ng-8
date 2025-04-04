package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class RegisterTest {

  private static final Config CFG = Config.getInstance();
  private final Faker faker = new Faker();

  @Test
  void shouldRegisterNewUser() {
    String username = faker.name().firstName();
    String password = faker.internet().password(4, 8);

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .goRegisterPage()
        .doRegister(username, password, password)
        .checkMessageText("Congratulations! You've registered!");
  }

  @Test
  void shouldNotRegisterUserWithExistingUsername() {
    String username = "duck";
    String password = faker.internet().password(4, 8);

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .goRegisterPage()
        .doRegister(username, password, password)
        .checkErrorMessageText("Username `duck` already exists");
  }

  @Test
  void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
    String username = faker.name().firstName();
    String password = faker.internet().password(4, 8);
    String confirmPassword = faker.internet().password(4, 8);

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .goRegisterPage()
        .doRegister(username, password, confirmPassword)
        .checkErrorMessageText("Passwords should be equal");
  }
}
