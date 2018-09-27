package com.thredim.regserver.repository;

import com.thredim.regserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByPassword(String password);
}
