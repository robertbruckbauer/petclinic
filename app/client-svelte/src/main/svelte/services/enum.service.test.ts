import { describe, it, expect, vi, beforeEach } from "vitest";
import { EnumService } from "./enum.service";
import { type EnumItem } from "../types/enum.type";
import { type ErrorItem } from "../types/error.type";

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
  let fetchMock: any;

  beforeEach(() => {
    // Mock window object for tests
    global.window = {
      location: {
        protocol: "http:",
        host: "localhost:5050",
      },
    } as any;

    fetchMock = vi.fn();
    global.fetch = fetchMock;
    enumService = new EnumService();
  });

  it("should be created", () => {
    expect(enumService).toBeTruthy();
  });

  describe("loadAllEnum", () => {
    it("should load enum items successfully", async () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({
          content: ALLSPECIES,
        }),
      });
      enumService.loadAllEnum("species").subscribe({
        next: (allItem) => {
          expect(allItem).toEqual(ALLSPECIES);
        },
      });
    });

    it("should handle errors gracefully", async () => {
      const errorItem: ErrorItem = {
        instance: "/api/enum/species",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        statusText: "Not Found",
        json: async () => errorItem,
      });
      enumService.loadAllEnum("species").subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(errorItem);
        },
      });
    });
  });

  describe("createEnum", () => {
    it("should create enum item successfully", async () => {
      const newItem = ALLSPECIES[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => newItem,
      });
      enumService.createEnum("species", newItem).subscribe({
        next: (createdItem) => {
          expect(createdItem).toEqual(newItem);
        },
      });
    });

    it("should handle errors gracefully", async () => {
      const errorItem: ErrorItem = {
        instance: "/api/enum/species",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        statusText: "Not Found",
        json: async () => errorItem,
      });
      enumService.createEnum("species", ALLSPECIES[0]).subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(errorItem);
        },
      });
    });
  });

  describe("updateEnum", () => {
    it("should update enum item successfully", async () => {
      const newItem = ALLSPECIES[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => newItem,
      });
      enumService.updateEnum("species", newItem).subscribe({
        next: (createdItem) => {
          expect(createdItem).toEqual(newItem);
        },
      });
    });

    it("should handle errors gracefully", async () => {
      const errorItem: ErrorItem = {
        instance: "/api/enum/species",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        statusText: "Not Found",
        json: async () => errorItem,
      });
      enumService.updateEnum("species", ALLSPECIES[0]).subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(errorItem);
        },
      });
    });
  });

  describe("removeEnum", () => {
    it("should remove enum item successfully", async () => {
      const newItem = ALLSPECIES[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => newItem,
      });
      enumService.removeEnum("species", newItem.code).subscribe({
        next: (createdItem) => {
          expect(createdItem).toEqual(newItem);
        },
      });
    });

    it("should handle errors gracefully", async () => {
      const errorItem: ErrorItem = {
        instance: "/api/enum/species",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        statusText: "Not Found",
        json: async () => errorItem,
      });
      enumService.removeEnum("species", 1).subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(errorItem);
        },
      });
    });
  });
});
