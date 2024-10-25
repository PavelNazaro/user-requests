package ru.pavelnazaro.userrequests.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pavelnazaro.userrequests.enums.RequestStatus;
import ru.pavelnazaro.userrequests.models.Request;
import ru.pavelnazaro.userrequests.models.User;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    Page<Request> findByUser(User user, Pageable pageable);

    Page<Request> findByStatus(RequestStatus status, Pageable pageable);

    @Query("SELECT r FROM Request r WHERE r.status=:status AND LOWER(r.user.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Request> findByUserNameContainingAndStatus(@Param("name") String name, @Param("status") RequestStatus status, Pageable pageable);

}
