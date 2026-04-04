import { describe, it, expect, vi, beforeEach } from "vitest";
import { get } from "svelte/store";

// A real-storage-backed mock so getItem/setItem stay consistent within a test.
// The internal `store` object is shared across all tests and cleared in beforeEach.
const storageData: Record<string, string> = {};

const localStorageMock = {
  getItem: vi.fn((key: string) => storageData[key] ?? null),
  setItem: vi.fn((key: string, value: string) => {
    storageData[key] = value;
  }),
  removeItem: vi.fn((key: string) => {
    delete storageData[key];
  }),
  clear: vi.fn(() => {
    Object.keys(storageData).forEach((k) => delete storageData[k]);
  }),
};

describe("owner.store", () => {
  beforeEach(() => {
    // Reset module registry so each test gets a fresh store initialised
    // with the current localStorage state — same pattern as router.test.ts.
    vi.resetModules();
    localStorageMock.clear();
    vi.clearAllMocks();
    vi.stubGlobal("localStorage", localStorageMock);
  });

  describe("initialization", () => {
    it("should use default payload when localStorage is empty", async () => {
      const { storedOwner } = await import("./owner.store");

      expect(get(storedOwner)).toEqual({ id: undefined });
    });

    it("should restore value from localStorage when a stored entry exists", async () => {
      const stored = { id: "42" };
      storageData["storedOwner"] = JSON.stringify(stored);

      const { storedOwner } = await import("./owner.store");

      expect(get(storedOwner)).toEqual(stored);
    });

    it("should fall back to default payload when localStorage contains null", async () => {
      storageData["storedOwner"] = "null";

      const { storedOwner } = await import("./owner.store");

      expect(get(storedOwner)).toEqual({ id: undefined });
    });
  });

  describe("persistence", () => {
    it("should write to localStorage when value is set", async () => {
      const { storedOwner } = await import("./owner.store");
      vi.clearAllMocks(); // ignore the initial subscribe write

      storedOwner.set({ id: "99" });

      expect(localStorageMock.setItem).toHaveBeenCalledWith(
        "storedOwner",
        JSON.stringify({ id: "99" })
      );
    });

    it("should write to localStorage when value is updated", async () => {
      const { storedOwner } = await import("./owner.store");
      vi.clearAllMocks();

      storedOwner.update((current) => ({ ...current, id: "123" }));

      expect(localStorageMock.setItem).toHaveBeenCalledWith(
        "storedOwner",
        JSON.stringify({ id: "123" })
      );
    });

    it("should write to localStorage on every change", async () => {
      const { storedOwner } = await import("./owner.store");
      vi.clearAllMocks();

      storedOwner.set({ id: "1" });
      storedOwner.set({ id: "2" });

      expect(localStorageMock.setItem).toHaveBeenCalledTimes(2);
      expect(localStorageMock.setItem).toHaveBeenLastCalledWith(
        "storedOwner",
        JSON.stringify({ id: "2" })
      );
    });
  });

  describe("store value", () => {
    it("should reflect new value after set", async () => {
      const { storedOwner } = await import("./owner.store");

      storedOwner.set({ id: "7" });

      expect(get(storedOwner)).toEqual({ id: "7" });
    });

    it("should reflect new value after update", async () => {
      const { storedOwner } = await import("./owner.store");

      storedOwner.update((current) => ({ ...current, id: "8" }));

      expect(get(storedOwner)).toEqual({ id: "8" });
    });

    it("should allow clearing the id", async () => {
      storageData["storedOwner"] = JSON.stringify({ id: "5" });
      const { storedOwner } = await import("./owner.store");

      storedOwner.set({ id: undefined });

      expect(get(storedOwner)).toEqual({ id: undefined });
    });
  });
});
