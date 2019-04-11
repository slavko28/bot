package com.booking.service.impl;

import com.booking.service.CalendarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class CalendarServiceImpl implements CalendarService {

    private InlineKeyboardMarkup calendar = new InlineKeyboardMarkup();

    @Override
    public InlineKeyboardMarkup getCalendar(LocalDate localDate) {
        return calendar.setKeyboard(createCalendar(localDate));
    }

    private List<List<InlineKeyboardButton>> createCalendar(LocalDate localDate) {
        log.debug("Preparing calendar for {}", localDate.getMonth().name());
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        addHeader(localDate, buttons);
        addDays(localDate, buttons);
        return buttons;
    }

    private void addDays(LocalDate localDateTime, List<List<InlineKeyboardButton>> buttons) {
        final int firstDayOfMonth = localDateTime.withDayOfMonth(1).getDayOfWeek().getValue();
        final int lastDayOfMonth = localDateTime.getMonth().maxLength();
        int dayCounter = 1;
        for (int weekNum = 0; weekNum < 5; weekNum++) {
            List<InlineKeyboardButton> buttonLine = new ArrayList<>();
            final int weekLength = DayOfWeek.values().length;
            for (int numOfDay = 0; numOfDay < weekLength; numOfDay++) {
                boolean isBefore = isBeforeMonth(firstDayOfMonth, weekNum, numOfDay);
                boolean isNext = isAfterMonth(lastDayOfMonth, dayCounter, weekNum);
                if (shouldBeEmpty(firstDayOfMonth, lastDayOfMonth, dayCounter, weekNum, numOfDay)) {
                    buttonLine.add(getButton(isBefore, isNext, localDateTime, " ", dayCounter));
                } else {
                    buttonLine.add(getButton(isBefore, isNext, localDateTime, String.valueOf(dayCounter), dayCounter++));
                }
            }
            buttons.add(buttonLine);
        }
    }

    private boolean shouldBeEmpty(int firstDayOfMonth, int lastDayOfMonth, int dayCounter, int weekNum, int dayNum) {
        return isBeforeMonth(firstDayOfMonth, weekNum, dayNum) || isAfterMonth(lastDayOfMonth, dayCounter, weekNum);
    }

    private boolean isBeforeMonth(int firstDayOfMonth, int weekNum, int dayNum) {
        return dayNum + 1 < firstDayOfMonth && weekNum == 0;
    }

    private boolean isAfterMonth(int lastDayOfMonth, int day, int weekNum) {
        return weekNum > 0 && day > lastDayOfMonth;
    }

    private InlineKeyboardButton getButton(Boolean isPrevious, Boolean isNext, LocalDate localDateTime, String buttonText, int dayCounter) {
        return new InlineKeyboardButton().setText(buttonText)
                .setCallbackData(getCallbackData(isPrevious, isNext, localDateTime, dayCounter));
    }

    private String getCallbackData(Boolean isPrevious, Boolean isNext, LocalDate localDate, int dayCounter) {
        if (isPrevious) {
            return getPreviousMonth(localDate);
        } else if (isNext) {
            return getNextMonth(localDate);
        }
        return "current/" + localDate.withDayOfMonth(dayCounter).toString();
    }

    private String getNextMonth(LocalDate localDateTime) {
        return "another/" + localDateTime.plusMonths(1).toString();
    }

    private String getPreviousMonth(LocalDate localDate) {
        return "another/" + localDate.minusMonths(1).toString();
    }

    private void addHeader(LocalDate localDate, List<List<InlineKeyboardButton>> buttons) {
        log.debug("Preparing header for the calendar");
        buttons.add(addMonthNavigation(localDate));
        buttons.add(addWeekDays());
    }

    private List<InlineKeyboardButton> addMonthNavigation(LocalDate localDate) {
        log.debug("Add month navigation to the calendar header");
        List<InlineKeyboardButton> headerLine = new ArrayList<>();
        headerLine.add(new InlineKeyboardButton().setText("<").setCallbackData(getPreviousMonth(localDate)));
        headerLine.add(new InlineKeyboardButton().setText(localDate.getMonth().name()).setCallbackData("/month"));
        headerLine.add(new InlineKeyboardButton().setText(">").setCallbackData(getNextMonth(localDate)));
        return headerLine;
    }

    private List<InlineKeyboardButton> addWeekDays() {
        log.debug("Add day of week to thr calendar header");
        List<InlineKeyboardButton> headerLine = new ArrayList<>();
        final DayOfWeek[] values = DayOfWeek.values();

        Arrays.stream(values).forEach(day ->
                headerLine.add(new InlineKeyboardButton().setText(day.getDisplayName(TextStyle.SHORT, Locale.US)).setCallbackData("/header")));
        return headerLine;
    }

}
