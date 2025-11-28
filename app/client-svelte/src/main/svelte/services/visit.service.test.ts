import { describe, it, expect, vi, beforeEach } from "vitest";
import { VisitService } from "./visit.service";
import { type Visit } from "../types/visit.type";
import { type ErrorItem } from "../types/error.type";

const MOCKVISIT: Visit = {
  id: "1",
  version: 1,
  date: "2024-01-01",
  text: "Regular checkup",
  owner: "owner1",
  pet: "pet1",
  vet: "vet1",
};

const MOCKVISITS: Visit[] = [MOCKVISIT];

describe("VisitService", () => {
  let visitService: VisitService;
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
    visitService = new VisitService();
  });

  it("should be created", () => {
    expect(visitService).toBeTruthy();
  });

  describe("loadAllVisit", () => {
    it("should load visits successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: MOCKVISITS }),
      });
      visitService.loadAllVisit().subscribe({
        next: (visits) => {
          expect(visits).toEqual(MOCKVISITS);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const errorItem: ErrorItem = {
        instance: "/api/visit",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        json: async () => errorItem,
      });
      visitService.loadAllVisit().subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(errorItem);
        },
      });
    });
  });

  describe("loadOneVisit", () => {
    it("should load one visit successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => MOCKVISIT,
      });
      visitService.loadOneVisit("1").subscribe({
        next: (visit) => {
          expect(visit).toEqual(MOCKVISIT);
        },
      });
    });
  });

  describe("createVisit", () => {
    it("should create visit successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => MOCKVISIT,
      });
      visitService.createVisit(MOCKVISIT).subscribe({
        next: (visit) => {
          expect(visit).toEqual(MOCKVISIT);
        },
      });
    });
  });

  describe("updateVisit", () => {
    it("should update visit successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => MOCKVISIT,
      });
      visitService.updateVisit(MOCKVISIT.id, MOCKVISIT).subscribe({
        next: (visit) => {
          expect(visit).toEqual(MOCKVISIT);
        },
      });
    });
  });

  describe("updateVisitPatch", () => {
    it("should patch visit successfully", () => {
      const patch = { text: "Updated checkup" };
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ ...MOCKVISIT, ...patch }),
      });
      visitService.updateVisitPatch("1", patch).subscribe({
        next: (visit) => {
          expect(visit.text).toEqual("Updated checkup");
        },
      });
    });
  });

  describe("removeVisit", () => {
    it("should remove visit successfully", () => {
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => MOCKVISIT,
      });
      visitService.removeVisit("1").subscribe({
        next: (visit) => {
          expect(visit).toEqual(MOCKVISIT);
        },
      });
    });
  });
});
