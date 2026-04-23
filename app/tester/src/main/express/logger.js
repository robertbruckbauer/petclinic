import winston from "winston";

const { combine, timestamp, printf, colorize } = winston.format;

// Custom format for console output
const consoleFormat = printf(({ level, message, timestamp, ...metadata }) => {
  let msg = `${timestamp} [${level}] ${message}`;

  // Add metadata if present
  if (Object.keys(metadata).length > 0) {
    msg += ` ${JSON.stringify(metadata)}`;
  }

  return msg;
});

// Create the base logger
const baseLogger = winston.createLogger({
  level: process.env.LOG_LEVEL || "info",
  format: combine(
    timestamp({ format: "YYYY-MM-DD HH:mm:ss" }),
    winston.format.errors({ stack: true }),
    winston.format.splat(),
    winston.format.json()
  ),
  defaultMeta: { service: "tester" },
  transports: [
    // Console transport with color
    new winston.transports.Console({
      format: combine(
        colorize(),
        timestamp({ format: "YYYY-MM-DD HH:mm:ss" }),
        consoleFormat
      ),
    }),
  ],
});

/**
 * Logger wrapper that accepts scope as first parameter
 */
const logger = {
  info: (scope, message, ...meta) => {
    baseLogger.info(`[${scope}] ${message}`, ...meta);
  },
  warn: (scope, message, ...meta) => {
    baseLogger.warn(`[${scope}] ${message}`, ...meta);
  },
  error: (scope, message, ...meta) => {
    baseLogger.error(`[${scope}] ${message}`, ...meta);
  },
  debug: (scope, message, ...meta) => {
    baseLogger.debug(`[${scope}] ${message}`, ...meta);
  },
};

export default logger;
