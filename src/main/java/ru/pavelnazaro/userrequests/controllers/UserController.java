package ru.pavelnazaro.userrequests.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pavelnazaro.userrequests.enums.Role;
import ru.pavelnazaro.userrequests.models.User;
import ru.pavelnazaro.userrequests.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    protected static final String ROLE_OPERATOR_ASSIGNED = String.format("Role %s assigned", Role.OPERATOR.name());

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/{id}/assign-operator")
    public ResponseEntity<String> assignOperatorRole(@PathVariable Long id) {
        userService.assignRole(id, Role.OPERATOR.name());
        return new ResponseEntity<>(ROLE_OPERATOR_ASSIGNED, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByName(@RequestParam String name) {
        List<User> users = userService.searchUsersByName(name);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}
