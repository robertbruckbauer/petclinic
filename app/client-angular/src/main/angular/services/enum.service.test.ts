import { describe, it, expect, vi, beforeEach } from "vitest";
import { of } from "rxjs";
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
  });

  describe("loadAllEnum", () => {
    it("should load enum items successfully", () => {
      const content = ALLSPECIES;
      httpClientMock.get.mockReturnValue(of({ content: content }));
      enumService.loadAllEnum("species").subscribe({
        next: (allItem) => {
          expect(allItem).toEqual(content);
        },
      });
    });
  });

  describe("createEnum", () => {
    it("should create enum item successfully", () => {
      const content = ALLSPECIES[0];
      httpClientMock.post.mockReturnValue(of(content));
      enumService.createEnum("species", content).subscribe({
        next: (createdItem) => {
          expect(createdItem).toEqual(content);
        },
      });
    });
  });

  describe("updateEnum", () => {
    it("should create enum item successfully", () => {
      const content = ALLSPECIES[0];
      httpClientMock.put.mockReturnValue(of(content));
      enumService.updateEnum("species", content).subscribe({
        next: (createdItem) => {
          expect(createdItem).toEqual(content);
        },
      });
    });
  });

  describe("removeEnum", () => {
    it("should create enum item successfully", () => {
      const content = ALLSPECIES[0];
      httpClientMock.delete.mockReturnValue(of(content));
      enumService.removeEnum("species", content.code).subscribe({
        next: (createdItem) => {
          expect(createdItem).toEqual(content);
        },
      });
    });
  });
});
