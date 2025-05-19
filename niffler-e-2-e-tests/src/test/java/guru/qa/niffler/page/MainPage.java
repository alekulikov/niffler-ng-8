package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDiffResult;
import lombok.SneakyThrows;
import org.openqa.selenium.By;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MainPage {

  private final SelenideElement spendings = $("#spendings");
  private final SelenideElement statistics = $("#stat");
  private final ElementsCollection tableRows = spendings.$$("tbody tr");
  private final SelenideElement profileBtn = $("button[aria-label=\"Menu\"]");
  private final SelenideElement profileLink = $(By.linkText("Profile"));
  private final SelenideElement friendsLink = $(By.linkText("Friends"));
  private final SelenideElement spendingSearch = $("input[placeholder='Search']");
  private final SelenideElement statisticDiagram = $("canvas[role='img']");
  private final SelenideElement statisticLegend = $("#legend-container");
  private final SelenideElement deleteBtn = $("#delete");
  private final SelenideElement dialogWindow = $("div[role='dialog']");

  public EditSpendingPage editSpending(String spendingDescription) {
    filterSpendingsByDescription(spendingDescription);
    tableRows.find(text(spendingDescription))
        .$$("td")
        .get(5)
        .click();
    return new EditSpendingPage();
  }

  public MainPage deleteSpending(String spendingDescription) {
    tableRows.find(text(spendingDescription))
        .$$("td")
        .get(0)
        .click();
    deleteBtn.click();
    dialogWindow.$(byText("Delete")).click();
    return new MainPage();
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

  @SneakyThrows
  public MainPage checkStatisticDiagram(BufferedImage expected) {
    Selenide.sleep(3000);
    BufferedImage actual = ImageIO.read(statisticDiagram.screenshot());
    assertFalse(new ScreenDiffResult(
        actual,
        expected
    ));
    return this;
  }

  public MainPage checkStatisticLegend(List<String> spends) {
    for (String description : spends) {
      statisticLegend.shouldHave(text(description));
    }
    return this;
  }
}
