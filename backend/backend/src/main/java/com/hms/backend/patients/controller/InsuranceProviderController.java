package com.hms.backend.patients.controller;

import com.hms.backend.patients.entity.InsuranceProvider;
import com.hms.backend.patients.repository.InsuranceProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/insurance-providers")
@RequiredArgsConstructor
public class InsuranceProviderController {

    private final InsuranceProviderRepository insuranceProviderRepository;

    @GetMapping
    public ResponseEntity<List<InsuranceProvider>> getAllProviders() {
        return ResponseEntity.ok(insuranceProviderRepository.findAll());
    }
}
