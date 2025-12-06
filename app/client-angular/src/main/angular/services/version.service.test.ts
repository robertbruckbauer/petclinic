import { describe, it, expect, vi, beforeEach } from "vitest";
import { of, throwError } from "rxjs";
import { VersionService } from "./version.service";
import { type Version } from "../types/version.type";
import { ErrorItem } from "../types/error.type";
import { HttpErrorResponse } from "@angular/common/http";

const VERSION: Version = {
  version: "1.2.0",
};

describe("VersionService", () => {
  let versionService: VersionService;
  let httpClientMock: any;

  beforeEach(() => {
    httpClientMock = {
      get: vi.fn(),
    };
    versionService = new VersionService(httpClientMock);
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
      httpClientMock.get.mockReturnValue(of(VERSION));
      versionService.loadVersion().subscribe({
        next: (body) => {
          expect(body).toEqual(VERSION);
        },
      });
    });

    it("should handle backend errors gracefully", () => {
      const error: ErrorItem = {
        instance: "/version",
        status: 404,
      };
      httpClientMock.get.mockReturnValue(
        throwError(
          () =>
            new HttpErrorResponse({
              error: error,
              status: error.status,
            })
        )
      );
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
      httpClientMock.get.mockReturnValue(
        throwError(
          () =>
            new HttpErrorResponse({
              error: "Network failure",
              status: 0,
            })
        )
      );
      versionService.loadVersion().subscribe({
        error: (body) => {
          expect(body.instance).toBe(error.instance);
          expect(body.status).toBe(error.status);
        },
      });
    });
  });
});
