package com.booking.tennis.service;

import com.booking.tennis.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    void create(Integer userId, LocalDateTime start);

    void cancel(Integer id);

    List<Booking> getAllByUserId(Integer id);

}
