package ru.pavelnazaro.userrequests.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.pavelnazaro.userrequests.enums.RequestStatus;
import ru.pavelnazaro.userrequests.models.Request;
import ru.pavelnazaro.userrequests.models.User;
import ru.pavelnazaro.userrequests.services.RequestService;
import ru.pavelnazaro.userrequests.services.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    protected static final String REQUEST_APPROVED = "Request approved";
    protected static final String REQUEST_REJECTED = "Request rejected";
    protected static final String REQUEST_SENT_FOR_REVIEW = "Request sent for review";

    private final RequestService requestService;
    private final UserService userService;

    @Autowired
    public RequestController(RequestService requestService, UserService userService) {
        this.requestService = requestService;
        this.userService = userService;
    }

    @PostMapping
    @Secured("ROLE_USER")
    public ResponseEntity<Request> createRequest(@RequestBody String text, Principal principal) {
        User user = userService.findByName(principal.getName());
        Request request = requestService.createRequest(user, text);
        return new ResponseEntity<>(request, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/edit")
    @Secured("ROLE_USER")
    public ResponseEntity<Request> editRequest(@PathVariable Long id, @RequestBody String newText, Principal principal) {
        Request updatedRequest = requestService.editRequest(id, newText, principal.getName());
        return new ResponseEntity<>(updatedRequest, HttpStatus.OK);
    }

    @PutMapping("/{id}/send")
    @Secured("ROLE_USER")
    public ResponseEntity<String> sendRequest(@PathVariable Long id, Principal principal) {
        requestService.sendRequest(id, principal.getName());
        return new ResponseEntity<>(REQUEST_SENT_FOR_REVIEW, HttpStatus.OK);
    }

    @GetMapping
    @Secured("ROLE_OPERATOR")
    public ResponseEntity<Page<Request>> getAllRequests(
            @RequestParam(required = false, defaultValue = "false") Boolean isDirectionDesc,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Request> requests = requestService.getAllRequests(pageable, isDirectionDesc);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_USER", "ROLE_OPERATOR"})
    public ResponseEntity<Request> getRequestById(@PathVariable Long id, Principal principal) {
        Request request = requestService.getRequestById(id, principal.getName());
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @GetMapping("/user")
    @Secured("ROLE_USER")
    public ResponseEntity<Page<Request>> getUserRequests(
            @RequestParam(required = false, defaultValue = "false") Boolean isDirectionDesc,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Principal principal) {

        Pageable pageable = PageRequest.of(page, size);
        User user = userService.findByName(principal.getName());
        Page<Request> requests = requestService.getUserRequests(user, pageable, isDirectionDesc);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @GetMapping("/searchByUserName")
    @Secured("ROLE_OPERATOR")
    public ResponseEntity<Page<Request>> getRequestsByUserName(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false, defaultValue = "false") boolean isDirectionDesc) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Request> requests = requestService.getRequestsByUserNameAndStatus(username, pageable, isDirectionDesc);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @PutMapping("/{id}/approve")
    @Secured("ROLE_OPERATOR")
    public ResponseEntity<String> approveRequest(@PathVariable Long id) {
        requestService.updateRequestStatus(id, RequestStatus.APPROVED);
        return new ResponseEntity<>(REQUEST_APPROVED, HttpStatus.OK);
    }

    @PutMapping("/{id}/reject")
    @Secured("ROLE_OPERATOR")
    public ResponseEntity<String> rejectRequest(@PathVariable Long id) {
        requestService.updateRequestStatus(id, RequestStatus.REJECTED);
        return new ResponseEntity<>(REQUEST_REJECTED, HttpStatus.OK);
    }

}
