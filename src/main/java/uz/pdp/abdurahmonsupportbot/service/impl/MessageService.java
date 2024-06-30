package uz.pdp.abdurahmonsupportbot.service.impl;

import uz.pdp.abdurahmonsupportbot.entity.Message;

public interface MessageService {
    void save(Message newMessage);

    Message getByMessageId(Integer messageId);
}
