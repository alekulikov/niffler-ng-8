package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.component.SpendTable;
import guru.qa.niffler.page.component.StatComponent;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

  private final SelenideElement profileBtn = $("button[aria-label=\"Menu\"]");
  private final SelenideElement profileLink = $(By.linkText("Profile"));
  private final SelenideElement friendsLink = $(By.linkText("Friends"));

  private final StatComponent statComponent = new StatComponent();
  private final SpendTable spendsTable = new SpendTable();

  @Nonnull
  public MainPage checkMainPageBeenLoad() {
    spendsTable.self.shouldBe(visible);
    statComponent.self.shouldBe(visible);
    return this;
  }

  @Nonnull
  public ProfilePage goProfilePage() {
    profileBtn.click();
    profileLink.click();
    return new ProfilePage();
  }

  @Nonnull
  public FriendsPage goFriendsPage() {
    profileBtn.click();
    friendsLink.click();
    return new FriendsPage();
  }

  @Nonnull
  public MainPage checkStatisticDiagram(BufferedImage expected) throws IOException {
    statComponent.checkStatisticDiagram(expected);
    return this;
  }

  @Nonnull
  public MainPage checkBubbles(Bubble... expectedBubbles) {
    statComponent.checkBubbles(expectedBubbles);
    return this;
  }

  @Nonnull
  public EditSpendingPage editSpending(String spendingDescription) {
    return spendsTable.editSpending(spendingDescription);
  }

  @Nonnull
  public MainPage deleteSpending(String spendingDescription) {
    spendsTable.deleteSpending(spendingDescription);
    return this;
  }

  @Nonnull
  public MainPage checkThatTableContains(String spendingDescription) {
    spendsTable.checkThatTableContains(spendingDescription);
    return this;
  }

  @Nonnull
  public MainPage filterSpendingsByDescription(String spendingDescription) {
    spendsTable.filterSpendingsByDescription(spendingDescription);
    return this;
  }

  @Nonnull
  public MainPage checkSpendTable(SpendJson... expectedSpends) {
    spendsTable.checkSpendTable(expectedSpends);
    return this;
  }
}
