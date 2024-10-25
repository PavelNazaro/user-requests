package ru.pavelnazaro.userrequests.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pavelnazaro.userrequests.enums.Role;
import ru.pavelnazaro.userrequests.exceptions.UserNotFoundException;
import ru.pavelnazaro.userrequests.models.User;
import ru.pavelnazaro.userrequests.repositories.UserRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    protected static final String USER_NOT_FOUND = "User not found";
    private static final String INVALID_ROLE = "Invalid role: ";

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByName(String name) {
        logger.info("Finding user by name: {}", name);
        return userRepository.findByName(name);
    }

    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll();
    }

    @Transactional
    public void assignRole(Long userId, String role) {
        logger.info("Assigning role {} to user with ID {}", role, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        if (!isValidRole(role)) {
            throw new IllegalArgumentException(INVALID_ROLE + role);
        }

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }

    private boolean isValidRole(String role) {
        return Arrays.stream(Role.values()).anyMatch(r -> r.name().equals(role));
    }

    public List<User> searchUsersByName(String name) {
        logger.info("Searching users by name: {}", name);
        return userRepository.findByNameContainingIgnoreCase(name);
    }

}
