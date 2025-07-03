package esy;

/**
 * Die Klasse definiert die <i>base package</i> des standardisierten
 * JPA-Datenmodells der Anwendung.
 */
public final class EsyEntityRoot {

    public static final String PACKAGE_NAME = "esy.api";
    public static final String PACKAGE_PATH = "esy/api";

    public EsyEntityRoot() {
        if (!PACKAGE_NAME.startsWith(getClass().getPackageName())) {
            throw new IllegalStateException("expected " + PACKAGE_NAME);
        }
    }
}
