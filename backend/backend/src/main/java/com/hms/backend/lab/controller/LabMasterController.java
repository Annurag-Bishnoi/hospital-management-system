package com.hms.backend.lab.controller;

import com.hms.backend.lab.dto.LabTestMasterDto;
import com.hms.backend.lab.entity.LabTestMaster;
import com.hms.backend.lab.repository.LabTestMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lab-master")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LabMasterController {

    private final LabTestMasterRepository labTestMasterRepository;

    @GetMapping
    public ResponseEntity<List<LabTestMasterDto>> getAllTests() {
        List<LabTestMasterDto> tests = labTestMasterRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(tests);
    }

    @PostMapping
    public ResponseEntity<LabTestMasterDto> approveTest(@RequestBody LabTestMasterDto request) {
        if (labTestMasterRepository.existsByCielConceptId(request.getCielConceptId())) {
            throw new IllegalArgumentException("Test is already approved");
        }

        LabTestMaster test = LabTestMaster.builder()
                .cielConceptId(request.getCielConceptId())
                .testName(request.getTestName())
                .conceptClass(request.getConceptClass())
                .unitPrice(request.getUnitPrice())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        LabTestMaster saved = labTestMasterRepository.save(test);
        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabTestMasterDto> updateTest(@PathVariable Long id, @RequestBody LabTestMasterDto request) {
        LabTestMaster test = labTestMasterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if (request.getUnitPrice() != null) {
            test.setUnitPrice(request.getUnitPrice());
        }
        if (request.getActive() != null) {
            test.setActive(request.getActive());
        }

        LabTestMaster saved = labTestMasterRepository.save(test);
        return ResponseEntity.ok(toDto(saved));
    }

    private LabTestMasterDto toDto(LabTestMaster entity) {
        LabTestMasterDto dto = new LabTestMasterDto();
        dto.setId(entity.getId());
        dto.setCielConceptId(entity.getCielConceptId());
        dto.setTestName(entity.getTestName());
        dto.setConceptClass(entity.getConceptClass());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setActive(entity.getActive());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
