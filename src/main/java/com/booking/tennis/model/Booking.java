package com.booking.tennis.model;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@RedisHash
public class Booking {

    Integer id;
    Integer userId;
    LocalDateTime start;
    LocalDateTime finish;

}
