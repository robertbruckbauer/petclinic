import lombok.NonNull;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class VersionTester implements Predicate<String>, Function<String, Version> {

    static final Pattern VERSION_PATTERN = Pattern.compile(
            "^(v)?(\\d+)\\.(\\d+)(\\.?.*)$"
    );

    @Override
    public boolean test(@NonNull final String version) {
        final var matcher = VERSION_PATTERN.matcher(version);
        return matcher.matches();
    }

    @Override
    public Version apply(@NonNull final String version) {
        final var matcher = VERSION_PATTERN.matcher(version);
        if (matcher.matches()) {
            final var prefix = matcher.group(1);
            final var major = Integer.parseInt(matcher.group(2));
            final var minor = Integer.parseInt(matcher.group(3));
            final var patch = matcher.group(4);
            return new Version(prefix != null ? prefix : "", major, minor, patch);
        } else {
            return new Version("", 0, 0, "");
        }
    }
}
