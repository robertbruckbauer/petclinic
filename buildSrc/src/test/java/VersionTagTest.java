import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class VersionTagTest {

    @Test
    void isValid() {
        final var now = Instant.now();
        final var tag = new VersionTag(Version.with(0,2), now);
        assertEquals("refs/tags/0.2", tag.toRef());
        assertEquals("0.2", tag.toSemVer());
        assertFalse(tag.toString().isBlank());
        assertTrue(tag.isValid());
    }

    @Test
    void isNotValid() {
        final var now = Instant.now();
        final var tag = new VersionTag(Version.with(0,0), now);
        assertEquals("refs/tags/0.0", tag.toRef());
        assertEquals("0.0", tag.toSemVer());
        assertFalse(tag.toString().isBlank());
        assertFalse(tag.isValid());
    }

    @Test
    void equalTo() {
        final var t1 = Instant.now();
        final var t2 = t1.plusMillis(1L);
        final var v1 = Version.with(0, 1);
        final var v2 = Version.with(0, 2);
        assertEquals(new VersionTag(v1, t1), new VersionTag(v1, t1));
        assertNotEquals(new VersionTag(v1, t1), new VersionTag(v2, t1));
        assertEquals(new VersionTag(v1, t1), new VersionTag(v1, t2));
        assertNotEquals(new VersionTag(v1, t1), new VersionTag(v2, t2));
    }

    @Test
    void compareTo() {
        final var now = Instant.now();
        final var allTagSorted = new TreeSet<VersionTag>();
        allTagSorted.add(new VersionTag(
                Version.with(0, 1),
                now.minusMillis(1L)));
        allTagSorted.add(new VersionTag(
                Version.with(0, 2),
                now));
        allTagSorted.add(new VersionTag(
                Version.with(1, 0),
                now.plusMillis(1L)));
        allTagSorted.add(new VersionTag(
                Version.with(0, 3),
                now.plusMillis(1L)));
        assertEquals(List.of(1, 0, 0, 0), allTagSorted.stream().map(VersionTag::getMajor).toList());
        assertEquals(List.of(0, 3, 2, 1), allTagSorted.stream().map(VersionTag::getMinor).toList());
    }

    @Test
    void followsAfter() {
        final var now = Instant.now();
        final var tag = new VersionTag(Version.with(1, 2), now);
        assertTrue(tag.followsAfter(
                new VersionTag(Version.with(0, 2), now)));
        assertTrue(tag.followsAfter(
                new VersionTag(Version.with(1, 1), now)));
        assertFalse(tag.followsAfter(
                new VersionTag(Version.with(1, 2), now)));
        assertFalse(tag.followsAfter(
                new VersionTag(Version.with(1, 3), now)));
        assertFalse(tag.followsAfter(
                new VersionTag(Version.with(2, 0), now)));
    }
}