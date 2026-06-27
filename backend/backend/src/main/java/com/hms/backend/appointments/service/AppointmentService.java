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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

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

        Appointment appointment = Appointment.builder()
                .appointmentNumber(generateAppointmentNumber())
                .tokenNumber(generateTokenNumber())
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .reasonForVisit(request.getReasonForVisit())
                .status("SCHEDULED")
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

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

    private String generateAppointmentNumber() {
        return "APT-" + System.currentTimeMillis();
    }

    private String generateTokenNumber() {
        return "TK-" + (System.currentTimeMillis() % 10000);
    }
}