import java.time.Instant;

public record VersionTag(String tagName, Instant commitAt) implements Comparable<VersionTag> {

    @Override
    public int compareTo(final VersionTag that) {
        // descending by commit time
        return that.commitAt.compareTo(this.commitAt);
    }
}
