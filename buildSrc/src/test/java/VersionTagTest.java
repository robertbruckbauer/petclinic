import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class VersionTagTest {

    @Test
    void stringify() {
        final var now = Instant.now();
        final var tag = new VersionTag(new int[]{0,2}, now);
        assertEquals("refs/tags/0.2", tag.toRef());
        assertEquals("0.2", tag.toSemVer());
        assertFalse(tag.toString().isBlank());
    }

    @Test
    void compareTo() {
        final var now = Instant.now();
        final var allTagSorted = new TreeSet<VersionTag>();
        allTagSorted.add(new VersionTag(new int[]{0,1}, now.minusMillis(1L)));
        allTagSorted.add(new VersionTag(new int[]{0,2}, now));
        allTagSorted.add(new VersionTag(new int[]{1,0}, now.plusMillis(1L)));
        allTagSorted.add(new VersionTag(new int[]{0,3}, now.plusMillis(1L)));
        assertEquals(List.of(1, 0, 0, 0), allTagSorted.stream().map(VersionTag::getMajor).toList());
        assertEquals(List.of(0, 3, 2, 1), allTagSorted.stream().map(VersionTag::getMinor).toList());
    }

}