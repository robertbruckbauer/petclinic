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
    expect(versionService["httpClient"]).toBeDefined();
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

    it("should handle errors gracefully", () => {
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
        error: (err) => {
          expect(err).toBe(error);
        },
      });
    });
  });
});
