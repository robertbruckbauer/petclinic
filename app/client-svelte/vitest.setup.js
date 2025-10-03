import { vi, afterEach } from "vitest";

vi.stubGlobal("window", {
  location: { protocol: "http:", host: "localhost:5000" },
});

afterEach(() => {
  vi.unstubAllGlobals?.();
});
