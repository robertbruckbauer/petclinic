package esy;

@SuppressWarnings("java:S1214")
public interface EsyBackendAware {

    /**
     * Path to the root page.
     */
    String ROOT_PATH = "/";

    /**
     * Path returning the version.
     */
    String HEALTHZ_PATH = "/healthz";

    /**
     * Base path of REST API requests.
     */
    String API_PATH = "/api";

    /**
     * Path to the landing page.
     */
    String HOME_PATH = "/home";

    /**
     * Path returning the version.
     */
    String VERSION_PATH = "/version";
}
