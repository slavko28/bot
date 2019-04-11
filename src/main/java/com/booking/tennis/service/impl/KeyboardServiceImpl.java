package com.booking.tennis.service.impl;

import com.booking.tennis.model.Booking;
import com.booking.tennis.service.KeyboardService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyboardServiceImpl implements KeyboardService {

    @Override
    public InlineKeyboardMarkup getStartKeyboard() {
        InlineKeyboardMarkup startKeyboardMarkup = new InlineKeyboardMarkup();
            startKeyboardMarkup.setKeyboard(getStartButtons());
        return startKeyboardMarkup;
    }

    private List<List<InlineKeyboardButton>> getStartButtons() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttonLine = getInlineKeyboardButton();
        buttons.add(buttonLine);
        return buttons;
    }

    private List<InlineKeyboardButton> getInlineKeyboardButton() {
        List<InlineKeyboardButton> booking = new ArrayList<>();
        booking.add(new InlineKeyboardButton().setText("New booking").setCallbackData("/booking"));
        booking.add(new InlineKeyboardButton().setText("View my bookings").setCallbackData("/bookingList"));
        return booking;
    }

    @Override
    public InlineKeyboardMarkup getBookingList(List<Booking> bookings) {

        return null;
    }

}
