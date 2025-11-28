import { describe, it, expect, vi, beforeEach } from "vitest";
import { PetService } from "./pet.service";
import { type Pet, type PetItem } from "../types/pet.type";
import { type ErrorItem } from "../types/error.type";

const MOCKPET: Pet = {
  id: "1",
  version: 1,
  owner: "owner1",
  name: "Fluffy",
  born: "2020-01-01",
  species: "Dog",
};

const MOCKPETS: Pet[] = [MOCKPET];

const MOCKPETITEM: PetItem = {
  value: "1",
  text: "Fluffy",
};

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
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: MOCKPETS }),
      });
      petService.loadAllPet().subscribe({
        next: (pets) => {
          expect(pets).toEqual(MOCKPETS);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const errorItem: ErrorItem = {
        instance: "/api/pet",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        json: async () => errorItem,
      });
      petService.loadAllPet().subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(errorItem);
        },
      });
    });
  });

  describe("loadOnePet", () => {
    it("should load one pet successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => MOCKPET,
      });
      petService.loadOnePet("1").subscribe({
        next: (pet) => {
          expect(pet).toEqual(MOCKPET);
        },
      });
    });
  });

  describe("loadAllPetItem", () => {
    it("should load pet items successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: [MOCKPETITEM] }),
      });
      petService.loadAllPetItem("owner1").subscribe({
        next: (items) => {
          expect(items).toEqual([MOCKPETITEM]);
        },
      });
    });
  });

  describe("createPet", () => {
    it("should create pet successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => MOCKPET,
      });
      petService.createPet(MOCKPET).subscribe({
        next: (pet) => {
          expect(pet).toEqual(MOCKPET);
        },
      });
    });
  });

  describe("updatePet", () => {
    it("should update pet successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => MOCKPET,
      });
      petService.updatePet(MOCKPET.id, MOCKPET).subscribe({
        next: (pet) => {
          expect(pet).toEqual(MOCKPET);
        },
      });
    });
  });

  describe("removePet", () => {
    it("should remove pet successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => MOCKPET,
      });
      petService.removePet("1").subscribe({
        next: (pet) => {
          expect(pet).toEqual(MOCKPET);
        },
      });
    });
  });
});
