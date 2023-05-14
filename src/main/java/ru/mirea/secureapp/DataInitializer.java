package ru.mirea.secureapp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.mirea.secureapp.models.Role;
import ru.mirea.secureapp.models.User;
import ru.mirea.secureapp.repositories.RoleRepository;
import ru.mirea.secureapp.repositories.UserRepository;
import ru.mirea.secureapp.services.UserService;

import java.util.List;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final UserService userService;

    @Override
    public void run(String... args) {
        if (roleRepository.findAll().size() == 0) {
            log.debug("Initializing roles' data...");
            roleRepository.saveAll(List.of(
                    new Role(1L, "ROLE_ADMIN"),
                    new Role(2L, "ROLE_USER"),
                    new Role(3L, "ROLE_VIEWER"),
                    new Role(4L, "ROLE_EDITOR")));
        }
        if (userRepository.findAll().isEmpty()) {
            log.debug("Initializing users' data...");
            var user = new User("admin", "P@ssw0rd");
            user.setRoles(Set.of(
                    new Role(1L, "ROLE_ADMIN"),
                    new Role(2L, "ROLE_USER")
            ));
            userService.save(user);
        }
    }
}
