import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class VersionTagTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "refs/tags/0.0",
            "refs/tags/0.9",
            "refs/tags/1.20"
    })
    void tagNameOk(final String tagName) {
        final var now = Instant.now();
        final var cleanTag = VersionTag.cleanTag(tagName, now).orElseThrow();
        assertEquals(tagName, cleanTag.toString());
        final var dirtyTag = VersionTag.dirtyTag(tagName).orElseThrow();
        assertNotEquals(tagName, dirtyTag.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0.0",
            "refs/tags/0",
            "refs/tags/a.0",
            "refs/tags/0.a",
            "refs/tags/0.0.0"
    })
    void tagNameNotOk(final String tagName) {
        final var now = Instant.now();
        assertFalse(VersionTag.cleanTag(tagName, now).isPresent());
        assertFalse(VersionTag.dirtyTag(tagName).isPresent());
    }

    @Test
    void compareTo() {
        final var now = Instant.now();
        final var allTagSorted = new TreeSet<VersionTag>();
        allTagSorted.add(VersionTag.cleanTag("refs/tags/0.1", now.minusMillis(1L)).orElseThrow());
        allTagSorted.add(VersionTag.cleanTag("refs/tags/0.2", now).orElseThrow());
        allTagSorted.add(VersionTag.cleanTag("refs/tags/1.0", now.plusMillis(1L)).orElseThrow());
        allTagSorted.add(VersionTag.cleanTag("refs/tags/0.3", now.plusMillis(1L)).orElseThrow());
        assertEquals(List.of(1, 0, 0, 0), allTagSorted.stream().map(VersionTag::getMajor).toList());
        assertEquals(List.of(0, 3, 2, 1), allTagSorted.stream().map(VersionTag::getMinor).toList());
    }

}