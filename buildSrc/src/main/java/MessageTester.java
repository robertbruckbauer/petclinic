import lombok.NonNull;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class MessageTester implements Predicate<String> {

    static final Pattern MESSAGE_PATTERN = Pattern.compile(
            "^([a-z]+|[a-zA-Z]+-[0-9]+)(\\([a-zA-Z0-9_-]+\\))?!?:\\s*(.+)$"
    );

    @Override
    public boolean test(@NonNull final String message) {
        final var matcher = MESSAGE_PATTERN.matcher(message);
        return matcher.matches();
    }
}
