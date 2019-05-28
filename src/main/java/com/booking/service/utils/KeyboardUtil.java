package com.booking.service.utils;

import com.booking.model.Booking;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class KeyboardUtil {

    public InlineKeyboardMarkup getStartKeyboard() {
        InlineKeyboardMarkup startKeyboardMarkup = new InlineKeyboardMarkup();
        startKeyboardMarkup.setKeyboard(getStartButtons());
        return startKeyboardMarkup;
    }

    private String getButtonText(Booking booking) {
        return getTimeInText(booking.getStart()) + " - " + getTimeInText(booking.getFinish());
    }

    private String getTimeInText(LocalDateTime localDateTime) {
        return localDateTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES).toString();
    }

    private List<List<InlineKeyboardButton>> getStartButtons() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttonsLine = new ArrayList<>();
        buttonsLine.add(createButton("New booking", "/booking"));
        buttonsLine.add(createButton("View my bookings", "/bookingList"));
        buttons.add(buttonsLine);
        return buttons;
    }

    private InlineKeyboardButton createButton(String buttonText, String callbackData) {
        return new InlineKeyboardButton().setText(buttonText).setCallbackData(callbackData);
    }

    public InlineKeyboardMarkup getBookingListButtons(List<Booking> bookings) {
        log.info("Prepare booking list buttons");
        InlineKeyboardMarkup listKeyboardMarkup = new InlineKeyboardMarkup();
        listKeyboardMarkup.setKeyboard(getButtonsForBookingList(bookings));
        return listKeyboardMarkup;
    }

    private List<List<InlineKeyboardButton>> getButtonsForBookingList(List<Booking> bookings) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        bookings.forEach(booking -> buttons.add(ImmutableList.of(new InlineKeyboardButton()
                .setText(getButtonText(booking)).setCallbackData(booking.getId().toString()))));
        return buttons;
    }

}
