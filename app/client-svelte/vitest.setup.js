import { vi, afterEach, beforeEach } from "vitest";

const mockWindow = {
  location: {
    protocol: "http:",
    host: "localhost:5050",
    hostname: "localhost",
    port: "5050",
    pathname: "/",
    href: "http://localhost:5050/"
  }
};

beforeEach(() => {
  vi.stubGlobal("window", mockWindow);
});

afterEach(() => {
  vi.unstubAllGlobals?.();
});
