package pl.urban.taw_backend.service;

import org.springframework.stereotype.Service;
import pl.urban.taw_backend.model.User;
import pl.urban.taw_backend.model.UserSecurity;
import pl.urban.taw_backend.repository.UserSecurityRepository;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserSecurityService {

    private final UserSecurityRepository userSecurityRepository;
    private final EmailService emailService;

    public UserSecurityService(UserSecurityRepository userSecurityRepository, EmailService emailService) {
        this.userSecurityRepository = userSecurityRepository;
        this.emailService = emailService;
    }

    public void incrementFailedLoginAttempts(User user) {
        UserSecurity userSecurity =  ensureUserSecurity(user);

        userSecurity.setFailedLoginAttempts(userSecurity.getFailedLoginAttempts() + 1);
        userSecurity.setLastFailedLoginAttempt(LocalDateTime.now());
        userSecurityRepository.save(userSecurity);
    }

    public void resetFailedLoginAttempts(User user) {
        UserSecurity userSecurity =  ensureUserSecurity(user);

        userSecurity.setFailedLoginAttempts(0);
        userSecurity.setLastFailedLoginAttempt(null);
        userSecurityRepository.save(userSecurity);
    }

    public boolean isAccountLocked(User user) {
        UserSecurity userSecurity =  ensureUserSecurity(user);

        if (userSecurity.getFailedLoginAttempts() >= 5) {
            LocalDateTime localTime = userSecurity.getLastFailedLoginAttempt().plusMinutes(10);
            return LocalDateTime.now().isBefore(localTime);
        }

        return false;
    }

    public void generateAndSendTwoFactorCode(User user) {
        UserSecurity userSecurity = user.getUserSecurity();

        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        userSecurity.setTwoFactorCode(String.valueOf(code));
        userSecurity.setTwoFactorCodeExpireTime(System.currentTimeMillis() + 300000);
        userSecurityRepository.save(userSecurity);

        String emailContent = "Witaj " + user.getName() + "!<br><br>Twój kod dostępu umożliwiający jednorazowe logowanie do restauracji TAW to:<br><br><b>" + code + "</b><br><br>Jeśli uważasz, że ten e-mail został wysłany nieprawidłowo, odpowiedz lub wyślij e-mail do wsparcia restauracji na adres: grzegorzurban248@gmail.com<br><br>Pozdrawiamy,<br>Zespół TAW";
        emailService.sendEmail(user.getEmail(), "Kod dostępu do restauracji TAW", emailContent);
    }

    public boolean verifyTwoFactorCode(User user, String code) {
        UserSecurity userSecurity = user.getUserSecurity();

        return userSecurity != null && userSecurity.getTwoFactorCode().equals(code) && userSecurity.getTwoFactorCodeExpireTime() > System.currentTimeMillis();
    }


    private UserSecurity ensureUserSecurity(User user) {
        UserSecurity userSecurity = user.getUserSecurity();
        if (userSecurity == null) {
            userSecurity = new UserSecurity();
            userSecurity.setUser(user);
            user.setUserSecurity(userSecurity);
        }
        return userSecurity;
    }
}
