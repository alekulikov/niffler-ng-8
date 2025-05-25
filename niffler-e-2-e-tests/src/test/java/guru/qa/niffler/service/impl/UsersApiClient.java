package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.UserApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserDataJson;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersApiClient implements UsersClient {

  private static final Config CFG = Config.getInstance();
  private static final String defaultPassword = "12345";

  private final OkHttpClient client = new OkHttpClient.Builder()
      .addNetworkInterceptor(new AllureOkHttp3()
          .setRequestTemplate("request-attachment.ftl")
          .setResponseTemplate("response-attachment.ftl"))
      .build();
  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(CFG.userdataUrl())
      .client(client)
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final UserApi userApi = retrofit.create(UserApi.class);

  @Override
  public UserDataJson createUser(String username, String password) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void createIncomeInvitations(UserDataJson addresseeUser, int count) {
    for (int i = 0; i < count; i++) {
      UserDataJson requester = createUser(randomUsername(), defaultPassword);
      Response<UserDataJson> response = execute(
          userApi.sendInvitation(requester.username(), addresseeUser.username()),
          200
      );
      if (response.isSuccessful() && response.body() != null) {
        addresseeUser.testData().incomeInvitations().add(response.body().username());
      }
    }
  }

  @Override
  public void createOutcomeInvitations(UserDataJson requesterUser, int count) {
    for (int i = 0; i < count; i++) {
      UserDataJson addresseeUser = createUser(randomUsername(), defaultPassword);
      Response<UserDataJson> response = execute(
          userApi.sendInvitation(requesterUser.username(),
              addresseeUser.username()), 200
      );
      if (response.isSuccessful() && response.body() != null) {
        requesterUser.testData().outcomeInvitations().add(response.body().username());
      }
    }
  }

  @Override
  public void createFriends(UserDataJson user, int count) {
    for (int i = 0; i < count; i++) {
      UserDataJson friend = createUser(randomUsername(), defaultPassword);
      Response<UserDataJson> response = execute(
          userApi.sendInvitation(user.username(), friend.username()),
          200
      );
      if (response.isSuccessful() && response.body() != null) {
        user.testData().friends().add(response.body().username());
      }
    }
  }

  private <T> Response<T> execute(Call<T> request, int statusCode) {
    Response<T> response;
    try {
      response = request.execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(statusCode, response.code());
    return response;
  }
}
