package com.niranjan.ems.repo;

import com.niranjan.ems.models.Project;
import com.niranjan.ems.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByManager(User user);
}
