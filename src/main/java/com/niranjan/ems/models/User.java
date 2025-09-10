package com.niranjan.ems.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 50)
    private String role;  // ADMIN / MANAGER / EMPLOYEE

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;


    // Many Users → One Department
    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonBackReference
    private Department department;

    // One Manager -> Many Projects
    @OneToMany(mappedBy = "manager")
    @JsonIgnoreProperties({"manager", "employees"})
    private List<Project> managedProjects;

    // Many Users <-> Many Projects
    @ManyToMany(mappedBy = "employees")
    @JsonIgnoreProperties({"manager", "employees"})
    private List<Project> projects;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public List<Project> getManagedProjects() { return managedProjects; }
    public void setManagedProjects(List<Project> managedProjects) { this.managedProjects = managedProjects; }

    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User() {

    }

    public User(String password, String username, String email, String name) {
        this.password = password;
        this.username = username;
        this.role = "EMPLOYEE";
        this.email = email;
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
