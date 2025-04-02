package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {

  private final ElementsCollection friends = $$("#friends tr");
  private final ElementsCollection incomeRequests = $$("#requests tr");
  private final SelenideElement allPeopleTab = $(By.linkText("All people"));
  private final ElementsCollection people = $$("#all tr");

  public FriendsPage checkThatFriendsContains(String username) {
    friends.find(text(username)).shouldBe(visible);
    return this;
  }

  public FriendsPage checkThatFriendsEmpty() {
    friends.shouldHave(empty);
    return this;
  }

  public FriendsPage checkThatIncomeRequestsContains(String username) {
    incomeRequests.find(text(username)).shouldBe(visible);
    return this;
  }

  public FriendsPage selectAllPeopleTab() {
    allPeopleTab.click();
    return this;
  }

  public FriendsPage checkThatOutcomeRequestsContains(String username) {
    people.find(text(username))
        .shouldBe(visible)
        .shouldHave(text("Waiting..."));
    return this;
  }

}
