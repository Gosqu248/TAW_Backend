package pl.urban.taw_backend.service;

import org.springframework.stereotype.Service;
import pl.urban.taw_backend.repository.UserSecurityRepository;

@Service
public class UserSecurityService {

    private final UserSecurityRepository userSecurityRepository;
    private final EmailService emailService;

    public UserSecurityService(UserSecurityRepository userSecurityRepository, EmailService emailService) {
        this.userSecurityRepository = userSecurityRepository;
        this.emailService = emailService;
    }
}
