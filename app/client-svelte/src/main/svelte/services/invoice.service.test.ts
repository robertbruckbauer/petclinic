import { describe, it, expect, vi, beforeEach } from "vitest";
import { InvoiceService } from "./invoice.service";
import type { Invoice } from "../types/invoice.type";

const ALLINVOICE: Invoice[] = [
  {
    id: "1",
    version: 1,
    issued: "2024-01-15",
    period: "PT720H",
    status: "D",
    text: "Lorem ipsum.",
  },
  {
    id: "2",
    version: 1,
    issued: "2024-03-01",
    period: "PT360H",
    status: "I",
    text: "Dolor sit amet.",
  },
];

describe("InvoiceService", () => {
  let invoiceService: InvoiceService;
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
    invoiceService = new InvoiceService();
  });

  it("should be created", () => {
    expect(invoiceService).toBeTruthy();
  });

  describe("loadAllInvoice", () => {
    it("should load invoices successfully", () => {
      const content: Invoice[] = ALLINVOICE;
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => ({ content: content }),
      });
      invoiceService.loadAllInvoice().subscribe({
        next: (allInvoice) => {
          expect(allInvoice).toEqual(content);
        },
      });
    });
  });

  describe("loadOneInvoice", () => {
    it("should load one invoice successfully", () => {
      const content: Invoice = ALLINVOICE[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      invoiceService.loadOneInvoice(content.id!).subscribe({
        next: (invoice) => {
          expect(invoice).toEqual(content);
        },
      });
    });
  });

  describe("createInvoice", () => {
    it("should create invoice successfully", () => {
      const content: Invoice = ALLINVOICE[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      invoiceService.createInvoice(content).subscribe({
        next: (invoice) => {
          expect(invoice).toEqual(content);
        },
      });
    });
  });

  describe("mutateInvoice", () => {
    it("should mutate invoice successfully", () => {
      const content: Invoice = ALLINVOICE[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      invoiceService.mutateInvoice(content.id!, content).subscribe({
        next: (invoice) => {
          expect(invoice).toEqual(content);
        },
      });
    });
  });

  describe("removeInvoice", () => {
    it("should remove invoice successfully", () => {
      const content: Invoice = ALLINVOICE[0];
      fetchMock.mockResolvedValue({
        ok: true,
        json: async () => content,
      });
      invoiceService.removeInvoice(content.id!).subscribe({
        next: (invoice) => {
          expect(invoice).toEqual(content);
        },
      });
    });
  });
});
