package guru.qa.niffler.service;

import guru.qa.niffler.model.UserDataJson;

public interface UsersClient {

  UserDataJson createUser(String username, String password);

  void createIncomeInvitations(UserDataJson targetUser, int count);

  void createOutcomeInvitations(UserDataJson targetUser, int count);

  void createFriends(UserDataJson targetUser, int count);
}
