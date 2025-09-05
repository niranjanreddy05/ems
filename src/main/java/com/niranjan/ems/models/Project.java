package com.niranjan.ems.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Project() {
    }

    // Convenience constructor
    public Project(String name, String description, User manager) {
        this.name = name;
        this.description = description;
        this.manager = manager;
        this.createdAt = LocalDateTime.now();
    }

    // Many Projects -> One Manager
    @ManyToOne
    @JoinColumn(name = "manager_id")
    @JsonIgnoreProperties({"managedProjects", "projects"})
    private User manager;

    // Many Projects <-> Many Employees
    @ManyToMany
    @JoinTable(
            name = "project_employees",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnoreProperties({"managedProjects", "projects"})
    private List<User> employees;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getManager() { return manager; }
    public void setManager(User manager) { this.manager = manager; }

    public List<User> getEmployees() { return employees; }
    public void setEmployees(List<User> employees) { this.employees = employees; }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", manager=" + manager +
                ", employees=" + employees +
                '}';
    }
}
