package pl.urban.taw_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.urban.taw_backend.response.JwtResponse;
import pl.urban.taw_backend.service.GoogleAuthService;

@RestController
@RequestMapping("/api/auth")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    public GoogleAuthController(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    @GetMapping("/google")
    public ResponseEntity<JwtResponse> googleLoginSuccess(@AuthenticationPrincipal OAuth2User principal) {
        return ResponseEntity.ok(new JwtResponse(googleAuthService.googleLogin(principal)));

    }

    @GetMapping("/google/failure")
    public ResponseEntity<?> googleLoginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Logowanie przez Google nie powiodło się.");
    }

}
