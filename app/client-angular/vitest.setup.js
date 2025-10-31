import { vi, afterEach } from "vitest";

vi.stubGlobal("window", {
  location: { protocol: "http:", host: "localhost:5052" },
});

afterEach(() => {
  vi.unstubAllGlobals?.();
});
