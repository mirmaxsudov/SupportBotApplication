package uz.pdp.abdurahmonsupportbot.service.bot;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pdp.abdurahmonsupportbot.config.BotConfiguration;
import uz.pdp.abdurahmonsupportbot.entity.User;
import uz.pdp.abdurahmonsupportbot.entity.enums.Operation;
import uz.pdp.abdurahmonsupportbot.entity.enums.UserRole;
import uz.pdp.abdurahmonsupportbot.service.impl.MessageService;
import uz.pdp.abdurahmonsupportbot.service.impl.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfiguration botConfiguration;
    private final UserService userService;
    private final MessageService messageService;
    private Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    @Override
    public void onUpdateReceived(Update update) {
        CompletableFuture.runAsync(() -> processUpdate(update));
    }

    @Override
    public String getBotUsername() {
        return botConfiguration.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfiguration.getBotToken();
    }

    private static Map<Long, String> MP = new HashMap<>();

    private void processUpdate(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();

            User currentUser = userService.getUserByChatId(chatId);
            String operation = MP.get(chatId);

            System.out.println("operation = " + operation);

            if (operation != null) {
                if (operation.startsWith("ANSWER_MESSAGE")) {
                    System.out.println("ANSWER IN");
                    int messageId = Integer.parseInt(operation.split(" ")[1]);

                    uz.pdp.abdurahmonsupportbot.entity.Message message1 = messageService.getByMessageId(messageId);

                    System.out.println("message1.getChatId() = " + message1.getChatId());
                    System.out.println("message1.getMessage() = " + message1.getMessage());

                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText(message.getText());
                    sendMessage.setChatId(message1.getChatId());
                    sendMessage.setReplyToMessageId(message1.getMessageId());
                    executeCustom(sendMessage);

                    MP.remove(chatId);
                }
                return;
            }
            if (currentUser == null) {
                if (message.hasContact()) {
                    Contact contact = message.getContact();

                    String firstName = contact.getFirstName();
                    String lastName = contact.getLastName();
                    String phoneNumber = contact.getPhoneNumber();

                    User user = new User();
                    user.setChatId(chatId);
                    user.setBlocked(false);
                    user.setMessages(null);
                    user.setLastName(lastName);
                    user.setFirstName(firstName);
                    user.setPhoneNumber(phoneNumber);
                    user.setUserRole(UserRole.USER);

                    userService.save(user);
                    ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
                    remove.setSelective(false);
                    remove.setRemoveKeyboard(true);
                    SendMessage loginMessage = new SendMessage();
                    loginMessage.setChatId(chatId);
                    loginMessage.setText("Xabaringizni yuboring");
                    loginMessage.setReplyMarkup(remove);

                    executeCustom(loginMessage);
                } else {
                    SendMessage login = new SendMessage();
                    login.setText("Telefon raqamini yuboring");
                    login.setChatId(chatId.toString());
                    login.setReplyMarkup(phoneNumber());
                    executeCustom(login);
                }
                return;
            } else {
                String text = message.getText();
                Integer messageId = message.getMessageId();

                if (currentUser.getUserRole().equals(UserRole.ADMIN)) {
                    Message replyToMessage = message.getReplyToMessage();
                    System.out.println("replyToMessage.getMessageId() = " + replyToMessage.getMessageId());

                    uz.pdp.abdurahmonsupportbot.entity.Message sentMessage =
                            messageService.getByMessageId(replyToMessage.getMessageId());

                    User sendUser = sentMessage.getUser();

                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(sendUser.getChatId());
                    sendMessage.setText(text);
                    sendMessage.setReplyToMessageId(messageId);

                    executeCustom(sendMessage);
                } else {
                    System.out.println("USER");
                    uz.pdp.abdurahmonsupportbot.entity.Message newMessage = new uz.pdp.abdurahmonsupportbot.entity.Message();
                    newMessage.setChatId(chatId);
                    newMessage.setMessage(text);
                    newMessage.setUser(currentUser);
                    newMessage.setMessageId(messageId);

                    messageService.save(newMessage);

                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                    InlineKeyboardButton answer = new InlineKeyboardButton();
                    answer.setCallbackData("ANSWER: " + newMessage.getMessageId());
                    answer.setText("Answer ");

                    markup.setKeyboard(
                            List.of(
                                    List.of(answer)
                            )
                    );

                    String userMessage = String.format("""
                                    %s %s
                                    %s
                                    %s
                                    """,
                            currentUser.getFirstName(),
                            currentUser.getLastName(),
                            newMessage.getMessage(),
                            currentUser.getPhoneNumber()
                    );

                    User admin = userService.getAdmin();

                    SendMessage m = new SendMessage();
                    m.setChatId(admin.getChatId());
                    m.setReplyMarkup(markup);
                    m.setText(userMessage);

                    executeCustom(m);
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();

            String data = callbackQuery.getData();

            if (data.startsWith("ANSWER: ")) {
                System.out.println("data = " + data);
                int messageId = Integer.parseInt(data.split(" ")[1]);

                MP.put(callbackQuery.getMessage().getChatId(), Operation.ANSWER_MESSAGE + " " + messageId);
                System.out.println("MP = " + MP);
            }
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        executeCustom(message);
    }

    private void executeCustom(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private ReplyKeyboard phoneNumber() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();

        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        KeyboardRow row = new KeyboardRow();

        KeyboardButton button = new KeyboardButton();
        button.setRequestContact(true);
        button.setText("Telefon raqamini yuboring");
        row.add(button);

        markup.setKeyboard(
                List.of(row)
        );

        return markup;
    }
}