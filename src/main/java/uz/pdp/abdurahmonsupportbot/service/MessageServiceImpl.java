package uz.pdp.abdurahmonsupportbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.abdurahmonsupportbot.entity.Message;
import uz.pdp.abdurahmonsupportbot.repo.MessageRepository;
import uz.pdp.abdurahmonsupportbot.service.impl.MessageService;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    @Override
    public void save(Message newMessage) {
        messageRepository.save(newMessage);
    }

    @Override
    public Message getByMessageId(Integer messageId) {
        return messageRepository.findByMessageId(messageId);
    }
}