package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class MainPage {

  private final SelenideElement spendings = $("#spendings");
  private final SelenideElement statistics = $("#stat");
  private final ElementsCollection tableRows = spendings.$$("tbody tr");
  private final SelenideElement profileBtn = $("button[aria-label=\"Menu\"]");
  private final SelenideElement profileLink = $(By.linkText("Profile"));
  private final SelenideElement friendsLink = $(By.linkText("Friends"));
  private final SelenideElement spendingSearch = $("input[placeholder='Search']");

  public EditSpendingPage editSpending(String spendingDescription) {
    filterSpendingsByDescription(spendingDescription);
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

  public FriendsPage goFriendsPage() {
    profileBtn.click();
    friendsLink.click();
    return new FriendsPage();
  }

  public MainPage filterSpendingsByDescription(String spendingDescription) {
    executeJavaScript("arguments[0].value = '';", spendingSearch);
    spendingSearch.setValue(spendingDescription).pressEnter();
    return this;
  }
}
