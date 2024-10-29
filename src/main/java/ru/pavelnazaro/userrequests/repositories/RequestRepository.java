package ru.pavelnazaro.userrequests.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pavelnazaro.userrequests.enums.RequestStatus;
import ru.pavelnazaro.userrequests.models.Request;
import ru.pavelnazaro.userrequests.models.User;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    Page<Request> findByUser(User user, Pageable pageable);

    Page<Request> findByStatus(RequestStatus status, Pageable pageable);

    Page<Request> findByUser_NameContainingIgnoreCaseAndStatus(String username, RequestStatus status, Pageable pageable);

}
