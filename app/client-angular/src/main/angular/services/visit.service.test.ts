import { describe, it, expect, vi, beforeEach } from "vitest";
import { of } from "rxjs";
import { VisitService } from "./visit.service";
import { type Visit } from "../types/visit.type";

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
  let httpClientMock: any;

  beforeEach(() => {
    httpClientMock = {
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      patch: vi.fn(),
      delete: vi.fn(),
    };
    visitService = new VisitService(httpClientMock);
  });

  it("should be created", () => {
    expect(visitService).toBeTruthy();
  });

  describe("loadAllVisit", () => {
    it("should load visits successfully", () => {
      const content: Visit[] = ALLVISIT;
      httpClientMock.get.mockReturnValue(of({ content: content }));
      visitService.loadAllVisit().subscribe({
        next: (allVisit) => {
          expect(allVisit).toEqual(content);
        },
      });
    });
  });

  describe("loadOneVisit", () => {
    it("should load one visit successfully", () => {
      const content: Visit = ALLVISIT[0];
      httpClientMock.get.mockReturnValue(of(content));
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
      httpClientMock.post.mockReturnValue(of(content));
      visitService.createVisit(content).subscribe({
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
      httpClientMock.patch.mockReturnValue(of({ ...content, ...patch }));
      visitService.mutateVisit(content.id!, patch).subscribe({
        next: (visit) => {
          expect(visit.text).toEqual(patch.text);
        },
      });
    });
  });

  describe("removeVisit", () => {
    it("should remove visit successfully", () => {
      const content: Visit = ALLVISIT[0];
      httpClientMock.delete.mockReturnValue(of(content));
      visitService.removeVisit(content.id!).subscribe({
        next: (visit) => {
          expect(visit).toEqual(content);
        },
      });
    });
  });
});
