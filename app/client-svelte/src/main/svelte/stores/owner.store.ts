import { writable } from "svelte/store";

// https://dev.to/danawoodman/svelte-quick-tip-connect-a-store-to-local-storage-4idi
const KEY = "storedOwner";

interface Payload {
  id?: string;
}

function payload(): Payload {
  return {
    id: undefined,
  };
}

export const storedOwner = writable<Payload>(
  JSON.parse(localStorage.getItem(KEY) || "null") || payload()
);

storedOwner.subscribe((value) => {
  if (typeof value === "object") {
    localStorage.setItem(KEY, JSON.stringify(value));
  }
});
