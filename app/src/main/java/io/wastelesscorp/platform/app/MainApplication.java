package io.wastelesscorp.platform.app;

import io.wastelesscorp.platform.app.controller.ControllerConfig;
import io.wastelesscorp.platform.app.security.SecurityConfig;
import io.wastelesscorp.platform.atoms.user.logic.UserServiceConfig;
import io.wastelesscorp.platform.atoms.weightedwaste.logic.WeightedWasteServiceConfig;
import io.wastelesscorp.platform.support.WebFluxConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
    ControllerConfig.class,
    WebFluxConfig.class,
    SecurityConfig.class,
    UserServiceConfig.class,
    WeightedWasteServiceConfig.class
})
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
