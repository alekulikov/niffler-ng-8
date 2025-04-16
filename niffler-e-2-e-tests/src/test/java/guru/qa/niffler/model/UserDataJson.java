package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.UUID;

public record UserDataJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("currency")
    CurrencyValues currency,
    @JsonProperty("firstname")
    String firstname,
    @JsonProperty("surname")
    String surname,
    @JsonProperty("fullname")
    String fullname,
    @JsonProperty("photo")
    byte[] photo,
    @JsonProperty("photoSmall")
    byte[] photoSmall) {

  public static UserDataJson fromEntity(UserEntity entity) {
    return new UserDataJson(
        entity.getId(),
        entity.getUsername(),
        entity.getCurrency(),
        entity.getFirstname(),
        entity.getSurname(),
        entity.getFullname(),
        entity.getPhoto(),
        entity.getPhotoSmall()
    );
  }
}
