package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.auth.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository {

  UserEntity create(UserEntity user);

  Optional<UserEntity> findById(UUID id);

}
