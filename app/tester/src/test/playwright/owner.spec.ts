import { test, expect } from "./fixtures";

test.describe("Phase 2 - Owner CDC validation", () => {
  test("should create and delete an owner via REST API", ({
    queryAllEvent,
  }) => {
    const events = queryAllEvent(
      "should create and delete an owner via REST API"
    );
    expect(events.length).toBe(2);
  });
});
