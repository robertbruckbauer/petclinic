import { describe, it, expect, vi, beforeEach } from "vitest";
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
    petService = new PetService();
  });

  it("should be created", () => {
    expect(petService).toBeTruthy();
  });

  describe("loadAllPet", () => {
    it("should load pets successfully", () => {
      const content: Pet[] = ALLPET;
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: content }),
      });
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
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: content }),
      });
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
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
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
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      petService.createPet(content).subscribe({
        next: (pet) => {
          expect(pet).toEqual(content);
        },
      });
    });
  });

  describe("updatePet", () => {
    it("should update pet successfully", () => {
      const content: Pet = ALLPET[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      petService.updatePet(content).subscribe({
        next: (pet) => {
          expect(pet).toEqual(content);
        },
      });
    });
  });

  describe("removePet", () => {
    it("should remove pet successfully", () => {
      const content: Pet = ALLPET[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      petService.removePet(content.id!).subscribe({
        next: (pet) => {
          expect(pet).toEqual(content);
        },
      });
    });
  });
});
