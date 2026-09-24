package com.veridian.it_support_agent.controller;

import com.veridian.it_support_agent.entity.EmployeeRequest;
import com.veridian.it_support_agent.repository.EmployeeRequestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "*")
public class EmployeeRequestController {

    private final EmployeeRequestRepository employeeRequestRepository;

    public EmployeeRequestController(
            EmployeeRequestRepository employeeRequestRepository) {
        this.employeeRequestRepository = employeeRequestRepository;
    }

    @GetMapping
    public List<EmployeeRequest> getAllRequests() {
        return employeeRequestRepository.findAll();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<EmployeeRequest> getRequestById(
            @PathVariable String requestId) {

        return employeeRequestRepository.findByRequestId(requestId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}