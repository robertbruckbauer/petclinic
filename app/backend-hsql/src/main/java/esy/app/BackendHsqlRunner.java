package esy.app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class BackendHsqlRunner {

    public static void main(final String[] args) {
        final SpringApplicationBuilder builder = new SpringApplicationBuilder(BackendHsqlRunner.class);
        builder.run(args);
    }
}
