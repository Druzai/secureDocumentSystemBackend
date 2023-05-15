package ru.mirea.secureapp.models;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "docUsersRights")
@Getter
@Setter
public class DocumentUserRights {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @OneToOne
    private Role role;

    public DocumentUserRights(User user, Role role){
        this.user = user;
        this.role = role;
    }

    public DocumentUserRights() {
    }
}
