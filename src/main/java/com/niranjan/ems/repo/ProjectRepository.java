package com.niranjan.ems.repo;

import com.niranjan.ems.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
