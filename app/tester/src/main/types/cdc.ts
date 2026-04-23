export interface CdcEvent {
  id: number;
  timestamp: string;
  entity_id: string;
  entity_type: string;
  operation: string;
}
