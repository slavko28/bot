package com.booking.tennis;

import com.booking.tennis.model.Booking;
import com.booking.tennis.service.BookingService;
import com.booking.tennis.service.CalendarService;
import com.booking.tennis.service.KeyboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    private final CalendarService calendarService;

    private final BookingService bookingService;

    private final KeyboardService keyboardService;

    @Value("${telegram.bot.name}")
    private String botName;
    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired
    public Bot(CalendarService calendarService, BookingService bookingService, KeyboardService keyboardService) {
        this.calendarService = calendarService;
        this.bookingService = bookingService;
        this.keyboardService = keyboardService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            sendMessage(update.getMessage().getChatId(), "Greeting!\nWhat do you want to do?", keyboardService.getStartKeyboard());
        } else {
            processCallbackQuery(update);
        }
    }

    private void processCallbackQuery(Update update) {
        final String data = update.getCallbackQuery().getData();
        switch (data) {
            case "/booking":
                sendMessage(update.getCallbackQuery().getMessage().getChatId(), "Please, select day.", calendarService.getCalendar(LocalDate.now()));
                break;
            case "/bookingList":
                final Integer userId = update.getCallbackQuery().getFrom().getId();
                final List<Booking> allByUserId = bookingService.getAllByUserId(userId);
                sendMessage(update.getCallbackQuery().getMessage().getChatId(), "List", keyboardService.getBookingList(allByUserId));
                break;
            default:
                processCalendarCallback(update);
        }
    }

    private void processCalendarCallback(Update update) {
        final String data = update.getCallbackQuery().getData();
        List<String> strings = Arrays.asList(data.split("/"));
        switch (strings.get(0)) {
            case "current":
                log.info("processing request to booking list for date {}", strings.get(1));
                break;
            case "another":
                try {
                    LocalDate localDate = LocalDate.parse(strings.get(1));
                    sendMessage(update.getCallbackQuery().getMessage().getChatId(), "Please, select day.", calendarService.getCalendar(localDate));
                } catch (DateTimeParseException e) {
                    log.warn("Can not parse to date: {}", data);
                }
                break;
            default:
                log.warn("Can not parse calendar request: {}", data);
        }

    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private synchronized void sendMessage(Long chatId, String text, InlineKeyboardMarkup keyboardMarkup) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
