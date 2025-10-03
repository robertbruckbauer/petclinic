import { describe, it, expect, vi } from "vitest";
import * as restApi from "./rest";

describe("rest", () => {
  describe("apiExplorerUrl", () => {
    it("should return a valid URL", () => {
      const url = restApi.apiExplorerUrl();
      expect(url).toBe("http://localhost:8080/api/explorer");
    });
  });

  describe("apiGraphiqlUrl", () => {
    it("should return a valid URL", () => {
      const url = restApi.apiGraphiqlUrl();
      expect(url).toBe("http://localhost:8080/api/graphiql");
    });
  });
});
