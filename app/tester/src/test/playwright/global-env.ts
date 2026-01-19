/**
 * Express application for the setup and taerdown of the debezium connector
 * and the redpanda topic consumer and for convenience operations for the
 * sqlite database.
 */
export const TESTER_URL = process.env.TESTER_URL || "http://localhost:9090";
