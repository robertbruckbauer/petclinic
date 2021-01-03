package esy;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ServerRunner {

    public static void main(final String[] args) {
        final SpringApplicationBuilder builder = new SpringApplicationBuilder(ServerRunner.class);
        builder.run(args);
    }
}
