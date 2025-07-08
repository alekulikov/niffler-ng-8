package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userdata.UdUserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UdUserDao {

  @Nonnull
  UdUserEntity create(UdUserEntity user);

  @Nonnull
  Optional<UdUserEntity> findById(UUID id);

  @Nonnull
  Optional<UdUserEntity> findByUsername(String username);

  void delete(UdUserEntity user);

  @Nonnull
  List<UdUserEntity> findAll();
}
