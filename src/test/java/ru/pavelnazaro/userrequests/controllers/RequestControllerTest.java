package ru.pavelnazaro.userrequests.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.pavelnazaro.userrequests.enums.RequestStatus;
import ru.pavelnazaro.userrequests.models.Request;
import ru.pavelnazaro.userrequests.models.User;
import ru.pavelnazaro.userrequests.services.RequestService;
import ru.pavelnazaro.userrequests.services.UserService;

import java.security.Principal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.pavelnazaro.userrequests.controllers.RequestController.*;

public class RequestControllerTest {

    private static final String TEST_USER = "testUser";
    private static final String TEST_REQUEST = "Test request";
    private static final String UPDATED_TEXT = "Updated text";

    @InjectMocks
    private RequestController requestController;

    @Mock
    private RequestService requestService;

    @Mock
    private UserService userService;

    @Mock
    private Principal principal;

    private Request request;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName(TEST_USER);

        request = new Request();
        request.setId(1L);
        request.setText(TEST_REQUEST);
        request.setUser(user);
    }

    @Test
    public void testCreateRequest_Success() {
        when(principal.getName()).thenReturn(TEST_USER);
        when(userService.findByName(TEST_USER)).thenReturn(user);
        when(requestService.createRequest(any(User.class), any(String.class))).thenReturn(request);

        ResponseEntity<Request> response = requestController.createRequest(TEST_REQUEST, principal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(request, response.getBody());
        verify(requestService, times(1)).createRequest(user, TEST_REQUEST);
    }

    @Test
    public void testEditRequest_Success() {
        when(principal.getName()).thenReturn(TEST_USER);
        when(requestService.editRequest(1L, UPDATED_TEXT, TEST_USER)).thenReturn(request);

        ResponseEntity<Request> response = requestController.editRequest(1L, UPDATED_TEXT, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(request, response.getBody());
        verify(requestService, times(1)).editRequest(1L, UPDATED_TEXT, TEST_USER);
    }

    @Test
    public void testSendRequest_Success() {
        when(principal.getName()).thenReturn(TEST_USER);

        ResponseEntity<String> response = requestController.sendRequest(1L, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(REQUEST_SENT_FOR_REVIEW, response.getBody());
        verify(requestService, times(1)).sendRequest(1L, TEST_USER);
    }

    @Test
    public void testGetAllRequests_Success() {
        Page<Request> requestPage = new PageImpl<>(Collections.singletonList(request));
        when(requestService.getAllRequests(any(Pageable.class), any(Boolean.class))).thenReturn(requestPage);

        ResponseEntity<Page<Request>> response = requestController.getAllRequests(false, 0, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(requestPage, response.getBody());
        verify(requestService, times(1)).getAllRequests(any(Pageable.class), any(Boolean.class));
    }

    @Test
    public void testGetRequestById_Success() {
        when(principal.getName()).thenReturn(TEST_USER);
        when(requestService.getRequestById(1L, TEST_USER)).thenReturn(request);

        ResponseEntity<Request> response = requestController.getRequestById(1L, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(request, response.getBody());
        verify(requestService, times(1)).getRequestById(1L, TEST_USER);
    }

    @Test
    public void testGetUserRequests_Success() {
        Page<Request> requestPage = new PageImpl<>(Collections.singletonList(request));
        when(principal.getName()).thenReturn(TEST_USER);
        when(userService.findByName(TEST_USER)).thenReturn(user);
        when(requestService.getUserRequests(any(User.class), any(Pageable.class), any(Boolean.class))).thenReturn(requestPage);

        ResponseEntity<Page<Request>> response = requestController.getUserRequests(false, 0, 5, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(requestPage, response.getBody());
    }

    @Test
    public void testApproveRequest_Success() {
        ResponseEntity<String> response = requestController.approveRequest(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(REQUEST_APPROVED, response.getBody());
        verify(requestService, times(1)).updateRequestStatus(1L, RequestStatus.APPROVED);
    }

    @Test
    public void testRejectRequest_Success() {
        ResponseEntity<String> response = requestController.rejectRequest(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(REQUEST_REJECTED, response.getBody());
        verify(requestService, times(1)).updateRequestStatus(1L, RequestStatus.REJECTED);
    }
}
