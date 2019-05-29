package com.booking.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Entity
public class Booking {

    @Id
    @GeneratedValue
    private Integer id;
    private Integer userId;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime finishTime;

}
