package com.hms.backend.appointments.service;

import com.hms.backend.appointments.dto.AppointmentCreateRequest;
import com.hms.backend.appointments.dto.AppointmentDetailsResponse;
import com.hms.backend.appointments.dto.AppointmentResponse;
import com.hms.backend.appointments.entity.Appointment;
import com.hms.backend.appointments.repository.AppointmentRepository;
import com.hms.backend.doctors.entity.Doctor;
import com.hms.backend.doctors.repository.DoctorRepository;
import com.hms.backend.patients.entity.Patient;
import com.hms.backend.patients.repository.PatientRepository;
import com.hms.backend.billing.service.BillingService;
import com.hms.backend.billing.dto.BillRequest;
import com.hms.backend.billing.dto.BillItemRequest;
import com.hms.backend.billing.dto.PaymentRequest;
import com.hms.backend.billing.dto.BillResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final BillingService billingService;

    public AppointmentResponse bookAppointment(AppointmentCreateRequest request) {

        Patient patient = patientRepository
                .findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository
                .findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        boolean slotBooked =
                appointmentRepository.existsByDoctorDoctorIdAndAppointmentDateAndAppointmentTime(
                        doctor.getDoctorId(),
                        request.getAppointmentDate(),
                        request.getAppointmentTime()
                );

        if (slotBooked) {
            throw new RuntimeException("Selected appointment slot is already booked");
        }

        Double fee = doctor.getConsultationFee() != null ? doctor.getConsultationFee() : 0.0;
        
        Appointment appointment = Appointment.builder()
                .appointmentNumber(generateAppointmentNumber())
                .tokenNumber(generateTokenNumber())
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .reasonForVisit(request.getReasonForVisit())
                .consultationType(request.getConsultationType())
                .notes(request.getNotes())
                .consultationFee(fee)
                .paymentStatus("PENDING")
                .status("SCHEDULED")
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Generate the consultation bill
        BillRequest billReq = new BillRequest();
        billReq.setPatientId(patient.getPatientId());
        billReq.setPatientName(patient.getUser() != null ? patient.getUser().getFullName() : "Unknown");
        billReq.setDepartment("CONSULTATION");
        billReq.setGeneratedBy("System (Auto-generated)");

        BillItemRequest itemReq = new BillItemRequest();
        itemReq.setDescription("Consultation Fee - Dr. " + (doctor.getUser() != null ? doctor.getUser().getFullName() : "Unknown"));
        itemReq.setQuantity(1);
        itemReq.setUnitPrice(BigDecimal.valueOf(fee));
        billReq.setItems(Collections.singletonList(itemReq));

        BillResponse billResponse = billingService.generateBill(billReq);
        savedAppointment.setBillId(billResponse.getId());
        appointmentRepository.save(savedAppointment);

        return AppointmentResponse.builder()
                .appointmentId(savedAppointment.getAppointmentId())
                .appointmentNumber(savedAppointment.getAppointmentNumber())
                .tokenNumber(savedAppointment.getTokenNumber())
                .status(savedAppointment.getStatus())
                .message("Appointment booked successfully")
                .build();
    }

    @Transactional(readOnly = true)
    public List<AppointmentDetailsResponse> getAllAppointments() {
        return appointmentRepository.findAllAppointmentsWithDetails()
                .stream()
                .map(this::mapToDetailsResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AppointmentDetailsResponse getAppointmentById(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        return mapToDetailsResponse(appointment);
    }

    public AppointmentResponse cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        appointment.setStatus("CANCELLED");

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return AppointmentResponse.builder()
                .appointmentId(updatedAppointment.getAppointmentId())
                .appointmentNumber(updatedAppointment.getAppointmentNumber())
                .tokenNumber(updatedAppointment.getTokenNumber())
                .status(updatedAppointment.getStatus())
                .message("Appointment cancelled successfully")
                .build();
    }

    public AppointmentResponse rescheduleAppointment(Long appointmentId, AppointmentCreateRequest request) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        boolean slotBooked = appointmentRepository
                .existsByDoctorDoctorIdAndAppointmentDateAndAppointmentTimeAndAppointmentIdNot(
                        appointment.getDoctor().getDoctorId(),
                        request.getAppointmentDate(),
                        request.getAppointmentTime(),
                        appointmentId
                );

        if (slotBooked) {
            throw new RuntimeException("Selected appointment slot is already booked");
        }

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setStatus("SCHEDULED");

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return AppointmentResponse.builder()
                .appointmentId(updatedAppointment.getAppointmentId())
                .appointmentNumber(updatedAppointment.getAppointmentNumber())
                .tokenNumber(updatedAppointment.getTokenNumber())
                .status(updatedAppointment.getStatus())
                .message("Appointment rescheduled successfully")
                .build();
    }

    @Transactional
    public AppointmentResponse updateStatus(Long appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));
        appointment.setStatus(status);
        Appointment updated = appointmentRepository.save(appointment);
        return AppointmentResponse.builder()
                .appointmentId(updated.getAppointmentId())
                .appointmentNumber(updated.getAppointmentNumber())
                .tokenNumber(updated.getTokenNumber())
                .status(updated.getStatus())
                .message("Status updated to " + status)
                .build();
    }

    @Transactional
    public AppointmentResponse markPaymentPaid(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));
        
        appointment.setPaymentStatus("PAID");
        
        // Auto-transition to Nurse queue
        if ("SCHEDULED".equals(appointment.getStatus()) || "CONFIRMED".equals(appointment.getStatus())) {
            appointment.setStatus("WAITING_FOR_VITALS");
        }
        
        Appointment updated = appointmentRepository.save(appointment);

        if (updated.getBillId() != null) {
            PaymentRequest payReq = new PaymentRequest();
            payReq.setProcessedBy("Receptionist");
            payReq.setDiscountAmount(BigDecimal.ZERO);
            payReq.setTaxPercentage(BigDecimal.ZERO);
            payReq.setInsuranceCoverageAmount(BigDecimal.ZERO);
            try {
                billingService.processPayment(updated.getBillId(), payReq);
            } catch (Exception e) {
                System.err.println("Warning: Could not process bill payment automatically: " + e.getMessage());
            }
        }

        return AppointmentResponse.builder()
                .appointmentId(updated.getAppointmentId())
                .appointmentNumber(updated.getAppointmentNumber())
                .tokenNumber(updated.getTokenNumber())
                .status(updated.getStatus())
                .message("Payment marked as PAID successfully")
                .build();
    }

    @Transactional(readOnly = true)
    public List<AppointmentDetailsResponse> getReadyAppointments() {
        return appointmentRepository.findAllAppointmentsWithDetails().stream()
                .filter(a -> "READY_FOR_DOCTOR".equals(a.getStatus()))
                .map(this::mapToDetailsResponse)
                .toList();
    }

    private AppointmentDetailsResponse mapToDetailsResponse(Appointment appointment) {

        String patientName = "N/A";
        String doctorName = "N/A";

        if (appointment.getPatient() != null &&
                appointment.getPatient().getUser() != null) {
            patientName = appointment.getPatient().getUser().getFullName();
        }

        if (appointment.getDoctor() != null &&
                appointment.getDoctor().getUser() != null) {
            doctorName = appointment.getDoctor().getUser().getFullName();
        }

        return AppointmentDetailsResponse.builder()
                .appointmentId(appointment.getAppointmentId())
                .appointmentNumber(appointment.getAppointmentNumber())
                .tokenNumber(appointment.getTokenNumber())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .reasonForVisit(appointment.getReasonForVisit())
                .consultationType(appointment.getConsultationType())
                .consultationFee(appointment.getConsultationFee())
                .paymentStatus(appointment.getPaymentStatus())
                .notes(appointment.getNotes())
                .status(appointment.getStatus())

                .patientId(appointment.getPatient() != null
                        ? appointment.getPatient().getPatientId()
                        : null)
                .patientName(patientName)

                .doctorId(appointment.getDoctor() != null
                        ? appointment.getDoctor().getDoctorId()
                        : null)
                .doctorName(doctorName)

                .build();
    }


    // Add this method inside your AppointmentService class
    @Transactional(readOnly = true)
    public List<AppointmentDetailsResponse> getAppointmentsForDoctor(
            Long doctorId,
            LocalDate date,
            String status,
            Boolean today
    ) {
        // If 'today' is true, override the date parameter with the current date
        if (Boolean.TRUE.equals(today)) {
            date = LocalDate.now();
        }

        // Fetch and map the results
        return appointmentRepository.findDoctorAppointmentsWithFilters(doctorId, date, status)
                .stream()
                .map(this::mapToDetailsResponse)
                .toList();
    }



    // Add this method inside your AppointmentService class
    @Transactional(readOnly = true)
    public List<AppointmentDetailsResponse> getAppointmentsForPatient(
            Long patientId,
            LocalDate date,
            String status,
            Boolean today
    ) {
        // If 'today' is true, override the date parameter with the current date
        if (Boolean.TRUE.equals(today)) {
            date = LocalDate.now();
        }

        // Fetch and map the results
        return appointmentRepository.findPatientAppointmentsWithFilters(patientId, date, status)
                .stream()
                .map(this::mapToDetailsResponse)
                .toList();
    }

    private String generateAppointmentNumber() {
        return "APT-" + System.currentTimeMillis();
    }

    private String generateTokenNumber() {
        return "TK-" + (System.currentTimeMillis() % 10000);
    }
}