package com.smxplore.proto.domain.ports.repository;

import com.smxplore.proto.domain.model.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User> findAll();

    List<User> search(UserSearchCriteria criteria);

    void deleteById(UUID id);
}
