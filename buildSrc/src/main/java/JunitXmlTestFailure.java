/**
 * Represents a {@code <failure>} or {@code <error>} element from a JUnit XML report.
 */
public record JunitXmlTestFailure(String message, String type, String content) {

    public JunitXmlTestFailure {
        message = message != null ? message : "";
        type = type != null ? type : "";
        content = content != null ? content : "";
    }
}
