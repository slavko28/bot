package com.booking.service.impl;

import com.booking.model.Booking;
import com.booking.repository.BookingRepository;
import com.booking.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;

    @Autowired
    public BookingServiceImpl(BookingRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(Integer userId, LocalDateTime start) {
        log.info("Create new booking. User Id: {}, start time: {}", userId, start.toString());
        Booking booking = Booking.builder()
                .userId(userId)
                .start(start)
                .finish(start.plusMinutes(15))
                .build();
        repository.save(booking);
    }

    @Override
    public void cancel(Integer id) {
        repository.findById(id).ifPresent(repository::delete);
    }

    @Override
    public List<Booking> getAllByUserId(Integer userId) {
        log.info("Get all bookings by user Id: {}", userId);
        return repository.findAllByUserId(userId);
    }

    @Override
    public List<Booking> getAllBookingsByDate(String selectedDate) {
//        LocalDate localDate = LocalDate.parse(selectedDate);
//        return repository.findAllByDate(localDate);
        Booking booking = Booking.builder()
                .id(1)
                .start(LocalDateTime.now())
                .finish(LocalDateTime.now().plusMinutes(15))
                .build();
        return Collections.singletonList(booking);
    }

}
