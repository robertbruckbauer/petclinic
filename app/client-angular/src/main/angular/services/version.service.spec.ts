import { describe, it, expect, vi, beforeEach } from "vitest";
import { provideZonelessChangeDetection } from "@angular/core";
import { TestBed } from "@angular/core/testing";
import { HttpClient } from "@angular/common/http";
import { of, throwError } from "rxjs";
import { VersionService } from "./version.service";
import { type Version } from "../types/version.type";

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
    TestBed.configureTestingModule({
      providers: [
        VersionService,
        { provide: HttpClient, useValue: httpClientMock },
        provideZonelessChangeDetection(),
      ],
    });
    versionService = TestBed.inject(VersionService);
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
      const notFoundError = {
        status: 404,
        statusText: "Not Found",
      };
      httpClientMock.get.mockReturnValue(throwError(() => notFoundError));
      versionService.loadVersion().subscribe({
        error: (err) => {
          expect(err).toBe(notFoundError);
        },
      });
    });
  });
});
