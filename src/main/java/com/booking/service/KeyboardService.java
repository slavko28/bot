package com.booking.service;

import com.booking.model.Booking;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

public interface KeyboardService {

    InlineKeyboardMarkup getStartKeyboard();

    InlineKeyboardMarkup getBookingListButtons(List<Booking> bookings);
}
