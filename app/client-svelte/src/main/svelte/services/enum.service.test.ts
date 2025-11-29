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
      const content = ALLSPECIES;
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({
          content: content,
        }),
      });
      enumService.loadAllEnum("species").subscribe({
        next: (allItem) => {
          expect(allItem).toEqual(content);
        },
      });
    });

    it("should handle errors gracefully", async () => {
      const error: ErrorItem = {
        instance: "/api/enum/species",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        statusText: "Not Found",
        json: async () => error,
      });
      enumService.loadAllEnum("species").subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(error);
        },
      });
    });
  });

  describe("createEnum", () => {
    it("should create enum item successfully", async () => {
      const content = ALLSPECIES[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      enumService.createEnum("species", content).subscribe({
        next: (createdItem) => {
          expect(createdItem).toEqual(content);
        },
      });
    });

    it("should handle errors gracefully", async () => {
      const content = ALLSPECIES[0];
      const error: ErrorItem = {
        instance: "/api/enum/species",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        statusText: "Not Found",
        json: async () => error,
      });
      enumService.createEnum("species", content).subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(error);
        },
      });
    });
  });

  describe("updateEnum", () => {
    it("should update enum item successfully", async () => {
      const content = ALLSPECIES[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      enumService.updateEnum("species", content).subscribe({
        next: (createdItem) => {
          expect(createdItem).toEqual(content);
        },
      });
    });

    it("should handle errors gracefully", async () => {
      const content = ALLSPECIES[0];
      const error: ErrorItem = {
        instance: "/api/enum/species",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        statusText: "Not Found",
        json: async () => error,
      });
      enumService.updateEnum("species", content).subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(error);
        },
      });
    });
  });

  describe("removeEnum", () => {
    it("should remove enum item successfully", async () => {
      const content = ALLSPECIES[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      enumService.removeEnum("species", content.code).subscribe({
        next: (createdItem) => {
          expect(createdItem).toEqual(content);
        },
      });
    });

    it("should handle errors gracefully", async () => {
      const content = ALLSPECIES[0];
      const error: ErrorItem = {
        instance: "/api/enum/species",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        statusText: "Not Found",
        json: async () => error,
      });
      enumService.removeEnum("species", content.code).subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(error);
        },
      });
    });
  });
});
