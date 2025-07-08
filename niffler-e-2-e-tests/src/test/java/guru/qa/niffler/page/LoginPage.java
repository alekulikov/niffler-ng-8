package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {

  private final SelenideElement usernameInput;
  private final SelenideElement passwordInput;
  private final SelenideElement submitBtn;
  private final SelenideElement registerLink;

  public LoginPage(SelenideDriver driver) {
    this.usernameInput = driver.$("input[name='username']");
    this.passwordInput = driver.$("input[name='password']");
    this.submitBtn = driver.$("button[type='submit']");
    this.registerLink = driver.$(By.linkText("Create new account"));
    WebDriverRunner.setWebDriver(driver.getWebDriver());
  }

  public LoginPage() {
    this.usernameInput = $("input[name='username']");
    this.passwordInput = $("input[name='password']");
    this.submitBtn = $("button[type='submit']");
    this.registerLink = $(By.linkText("Create new account"));
  }

  @Nonnull
  public MainPage doLogin(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    submitBtn.click();
    return new MainPage();
  }

  @Nonnull
  public RegisterPage goRegisterPage() {
    registerLink.click();
    return new RegisterPage();
  }
}
