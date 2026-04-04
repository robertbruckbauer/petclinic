import { describe, it, expect, vi, beforeEach } from "vitest";
import { of } from "rxjs";
import { EnumService, filterByCriteria } from "./enum.service";
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

describe("filterByCriteria", () => {
  const item: EnumItem = {
    code: 1,
    name: "Dog",
    text: "A dog is an animal of the species Canis lupus familiaris.",
  };

  it("should return true when criteria is null", () => {
    expect(filterByCriteria(null)(item)).toBe(true);
  });

  it("should return true when criteria is undefined", () => {
    expect(filterByCriteria(undefined)(item)).toBe(true);
  });

  it("should return true when criteria is empty string", () => {
    expect(filterByCriteria("")(item)).toBe(true);
  });

  it("should return true when name matches criteria", () => {
    expect(filterByCriteria("Dog")(item)).toBe(true);
  });

  it("should return true when name matches criteria case-insensitively", () => {
    expect(filterByCriteria("dog")(item)).toBe(true);
  });

  it("should return true when text matches criteria", () => {
    expect(filterByCriteria("A dog")(item)).toBe(true);
  });

  it("should return false when neither name nor text matches criteria", () => {
    expect(filterByCriteria("Cat")(item)).toBe(false);
  });
});
