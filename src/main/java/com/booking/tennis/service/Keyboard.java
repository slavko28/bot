package com.booking.tennis.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Keyboard {

    private static InlineKeyboardMarkup startKeyboardMarkup;

    private Keyboard() {
    }

    public static InlineKeyboardMarkup getStartKeyboardMarkup(){
        if (startKeyboardMarkup == null) {
            startKeyboardMarkup = new InlineKeyboardMarkup();
            startKeyboardMarkup.setKeyboard(getStartButtons());
        }
        return startKeyboardMarkup;
    }

    private static List<List<InlineKeyboardButton>> getStartButtons() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttonLine = getInlineKeyboardButton();
        buttons.add(buttonLine);
        return buttons;
    }

    private static List<InlineKeyboardButton> getInlineKeyboardButton() {
        List<InlineKeyboardButton> booking = new ArrayList<>();
        booking.add(new InlineKeyboardButton().setText("New booking").setCallbackData("/booking"));
        booking.add(new InlineKeyboardButton().setText("View my bookings").setCallbackData("/bookingList"));
        return booking;
    }

    public static InlineKeyboardMarkup getBookingList(Integer id) {
        return null;
    }

}
