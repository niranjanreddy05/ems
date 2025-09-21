package com.niranjan.ems.repo;

import com.niranjan.ems.models.Department;
import com.niranjan.ems.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(String role);

    Long countByDepartmentAndRole(Department dept, String role);

    User findByUsername(String username);

    List<User> findByRoleAndDepartmentId(String role, Long departmentId);

}
