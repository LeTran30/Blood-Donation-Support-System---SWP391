package com.example.blooddonationsupportsystem.service.appointment;

import com.example.blooddonationsupportsystem.dtos.request.appointment.AppointmentRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.dtos.responses.appointment.AppointmentResponse;
import com.example.blooddonationsupportsystem.models.Appointment;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.AppointmentRepository;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import com.example.blooddonationsupportsystem.service.reminder.IReminderService;
import com.example.blooddonationsupportsystem.utils.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService{
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final IReminderService reminderService;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<?> createAppointment(Integer userId, AppointmentRequest appointmentRequest) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("User not found")
                            .build()
            );
        }

        Appointment appointment = Appointment.builder()
                .user(user.get())
                .appointmentDate(appointmentRequest.getAppointmentDate())
                .status(AppointmentStatus.PENDING)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        
        return  ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.CREATED)
                        .message("Successfully created appointment")
                        .data(mapWithUserId(saved))
                        .build()
        );
    }

    @Override
    public ResponseEntity<?> getAppointmentById(Integer appointmentId) {
        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
        return appointment.map(value -> ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Successfully retrieved appointment")
                        .data(mapWithUserId(value))
                        .build()
        )).orElseGet(() -> ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .message("Appointment not found")
                        .build()
        ));
    }

    public ResponseEntity<?> getAppointmentsByUserId(Integer userId, int page, int size) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("User not found")
                            .build()
            );
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").descending());
        Page<Appointment> appointments = appointmentRepository.findAllByUserId(userId, pageable);

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Successfully retrieved appointments")
                        .data(appointments.map(this::mapWithUserId)) // map trả về dạng Page
                        .build()
        );
    }


    @Override
    public ResponseEntity<?> updateAppointmentStatus(Integer appointmentId, AppointmentStatus status) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isEmpty()) {
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Appointment not found")
                            .build()
            );
        }

        Appointment appointment = optionalAppointment.get();

        try {
            if (appointment.getStatus() == AppointmentStatus.MEDICAL_COMPLETED) {
                LocalDate completedDate = appointment.getAppointmentDate().toLocalDate();
                reminderService.createNextDonationReminder(appointment.getUser().getId(), completedDate);
            }
            appointment.setStatus(status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message("Invalid status value")
                            .build()
            );
        }

        Appointment saved = appointmentRepository.save(appointment);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Successfully updated appointment")
                        .data(mapWithUserId(saved))
                        .build()
        );
    }


    @Override
    public ResponseEntity<?> cancelAppointment(Integer appointmentId) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isEmpty()) {
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Appointment not found")
                            .build()
            );
        }
        Appointment appointment = optionalAppointment.get();
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Successfully cancelled appointment")
                        .data(mapWithUserId(appointment))
                        .build()
        );
    }
    @Override
    public ResponseEntity<?> getAppointmentsWithFilters(LocalDateTime fromDateTime, LocalDateTime toDateTime, AppointmentStatus status, Integer userId, int page, int size){
        Specification<Appointment> spec = (root, query, cb) -> cb.conjunction();

        if (fromDateTime != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("appointmentDate"), fromDateTime));
        }

        if (toDateTime != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("appointmentDate"), toDateTime));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (userId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("user").get("id"), userId));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").descending());
        Page<Appointment> appointmentsPage = appointmentRepository.findAll(spec, pageable);

        List<AppointmentResponse> appointmentResponses = appointmentsPage
                .getContent()
                .stream()
                .map(this::mapWithUserId)
                .toList();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", appointmentResponses);

        Map<String, Object> pageInfo = new LinkedHashMap<>();
        pageInfo.put("size", appointmentsPage.getSize());
        pageInfo.put("number", appointmentsPage.getNumber());
        pageInfo.put("totalElements", appointmentsPage.getTotalElements());
        pageInfo.put("totalPages", appointmentsPage.getTotalPages());

        data.put("page", pageInfo);

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Appointments retrieved successfully")
                        .data(data)
                        .build()
        );
    }

        private AppointmentResponse mapWithUserId(Appointment appointment) {
        AppointmentResponse response = modelMapper.map(appointment, AppointmentResponse.class);
        response.setUserId(appointment.getUser().getId());
        return response;
    }

}
