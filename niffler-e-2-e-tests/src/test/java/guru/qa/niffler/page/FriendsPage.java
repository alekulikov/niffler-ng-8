package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class FriendsPage {

  private final ElementsCollection friends = $$("#friends tr");
  private final ElementsCollection incomeRequests = $$("#requests tr");
  private final SelenideElement allPeopleTab = $(By.linkText("All people"));
  private final ElementsCollection people = $$("#all tr");
  private final SelenideElement peopleSearch = $("input[placeholder='Search']");

  public FriendsPage checkThatFriendsContains(String... usernames) {
    for (String username : usernames) {
      filterPeopleByUsername(username);
      friends.find(text(username)).shouldBe(visible);
    }
    return this;
  }

  public FriendsPage checkThatFriendsEmpty() {
    friends.shouldBe(empty);
    return this;
  }

  public FriendsPage checkThatIncomeRequestsContains(String... usernames) {
    for (String username : usernames) {
      filterPeopleByUsername(username);
      incomeRequests.find(text(username)).shouldBe(visible);
    }
    return this;
  }

  public FriendsPage selectAllPeopleTab() {
    allPeopleTab.click();
    return this;
  }

  public FriendsPage checkThatOutcomeRequestsContains(String... usernames) {
    for (String username : usernames) {
      filterPeopleByUsername(username);
      people.find(text(username))
          .shouldBe(visible)
          .shouldHave(text("Waiting..."));
    }
    return this;
  }

  public FriendsPage filterPeopleByUsername(String username) {
    executeJavaScript("arguments[0].value = '';", peopleSearch);
    peopleSearch.setValue(username).pressEnter();
    return this;
  }
}
