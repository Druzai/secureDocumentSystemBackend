package ru.mirea.secureapp.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.mirea.secureapp.models.Role;

import java.util.Set;

@Component
@Getter
@Setter
public class UserInfo {
    private String username;
    private Set<Role> roles;
}
