import { describe, it, expect, vi, beforeEach } from "vitest";
import { mapOwnerToOwnerItem, OwnerService } from "./owner.service";
import type { ErrorItem } from "../types/error.type";
import type { Owner, OwnerItem } from "../types/owner.type";

const ALLOWNER = [
  {
    id: "1",
    version: 1,
    name: "John Doe",
    address: "123 Upper St",
    contact: "john@example.com",
    allPetItem: [],
  },
  {
    id: "2",
    version: 1,
    name: "Jane Doe",
    address: "456 Lower St",
    contact: "jane@example.com",
    allPetItem: [],
  },
];

describe("OwnerService", () => {
  let ownerService: OwnerService;
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
    ownerService = new OwnerService();
  });

  it("should be created", () => {
    expect(ownerService).toBeTruthy();
  });

  describe("loadAllOwner", () => {
    it("should load owners successfully", () => {
      const content: Owner[] = ALLOWNER;
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: content }),
      });
      ownerService.loadAllOwner().subscribe({
        next: (allOwner) => {
          expect(allOwner).toEqual(content);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const error: ErrorItem = {
        instance: "/api/owner",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        json: async () => error,
      });
      ownerService.loadAllOwner().subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(error);
        },
      });
    });
  });

  describe("loadOneOwner", () => {
    it("should load one owner successfully", () => {
      const content: Owner = ALLOWNER[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      ownerService.loadOneOwner(content.id!).subscribe({
        next: (owner) => {
          expect(owner).toEqual(content);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const error: ErrorItem = {
        instance: "/api/owner/1",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        json: async () => error,
      });
      ownerService.loadOneOwner("1").subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(error);
        },
      });
    });
  });

  describe("loadAllOwnerItem", () => {
    it("should load owner items successfully", () => {
      const content: OwnerItem[] = ALLOWNER.map(mapOwnerToOwnerItem);
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: [content] }),
      });
      ownerService.loadAllOwnerItem().subscribe({
        next: (allItem) => {
          expect(allItem).toEqual([content]);
        },
      });
    });
  });

  describe("createOwner", () => {
    it("should create owner successfully", () => {
      const content: Owner = ALLOWNER[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      ownerService.createOwner(content).subscribe({
        next: (owner) => {
          expect(owner).toEqual(content);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const content: Owner = ALLOWNER[0];
      const error: ErrorItem = {
        instance: "/api/owner",
        status: 400,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 400,
        json: async () => error,
      });
      ownerService.createOwner(content).subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(error);
        },
      });
    });
  });

  describe("updateOwner", () => {
    it("should update owner successfully", () => {
      const content: Owner = ALLOWNER[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      ownerService.updateOwner(content).subscribe({
        next: (owner) => {
          expect(owner).toEqual(content);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const content: Owner = ALLOWNER[0];
      const error: ErrorItem = {
        instance: "/api/owner/1",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        json: async () => error,
      });
      ownerService.updateOwner(content).subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(error);
        },
      });
    });
  });

  describe("removeOwner", () => {
    it("should remove owner successfully", () => {
      const content: Owner = ALLOWNER[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      ownerService.removeOwner(content.id!).subscribe({
        next: (owner) => {
          expect(owner).toEqual(content);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const content: Owner = ALLOWNER[0];
      const error: ErrorItem = {
        instance: "/api/owner/1",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        json: async () => error,
      });
      ownerService.removeOwner(content.id!).subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(error);
        },
      });
    });
  });
});
