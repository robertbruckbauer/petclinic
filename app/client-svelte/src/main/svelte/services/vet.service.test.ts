import { describe, it, expect, vi, beforeEach } from "vitest";
import { VetService } from "./vet.service";
import { type Vet, type VetItem } from "../types/vet.type";
import { type ErrorItem } from "../types/error.type";

const MOCKVET: Vet = {
  id: "1",
  version: 1,
  name: "Dr. Smith",
  allSkill: ["Surgery", "Dentistry"],
};

const MOCKVETS: Vet[] = [MOCKVET];

const MOCKVETITEM: VetItem = {
  value: "1",
  text: "Dr. Smith",
};

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
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: MOCKVETS }),
      });
      vetService.loadAllVet().subscribe({
        next: (vets) => {
          expect(vets).toEqual(MOCKVETS);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const errorItem: ErrorItem = {
        instance: "/api/vet",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        json: async () => errorItem,
      });
      vetService.loadAllVet().subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(errorItem);
        },
      });
    });
  });

  describe("loadOneVet", () => {
    it("should load one vet successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => MOCKVET,
      });
      vetService.loadOneVet("1").subscribe({
        next: (vet) => {
          expect(vet).toEqual(MOCKVET);
        },
      });
    });
  });

  describe("loadAllVetItem", () => {
    it("should load vet items successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: [MOCKVETITEM] }),
      });
      vetService.loadAllVetItem().subscribe({
        next: (items) => {
          expect(items).toEqual([MOCKVETITEM]);
        },
      });
    });
  });

  describe("createVet", () => {
    it("should create vet successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => MOCKVET,
      });
      vetService.createVet(MOCKVET).subscribe({
        next: (vet) => {
          expect(vet).toEqual(MOCKVET);
        },
      });
    });
  });

  describe("updateVet", () => {
    it("should update vet successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => MOCKVET,
      });
      vetService.updateVet(MOCKVET.id, MOCKVET).subscribe({
        next: (vet) => {
          expect(vet).toEqual(MOCKVET);
        },
      });
    });
  });

  describe("removeVet", () => {
    it("should remove vet successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => MOCKVET,
      });
      vetService.removeVet("1").subscribe({
        next: (vet) => {
          expect(vet).toEqual(MOCKVET);
        },
      });
    });
  });
});
