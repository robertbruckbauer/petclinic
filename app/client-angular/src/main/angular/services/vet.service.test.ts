import { describe, it, expect, vi, beforeEach } from "vitest";
import { of } from "rxjs";
import { mapVetToVetItem, VetService } from "./vet.service";
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
  let httpClientMock: any;

  beforeEach(() => {
    httpClientMock = {
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      patch: vi.fn(),
      delete: vi.fn(),
    };
    vetService = new VetService(httpClientMock);
  });

  it("should be created", () => {
    expect(vetService).toBeTruthy();
  });

  describe("loadAllVet", () => {
    it("should load vets successfully", () => {
      const content: Vet[] = ALLVET;
      httpClientMock.get.mockReturnValue(of({ content: content }));
      vetService.loadAllVet().subscribe({
        next: (allVet) => {
          expect(allVet).toEqual(content);
        },
      });
    });
  });

  describe("loadAllVetItem", () => {
    it("should load vet items successfully", () => {
      const content: VetItem[] = ALLVET.map(mapVetToVetItem);
      httpClientMock.get.mockReturnValue(of({ content: content }));
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
      httpClientMock.get.mockReturnValue(of(content));
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
      httpClientMock.post.mockReturnValue(of(content));
      vetService.createVet(content).subscribe({
        next: (vet) => {
          expect(vet).toEqual(content);
        },
      });
    });
  });

  describe("mutateVet", () => {
    it("should mutate vet successfully", () => {
      const content: Vet = ALLVET[0];
      const patch = { name: "Updated name" };
      httpClientMock.patch.mockReturnValue(of({ ...content, ...patch }));
      vetService.mutateVet(content.id!, content).subscribe({
        next: (vet) => {
          expect(vet.name).toEqual(patch.name);
        },
      });
    });
  });

  describe("removeVet", () => {
    it("should remove vet successfully", () => {
      const content: Vet = ALLVET[0];
      httpClientMock.delete.mockReturnValue(of(content));
      vetService.removeVet(content.id!).subscribe({
        next: (vet) => {
          expect(vet).toEqual(content);
        },
      });
    });
  });
});
