import { describe, it, expect, vi, beforeEach } from "vitest";
import { mapOwnerToOwnerItem, OwnerService } from "./owner.service";
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

    it("should load owners with search parameters successfully", () => {
      const content: Owner[] = ALLOWNER;
      const search = { sort: "name,asc" };
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: content }),
      });
      ownerService.loadAllOwner(search).subscribe({
        next: (allOwner) => {
          expect(allOwner).toEqual(content);
          expect(fetchMock).toHaveBeenCalledWith(
            "http://localhost:8080/api/owner?sort=name%2Casc",
            expect.anything()
          );
        },
      });
    });
  });

  describe("loadAllOwnerItem", () => {
    it("should load owner items successfully", () => {
      const content: OwnerItem[] = ALLOWNER.map(mapOwnerToOwnerItem);
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: content }),
      });
      ownerService.loadAllOwnerItem().subscribe({
        next: (allItem) => {
          expect(allItem).toEqual(content);
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
  });

  describe("mutateOwner", () => {
    it("should mutate owner successfully", () => {
      const content: Owner = ALLOWNER[0];
      const patch = { name: "Updated name" };
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ ...content, ...patch }),
      });
      ownerService.mutateOwner(content.id!, content).subscribe({
        next: (owner) => {
          expect(owner.name).toEqual(patch.name);
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
  });
});
