import { test, expect } from "./fixtures";
import { BACKEND_URL, CASE_OWNER_BASIC_CRUD } from "./global-env.js";
import { Owner } from "../../main/types/owner.type.js";

test.describe("Phase 1 - Owner Management", () => {
  test(CASE_OWNER_BASIC_CRUD, async ({ logEntity }) => {
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

    const patchAddressResponse = await fetch(
      `${BACKEND_URL}/api/owner/${ownerId}`,
      {
        method: "PATCH",
        headers: {
          "Content-Type": "application/merge-patch+json",
        },
        body: JSON.stringify({
          address: "456 Side Street, Springfield",
        }),
      }
    );
    expect(patchAddressResponse.status).toBe(200);
    const ownerWithUpdatedAddress =
      (await patchAddressResponse.json()) as Owner;
    expect(ownerWithUpdatedAddress.address).toBe(
      "456 Side Street, Springfield"
    );

    const patchContactResponse = await fetch(
      `${BACKEND_URL}/api/owner/${ownerId}`,
      {
        method: "PATCH",
        headers: {
          "Content-Type": "application/merge-patch+json",
        },
        body: JSON.stringify({
          contact: "5557654321",
        }),
      }
    );
    expect(patchContactResponse.status).toBe(200);
    const ownerWithUpdatedContact =
      (await patchContactResponse.json()) as Owner;
    expect(ownerWithUpdatedContact.contact).toBe("5557654321");

    const deleteResponse = await fetch(`${BACKEND_URL}/api/owner/${ownerId}`, {
      method: "DELETE",
    });
    expect(deleteResponse.status).toBe(200);

    await logEntity("owner", ownerId);
  });
});
