package pl.urban.taw_backend.service;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import pl.urban.taw_backend.enums.Role;
import pl.urban.taw_backend.model.User;
import pl.urban.taw_backend.repository.UserRepository;
import pl.urban.taw_backend.security.JwtUtil;
import pl.urban.taw_backend.security.PasswordGenerator;

@Service
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtToken;

    public GoogleAuthService(UserRepository userRepository, JwtUtil jwtToken) {
        this.userRepository = userRepository;
        this.jwtToken = jwtToken;
    }


    public  String googleLogin(OAuth2User principal) {
        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");

        final User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPassword(PasswordGenerator.generateRandomPassword());
            newUser.setRole(Role.user);
            return userRepository.save(newUser);
        });
        return jwtToken.generateToken(user, false);
    }
}
