package pl.urban.taw_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.urban.taw_backend.dto.UserDTO;
import pl.urban.taw_backend.model.User;
import pl.urban.taw_backend.request.LoginRequest;
import pl.urban.taw_backend.request.TwoFactorVerificationRequest;
import pl.urban.taw_backend.response.JwtResponse;
import pl.urban.taw_backend.security.JwtUtil;
import pl.urban.taw_backend.service.UserSecurityService;
import pl.urban.taw_backend.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtToken;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserSecurityService userSecurityService;

    public AuthController(UserService userService, JwtUtil jwtToken, BCryptPasswordEncoder passwordEncoder, UserSecurityService userSecurityService) {
        this.userService = userService;
        this.jwtToken = jwtToken;
        this.passwordEncoder = passwordEncoder;
        this.userSecurityService = userSecurityService;
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
            final String jwt = jwtToken.generateToken(user);
            return ResponseEntity.ok(new JwtResponse(jwt));
        } else {
            throw new Exception("Bad verification codes");
        }
    }

    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUser(@RequestHeader("Authorization") String token) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));
        return ResponseEntity.ok(userService.getUser(subject));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> passwords) {
            String subject = jwtToken.extractSubjectFromToken(token.substring(7));
            String  oldPassword = passwords.get("oldPassword");
            String newPassword = passwords.get("newPassword");
            userService.changePassword(subject, oldPassword, newPassword);

            return ResponseEntity.ok("Password changed successfully");
    }

    @PutMapping("/updateData")
    public ResponseEntity<String>  changeUserName(@RequestHeader("Authorization") String token, @RequestBody String name, @RequestBody String phoneNumber) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));
        String updatedName = userService.changeUserInfo(subject, name, phoneNumber);
        return ResponseEntity.ok(updatedName);
    }

}
