export interface LogRequest {
  testName: string;
  entityType: string;
  entityId: string;
}

export interface LogResponse {
  status: "success";
}
