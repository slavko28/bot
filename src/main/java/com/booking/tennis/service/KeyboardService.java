package com.booking.tennis.service;

import com.booking.tennis.model.Booking;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

public interface KeyboardService {

    InlineKeyboardMarkup getStartKeyboard();

    InlineKeyboardMarkup getBookingList(List<Booking> bookings);
}
