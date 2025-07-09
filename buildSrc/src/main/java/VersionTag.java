import lombok.*;

import java.time.Instant;

@EqualsAndHashCode
@ToString
@Getter
public final class VersionTag implements Comparable<VersionTag> {

    private final int major;

    private final int minor;

    private final Instant commitAt;

    public VersionTag(final int[] version, @NonNull final Instant commitAt) {
        this.major = version[0];
        this.minor = version[1];
        this.commitAt = commitAt;
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

    public String toRef() {
        return "refs/tags/%d.%d".formatted(major, minor);
    }

    public String toSemVer() {
        return "%d.%d".formatted(major, minor);
    }
}
