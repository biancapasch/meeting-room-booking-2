package com.biancapasch.meetingroombooking2.domain.controller;

import com.biancapasch.meetingroombooking2.dtos.BookingRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.BookingResponseDTO;
import com.biancapasch.meetingroombooking2.service.BookingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO request) {
        BookingResponseDTO response = bookingService.createBooking(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getBookingList() {
        List<BookingResponseDTO> bookings = bookingService.getAllBookings();

        return ResponseEntity.ok(bookings);
    }
}
