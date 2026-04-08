import org.gradle.api.GradleException;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses JUnit XML report files (both {@code <testsuites>} and {@code <testsuite>} root elements).
 *
 * <p>Hardened against XXE by disabling external entity resolution.
 */
public class JunitXmlParser {

    private static @NotNull DocumentBuilderFactory createXmlBuilderFactory() {
        try {
            // Prevent XML External Entity (XXE) attacks
            var factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setExpandEntityReferences(false);
            return factory;
        } catch (ParserConfigurationException e) {
            throw new GradleException("Failed to configure XML parser securely", e);
        }
    }

    private static DocumentBuilder createXmlBuilder(DocumentBuilderFactory factory) {
        try {
            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new GradleException("Failed to create XML parser", e);
        }
    }

    private static Document parseXmlFile(File xmlFile, DocumentBuilder builder) {
        try {
            return builder.parse(xmlFile);
        } catch (SAXException | IOException e) {
            throw new GradleException("Failed to parse JUnit XML: " + xmlFile.getAbsolutePath(), e);
        }
    }

    /**
     * Parses a single JUnit XML file and returns all test suites it contains.
     *
     * @param xmlFile the XML file to parse
     * @return list of parsed test suites; never {@code null}
     * @throws GradleException if the file cannot be read or is not valid XML
     */
    public List<JunitXmlTestSuite> parse(File xmlFile) {
        var factory = createXmlBuilderFactory();
        var builder = createXmlBuilder(factory);
        var doc = parseXmlFile(xmlFile, builder);
        var root = doc.getDocumentElement();
        var allTestSuite = new ArrayList<JunitXmlTestSuite>();
        if ("testsuites".equals(root.getTagName())) {
            var allNode = root.getChildNodes();
            for (int i = 0; i < allNode.getLength(); i++) {
                var node = allNode.item(i);
                if (node instanceof Element element && "testsuite".equals(element.getTagName())) {
                    allTestSuite.add(parseSuite(element));
                }
            }
        } else if ("testsuite".equals(root.getTagName())) {
            allTestSuite.add(parseSuite(root));
        }

        return allTestSuite;
    }

    private JunitXmlTestSuite parseSuite(Element element) {
        var name = element.getAttribute("name");
        var duration = parseDouble(element.getAttribute("time"));
        var stdout = getDirectChildText(element, "system-out");
        var stderr = getDirectChildText(element, "system-err");
        var allTestCase = new ArrayList<JunitXmlTestCase>();
        var allNode = element.getChildNodes();
        for (int i = 0; i < allNode.getLength(); i++) {
            var node = allNode.item(i);
            if (node instanceof Element element2 && "testcase".equals(element2.getTagName())) {
                allTestCase.add(parseTestCase(element2));
            }
        }

        return new JunitXmlTestSuite(name, duration, allTestCase, stdout, stderr);
    }

    private JunitXmlTestCase parseTestCase(Element element) {
        var name = element.getAttribute("name");
        var classname = element.getAttribute("classname");
        var duration = parseDouble(element.getAttribute("time"));
        var skipped = hasDirectChild(element, "skipped");
        var allTestFailure = new ArrayList<JunitXmlTestFailure>();
        var allNode = element.getChildNodes();
        for (int i = 0; i < allNode.getLength(); i++) {
            var node = allNode.item(i);
            if (node instanceof Element elem) {
                var tag = elem.getTagName();
                if ("failure".equals(tag) || "error".equals(tag)) {
                    allTestFailure.add(parseFailure(elem));
                }
            }
        }

        var stdout = getDirectChildText(element, "system-out");
        var stderr = getDirectChildText(element, "system-err");

        return new JunitXmlTestCase(name, classname, duration, skipped, allTestFailure, stdout, stderr);
    }

    private JunitXmlTestFailure parseFailure(Element element) {
        var message = element.getAttribute("message");
        var type = element.getAttribute("type");
        var content = element.getTextContent();
        return new JunitXmlTestFailure(message, type, content);
    }

    private boolean hasDirectChild(Element parent, String tagName) {
        var allNode = parent.getChildNodes();
        for (int i = 0; i < allNode.getLength(); i++) {
            var node = allNode.item(i);
            if (node instanceof Element elem && tagName.equals(elem.getTagName())) {
                return true;
            }
        }
        return false;
    }

    private String getDirectChildText(Element parent, String tagName) {
        var allNode = parent.getChildNodes();
        for (int i = 0; i < allNode.getLength(); i++) {
            var node = allNode.item(i);
            if (node instanceof Element elem && tagName.equals(elem.getTagName())) {
                return elem.getTextContent();
            }
        }
        return "";
    }

    private double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
