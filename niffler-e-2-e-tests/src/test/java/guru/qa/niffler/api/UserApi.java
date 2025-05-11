package guru.qa.niffler.api;

import guru.qa.niffler.model.UserDataJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface UserApi {

  @POST("/internal/invitations/send")
  Call<UserDataJson> sendInvitation(@Query("username") String username,
                                    @Query("targetUsername") String targetUsername);

  @POST("/internal/invitations/accept")
  Call<UserDataJson> acceptInvitation(@Query("username") String username,
                                      @Query("targetUsername") String targetUsername);

  @POST("/internal/invitations/decline")
  Call<UserDataJson> declineInvitation(@Query("username") String username,
                                       @Query("targetUsername") String targetUsername);

  @GET("/internal/users/current")
  Call<UserDataJson> currentUser(@Query("username") String username);

  @GET("/internal/users/all")
  Call<List<UserDataJson>> allUsers(@Query("username") String username,
                                    @Query("searchQuery") String searchQuery);

  @POST("/internal/users/update")
  Call<UserDataJson> updateUserInfo(@Body UserDataJson user);
}
