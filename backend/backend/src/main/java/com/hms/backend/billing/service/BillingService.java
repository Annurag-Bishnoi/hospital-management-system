package com.hms.backend.billing.service;

import com.hms.backend.billing.dto.*;
import com.hms.backend.billing.entity.Bill;
import com.hms.backend.billing.entity.BillItem;
import com.hms.backend.billing.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillRepository billRepository;

    @Transactional
    public BillResponse generateBill(BillRequest request) {
        Bill bill = Bill.builder()
                .patientId(request.getPatientId())
                .patientName(request.getPatientName())
                .department(request.getDepartment())
                .status("UNPAID")
                .generatedBy(request.getGeneratedBy())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (BillItemRequest itemReq : request.getItems()) {
            BigDecimal itemTotal = itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            total = total.add(itemTotal);

            BillItem item = BillItem.builder()
                    .bill(bill)
                    .description(itemReq.getDescription())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .totalPrice(itemTotal)
                    .build();
            bill.getItems().add(item);
        }

        bill.setTotalAmount(total);

        Bill savedBill = billRepository.save(bill);
        return mapToResponse(savedBill);
    }

    @Transactional
    public BillResponse processPayment(Long billId, PaymentRequest request) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found"));

        if ("PAID".equals(bill.getStatus())) {
            throw new IllegalStateException("Bill is already paid");
        }

        BigDecimal subtotal = bill.getTotalAmount();
        BigDecimal discount = request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal taxableAmount = subtotal.subtract(discount);
        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            taxableAmount = BigDecimal.ZERO;
        }

        BigDecimal taxPct = request.getTaxPercentage() != null ? request.getTaxPercentage() : BigDecimal.ZERO;
        BigDecimal tax = taxableAmount.multiply(taxPct).divide(BigDecimal.valueOf(100));
        
        BigDecimal grossTotal = taxableAmount.add(tax);
        BigDecimal insurance = request.getInsuranceCoverageAmount() != null ? request.getInsuranceCoverageAmount() : BigDecimal.ZERO;
        
        BigDecimal payable = grossTotal.subtract(insurance);
        if (payable.compareTo(BigDecimal.ZERO) < 0) {
            payable = BigDecimal.ZERO;
        }

        bill.setTaxPercentage(taxPct);
        bill.setDiscountAmount(discount);
        bill.setInsuranceCoverageAmount(insurance);
        bill.setPatientPayableAmount(payable);

        bill.setStatus("PAID");
        bill.setPaidAt(LocalDateTime.now());
        bill.setProcessedBy(request.getProcessedBy());

        Bill savedBill = billRepository.save(bill);
        return mapToResponse(savedBill);
    }

    public List<BillResponse> getAllBills(String status) {
        List<Bill> bills = status != null 
            ? billRepository.findByStatusOrderByCreatedAtDesc(status) 
            : billRepository.findAllByOrderByCreatedAtDesc();

        return bills.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BillResponse> getBillsByPatient(Long patientId) {
        return billRepository.findByPatientId(patientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BillResponse mapToResponse(Bill bill) {
        BillResponse response = new BillResponse();
        response.setId(bill.getId());
        response.setPatientId(bill.getPatientId());
        response.setPatientName(bill.getPatientName());
        response.setDepartment(bill.getDepartment());
        response.setStatus(bill.getStatus());
        response.setTotalAmount(bill.getTotalAmount());
        response.setTaxPercentage(bill.getTaxPercentage());
        response.setDiscountAmount(bill.getDiscountAmount());
        response.setInsuranceCoverageAmount(bill.getInsuranceCoverageAmount());
        response.setPatientPayableAmount(bill.getPatientPayableAmount());
        response.setCreatedAt(bill.getCreatedAt());
        response.setPaidAt(bill.getPaidAt());
        response.setGeneratedBy(bill.getGeneratedBy());
        response.setProcessedBy(bill.getProcessedBy());

        List<BillItemResponse> itemResponses = bill.getItems().stream().map(item -> {
            BillItemResponse r = new BillItemResponse();
            r.setId(item.getId());
            r.setDescription(item.getDescription());
            r.setQuantity(item.getQuantity());
            r.setUnitPrice(item.getUnitPrice());
            r.setTotalPrice(item.getTotalPrice());
            return r;
        }).collect(Collectors.toList());

        response.setItems(itemResponses);
        return response;
    }
}
