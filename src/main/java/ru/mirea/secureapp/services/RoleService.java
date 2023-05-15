package ru.mirea.secureapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.secureapp.models.Role;
import ru.mirea.secureapp.repositories.RoleRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Role> getRolesRights(){
        return roleRepository.findAll().stream().filter(i -> i.getId() > 2).peek(j -> {
            if (Objects.equals(j.getName(), "ROLE_VIEWER"))
                j.setName("Читатель");
            if (Objects.equals(j.getName(), "ROLE_EDITOR"))
                j.setName("Редактор");
        }).toList();
    }

    @Transactional(readOnly = true)
    public Optional<Role> getUserRole() {
        return roleRepository.findById(2L);
    }
}
