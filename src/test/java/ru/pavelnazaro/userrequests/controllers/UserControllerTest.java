package ru.pavelnazaro.userrequests.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.pavelnazaro.userrequests.enums.Role;
import ru.pavelnazaro.userrequests.models.User;
import ru.pavelnazaro.userrequests.services.UserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static ru.pavelnazaro.userrequests.controllers.UserController.ROLE_OPERATOR_ASSIGNED;

public class UserControllerTest {

    private static final String TEST = "test";

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName("testUser");
    }

    @Test
    public void testGetAllUsers_Success() {
        List<User> users = Arrays.asList(user);
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    public void testAssignOperatorRole_Success() {
        Long userId = 1L;

        ResponseEntity<String> response = userController.assignOperatorRole(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ROLE_OPERATOR_ASSIGNED, response.getBody());
        verify(userService, times(1)).assignRole(userId, Role.OPERATOR.name());
    }

    @Test
    public void testSearchUsersByName_Success() {
        List<User> users = Arrays.asList(user);
        when(userService.searchUsersByName(TEST)).thenReturn(users);

        ResponseEntity<List<User>> response = userController.searchUsersByName(TEST);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
        verify(userService, times(1)).searchUsersByName(TEST);
    }
}
