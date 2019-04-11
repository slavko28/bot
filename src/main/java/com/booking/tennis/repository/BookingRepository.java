package com.booking.tennis.repository;

import com.booking.tennis.model.Booking;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Integer> {

    List<Booking> findAllByUserId(Integer id);
}
