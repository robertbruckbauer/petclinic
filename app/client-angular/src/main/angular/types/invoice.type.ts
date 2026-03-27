export type InvoiceStatus = "D" | "I" | "C" | "X";

export interface Invoice {
  id?: string;
  version: number;
  at: string;
  due: string;
  status: InvoiceStatus;
  text: string;
}
