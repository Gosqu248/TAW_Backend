package pl.urban.taw_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.urban.taw_backend.model.User;
import pl.urban.taw_backend.security.JwtUtil;
import pl.urban.taw_backend.service.UserSecurityService;
import pl.urban.taw_backend.service.UserService;

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

}
