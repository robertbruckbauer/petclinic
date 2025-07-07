import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;
import java.util.Optional;
import java.util.regex.Pattern;

@EqualsAndHashCode
@Getter
public final class VersionTag implements Comparable<VersionTag> {

    static final Pattern VERSION_PATTERN = Pattern.compile(
            "^refs/tags/(\\d+)\\.(\\d+)$"
    );

    private final int major;

    private final int minor;

    private final Instant commitAt;

    VersionTag(final int major, final int minor, @NonNull final Instant commitAt) {
        this.major = major;
        this.minor = minor;
        this.commitAt = commitAt;
    }

    public VersionTag increment() {
        return new VersionTag(major, minor + 1, Instant.now());
    }

    @Override
    public int compareTo(@NonNull final VersionTag that) {
        // descending by commit time, major and minor
        if (!that.commitAt.equals(this.commitAt)) {
            return that.commitAt.compareTo(this.commitAt);
        }
        if (that.major != this.major) {
            return Integer.compare(that.major, this.major);
        }
        if (that.minor != this.minor) {
            return Integer.compare(that.minor, this.minor);
        }
        return 0;
    }

    @Override
    public String toString() {
        return "refs/tags/%d.%d".formatted(major, minor);
    }

    static Optional<VersionTag> cleanTag(@NonNull final String tagName, @NonNull final Instant commitAt) {
        final var matcher = VERSION_PATTERN.matcher(tagName);
        if (matcher.matches()) {
            final var major = Integer.parseInt(matcher.group(1));
            final var minor = Integer.parseInt(matcher.group(2));
            return Optional.of(new VersionTag(major, minor, commitAt));
        } else {
            return Optional.empty();
        }
    }

    static Optional<VersionTag> dirtyTag(@NonNull final String tagName) {
        final var matcher = VERSION_PATTERN.matcher(tagName);
        if (matcher.matches()) {
            final var major = Integer.parseInt(matcher.group(1));
            final var minor = Integer.parseInt(matcher.group(2)) + 1;
            return Optional.of(new VersionTag(major, minor, Instant.now()));
        } else {
            return Optional.empty();
        }
    }
}
