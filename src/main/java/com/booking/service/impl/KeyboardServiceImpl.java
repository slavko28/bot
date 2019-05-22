package com.booking.service.impl;

import com.booking.model.Booking;
import com.booking.service.KeyboardService;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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
        List<InlineKeyboardButton> buttonLine = getButtonForStart();
        buttons.add(buttonLine);
        return buttons;
    }

    private List<InlineKeyboardButton> getButtonForStart() {
        List<InlineKeyboardButton> booking = new ArrayList<>();
        booking.add(new InlineKeyboardButton().setText("New booking").setCallbackData("/booking"));
        booking.add(new InlineKeyboardButton().setText("View my bookings").setCallbackData("/bookingList"));
        return booking;
    }

    @Override
    public InlineKeyboardMarkup getBookingListButtons(List<Booking> bookings) {
        log.info("Prepare booking list buttons");
        InlineKeyboardMarkup listKeyboardMarkup = new InlineKeyboardMarkup();
        listKeyboardMarkup.setKeyboard(getButtonsForBookingList(bookings));
        return listKeyboardMarkup;
    }

    private List<List<InlineKeyboardButton>> getButtonsForBookingList(List<Booking> bookings) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        bookings.forEach(booking -> buttons.add(ImmutableList.of(new InlineKeyboardButton()
                .setText(String.format("From: %s To: %s", booking.getStart().toString(), booking.getFinish().toString()))
                .setCallbackData(booking.getId().toString()))));
        return buttons;
    }

}