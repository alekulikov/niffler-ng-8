package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient implements SpendClient {

  private static final Config CFG = Config.getInstance();

  private final OkHttpClient client = new OkHttpClient.Builder().build();
  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(CFG.spendUrl())
      .client(client)
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final SpendApi spendApi = retrofit.create(SpendApi.class);

  public SpendJson createSpend(SpendJson spend) {
    Response<SpendJson> response = execute(
        spendApi.addSpend(spend),
        201
    );
    return response.body();
  }

  public CategoryJson createCategory(CategoryJson category) {
    Response<CategoryJson> response = execute(
        spendApi.addCategory(category),
        200
    );
    return response.body();
  }

  public CategoryJson updateCategory(CategoryJson category) {
    Response<CategoryJson> response = execute(
        spendApi.updateCategory(category),
        200
    );
    return response.body();
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
