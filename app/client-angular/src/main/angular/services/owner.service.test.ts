import { describe, it, expect, vi, beforeEach } from "vitest";
import { of } from "rxjs";
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
  let httpClientMock: any;

  beforeEach(() => {
    httpClientMock = {
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      patch: vi.fn(),
      delete: vi.fn(),
    };
    ownerService = new OwnerService(httpClientMock);
  });

  it("should be created", () => {
    expect(ownerService).toBeTruthy();
  });

  describe("loadAllOwner", () => {
    it("should load owners successfully", () => {
      const content: Owner[] = ALLOWNER;
      httpClientMock.get.mockReturnValue(of({ content: content }));
      ownerService.loadAllOwner().subscribe({
        next: (allOwner) => {
          expect(allOwner).toEqual(content);
        },
      });
    });

    it("should load owners with search parameters successfully", () => {
      const content: Owner[] = ALLOWNER;
      const search = { sort: "name,asc" };
      httpClientMock.get.mockReturnValue(of({ content: content }));
      ownerService.loadAllOwner(search).subscribe({
        next: (allOwner) => {
          expect(allOwner).toEqual(content);
          expect(httpClientMock.get).toHaveBeenCalledWith(
            expect.stringContaining("/api/owner"),
            expect.objectContaining({
              params: expect.anything(),
            })
          );
        },
      });
    });
  });

  describe("loadAllOwnerItem", () => {
    it("should load owner items successfully", () => {
      const content: OwnerItem[] = ALLOWNER.map(mapOwnerToOwnerItem);
      httpClientMock.get.mockReturnValue(of({ content: content }));
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
      httpClientMock.get.mockReturnValue(of(content));
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
      httpClientMock.post.mockReturnValue(of(content));
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
      httpClientMock.patch.mockReturnValue(of(content));
      ownerService.mutateOwner(content.id!, content).subscribe({
        next: (owner) => {
          expect(owner).toEqual(content);
        },
      });
    });
  });

  describe("removeOwner", () => {
    it("should remove owner successfully", () => {
      const content: Owner = ALLOWNER[0];
      httpClientMock.delete.mockReturnValue(of(content));
      ownerService.removeOwner(content.id!).subscribe({
        next: (owner) => {
          expect(owner).toEqual(content);
        },
      });
    });
  });
});
