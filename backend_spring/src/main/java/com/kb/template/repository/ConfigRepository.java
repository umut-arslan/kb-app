package com.kb.template.repository;

import com.kb.template.model.entity.Config;
import com.kb.template.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Long> {
    Config findByKeyAndUser(String key, User user);
    Optional<Config> findByIdAndUser(long id, User user);
    List<Config> findAllByUser(User user);
}
