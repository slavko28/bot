package com.booking.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@Builder
@RedisHash
public class Booking {

    Integer id;
    Integer userId;
    LocalDateTime start;
    LocalDateTime finish;

}
