package com.niranjan.ems.repo;

import com.niranjan.ems.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
