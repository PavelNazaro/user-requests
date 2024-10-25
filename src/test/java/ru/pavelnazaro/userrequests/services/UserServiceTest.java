package ru.pavelnazaro.userrequests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.pavelnazaro.userrequests.enums.Role;
import ru.pavelnazaro.userrequests.models.User;
import ru.pavelnazaro.userrequests.repositories.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static ru.pavelnazaro.userrequests.services.UserService.USER_NOT_FOUND;

public class UserServiceTest {

    private static final String JOHN = "John";
    private static final String JOHN_DOE = "John Doe";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName(JOHN_DOE);
        user.setRoles(new HashSet<>());
    }

    @Test
    public void testFindByName() {
        when(userRepository.findByName(anyString())).thenReturn(user);

        User foundUser = userService.findByName(JOHN_DOE);

        assertNotNull(foundUser);
        assertEquals(user.getName(), foundUser.getName());
        verify(userRepository, times(1)).findByName(JOHN_DOE);
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> foundUsers = userService.getAllUsers();

        assertNotNull(foundUsers);
        assertEquals(1, foundUsers.size());
        assertEquals(user.getName(), foundUsers.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testAssignRole() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.assignRole(1L, Role.USER.name());

        assertTrue(user.getRoles().contains(Role.USER.name()));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testSearchUsersByName() {
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(users);

        List<User> foundUsers = userService.searchUsersByName(JOHN);

        assertNotNull(foundUsers);
        assertEquals(1, foundUsers.size());
        assertEquals(user.getName(), foundUsers.get(0).getName());
        verify(userRepository, times(1)).findByNameContainingIgnoreCase(JOHN);
    }

    @Test
    public void testAssignRole_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.assignRole(1L, Role.USER.name());
        });

        assertEquals(USER_NOT_FOUND, exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any());
    }

}
