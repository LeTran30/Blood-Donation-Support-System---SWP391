package com.example.blooddonationsupportsystem.service.appointment;

import com.example.blooddonationsupportsystem.dtos.request.appointment.AppointmentRequest;
import com.example.blooddonationsupportsystem.dtos.responses.appointment.AppointmentResponse;
import com.example.blooddonationsupportsystem.dtos.responses.appointment.ListAppointmentResponse;
import com.example.blooddonationsupportsystem.models.Appointment;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.AppointmentRepository;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import com.example.blooddonationsupportsystem.utils.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService{
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<AppointmentResponse> createAppointment(Integer userId, AppointmentRequest appointmentRequest) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AppointmentResponse.builder().message("User not found").build());
        }
    
        Appointment appointment = Appointment.builder()
                .user(user.get())
                .appointmentDate(appointmentRequest.getAppointmentDate())
                .status(AppointmentStatus.PENDING)
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        AppointmentResponse appointmentResponse = modelMapper.map(saved, AppointmentResponse.class);
        appointmentResponse.setUserId(userId);
        appointmentResponse.setMessage("Appointment created successfully");
        return new ResponseEntity<>(appointmentResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<AppointmentResponse> getAppointmentById(Integer appointmentId) {
        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
        if (appointment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AppointmentResponse.builder().message("Appointment not found").build());
        }
        AppointmentResponse appointmentResponse = modelMapper.map(appointment.get(), AppointmentResponse.class);
        appointmentResponse.setMessage("Appointment retrieved successfully");
        return new ResponseEntity<>(appointmentResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ListAppointmentResponse> getAppointmentsByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ListAppointmentResponse.builder().message("User not found").build());
        }

        List<Appointment> appointments = appointmentRepository.findAllByUserId(userId);
        List<AppointmentResponse> mapped = appointments.stream()
                .map(a -> modelMapper.map(a, AppointmentResponse.class))
                .toList();

        ListAppointmentResponse listAppointmentResponse = new ListAppointmentResponse();
        listAppointmentResponse.setAppointments(mapped);
        listAppointmentResponse.setMessage("Appointment retrieved successfully");
        return new ResponseEntity<>(listAppointmentResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(Integer appointmentId, AppointmentStatus status) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AppointmentResponse.builder().message("Appointment not found").build());
        }

        Appointment appointment = optionalAppointment.get();

        try {
            appointment.setStatus(status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AppointmentResponse.builder().message("Invalid appointment status").build());
        }

        Appointment saved = appointmentRepository.save(appointment);
        AppointmentResponse appointmentResponse = modelMapper.map(saved, AppointmentResponse.class);
        appointmentResponse.setMessage("Appointment updated successfully");
        return new ResponseEntity<>(appointmentResponse, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<AppointmentResponse> cancelAppointment(Integer appointmentId) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AppointmentResponse.builder().message("Appointment not found").build());
        }
        Appointment appointment = optionalAppointment.get();
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        AppointmentResponse appointmentResponse = modelMapper.map(appointment, AppointmentResponse.class);
        appointmentResponse.setMessage("Appointment cancelled successfully");
        return new ResponseEntity<>(appointmentResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ListAppointmentResponse> getUpcomingAppointments(LocalDateTime fromDateTime) {
        List<Appointment> appointments = appointmentRepository.findAllByAppointmentDateAfter(fromDateTime);
        List<AppointmentResponse> mapped = appointments.stream()
                .map(a -> modelMapper.map(a, AppointmentResponse.class))
                .toList();

        ListAppointmentResponse listAppointmentResponse = new ListAppointmentResponse();
        listAppointmentResponse.setAppointments(mapped);
        listAppointmentResponse.setMessage("Upcoming appointments retrieved successfully");
        return new ResponseEntity<>(listAppointmentResponse, HttpStatus.OK);
    }

}
