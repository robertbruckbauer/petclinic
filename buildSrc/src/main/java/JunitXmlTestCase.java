import java.util.List;

/**
 * Represents a {@code <testcase>} element from a JUnit XML report.
 */
public record JunitXmlTestCase(
        String name,
        String classname,
        double durationSeconds,
        boolean skipped,
        List<JunitXmlTestFailure> failures,
        String stdout,
        String stderr) {}
