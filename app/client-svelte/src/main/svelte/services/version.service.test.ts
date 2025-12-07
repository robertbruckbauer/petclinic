import { describe, it, expect, vi, beforeEach } from "vitest";
import { VersionService } from "./version.service";
import type { Version } from "../types/version.type";
import type { ErrorItem } from "../types/error.type";

const VERSION = {
  version: "1.2.0",
};

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

  it("should return correct API explorer URL", () => {
    const url = versionService.apiExplorerUrl();
    expect(url.toString()).toBe("http://localhost:8080/api/explorer");
  });

  it("should return correct API GraphiQL URL", () => {
    const url = versionService.apiGraphiqlUrl();
    expect(url.toString()).toBe("http://localhost:8080/api/graphiql");
  });

  describe("loadVersion", () => {
    it("should load version successfully", () => {
      const content: Version = VERSION;
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      versionService.loadVersion().subscribe({
        next: (body) => {
          expect(body).toBe(content);
        },
      });
    });

    it("should handle backend errors gracefully", () => {
      const error: ErrorItem = {
        instance: "/version",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        json: async () => error,
      });
      versionService.loadVersion().subscribe({
        error: (body) => {
          expect(body.instance).toBe(error.instance);
          expect(body.status).toBe(error.status);
        },
      });
    });

    it("should handle network errors gracefully", () => {
      const error: ErrorItem = {
        instance: "/version",
        status: 0,
      };
      fetchMock.mockRejectedValueOnce(new TypeError("Failed to fetch"));
      versionService.loadVersion().subscribe({
        error: (body) => {
          expect(body.instance).toBe(error.instance);
          expect(body.status).toBe(error.status);
        },
      });
    });
  });
});
