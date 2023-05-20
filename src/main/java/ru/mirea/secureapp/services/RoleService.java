package ru.mirea.secureapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.secureapp.models.Role;
import ru.mirea.secureapp.repositories.RoleRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Role> getNamedRoles() {
        return roleRepository.findAll().stream()
                .map(r -> new Role(r.getId(), r.getName(), getRoleUserName(r.getName())))
                .collect(Collectors.toList());
    }

    public List<Role> processRoles(Collection<Role> roles){
        return roles.stream()
                .map(r -> new Role(r.getId(), r.getName(), getRoleUserName(r.getName())))
                .collect(Collectors.toList());
    }

    private static String getRoleUserName(String roleName) {
        switch (roleName) {
            case "ROLE_ADMIN" -> {
                return "Админ";
            }
            case "ROLE_USER" -> {
                return "Пользователь";
            }
            case "ROLE_VIEWER" -> {
                return "Читатель";
            }
            case "ROLE_EDITOR" -> {
                return "Редактор";
            }
            default -> {
                return roleName;
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Role> getRolesRights() {
        return roleRepository.findAll().stream().filter(i -> i.getId() > 2)
                .map(j -> new Role(j.getId(), j.getName(), getRoleUserName(j.getName())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Role> getUserRole() {
        return roleRepository.findById(2L);
    }
}
