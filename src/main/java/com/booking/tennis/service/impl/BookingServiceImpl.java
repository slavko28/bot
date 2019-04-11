package com.booking.tennis.service.impl;

import com.booking.tennis.model.Booking;
import com.booking.tennis.repository.BookingRepository;
import com.booking.tennis.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;

    @Autowired
    public BookingServiceImpl(BookingRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(Integer userId, LocalDateTime start) {
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setStart(start);
    }

    @Override
    public void cancel(Integer id) {
        repository.findById(id).ifPresent(repository::delete);
    }

    @Override
    public List<Booking> getAllByUserId(Integer id) {
        return repository.findAllByUserId(id);
    }
}
