package io.wastelesscorp.platform.app;

import static io.wastelesscorp.platform.app.MainApplication.ServiceConfig;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.wastelesscorp.platform.app.controller.ControllerConfig;
import io.wastelesscorp.platform.app.security.SecurityConfig;
import io.wastelesscorp.platform.atoms.user.logic.UserServiceConfig;
import io.wastelesscorp.platform.atoms.weightedwaste.logic.WeightedWasteServiceConfig;
import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFluxSecurity
@EnableWebFlux
@Import({ControllerConfig.class, SecurityConfig.class, ServiceConfig.class})
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Import({WeightedWasteServiceConfig.class, UserServiceConfig.class})
    public static class ServiceConfig {
        @Bean
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new GuavaModule());
            mapper.registerModule(new Jdk8Module());
            mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                    .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
                    .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                    .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            return mapper;
        }
    }
}
