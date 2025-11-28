import { describe, it, expect, vi, beforeEach } from "vitest";
import { OwnerService } from "./owner.service";
import { type Owner, type OwnerItem } from "../types/owner.type";
import { type ErrorItem } from "../types/error.type";

const MOCKOWNER: Owner = {
  id: "1",
  version: 1,
  name: "John Doe",
  address: "123 Main St",
  contact: "john@example.com",
  allPetItem: [],
};

const MOCKOWNERS: Owner[] = [MOCKOWNER];

const MOCKOWNERITEM: OwnerItem = {
  value: "1",
  text: "John Doe",
};

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
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: MOCKOWNERS }),
      });
      ownerService.loadAllOwner().subscribe({
        next: (owners) => {
          expect(owners).toEqual(MOCKOWNERS);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const errorItem: ErrorItem = {
        instance: "/api/owner",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        json: async () => errorItem,
      });
      ownerService.loadAllOwner().subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(errorItem);
        },
      });
    });
  });

  describe("loadOneOwner", () => {
    it("should load one owner successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => MOCKOWNER,
      });
      ownerService.loadOneOwner("1").subscribe({
        next: (owner) => {
          expect(owner).toEqual(MOCKOWNER);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const errorItem: ErrorItem = {
        instance: "/api/owner/1",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        json: async () => errorItem,
      });
      ownerService.loadOneOwner("1").subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(errorItem);
        },
      });
    });
  });

  describe("loadAllOwnerItem", () => {
    it("should load owner items successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: [MOCKOWNERITEM] }),
      });
      ownerService.loadAllOwnerItem().subscribe({
        next: (items) => {
          expect(items).toEqual([MOCKOWNERITEM]);
        },
      });
    });
  });

  describe("createOwner", () => {
    it("should create owner successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => MOCKOWNER,
      });
      ownerService.createOwner(MOCKOWNER).subscribe({
        next: (owner) => {
          expect(owner).toEqual(MOCKOWNER);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const errorItem: ErrorItem = {
        instance: "/api/owner",
        status: 400,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 400,
        json: async () => errorItem,
      });
      ownerService.createOwner(MOCKOWNER).subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(errorItem);
        },
      });
    });
  });

  describe("updateOwner", () => {
    it("should update owner successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => MOCKOWNER,
      });
      ownerService.updateOwner(MOCKOWNER.id, MOCKOWNER).subscribe({
        next: (owner) => {
          expect(owner).toEqual(MOCKOWNER);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const errorItem: ErrorItem = {
        instance: "/api/owner/1",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        json: async () => errorItem,
      });
      ownerService.updateOwner(MOCKOWNER.id, MOCKOWNER).subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(errorItem);
        },
      });
    });
  });

  describe("removeOwner", () => {
    it("should remove owner successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => MOCKOWNER,
      });
      ownerService.removeOwner("1").subscribe({
        next: (owner) => {
          expect(owner).toEqual(MOCKOWNER);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const errorItem: ErrorItem = {
        instance: "/api/owner/1",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        json: async () => errorItem,
      });
      ownerService.removeOwner("1").subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(errorItem);
        },
      });
    });
  });
});
