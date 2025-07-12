import lombok.*;

import java.time.Instant;

@EqualsAndHashCode
@ToString
@Getter
public final class VersionTag implements Comparable<VersionTag> {

    private final Version version;

    @EqualsAndHashCode.Exclude
    private final Instant commitAt;

    public VersionTag(@NonNull final Version version, @NonNull final Instant commitAt) {
        this.version = version;
        this.commitAt = commitAt;
    }

    public int getMajor() {
        return version.major();
    }

    public int getMinor() {
        return version.minor();
    }

    public boolean isValid() {
        return !(version.major() == 0 && version.minor() == 0);
    }

    @Override
    public int compareTo(@NonNull final VersionTag that) {
        // descending by commit time, major and minor
        if (!that.commitAt.equals(this.commitAt)) {
            return that.commitAt.compareTo(this.commitAt);
        }
        if (that.version.major() != this.version.major()) {
            return Integer.compare(that.version.major(), this.version.major());
        }
        if (that.version.minor() != this.version.minor()) {
            return Integer.compare(that.version.minor(), this.version.minor());
        }
        return 0;
    }

    public boolean followsAfter(@NonNull final VersionTag that) {
        if (that.version.major() != this.version.major()) {
            return that.version.major() < this.version.major();
        }
        if (that.version.minor() != this.version.minor()) {
            return that.version.minor() < this.version.minor();
        }
        return false;
    }

    public String toRef() {
        return "refs/tags/%s%d.%d%s".formatted(
                version.prefix(),
                version.major(),
                version.minor(),
                version.patch());
    }

    public String toSemVer() {
        return "%d.%d".formatted(
                version.major(),
                version.minor());
    }
}
