package pl.urban.taw_backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.urban.taw_backend.dto.UserDTO;
import pl.urban.taw_backend.model.User;
import pl.urban.taw_backend.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public Boolean changePassword(String subject, String password, String newPassword) {
        User user = getUserBySubject(subject);
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Old password does not match");
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    public User getUserBySubject(String subject) {
        return userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));
    }

    public UserDTO getUser(String subject) {
        User user = getUserBySubject(subject);
        return convertToDTO(user);
    }

    public UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(String.valueOf(user.getRole()));
        return dto;
    }

    public String changeUserInfo(String subject, String name, String phoneNumber) {
        User user = getUserBySubject(subject);
        user.setName(name);
        user.setPhoneNumber(phoneNumber);
        userRepository.save(user);
        return "User info updated successfully";
    }

    public String updatePasswordByMail(String subject, String newPassword) {
        User user = getUserBySubject(subject);
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Password changed successfully";
    }
}
