package com.veridian.it_support_agent.repository;

import com.veridian.it_support_agent.entity.EmployeeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRequestRepository
        extends JpaRepository<EmployeeRequest, Long> {

    Optional<EmployeeRequest> findByRequestId(String requestId);

    Optional<EmployeeRequest> findByEmail(String email);
}