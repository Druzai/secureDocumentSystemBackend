package ru.mirea.secureapp.models;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "roles")
@Setter
@Getter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Role(long id, String role_user) {
        this.id = id;
        this.name = role_user;
    }

    public Role() {}
}
