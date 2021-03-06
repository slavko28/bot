package com.booking;

import com.booking.model.Booking;
import com.booking.service.BookingService;
import com.booking.service.utils.CalendarUtil;
import com.booking.service.utils.KeyboardUtil;
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

    private final CalendarUtil calendarUtil;

    private final BookingService bookingService;

    private final KeyboardUtil keyboardUtil;

    @Value("${telegram.bot.name}")
    private String botName;
    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired
    public Bot(CalendarUtil calendarUtil, BookingService bookingService, KeyboardUtil keyboardUtil) {
        this.calendarUtil = calendarUtil;
        this.bookingService = bookingService;
        this.keyboardUtil = keyboardUtil;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            SendMessage startMessage = getStartMessage(update);
            sendMessage(startMessage);
        } else {
            processCallbackQuery(update);
        }
    }

    private SendMessage getStartMessage(Update update) {
        return createMessage(update.getMessage(), "Hello!\nWhat do you want to do?", keyboardUtil.getStartKeyboard());
    }

    private void processCallbackQuery(Update update) {
        final String data = update.getCallbackQuery().getData();
        if (!data.isBlank()) {
            switch (data) {
                case "/booking":
                    log.info("Receive \"/booking\" request");
                    createAndSendMessage(update, "Please, select day.", calendarUtil.getCalendar(LocalDate.now()));
                    break;
                case "/bookingList":
                    log.info("Receive \"/bookingList\" request");
                    final List<Booking> allByUserId = getBookings(update.getCallbackQuery().getFrom().getId());
                    createAndSendMessage(update, "List", keyboardUtil.getBookingListButtons(allByUserId));
                    break;
                default:
                    processCalendarCallback(update);
            }
        } else {
            log.info("The callback query data is empty");
            createAndSendStartMessage(update);
        }
    }

    private void createAndSendMessage(Update update, String text, InlineKeyboardMarkup markup) {
        SendMessage message = createMessage(update.getCallbackQuery().getMessage(), text, markup);
        sendMessage(message);
    }

    private void createAndSendStartMessage(Update update) {
        SendMessage startMessage = getStartMessage(update);
        sendMessage(startMessage);
    }

    private List<Booking> getBookings(Integer userId) {
        return bookingService.getAllByUserId(userId);
    }

    private SendMessage createMessage(Message message, String text, InlineKeyboardMarkup keyboardMarkup) {
        log.info("Create message for chat id: {}", message.getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(false);
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
                processCurrentMonth(update, strings.get(1));
                break;
            case "another":
                processAnotherMonth(update, data, strings.get(1));
                break;
            default:
                log.warn("Can not parse calendar request: {}", data);
        }
    }

    private void processCurrentMonth(Update update, String selectedDate) {
        log.info("Processing request for booking list. Date - {}", selectedDate);
        List<Booking> availableBookingList = bookingService.getAllBookingsByDate(selectedDate);
        SendMessage message = createMessage(update.getCallbackQuery().getMessage(),
                "Selected - " + selectedDate, keyboardUtil.getBookingListButtons(availableBookingList));
        sendMessage(message);
    }

    private void processAnotherMonth(Update update, String data, String selectedDate) {
        log.info("Processing request fot another month. Selected date - {}", selectedDate);
        try {
            LocalDate localDate = LocalDate.parse(selectedDate);
            EditMessageText message = editMessage(update.getCallbackQuery().getMessage(), calendarUtil.getCalendar(localDate));
            sendMessage(message);
        } catch (DateTimeParseException e) {
            log.warn("Can not parse to date: {}", data);
        }
    }


    private EditMessageText editMessage(Message message, InlineKeyboardMarkup keyboardMarkup) {
        log.info("Edit message with id: {}, for chat id: {}", message.getMessageId(), message.getChatId());
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
            log.info("Message send successfully");
        } catch (TelegramApiException e) {
            log.error("Can not send message.\n{}", e.getMessage());
        }
    }

}
