import { describe, it, expect, vi, beforeEach } from "vitest";
import { mapVetToVetItem, VetService } from "./vet.service";
import type { ErrorItem } from "../types/error.type";
import type { Vet, VetItem } from "../types/vet.type";

const ALLVET = [
  {
    id: "1",
    version: 1,
    name: "Dr. Smith",
    allSkill: ["Surgery", "Dentistry"],
  },
  {
    id: "2",
    version: 1,
    name: "Dr. Doolittle",
    allSkill: ["Surgery"],
  },
];

describe("VetService", () => {
  let vetService: VetService;
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
    vetService = new VetService();
  });

  it("should be created", () => {
    expect(vetService).toBeTruthy();
  });

  describe("loadAllVet", () => {
    it("should load vets successfully", () => {
      const content: Vet[] = ALLVET;
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: content }),
      });
      vetService.loadAllVet().subscribe({
        next: (allVet) => {
          expect(allVet).toEqual(content);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const error: ErrorItem = {
        instance: "/api/vet",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        json: async () => error,
      });
      vetService.loadAllVet().subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(error);
        },
      });
    });
  });

  describe("loadAllVetItem", () => {
    it("should load vet items successfully", () => {
      const content: VetItem[] = ALLVET.map(mapVetToVetItem);
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: content }),
      });
      vetService.loadAllVetItem().subscribe({
        next: (allItem) => {
          expect(allItem).toEqual(content);
        },
      });
    });
  });

  describe("loadOneVet", () => {
    it("should load one vet successfully", () => {
      const content: Vet = ALLVET[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      vetService.loadOneVet(content.id!).subscribe({
        next: (vet) => {
          expect(vet).toEqual(content);
        },
      });
    });
  });

  describe("createVet", () => {
    it("should create vet successfully", () => {
      const content: Vet = ALLVET[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      vetService.createVet(content).subscribe({
        next: (vet) => {
          expect(vet).toEqual(content);
        },
      });
    });
  });

  describe("updateVet", () => {
    it("should update vet successfully", () => {
      const content: Vet = ALLVET[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      vetService.updateVet(content).subscribe({
        next: (vet) => {
          expect(vet).toEqual(content);
        },
      });
    });
  });

  describe("removeVet", () => {
    it("should remove vet successfully", () => {
      const content: Vet = ALLVET[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      vetService.removeVet(content.id!).subscribe({
        next: (vet) => {
          expect(vet).toEqual(content);
        },
      });
    });
  });
});
