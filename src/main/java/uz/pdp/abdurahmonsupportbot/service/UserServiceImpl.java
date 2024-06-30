package uz.pdp.abdurahmonsupportbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.abdurahmonsupportbot.entity.User;
import uz.pdp.abdurahmonsupportbot.repo.UserRepository;
import uz.pdp.abdurahmonsupportbot.service.impl.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getUserByChatId(Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User getAdmin() {
        return userRepository.findByAdmin();
    }
}
