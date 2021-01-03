package esy.http;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
public class RestApiConnection {

    private final HttpURLConnection connection;

    public RestApiResult options(@NonNull final String method) throws IOException {
        try {
            connection.setRequestMethod("OPTIONS");
            connection.setRequestProperty("Access-Control-Request-Method", method);
            connection.setRequestProperty("Access-Control-Request-Headers", "Content-Type");
            connection.connect();
            return new RestApiResult(connection);
        } finally {
            connection.disconnect();
        }
    }

    public RestApiResult get(@NonNull final String accept) throws IOException {
        try {
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", accept);
            connection.connect();
            return new RestApiResult(connection);
        } finally {
            connection.disconnect();
        }
    }

    public RestApiResult get() throws IOException {
        return get("application/json;charset=UTF-8");
    }


    private RestApiResult doWithJson(final String requestMethod, final String requestJson) throws IOException {
        try {
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json; charset=UTF-8");
            connection.setConnectTimeout(1000);
            connection.setDoOutput(true);
            try (final OutputStream os = connection.getOutputStream()) {
                final byte[] jsonBytes = requestJson.getBytes(UTF_8);
                os.write(jsonBytes, 0, jsonBytes.length);
            }
            connection.connect();
            return new RestApiResult(connection);
        } finally {
            connection.disconnect();
        }
    }


    public RestApiResult put(@NonNull final String requestJson) throws IOException {
        return doWithJson("PUT", requestJson);
    }

    public RestApiResult post(@NonNull final String requestJson) throws IOException {
        return doWithJson("POST", requestJson);
    }

    public RestApiResult delete() throws IOException {
        try {
            connection.setRequestMethod("DELETE");
            connection.connect();
            return new RestApiResult(connection);
        } finally {
            connection.disconnect();
        }
    }

    public RestApiConnection timeout(final long time, final TimeUnit timeUnit) {
        connection.setConnectTimeout((int)timeUnit.toMillis(time));
        return this;
    }

    public RestApiConnection timeout(final Duration duration) {
        connection.setConnectTimeout((int)duration.toMillis());
        return this;
    }

    public static RestApiConnection with(final String url) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(1000);
        connection.setRequestProperty("Origin", url);
        final byte[] credential = Base64.getEncoder().encode("user:password".getBytes(UTF_8));
        connection.setRequestProperty("Authorization", "Basic " + new String(credential));
        return new RestApiConnection(connection);
    }
}
