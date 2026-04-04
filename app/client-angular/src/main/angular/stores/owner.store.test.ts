import { describe, it, expect, vi, beforeEach } from "vitest";

// A real-storage-backed mock so getItem/setItem stay consistent within a test.
// The internal store object is shared across all tests and cleared in beforeEach.
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
    // Reset module registry so each test gets a fresh signal initialised
    // with the current localStorage state.
    vi.resetModules();
    localStorageMock.clear();
    vi.clearAllMocks();
    vi.stubGlobal("localStorage", localStorageMock);
  });

  describe("initialization", () => {
    it("should use default payload when localStorage is empty", async () => {
      const { getStoredOwner } = await import("./owner.store");

      expect(getStoredOwner()).toEqual({ id: undefined });
    });

    it("should restore value from localStorage when a stored entry exists", async () => {
      const stored = { id: "42" };
      storageData["storedOwner"] = JSON.stringify(stored);

      const { getStoredOwner } = await import("./owner.store");

      expect(getStoredOwner()).toEqual(stored);
    });

    it("should fall back to default payload when localStorage contains null", async () => {
      storageData["storedOwner"] = "null";

      const { getStoredOwner } = await import("./owner.store");

      expect(getStoredOwner()).toEqual({ id: undefined });
    });
  });

  describe("persistence", () => {
    it("should write to localStorage when setStoredOwner is called", async () => {
      const { setStoredOwner } = await import("./owner.store");
      vi.clearAllMocks();

      setStoredOwner({ id: "99" });

      expect(localStorageMock.setItem).toHaveBeenCalledWith(
        "storedOwner",
        JSON.stringify({ id: "99" })
      );
    });

    it("should write to localStorage on every call to setStoredOwner", async () => {
      const { setStoredOwner } = await import("./owner.store");
      vi.clearAllMocks();

      setStoredOwner({ id: "1" });
      setStoredOwner({ id: "2" });

      expect(localStorageMock.setItem).toHaveBeenCalledTimes(2);
      expect(localStorageMock.setItem).toHaveBeenLastCalledWith(
        "storedOwner",
        JSON.stringify({ id: "2" })
      );
    });
  });

  describe("store value", () => {
    it("should reflect new value after setStoredOwner", async () => {
      const { getStoredOwner, setStoredOwner } = await import("./owner.store");

      setStoredOwner({ id: "7" });

      expect(getStoredOwner()).toEqual({ id: "7" });
    });

    it("should reflect successive updates", async () => {
      const { getStoredOwner, setStoredOwner } = await import("./owner.store");

      setStoredOwner({ id: "7" });
      setStoredOwner({ id: "8" });

      expect(getStoredOwner()).toEqual({ id: "8" });
    });

    it("should allow clearing the id", async () => {
      storageData["storedOwner"] = JSON.stringify({ id: "5" });
      const { getStoredOwner, setStoredOwner } = await import("./owner.store");

      setStoredOwner({ id: undefined });

      expect(getStoredOwner()).toEqual({ id: undefined });
    });
  });
});
