package ru.mirea.secureapp.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.secureapp.models.User;
import ru.mirea.secureapp.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CipherService cipherService;

    @Transactional
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCryptKey(cipherService.getBase64Key());
        log.info("Save new user - " + user.getUsername());
        userRepository.save(user);
    }

    @Transactional
    public void save(String username, String password) {
        User user = new User(username, password);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCryptKey(cipherService.getBase64Key());
        log.info("Save new user - " + user.getUsername());
        userRepository.save(user);
    }

    @Transactional
    public void update(User user){
        log.info("Update user - " + user.getUsername());
        userRepository.save(user);
    }

    @Transactional
    public void updateUserKey(String username) {
        User user = findByUsername(username);
        user.setCryptKey(cipherService.getBase64Key());
        cipherService.updateKey(user.getId(), user.getCryptKey());
        update(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        log.info("Find user by username - " + username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found"));
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long Id) {
        return userRepository.findById(Id);
    }

    @Transactional(readOnly = true)
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public void validate(String username, String password) {
        if (username.length() < 4 || username.length() > 64) {
            throw new BadCredentialsException("Bad username length: expected between 4 and 64, got - " + username.length());
        }
        try {
            findByUsername(username);
            throw new BadCredentialsException("Duplicated username");
        } catch (UsernameNotFoundException ignored) {
        }

        if (password.length() < 4 || password.length() > 64) {
            throw new BadCredentialsException("Bad password length: expected between 4 and 64, got - " + password.length());
        }
    }
}
