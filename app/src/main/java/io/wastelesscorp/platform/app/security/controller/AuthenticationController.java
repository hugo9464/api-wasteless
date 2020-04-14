package io.wastelesscorp.platform.app.security.controller;

import io.wastelesscorp.platform.atoms.user.api.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth/1/")
public class AuthenticationController {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthenticationController(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @PostMapping(value = "/signup")
    public Mono<Void> signup(@RequestBody SignupRequestJson request) {
        return Mono.fromSupplier(() -> request.toCreateUserRequest(passwordEncoder))
                .flatMap(userService::create);
    }

    @GetMapping(value = "/login")
    public Mono<String> dumbEndpoint() {
        return Mono.empty();
    }
}
