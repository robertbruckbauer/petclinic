import java.util.List;

/**
 * Represents a {@code <testsuite>} element from a JUnit XML report.
 */
public record JunitXmlTestSuite(
        String name,
        double durationSeconds,
        List<JunitXmlTestCase> testCases,
        String stdout,
        String stderr) {}
