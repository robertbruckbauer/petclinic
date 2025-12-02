import { describe, it, expect, vi, beforeEach } from "vitest";
import { VisitService } from "./visit.service";
import { type Visit } from "../types/visit.type";
import { type ErrorItem } from "../types/error.type";

const ALLVISIT = [
  {
    id: "1",
    version: 1,
    date: "2024-01-01",
    text: "Regular checkup",
    pet: "/api/pet/1",
    vet: "/api/vet/1",
  },
  {
    id: "2",
    version: 1,
    date: "2024-01-02",
    text: "Regular checkup",
    pet: "/api/pet/2",
    vet: "/api/vet/2",
  },
];

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
      const content: Visit[] = ALLVISIT;
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: content }),
      });
      visitService.loadAllVisit().subscribe({
        next: (allVisit) => {
          expect(allVisit).toEqual(content);
        },
      });
    });

    it("should handle errors gracefully", () => {
      const error: ErrorItem = {
        instance: "/api/visit",
        status: 404,
      };
      fetchMock.mockResolvedValue({
        ok: false,
        status: 404,
        json: async () => error,
      });
      visitService.loadAllVisit().subscribe({
        error: (err) => {
          expect(err).toBeDefined();
          expect(err).toEqual(error);
        },
      });
    });
  });

  describe("loadOneVisit", () => {
    it("should load one visit successfully", () => {
      const content: Visit = ALLVISIT[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      visitService.loadOneVisit(content.id!).subscribe({
        next: (visit) => {
          expect(visit).toEqual(content);
        },
      });
    });
  });

  describe("createVisit", () => {
    it("should create visit successfully", () => {
      const content: Visit = ALLVISIT[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      visitService.createVisit(content).subscribe({
        next: (visit) => {
          expect(visit).toEqual(content);
        },
      });
    });
  });

  describe("updateVisit", () => {
    it("should update visit successfully", () => {
      const content: Visit = ALLVISIT[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      visitService.updateVisit(content).subscribe({
        next: (visit) => {
          expect(visit).toEqual(content);
        },
      });
    });
  });

  describe("mutateVisit", () => {
    it("should patch visit successfully", () => {
      const content: Visit = ALLVISIT[0];
      const patch = { text: "Updated checkup" };
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ ...content, ...patch }),
      });
      visitService.mutateVisit(content.id!, patch).subscribe({
        next: (visit) => {
          expect(visit.text).toEqual("Updated checkup");
        },
      });
    });
  });

  describe("removeVisit", () => {
    it("should remove visit successfully", () => {
      const content: Visit = ALLVISIT[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      visitService.removeVisit(content.id!).subscribe({
        next: (visit) => {
          expect(visit).toEqual(content);
        },
      });
    });
  });
});
