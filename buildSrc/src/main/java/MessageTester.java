import lombok.NonNull;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class MessageTester implements Predicate<String>, Function<String, Message> {

    static final Pattern MESSAGE_PATTERN = Pattern.compile(
            "^([a-z]+|[a-zA-Z]+-[0-9]+)(\\([a-zA-Z0-9_-]+\\))?(!?):\\s*(.+)$"
    );

    @Override
    public boolean test(@NonNull final String text) {
        final var matcher = MESSAGE_PATTERN.matcher(text);
        return matcher.matches();
    }

    @Override
    public Message apply(@NonNull final String text) {
        final var matcher = MESSAGE_PATTERN.matcher(text);
        if (matcher.matches()) {
            final var type = matcher.group(1).toLowerCase();
            final var major = !"".equals(matcher.group(3));
            return new Message(type, text, major);
        } else {
            return new Message("", text, false);
        }
    }
}
