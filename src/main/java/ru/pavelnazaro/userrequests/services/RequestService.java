package ru.pavelnazaro.userrequests.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pavelnazaro.userrequests.enums.RequestStatus;
import ru.pavelnazaro.userrequests.enums.Role;
import ru.pavelnazaro.userrequests.exceptions.RequestNotFoundException;
import ru.pavelnazaro.userrequests.models.Request;
import ru.pavelnazaro.userrequests.models.User;
import ru.pavelnazaro.userrequests.repositories.RequestRepository;
import ru.pavelnazaro.userrequests.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RequestService {

    private static final Logger logger = LoggerFactory.getLogger(RequestService.class);

    private static final String REQUEST_NOT_FOUND = "Request not found";
    private static final String CREATED_DATE = "createdDate";
    private static final String ACCESS_DENIED = "Access denied";
    private static final String REQUEST_STATUS_IS_NOT_SENT = "Request status is not SENT";
    private static final String USER_IS_NOT_AUTHORIZED_TO_SEND_THIS_REQUEST = "User is not authorized to send this request";
    private static final String ONLY_REQUESTS_IN_DRAFT_STATUS_CAN_BE_SENT = "Only requests in DRAFT status can be sent";
    protected static final String ONLY_REQUESTS_IN_DRAFT_STATUS_CAN_BE_EDITED = "Only requests in DRAFT status can be edited";
    private static final String ONLY_REQUESTS_IN_SENT_STATUS_CAN_BE_VIEWED = "Only requests in SENT status can be viewed";

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    public Request createRequest(User user, String text) {
        logger.info("Creating request for user: {}", user.getName());
        Request request = new Request();
        request.setUser(user);
        request.setText(text);
        request.setStatus(RequestStatus.DRAFT);
        request.setCreatedDate(LocalDateTime.now());
        return requestRepository.save(request);
    }

    @Transactional
    public Request editRequest(Long id, String newText, String username) {
        logger.info("Editing request with id: {} by user: {}", id, username);
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Request not found: {}", id);
                    return new RequestNotFoundException(REQUEST_NOT_FOUND);
                });

        if (!request.getUser().getName().equals(username)) {
            logger.warn("User {} is not authorized to edit this request.", username);
            throw new RequestNotFoundException(USER_IS_NOT_AUTHORIZED_TO_SEND_THIS_REQUEST);
        }

        if (request.getStatus() == RequestStatus.DRAFT) {
            request.setText(newText);
            logger.info("Request with id: {} updated.", id);
            return requestRepository.save(request);
        }

        logger.error("Attempt to edit request with id: {} that is not in DRAFT status.", id);
        throw new RequestNotFoundException(ONLY_REQUESTS_IN_DRAFT_STATUS_CAN_BE_EDITED);
    }

    public void sendRequest(Long requestId, String username) {
        logger.info("Sending request with id: {} by user: {}", requestId, username);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    logger.error(REQUEST_NOT_FOUND + " with id: {}", requestId);
                    return new RequestNotFoundException(REQUEST_NOT_FOUND);
                });

        if (!request.getUser().getName().equals(username)) {
            logger.error("User is not authorised to send this request, id: {}", requestId);
            throw new RuntimeException(USER_IS_NOT_AUTHORIZED_TO_SEND_THIS_REQUEST);
        }

        if (request.getStatus() != RequestStatus.DRAFT) {
            logger.error("Request status is not DRAFT for request id: {}", requestId);
            throw new RuntimeException(ONLY_REQUESTS_IN_DRAFT_STATUS_CAN_BE_SENT);
        }

        request.setStatus(RequestStatus.SENT);
        requestRepository.save(request);
        logger.info("Request with id: {} has been sent", requestId);
    }

    public Page<Request> getAllRequests(Pageable pageable, Boolean isDirectionDesc) {
        Sort sortOrder = getSortOrder(isDirectionDesc);

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
        return requestRepository.findByStatus(RequestStatus.SENT, sortedPageable);
    }

    public Request getRequestById(Long requestId, String username) {
        Optional<Request> requestOptional = requestRepository.findById(requestId);
        Request request = requestOptional.orElseThrow(() -> new RuntimeException(REQUEST_NOT_FOUND));
        User user = userRepository.findByName(username);

        if (user.getRoles().contains(Role.OPERATOR.name())) {
            if (request.getStatus() != RequestStatus.SENT) {
                logger.error("Request status is not {} for request Id: {}", RequestStatus.SENT, requestId);
                throw new RuntimeException(ONLY_REQUESTS_IN_SENT_STATUS_CAN_BE_VIEWED);
            }

            String text = request.getText();
            if (text != null) {
                request.setText(String.join("-", text.split("")));
            }
            return request;
        }

        if (request.getUser().equals(user)) {
            return request;
        }

        throw new RuntimeException(ACCESS_DENIED);
    }

    public Page<Request> getUserRequests(User user, Pageable pageable, Boolean isDirectionDesc) {
        Sort sortOrder = getSortOrder(isDirectionDesc);

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
        return requestRepository.findByUser(user, sortedPageable);
    }

    public Page<Request> getRequestsByUserNameAndStatus(String username, Pageable pageable, boolean isDirectionDesc) {
        Sort sortOrder = getSortOrder(isDirectionDesc);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
        return requestRepository.findByUserNameContainingAndStatus(username, RequestStatus.SENT, sortedPageable);
    }

    private static Sort getSortOrder(Boolean isDirectionDesc) {
        return (isDirectionDesc != null && isDirectionDesc)
                ? Sort.by(CREATED_DATE).descending()
                : Sort.by(CREATED_DATE).ascending();
    }

    @Transactional
    public void updateRequestStatus(Long requestId, RequestStatus status) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException(REQUEST_NOT_FOUND));

        if (request.getStatus() != RequestStatus.SENT) {
            throw new RuntimeException(REQUEST_STATUS_IS_NOT_SENT);
        }

        request.setStatus(status);
        requestRepository.save(request);
    }

}
