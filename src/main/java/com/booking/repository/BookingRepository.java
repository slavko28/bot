package com.booking.repository;

import com.booking.model.Booking;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Integer> {

    List<Booking> findAllByUserId(Integer id);

    List<Booking> findAllByDate(LocalDate localDate);
}
