package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static guru.qa.niffler.condition.SpendConditions.spends;

@ParametersAreNonnullByDefault
public class SpendTable {

  public final SelenideElement self = $("#spendings");
  private final ElementsCollection spends = self.$$("tbody tr");
  private final SelenideElement spendingSearch = $("input[placeholder='Search']");
  private final SelenideElement deleteBtn = $("#delete");
  private final SelenideElement dialogWindow = $("div[role='dialog']");

  @Nonnull
  public EditSpendingPage editSpending(String spendingDescription) {
    filterSpendingsByDescription(spendingDescription);
    spends.find(text(spendingDescription))
        .$$("td")
        .get(5)
        .click();
    return new EditSpendingPage();
  }

  @Nonnull
  public SpendTable deleteSpending(String spendingDescription) {
    spends.find(text(spendingDescription))
        .$$("td")
        .get(0)
        .click();
    deleteBtn.click();
    dialogWindow.$(byText("Delete")).click();
    return this;
  }

  public void checkThatTableContains(String spendingDescription) {
    spends.find(text(spendingDescription))
        .should(visible);
  }

  @Nonnull
  public SpendTable filterSpendingsByDescription(String spendingDescription) {
    executeJavaScript("arguments[0].value = '';", spendingSearch);
    spendingSearch.setValue(spendingDescription).pressEnter();
    return this;
  }

  @Nonnull
  public SpendTable checkSpendTable(SpendJson... expectedSpends) {
    spends.shouldHave(spends(expectedSpends));
    return this;
  }
}
