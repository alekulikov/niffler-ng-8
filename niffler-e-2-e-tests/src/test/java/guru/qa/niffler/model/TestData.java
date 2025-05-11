package guru.qa.niffler.model;

import java.util.ArrayList;
import java.util.List;

public record TestData(String password,
                       List<CategoryJson> categories,
                       List<SpendJson> spends,
                       List<String> incomeInvitations,
                       List<String> outcomeInvitations,
                       List<String> friends) {

  public TestData(String password) {
    this(password,
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>()
    );
  }

  public TestData(String password,
                  List<CategoryJson> categories,
                  List<SpendJson> spends,
                  List<String> incomeInvitations,
                  List<String> outcomeInvitations,
                  List<String> friends) {
    this.password = password;
    this.categories = categories;
    this.spends = spends;
    this.incomeInvitations = incomeInvitations;
    this.outcomeInvitations = outcomeInvitations;
    this.friends = friends;
  }
}
