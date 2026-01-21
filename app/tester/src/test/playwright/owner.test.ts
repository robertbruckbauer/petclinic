import { test, expect } from "./fixtures";
import { BACKEND_URL } from "./global-env.js";
import { Owner } from "../../main/types/owner.type.js";

test.describe("Phase 1 - Owner Management", () => {
  test("should create and delete an owner via REST API", async ({
    logEntity,
  }) => {
    const createResponse = await fetch(`${BACKEND_URL}/api/owner`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        version: 0,
        name: "John Doe",
        address: "123 Main Street, Springfield",
        contact: "5551234567",
      }),
    });
    expect(createResponse.status).toBe(201);
    const owner = (await createResponse.json()) as Owner;
    expect(owner.id).toBeDefined();
    const ownerId = owner.id!;

    const deleteResponse = await fetch(`${BACKEND_URL}/api/owner/${ownerId}`, {
      method: "DELETE",
    });
    expect(deleteResponse.status).toBe(200);

    await logEntity("should create and delete an owner via REST API", ownerId);
  });
});
