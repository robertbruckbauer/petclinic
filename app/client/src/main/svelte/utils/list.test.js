import { describe, it, expect, vi, beforeEach } from "vitest";
import { mapify } from "./list.js";

describe("list", () => {
  let allItem, keyFunction, sortFunction;

  beforeEach(() => {
    allItem = [
      { code: "a", name: "Item eins" },
      { code: "b", name: "Item zwei" },
      { code: "a", name: "Item drei" },
      { name: "Item vier" },
    ];
    keyFunction = (e) => e.code;
    sortFunction = (e1, e2) => e2.name.localeCompare(e1.name);
  });

  describe("mapify", () => {
    it("should convert array to map by id", () => {
      const result = mapify(allItem, keyFunction, sortFunction);
      expect(result).toBeInstanceOf(Map);
      expect(result.size).toBe(3);
      expect(result.get("a")).toEqual([allItem[0], allItem[2]]);
      expect(result.get("b")).toEqual([allItem[1]]);
      expect(result.get(undefined)).toEqual([allItem[3]]);
    });

    it("should return empty map for empty array", () => {
      const result = mapify([], keyFunction, sortFunction);
      expect(result).toBeInstanceOf(Map);
      expect(result.size).toBe(0);
    });

    it("should fail for null input", () => {
      expect(() => mapify(null, keyFunction, sortFunction)).toThrowError(
        "items are required"
      );
      expect(() => mapify(allItem, null, sortFunction)).toThrowError(
        "key function is required"
      );
      expect(() => mapify(allItem, keyFunction, null)).toThrowError(
        "sort function is required"
      );
    });

    it("should fail for undefined input", () => {
      expect(() => mapify(undefined, keyFunction, sortFunction)).toThrowError(
        "items are required"
      );
      expect(() => mapify(allItem, undefined, sortFunction)).toThrowError(
        "key function is required"
      );
      expect(() => mapify(allItem, keyFunction, undefined)).toThrowError(
        "sort function is required"
      );
    });
  });
});
