package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.auth.UserEntity;

import java.util.UUID;

public record UserAuthJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("password")
    String password,
    @JsonProperty("enabled")
    Boolean enabled,
    @JsonProperty("accountNonExpired")
    Boolean accountNonExpired,
    @JsonProperty("accountNonLocked")
    Boolean accountNonLocked,
    @JsonProperty("credentialsNonExpired")
    Boolean credentialsNonExpired) {

  public static UserAuthJson fromEntity(UserEntity entity) {
    return new UserAuthJson(
        entity.getId(),
        entity.getUsername(),
        entity.getPassword(),
        entity.getEnabled(),
        entity.getAccountNonExpired(),
        entity.getAccountNonLocked(),
        entity.getCredentialsNonExpired()
    );
  }
}
