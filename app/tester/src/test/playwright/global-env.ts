/**
 * Express application for the setup and taerdown of the debezium connector
 * and the redpanda topic consumer and for convenience operations for the
 * sqlite database.
 */
export const TESTER_URL = process.env.TESTER_URL || "http://localhost:9090";

/**
 * Spring Boot application acting as the backend for frontend applications.
 */
export const BACKEND_URL = process.env.BACKEND_URL || "http://localhost:8080";
