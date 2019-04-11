package com.booking.tennis.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;

public interface CalendarService {

    InlineKeyboardMarkup getCalendar(LocalDate localDate);

}
