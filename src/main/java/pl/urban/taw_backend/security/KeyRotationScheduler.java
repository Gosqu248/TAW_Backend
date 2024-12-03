package pl.urban.taw_backend.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KeyRotationScheduler {

    private final JwtUtil jwtUtil;

    public KeyRotationScheduler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Scheduled(cron = "0 0 0 * * SUN") // Co niedzielę o północy
    private void rotateKeys() {
        jwtUtil.rotateKey();
    }
}
