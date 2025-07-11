import lombok.NonNull;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class VersionTester implements Predicate<String>, Function<String, int[]> {

    static final Pattern VERSION_PATTERN = Pattern.compile(
            "^v?(\\d+)\\.(\\d+).*$"
    );

    @Override
    public boolean test(@NonNull final String version) {
        final var matcher = VERSION_PATTERN.matcher(version);
        return matcher.matches();
    }

    @Override
    public int[] apply(@NonNull final String version) {
        final var matcher = VERSION_PATTERN.matcher(version);
        if (matcher.matches()) {
            final var major = Integer.parseInt(matcher.group(1));
            final var minor = Integer.parseInt(matcher.group(2));
            return new int[]{major, minor};
        } else {
            return new int[]{0, 0};
        }
    }
}
