import { vi, afterEach, beforeEach } from "vitest";
import '@angular/compiler';

const mockWindow = {
  location: {
    protocol: "http:",
    host: "localhost:5052",
    hostname: "localhost",
    port: "5052",
    pathname: "/",
    href: "http://localhost:5052/"
  }
};

beforeEach(() => {
  vi.stubGlobal("window", mockWindow);
});

afterEach(() => {
  vi.unstubAllGlobals?.();
});
