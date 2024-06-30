package uz.pdp.abdurahmonsupportbot.service.impl;

import uz.pdp.abdurahmonsupportbot.entity.User;

public interface UserService {
    User getUserByChatId(Long chatId);

    void save(User user);

    User getAdmin();
}