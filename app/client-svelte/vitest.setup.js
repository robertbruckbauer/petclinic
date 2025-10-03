import { vi, afterEach } from "vitest";

vi.stubGlobal("window", {
  location: { protocol: "http:", host: "localhost:5050" },
});

afterEach(() => {
  vi.unstubAllGlobals?.();
});
