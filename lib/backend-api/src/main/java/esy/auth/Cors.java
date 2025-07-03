package esy.auth;

import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.function.Consumer;

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS">Cross-Origin Resource Sharing</a>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/Security/Same-origin_policy">Same-origin policy</a>
 * @see <a href="https://www.innoq.com/en/blog/2022/02/cors-private-network-access/">CORS extension “Private Network Access”</a>
 */
public class Cors {

    private Cors() {}

    public static void withCredentials(@NonNull final Consumer<Boolean> applier) {
        applier.accept(true);
    }

    public static void withMaxAge(@NonNull final Consumer<Long> applier) {
        applier.accept(3600L);
    }

    public static void withHeader(@NonNull final Consumer<String[]> applier) {
        applier.accept(new String[]{
                HttpHeaders.ACCEPT,
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.CONTENT_LENGTH,
                HttpHeaders.IF_MATCH,
                HttpHeaders.IF_NONE_MATCH
        });
    }

    public static void withMethod(@NonNull final Consumer<String[]> applier) {
        applier.accept(new String[]{
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name()
        });
    }

    public static void withLocalhost(@NonNull final Consumer<String[]> applier) {
        applier.accept(new String[]{
                "http://localhost:[*]",
                "http://localhost",
                "https://localhost",
                "https://*.cardsplus.info",
                "https://*.github.dev"
        });
    }

    public static void withPrivateNetwork(@NonNull final Consumer<Boolean> applier) {
        applier.accept(true);
    }
}
