package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {

  private final SelenideElement spendings = $("#spendings");
  private final SelenideElement statistics = $("#stat");
  private final ElementsCollection tableRows = spendings.$$("tbody tr");
  private final SelenideElement profileBtn = $("[data-testid=\"PersonIcon\"]");
  private final SelenideElement profileLink = $(By.linkText("Profile"));

  public EditSpendingPage editSpending(String spendingDescription) {
    tableRows.find(text(spendingDescription))
        .$$("td")
        .get(5)
        .click();
    return new EditSpendingPage();
  }

  public void checkThatTableContains(String spendingDescription) {
    tableRows.find(text(spendingDescription))
        .should(visible);
  }

  public MainPage checkMainPageBeenLoad() {
    spendings.shouldBe(visible);
    statistics.shouldBe(visible);
    return this;
  }

  public ProfilePage goProfilePage() {
    profileBtn.click();
    profileLink.click();
    return new ProfilePage();
  }


}
