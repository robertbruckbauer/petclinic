package esy;

@SuppressWarnings("java:S1214")
public interface EsyBackendAware {

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
