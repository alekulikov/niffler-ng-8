package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserDataJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsTest {

  private static final Config CFG = Config.getInstance();

  @User(
      friends = 3
  )
  @Test
  void friendShouldBePresentInFriendsTable(UserDataJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .goFriendsPage()
        .checkThatFriendsContains(user.testData().friends().toArray(String[]::new));
  }

  @User
  @Test
  void friendsTableShouldBeEmptyForNewUser(UserDataJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .goFriendsPage()
        .checkThatFriendsEmpty();
  }

  @User(
      incomeInvitations = 2
  )
  @Test
  void incomeInvitationBePresentInFriendsTable(UserDataJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .goFriendsPage()
        .checkThatIncomeRequestsContains(user.testData().incomeInvitations().toArray(String[]::new));
  }

  @User(
      outcomeInvitations = 1
  )
  @Test
  void outcomeInvitationBePresentInAllPeoplesTable(UserDataJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .goFriendsPage()
        .selectAllPeopleTab()
        .checkThatOutcomeRequestsContains(user.testData().outcomeInvitations().toArray(String[]::new));
  }
}
