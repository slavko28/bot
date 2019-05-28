package com.booking.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class CalendarUtil {

    public InlineKeyboardMarkup getCalendar(LocalDate localDate) {
        InlineKeyboardMarkup calendar = new InlineKeyboardMarkup();
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
        log.debug("Adding buttons to the calendar");
        final int firstDayOfMonth = localDateTime.withDayOfMonth(1).getDayOfWeek().getValue();
        final int numberOfDays = getNumberOfDays(localDateTime);
        final int numberOfWeeks = getNumberOfWeeks(firstDayOfMonth);
        int dayCounter = 1;
        for (int weekCount = 0; weekCount < numberOfWeeks; weekCount++) {
            List<InlineKeyboardButton> buttonLine = new ArrayList<>();
            final int weekLength = DayOfWeek.values().length;
            if (!isEndOfMonth(dayCounter, numberOfDays)) {
                for (int numOfDay = 0; numOfDay < weekLength; numOfDay++) {
                    boolean isBefore = isBeforeMonth(firstDayOfMonth, weekCount, numOfDay);
                    boolean isNext = isAfterMonth(numberOfDays, dayCounter, weekCount);
                    if (shouldBeEmpty(firstDayOfMonth, numberOfDays, dayCounter, weekCount, numOfDay)) {
                        buttonLine.add(getButton(isBefore, isNext, localDateTime, " ", dayCounter));
                    } else {
                        buttonLine.add(getButton(isBefore, isNext, localDateTime, String.valueOf(dayCounter), dayCounter++));
                    }
                }
            }
            buttons.add(buttonLine);
        }
    }

    private boolean isEndOfMonth(int dayCounter, int numberOfDays) {
        return dayCounter > numberOfDays;
    }

    private int getNumberOfWeeks(int firstDayOfMonth) {
        return (firstDayOfMonth > 5) ? 6 : 5;
    }

    private int getNumberOfDays(LocalDate localDateTime) {
        if (localDateTime.getMonth().equals(Month.FEBRUARY) && !localDateTime.isLeapYear()) {
            return localDateTime.getMonth().minLength();
        } else {
            return localDateTime.getMonth().maxLength();
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
        headerLine.add(createButton("<", getPreviousMonth(localDate)));
        headerLine.add(createButton(getMonthButtonText(localDate), "/month"));
        headerLine.add(createButton(">", getNextMonth(localDate)));
        return headerLine;
    }

    private String getMonthButtonText(LocalDate localDate) {
        String shortName = localDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.US);
        return shortName + " " + localDate.getYear();
    }

    private List<InlineKeyboardButton> addWeekDays() {
        log.debug("Added days of the week to the calendar header");
        List<InlineKeyboardButton> headerLine = new ArrayList<>();
        Arrays.stream(DayOfWeek.values()).forEach(day ->
                headerLine.add(createButton(day.getDisplayName(TextStyle.SHORT, Locale.US), "/header")));
        return headerLine;
    }

    private InlineKeyboardButton createButton(String buttonText, String callbackData) {
        return new InlineKeyboardButton().setText(buttonText).setCallbackData(callbackData);
    }

}