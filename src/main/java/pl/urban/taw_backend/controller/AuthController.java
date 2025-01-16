package pl.urban.taw_backend.controller;

import io.micrometer.common.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.urban.taw_backend.dto.UserDTO;
import pl.urban.taw_backend.model.User;
import pl.urban.taw_backend.request.*;
import pl.urban.taw_backend.response.JwtResponse;
import pl.urban.taw_backend.security.JwtUtil;
import pl.urban.taw_backend.service.EmailService;
import pl.urban.taw_backend.service.UserSecurityService;
import pl.urban.taw_backend.service.UserService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserSecurityService userSecurityService;
    private final EmailService emailService;

    public AuthController(UserService userService, JwtUtil jwtUtil, BCryptPasswordEncoder passwordEncoder, UserSecurityService userSecurityService, EmailService emailService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userSecurityService = userSecurityService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        userService.registerUser(user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {

        try {
            User user = userService.getUserBySubject(loginRequest.getEmail());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid credentials");
            }

            if (userSecurityService.isAccountLocked(user)) {
                return ResponseEntity.status(423)
                        .body("Account is locked. Try again later.");
            }

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                userSecurityService.incrementFailedLoginAttempts(user);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid credentials");
            }

            userSecurityService.resetFailedLoginAttempts(user);
            userSecurityService.generateAndSendTwoFactorCode(user);

            return ResponseEntity.ok(true);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during login");
        }
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verifyTwoFactorCode(@RequestBody TwoFactorVerificationRequest request) throws Exception {
        User user = userService.getUserBySubject(request.getEmail());

        if(user == null) {
            throw new IllegalArgumentException("User with this email not found");
        }

        if (userSecurityService.verifyTwoFactorCode(user, request.getCode())) {
            final String jwt = jwtUtil.generateToken(user, false);
            return ResponseEntity.ok(new JwtResponse(jwt));
        } else {
            throw new Exception("Bad verification codes");
        }
    }

    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUser(@RequestHeader("Authorization") String token) {
        String subject = jwtUtil.extractSubjectFromToken(token.substring(7));
        return ResponseEntity.ok(userService.getUser(subject));
    }

    @PutMapping("/changePassword")
    public ResponseEntity<Boolean> changePassword(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> passwords) {
        String subject = jwtUtil.extractSubjectFromToken(token.substring(7));
        String password = passwords.get("password");
        String newPassword = passwords.get("newPassword");
        Boolean updatePassword = userService.changePassword(subject, password, newPassword);
        return ResponseEntity.ok(updatePassword);
    }

    @PutMapping("/updateData")
    public ResponseEntity<String> changeUserName(@RequestHeader("Authorization") String token, @RequestBody UpdateUserDataRequest request) {
        String subject = jwtUtil.extractSubjectFromToken(token.substring(7));
        String updatedName = userService.changeUserInfo(subject, request.getName(), request.getPhoneNumber());
        return ResponseEntity.ok(updatedName);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        User user = userService.getUserBySubject(request.getEmail());

        if (user == null) {
            return ResponseEntity.badRequest().body("Użytkownik nie znaleziony");
        }

        String token = jwtUtil.generateToken(user, true);

        String resetUrl = "<a href='http://localhost:4200/reset-password?token=" + token + "'>http://localhost:4200/reset-password?token=" + token + "</a>";
        String emailContent = "Resetowanie hasła do strony TAW_Restaurant. <br> <br>  Kliknij w poniższy link, aby zresetować hasło: <br><br>" + resetUrl;

        emailService.sendEmail(user.getEmail(), "Resetowanie hasła", emailContent);

        return ResponseEntity.ok("Link do resetowania hasła został wysłany na email: " + request.getEmail());
    }

    @PostMapping("/confirm-reset-password")
    public ResponseEntity<Map<String, String>> confirmResetPassword(@RequestBody PasswordResetConfirmRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            String email = jwtUtil.extractSubjectFromToken(request.getToken());

            User user = userService.getUserBySubject(email);

            if (user == null) {
                response.put("error", "Invalid token or user not found.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (!jwtUtil.validateToken(request.getToken(), true)) {
                response.put("error", "Invalid or expired token.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String updated = userService.updatePasswordByMail(email, request.getNewPassword());

            response.put("message", updated);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "An error occurred while resetting the password.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




}
