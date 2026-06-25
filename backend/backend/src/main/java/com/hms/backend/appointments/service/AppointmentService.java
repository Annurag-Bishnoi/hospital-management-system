package com.hms.backend.appointments.service;

import com.hms.backend.appointments.dto.AppointmentCreateRequest;
import com.hms.backend.appointments.dto.AppointmentResponse;
import com.hms.backend.appointments.entity.Appointment;
import com.hms.backend.appointments.repository.AppointmentRepository;
import com.hms.backend.doctors.entity.Doctor;
import com.hms.backend.doctors.repository.DoctorRepository;
import com.hms.backend.patients.entity.Patient;
import com.hms.backend.patients.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;



    public AppointmentResponse bookAppointment(
            AppointmentCreateRequest request
    ) {

        Patient patient = patientRepository
                .findById(request.getPatientId())
                .orElseThrow(() ->
                        new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository
                .findById(request.getDoctorId())
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found"));

        boolean slotBooked =
                appointmentRepository
                        .existsByDoctorDoctorIdAndAppointmentDateAndAppointmentTime(
                                doctor.getDoctorId(),
                                request.getAppointmentDate(),
                                request.getAppointmentTime()
                        );

        if (slotBooked) {
            throw new RuntimeException(
                    "Selected appointment slot is already booked"
            );
        }

        Appointment appointment =
                Appointment.builder()
                        .appointmentNumber(generateAppointmentNumber())
                        .tokenNumber(generateTokenNumber())
                        .patient(patient)
                        .doctor(doctor)
                        .appointmentDate(request.getAppointmentDate())
                        .appointmentTime(request.getAppointmentTime())
                        .reasonForVisit(request.getReasonForVisit())
                        .status("SCHEDULED")
                        .build();

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        return AppointmentResponse.builder()
                .appointmentId(
                        savedAppointment.getAppointmentId()
                )
                .appointmentNumber(
                        savedAppointment.getAppointmentNumber()
                )
                .tokenNumber(
                        savedAppointment.getTokenNumber()
                )
                .status(
                        savedAppointment.getStatus()
                )
                .message(
                        "Appointment booked successfully"
                )
                .build();
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));
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

        // Check if the newly requested slot is already taken by this doctor
        boolean slotBooked = appointmentRepository
                .existsByDoctorDoctorIdAndAppointmentDateAndAppointmentTime(
                        appointment.getDoctor().getDoctorId(),
                        request.getAppointmentDate(),
                        request.getAppointmentTime()
                );

        if (slotBooked) {
            throw new RuntimeException("Selected appointment slot is already booked");
        }

        // Update the date, time, and set status back to SCHEDULED
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

    private String generateAppointmentNumber() {
        return "APT-" + System.currentTimeMillis();
    }

    private String generateTokenNumber() {
        return "TK-" + (System.currentTimeMillis() % 10000);
    }


}