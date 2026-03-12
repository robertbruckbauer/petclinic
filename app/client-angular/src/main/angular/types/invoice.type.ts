export type InvoiceStatus = "D" | "I" | "C" | "X";

export interface Invoice {
  id?: string;
  version: number;
  issued: string;
  period: string;
  status: InvoiceStatus;
  text: string;
  visit?: string | null;
}
