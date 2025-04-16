package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.UserAuthJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class UserEntity implements Serializable {
  private UUID id;
  private String username;
  private String password;
  private Boolean enabled;
  private Boolean accountNonExpired;
  private Boolean accountNonLocked;
  private Boolean credentialsNonExpired;

  public static UserEntity fromJson(UserAuthJson json) {
    UserEntity userEntity = new UserEntity();
    userEntity.setId(json.id());
    userEntity.setUsername(json.username());
    userEntity.setPassword(json.password());
    userEntity.setEnabled(json.enabled());
    userEntity.setAccountNonExpired(json.accountNonExpired());
    userEntity.setAccountNonLocked(json.accountNonLocked());
    userEntity.setCredentialsNonExpired(json.credentialsNonExpired());
    return userEntity;
  }
}
