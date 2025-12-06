import { describe, it, expect, vi, beforeEach } from "vitest";
import { of } from "rxjs";
import { mapPetToPetItem, PetService } from "./pet.service";
import type { Pet, PetItem } from "../types/pet.type";

const ALLPET = [
  {
    id: "1",
    version: 1,
    owner: "/api/owner/1",
    name: "Tom",
    born: "2020-01-01",
    species: "Cat",
  },
  {
    id: "2",
    version: 1,
    owner: "/api/owner/2",
    name: "Odi",
    born: "2020-12-31",
    species: "Dog",
  },
];

describe("PetService", () => {
  let petService: PetService;
  let httpClientMock: any;

  beforeEach(() => {
    httpClientMock = {
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      patch: vi.fn(),
      delete: vi.fn(),
    };
    petService = new PetService(httpClientMock);
  });

  it("should be created", () => {
    expect(petService).toBeTruthy();
  });

  describe("loadAllPet", () => {
    it("should load pets successfully", () => {
      const content: Pet[] = ALLPET;
      httpClientMock.get.mockReturnValue(of({ content: content }));
      petService.loadAllPet().subscribe({
        next: (allPet) => {
          expect(allPet).toEqual(content);
        },
      });
    });
  });

  describe("loadAllPetItem", () => {
    it("should load pet items successfully", () => {
      const content: PetItem[] = ALLPET.map(mapPetToPetItem);
      httpClientMock.get.mockReturnValue(of({ content: content }));
      petService.loadAllPetItem().subscribe({
        next: (allItem) => {
          expect(allItem).toEqual(content);
        },
      });
    });
  });

  describe("loadOnePet", () => {
    it("should load one pet successfully", () => {
      const content: Pet = ALLPET[0];
      httpClientMock.get.mockReturnValue(of(content));
      petService.loadOnePet(content.id!).subscribe({
        next: (pet) => {
          expect(pet).toEqual(content);
        },
      });
    });
  });

  describe("createPet", () => {
    it("should create pet successfully", () => {
      const content: Pet = ALLPET[0];
      httpClientMock.post.mockReturnValue(of(content));
      petService.createPet(content).subscribe({
        next: (pet) => {
          expect(pet).toEqual(content);
        },
      });
    });
  });

  describe("mutatePet", () => {
    it("should mutate pet successfully", () => {
      const content: Pet = ALLPET[0];
      httpClientMock.patch.mockReturnValue(of(content));
      petService.mutatePet(content.id!, content).subscribe({
        next: (pet) => {
          expect(pet).toEqual(content);
        },
      });
    });
  });

  describe("removePet", () => {
    it("should remove pet successfully", () => {
      const content: Pet = ALLPET[0];
      httpClientMock.delete.mockReturnValue(of(content));
      petService.removePet(content.id!).subscribe({
        next: (pet) => {
          expect(pet).toEqual(content);
        },
      });
    });
  });
});
