package com.booking;

import com.booking.model.Booking;
import com.booking.service.BookingService;
import com.booking.service.CalendarService;
import com.booking.service.KeyboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
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
            createMessage(update.getMessage(), "Hello!\nWhat do you want to do?", keyboardService.getStartKeyboard());
        } else {
            processCallbackQuery(update);
        }
    }

    private void processCallbackQuery(Update update) {
        final String data = update.getCallbackQuery().getData();
        SendMessage message;
        switch (data) {
            case "/booking":
                message = createMessage(update.getCallbackQuery().getMessage(), "Please, select day.", calendarService.getCalendar(LocalDate.now()));
                sendMessage(message);
                break;
            case "/bookingList":
                final Integer userId = update.getCallbackQuery().getFrom().getId();
                final List<Booking> allByUserId = bookingService.getAllByUserId(userId);
                message = createMessage(update.getCallbackQuery().getMessage(), "List", keyboardService.getBookingListButtons(allByUserId));
                sendMessage(message);
                break;
            default:
                processCalendarCallback(update);
        }
    }

    private SendMessage createMessage(Message message, String text, InlineKeyboardMarkup keyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
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
                    EditMessageText message = editMessage(update.getCallbackQuery().getMessage(), calendarService.getCalendar(localDate));
                    sendMessage(message);
                } catch (DateTimeParseException e) {
                    log.warn("Can not parse to date: {}", data);
                }
                break;
            default:
                log.warn("Can not parse calendar request: {}", data);
        }
    }

    private EditMessageText editMessage(Message message, InlineKeyboardMarkup keyboardMarkup) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.enableMarkdown(true);
        editMessageText.setChatId(message.getChatId());
        editMessageText.setText(message.getText());
        editMessageText.setReplyMarkup(keyboardMarkup);
        return editMessageText;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private synchronized void sendMessage(BotApiMethod message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
