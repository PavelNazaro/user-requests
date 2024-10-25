package ru.pavelnazaro.userrequests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.pavelnazaro.userrequests.enums.RequestStatus;
import ru.pavelnazaro.userrequests.enums.Role;
import ru.pavelnazaro.userrequests.models.Request;
import ru.pavelnazaro.userrequests.models.User;
import ru.pavelnazaro.userrequests.repositories.RequestRepository;
import ru.pavelnazaro.userrequests.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.pavelnazaro.userrequests.services.RequestService.ONLY_REQUESTS_IN_DRAFT_STATUS_CAN_BE_EDITED;

class RequestServiceTest {

    private static final String TEST_USER = "testUser";
    private static final String UPDATED_REQUEST = "Updated request";

    @InjectMocks
    private RequestService requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private Request request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName(TEST_USER);
        user.setRoles(Set.of(Role.USER.name()));

        request = new Request();
        request.setId(1L);
        request.setUser(user);
        request.setText("Test request");
        request.setStatus(RequestStatus.DRAFT);
        request.setCreatedDate(LocalDateTime.now());
    }

    @Test
    void createRequest_ShouldSaveRequest() {
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        Request createdRequest = requestService.createRequest(user, "Test request");

        assertNotNull(createdRequest);
        assertEquals(request.getText(), createdRequest.getText());
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void editRequest_ShouldUpdateRequest_WhenStatusIsDraft() {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        Request editedRequest = requestService.editRequest(1L, UPDATED_REQUEST, TEST_USER);

        assertNotNull(editedRequest);
        assertEquals(UPDATED_REQUEST, editedRequest.getText());
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void editRequest_ShouldThrowException_WhenStatusIsNotDraft() {
        request.setStatus(RequestStatus.SENT);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            requestService.editRequest(1L, UPDATED_REQUEST, TEST_USER);
        });

        assertEquals(ONLY_REQUESTS_IN_DRAFT_STATUS_CAN_BE_EDITED, exception.getMessage());
    }

    @Test
    void sendRequest_ShouldChangeStatusToSent() {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        requestService.sendRequest(1L, TEST_USER);

        assertEquals(RequestStatus.SENT, request.getStatus());
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void getRequestById_ShouldReturnRequest_WhenUserIsAuthorized() {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(userRepository.findByName(TEST_USER)).thenReturn(user);

        Request foundRequest = requestService.getRequestById(1L, TEST_USER);

        assertNotNull(foundRequest);
        assertEquals(request.getId(), foundRequest.getId());
    }

}
