package pl.urban.taw_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pl.urban.taw_backend.dto.UserDTO;
import pl.urban.taw_backend.model.User;
import pl.urban.taw_backend.request.LoginRequest;
import pl.urban.taw_backend.request.TwoFactorVerificationRequest;
import pl.urban.taw_backend.response.JwtResponse;
import pl.urban.taw_backend.security.JwtUtil;
import pl.urban.taw_backend.service.UserSecurityService;
import pl.urban.taw_backend.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtToken;
    private final AuthenticationManager authenticationManager;
    private final UserSecurityService userSecurityService;

    public AuthController(UserService userService, JwtUtil jwtToken, AuthenticationManager authenticationManager, UserSecurityService userSecurityService) {
        this.userService = userService;
        this.jwtToken = jwtToken;
        this.authenticationManager = authenticationManager;
        this.userSecurityService = userSecurityService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
         try {
             userService.registerUser(user);
             return ResponseEntity.ok("User registered successfully");
         } catch (Exception e) {
             return ResponseEntity.badRequest().build();
         }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) throws Exception {
        User user = userService.getUserBySubject(loginRequest.getEmail());

        if ( userSecurityService.isAccountLocked(user)) {
            return ResponseEntity.status(423).body("Account is locked. Try again later.");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            userSecurityService.resetFailedLoginAttempts(user);
            userSecurityService.generateAndSendTwoFactorCode(user);
        } catch (BadCredentialsException e) {
            userSecurityService.incrementFailedLoginAttempts(user);
            throw new Exception("Incorrect email or password", e);
        }
        return ResponseEntity.ok(true);
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
        return ResponseEntity.ok(userService.getUser(token));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> passwords) {
        try {
            String subject = jwtToken.extractSubjectFromToken(token.substring(7));
            String  oldPassword = passwords.get("oldPassword");
            String newPassword = passwords.get("newPassword");
            userService.changePassword(subject, oldPassword, newPassword);

            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Password change failed");
        }
    }

}
