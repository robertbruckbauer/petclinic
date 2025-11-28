import { describe, it, expect, vi, beforeEach } from "vitest";
import { VersionService } from "./version.service";

describe("VersionService", () => {
  let versionService: VersionService;
  let fetchMock: any;

  beforeEach(() => {
    global.window = {
      location: {
        protocol: "http:",
        host: "localhost:5050",
      },
    } as any;

    fetchMock = vi.fn();
    global.fetch = fetchMock;
    versionService = new VersionService();
  });

  it("should be created", () => {
    expect(versionService).toBeTruthy();
  });

  describe("version", () => {
    it("should load version successfully", () => {
      const mockResponse = new Response("1.0.0", { status: 200 });
      fetchMock.mockResolvedValue(mockResponse);

      versionService.version().subscribe({
        next: (response) => {
          expect(response).toBe(mockResponse);
        },
      });
    });

    it("should handle errors gracefully", () => {
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
      });

      versionService.version().subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err.message).toContain("404");
        },
      });
    });
  });

  describe("helper methods", () => {
    it("should return correct API explorer URL", () => {
      expect(versionService.apiExplorerUrl()).toBe(
        "http://localhost:8080/api/explorer"
      );
    });

    it("should return correct API GraphiQL URL", () => {
      expect(versionService.apiGraphiqlUrl()).toBe(
        "http://localhost:8080/api/graphiql"
      );
    });
  });
});
