import { describe, it, expect, vi, beforeEach } from "vitest";
import { of, throwError } from "rxjs";
import { EnumService } from "./enum.service";
import { type EnumItem } from "../types/enum.type";

const ALLSPECIES: EnumItem[] = [
  {
    code: 1,
    name: "Dog",
    text: "A dog is an animal of the species Canis lupus familiaris.",
  },
  {
    code: 2,
    name: "Cat",
    text: "A cat is an animal of the species Felis catus.",
  },
];

describe("EnumService", () => {
  let enumService: EnumService;
  let httpClientMock: any;

  beforeEach(() => {
    httpClientMock = {
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      delete: vi.fn(),
    };
    enumService = new EnumService(httpClientMock);
  });

  it("should be created", () => {
    expect(enumService).toBeTruthy();
    expect(enumService["httpClient"]).toBeDefined();
  });

  describe("loadAllEnum", () => {
    it("should load enum items successfully", () => {
      httpClientMock.get.mockReturnValue(
        of({
          content: ALLSPECIES,
        })
      );
      enumService.loadAllEnum("species").subscribe({
        next: (allItem) => {
          expect(allItem).toEqual(ALLSPECIES);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const notFoundError = {
        status: 404,
        statusText: "Not Found",
      };
      httpClientMock.get.mockReturnValue(throwError(() => notFoundError));
      enumService.loadAllEnum("species").subscribe({
        error: (err) => {
          expect(err).toBe(notFoundError);
        },
      });
    });
  });

  describe("createEnum", () => {
    it("should create enum item successfully", () => {
      const newItem = ALLSPECIES[0];
      httpClientMock.post.mockReturnValue(of(newItem));
      enumService.createEnum("species", newItem).subscribe({
        next: (createdItem) => {
          expect(createdItem).toEqual(newItem);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const notFoundError = {
        status: 404,
        statusText: "Not Found",
      };
      httpClientMock.post.mockReturnValue(throwError(() => notFoundError));
      enumService.createEnum("species", ALLSPECIES[0]).subscribe({
        error: (err) => {
          expect(err).toBe(notFoundError);
        },
      });
    });
  });

  describe("updateEnum", () => {
    it("should create enum item successfully", () => {
      const newItem = ALLSPECIES[0];
      httpClientMock.put.mockReturnValue(of(newItem));
      enumService.updateEnum("species", newItem).subscribe({
        next: (createdItem) => {
          expect(createdItem).toEqual(newItem);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const notFoundError = {
        status: 404,
        statusText: "Not Found",
      };
      httpClientMock.put.mockReturnValue(throwError(() => notFoundError));
      enumService.updateEnum("species", ALLSPECIES[0]).subscribe({
        error: (err) => {
          expect(err).toBe(notFoundError);
        },
      });
    });
  });

  describe("removeEnum", () => {
    it("should create enum item successfully", () => {
      const newItem = ALLSPECIES[0];
      httpClientMock.delete.mockReturnValue(of(newItem));
      enumService.removeEnum("species", newItem.code).subscribe({
        next: (createdItem) => {
          expect(createdItem).toEqual(newItem);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const notFoundError = {
        status: 404,
        statusText: "Not Found",
      };
      httpClientMock.delete.mockReturnValue(throwError(() => notFoundError));
      enumService.removeEnum("species", 0).subscribe({
        error: (err) => {
          expect(err).toBe(notFoundError);
        },
      });
    });
  });
});
